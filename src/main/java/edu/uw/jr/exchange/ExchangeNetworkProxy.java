package edu.uw.jr.exchange;

import edu.uw.ext.framework.exchange.ExchangeListener;
import edu.uw.ext.framework.exchange.StockExchange;
import edu.uw.ext.framework.exchange.StockQuote;
import edu.uw.ext.framework.order.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.Optional;
import java.util.concurrent.Executors;

import static edu.uw.jr.exchange.ExchangeConstants.*;

/**
 * Proxy for real stock exchange
 *
 * @author Jesse Ruth
 */
public class ExchangeNetworkProxy implements StockExchange {
    final static Logger logger = LoggerFactory.getLogger(ExchangeNetworkProxy.class);

    /**
     * Command IP address - the network adapter host
     */
    private String commandIpAddress;
    /**
     * The command port
     */
    private int commandPort;
    /**
     * The event processor, it propagates events to the registered listeners.
     */
    private NetEventProcessor eventProcessor;

    /**
     * Constructor
     *
     * @param eventIpAddress The multicast address to connect to
     * @param evenPort       the multicast port to connect to
     * @param cmdIpAddress   the address the exchange accepts request on
     * @param cmdPort        the port the exchange accepts request on
     */
    public ExchangeNetworkProxy(final String eventIpAddress, final int evenPort, final String cmdIpAddress, final int cmdPort) {
        commandIpAddress = cmdIpAddress;
        commandPort = cmdPort;
        eventProcessor = new NetEventProcessor(eventIpAddress, evenPort);
        Executors.newSingleThreadExecutor().execute(eventProcessor);
    }

    /**
     * Sends a command string to the exchange
     *
     * @param cmd the command to send
     * @return the response
     */
    private String sendTcpCmd(final String cmd) {
        String response = "";

        try (
                Socket sock = new Socket(commandIpAddress, commandPort);
                final InputStream inputStream = sock.getInputStream();
                final Reader reader = new InputStreamReader(inputStream, ENCODING);
                final BufferedReader br = new BufferedReader(reader);

                final OutputStream outputStream = sock.getOutputStream();
                final Writer writer = new OutputStreamWriter(outputStream, ENCODING);
                final PrintWriter printWriter = new PrintWriter(writer, true);

        ) {

            logger.info("connected to server {}:{}", sock.getLocalAddress(), sock.getLocalPort());

            printWriter.println(cmd);
            response = br.readLine();

        } catch (final IOException e) {
            logger.error("error sending command to exchage", e);
        }
        return response;
    }

    /**
     * The state of the exchange
     *
     * @return true if open otherwise false
     */
    @Override
    public boolean isOpen() {
        final String response = sendTcpCmd(GET_STATE_CMD);
        return OPEN_STATE.equals(response);
    }

    /**
     * Get the ticker symbols for all the stocks traded in the exchange.
     *
     * @return the stock ticker symbols
     */
    @Override
    public String[] getTickers() {
        final String response = sendTcpCmd(GET_TICKERS_CMD);
        return response.split(ELEMENT_DELIMITER);
    }

    /**
     * Gets a current stock price
     *
     * @param ticker the ticker symbol for the stock
     * @return the qoute, or null if unavailable
     */
    @Override
    public Optional<StockQuote> getQuote(final String ticker) {
        final String cmd = String.join(ELEMENT_DELIMITER, GET_QUOTE_CMD, ticker);
        final String response = sendTcpCmd(cmd);
        int price = INVALID_STOCK;

        try {
            price = Integer.parseInt(response);
        } catch (final NumberFormatException nex) {
            logger.warn("String to int conversion failed", response, nex);

        }
        return (price >= 0) ? Optional.of(new StockQuote(ticker, price)) : Optional.<StockQuote>empty();
    }


    /**
     * Adds a market even listener. Delegates to the NetEventProcessor
     *
     * @param exchangeListener listener to add
     */
    @Override
    public void addExchangeListener(final ExchangeListener exchangeListener) {
        eventProcessor.addExchangeListener(exchangeListener);
    }

    /**
     * Removed a market listener. Delegates to the NetEventProcessor
     *
     * @param exchangeListener listener to remove
     */
    @Override
    public void removeExchangeListener(final ExchangeListener exchangeListener) {
        eventProcessor.removeExchangeListener(exchangeListener);
    }

    /**
     * Creates a command to execute a trade and sends it to the exchange
     *
     * @param order the order to execute
     * @return the price the order was executed at
     */
    @Override
    public int executeTrade(final Order order) {
        final String orderType = (order.isBuyOrder()) ? BUY_ORDER : SELL_ORDER;
        final String cmd = String.join(ELEMENT_DELIMITER, EXECUTE_TRADE_CMD, orderType, order.getAccountId(), order.getStockTicker(), Integer.toString(order.getNumberOfShares()));
        final String response = sendTcpCmd(cmd);
        int executionPrice = 0;

        try {
            executionPrice = Integer.parseInt(response);
        } catch (final NumberFormatException nfe) {
            logger.warn("String to int conversion failed for {}", response, nfe);
        }

        return executionPrice;
    }
}
