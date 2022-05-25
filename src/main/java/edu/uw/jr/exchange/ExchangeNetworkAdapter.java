package edu.uw.jr.exchange;

import edu.uw.ext.framework.exchange.ExchangeAdapter;
import edu.uw.ext.framework.exchange.ExchangeEvent;
import edu.uw.ext.framework.exchange.StockExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static edu.uw.jr.exchange.ExchangeConstants.*;

/**
 * Provides a network interface to an exchange.
 *
 * @author Jesse Ruth
 */
public final class ExchangeNetworkAdapter implements ExchangeAdapter {

    final static Logger logger = LoggerFactory.getLogger(ExchangeNetworkAdapter.class);
    /**
     * Time to live
     */
    private static final int TTL = 2;
    /**
     * The exchange this adapter delegates to
     */
    private final StockExchange realExchange;
    /**
     * Event Socket
     */
    private MulticastSocket eventSocket;
    private final InetAddress multicastGroup;
    /**
     * Datagram packet used by this class
     */
    private final DatagramPacket datagramPacket;
    /**
     * Listener for inbound command connections
     */
    private final CommandListener commandListener;
    /**
     * Executor for commands
     */
    private final ExecutorService commandExecutorService;

    /**
     * Constructor
     *
     * @param realExchange  The actual exchange
     * @param multicastIP   Multicast address
     * @param multicastPort IP port used to propagate prices changes
     * @param commandPort   Port to listen for command
     * @throws UnknownHostException if unable to resolve IP
     */
    public ExchangeNetworkAdapter(final StockExchange realExchange, final String multicastIP, final int multicastPort, final int commandPort) throws UnknownHostException {
        this.realExchange = realExchange;
        multicastGroup = InetAddress.getByName(multicastIP);
        final byte[] buf = {};
        datagramPacket = new DatagramPacket(buf, 0, multicastGroup, multicastPort);
        try {
            eventSocket = new MulticastSocket();
            eventSocket.joinGroup(multicastGroup);
            eventSocket.setTimeToLive(TTL);
            logger.info("Multicasting events to {}:{}", multicastIP, multicastPort);
        } catch (IOException e) {
            logger.error("Failed to initialize event socket", e);
        }
        commandListener = new CommandListener(commandPort, realExchange);
        commandExecutorService = Executors.newSingleThreadExecutor();
        commandExecutorService.execute(commandListener);
        realExchange.addExchangeListener(this);
    }

    /**
     * Convenience method to send multicast events
     *
     * @param msg the string message to write
     * @throws IOException for a network failure
     */
    private synchronized void sendMulticastEvent(final String msg) throws IOException {
        logger.info("Sending multicast event message: {}", msg);
        final byte[] buf = msg.getBytes(ENCODING);
        datagramPacket.setData(buf);
        datagramPacket.setLength(buf.length);
        eventSocket.send(datagramPacket);
    }

    /**
     * Exchange opened and prices are adjusting.
     *
     * @param exchangeEvent the event
     */
    @Override
    public void exchangeOpened(ExchangeEvent exchangeEvent) {
        logger.info("Exchange Opened");
        try {
            sendMulticastEvent(OPEN_EVNT);
        } catch (final IOException ex) {
            logger.error("Error joining price change group:", ex);
        }
    }

    /**
     * The exchange has closed
     *
     * @param exchangeEvent the event
     */
    @Override
    public void exchangeClosed(ExchangeEvent exchangeEvent) {
        logger.info("Exchange Closed");
        try {
            sendMulticastEvent(CLOSED_EVNT);
        } catch (final IOException ex) {
            logger.error("Error multicasting the exchange closed:", ex);
        }

    }

    /**
     * Process price changes
     *
     * @param exchangeEvent the event
     */
    @Override
    public void priceChanged(ExchangeEvent exchangeEvent) {
        final String symbol = exchangeEvent.getTicker();
        final int price = exchangeEvent.getPrice();
        final String message = String.join(ELEMENT_DELIMITER, PRICE_CHANGE_EVNT, symbol, Integer.toString(price));
        logger.info("Price Changed: {}", message);

        try {
            sendMulticastEvent(message);
        } catch (final IOException ex) {
            logger.error("Error multicasting the price change:", ex);
        }
    }

    /**
     * Close the adapter
     */
    @Override
    public void close() {
        logger.info("Closed called");
        realExchange.removeExchangeListener(this);
        commandListener.terminate();
        commandExecutorService.shutdown();
        try {
            eventSocket.leaveGroup(multicastGroup);
        } catch (final IOException ex) {
            logger.warn("Failed to leave multicast group", ex);
        } finally {
            eventSocket.close();
        }
    }
}
