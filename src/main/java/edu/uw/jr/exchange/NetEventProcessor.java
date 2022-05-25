package edu.uw.jr.exchange;

import edu.uw.ext.framework.exchange.ExchangeEvent;
import edu.uw.ext.framework.exchange.ExchangeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.event.EventListenerList;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import static edu.uw.jr.exchange.ExchangeConstants.*;

/**
 * Listens for (by joining multicast group) and processes events received from the exchange. Processing the events
 * consists of propagating them to the registered even listeners.
 *
 * @author Jesse Ruth
 */
public class NetEventProcessor implements Runnable {
    final static Logger logger = LoggerFactory.getLogger(NetEventProcessor.class);

    /**
     * buffer size
     */
    private static final int BUFFER_SIZE = 1024;
    /**
     * The event multicast address
     */
    private final String eventIpAddress;

    /**
     * The event multicast port
     */
    private final int eventPort;

    /**
     * The event listener list
     */
    private final EventListenerList eventListenerList = new EventListenerList();

    /**
     * Constructor
     *
     * @param eventIpAddress the multicast address to connect to
     * @param eventPort      the multicast port to connect to
     */
    public NetEventProcessor(final String eventIpAddress, final int eventPort) {
        logger.info("New NetEventProcessor");
        this.eventIpAddress = eventIpAddress;
        this.eventPort = eventPort;
    }

    /**
     * Continuously accepts and processes market and price change events.
     */
    @Override
    public void run() {
        logger.info("NetEventProcessor RUN!!!!!");
        try (MulticastSocket eventSocket = new MulticastSocket(eventPort)) {
            final InetAddress eventGroup = InetAddress.getByName(eventIpAddress);
            eventSocket.joinGroup(eventGroup);
            logger.info("Receiving events from {}:{}", eventIpAddress, eventPort);
            final byte[] buf = new byte[BUFFER_SIZE];
            final DatagramPacket packet = new DatagramPacket(buf, buf.length);

            while (!eventSocket.isClosed()) {
                eventSocket.receive(packet);
                final String msg = new String(packet.getData(), packet.getOffset(), packet.getLength(), ENCODING);
                final String[] elements = msg.split(ELEMENT_DELIMITER);
                final String eventType = elements[EVENT_ELEMENT];

                switch (eventType) {
                    case OPEN_EVNT:
                        fireListeners(ExchangeEvent.newOpenedEvent(this));
                        break;
                    case CLOSED_EVNT:
                        fireListeners(ExchangeEvent.newClosedEvent(this));
                        break;
                    case PRICE_CHANGE_EVNT:
                        final String ticker = elements[PRICE_CHANGE_EVNT_TICKER_ELEMENT];
                        final String priceStr = elements[PRICE_CHANGE_EVNT_PRICE_ELEMENT];
                        int price = -1;

                        try {
                            price = Integer.parseInt(priceStr);
                        } catch (final NumberFormatException e) {
                            logger.warn("String to in conversion failed : {}", priceStr, e);
                        }
                        fireListeners(ExchangeEvent.newPriceChangedEvent(this, ticker, price));
                        break;
                    default:
                        logger.warn("Invalid event type received: {}", eventType);
                        break;
                }

            }
        } catch (final IOException e) {
            logger.error("Server error", e);
        }
        logger.info("Done processing events");
    }


    /**
     * Fires an exchange event
     *
     * @param event event to be fired
     */
    private void fireListeners(final ExchangeEvent event) {
        ExchangeListener[] listeners;
        listeners = eventListenerList.getListeners(ExchangeListener.class);
        for (ExchangeListener listener : listeners) {
            switch (event.getEventType()) {
                case OPENED:
                    listener.exchangeOpened(event);
                    break;
                case CLOSED:
                    listener.exchangeClosed(event);
                    break;
                case PRICE_CHANGED:
                    listener.priceChanged(event);
                    break;
                default:
                    logger.error("Bad exchange event {}", event.getEventType());
                    break;
            }
        }
    }

    /**
     * Adds a market listener
     *
     * @param exchangeListener the listener to add
     */
    public void addExchangeListener(final ExchangeListener exchangeListener) {
        eventListenerList.add(ExchangeListener.class, exchangeListener);
    }

    /**
     * Removes a market listener
     *
     * @param exchangeListener the listener to remove
     */
    public void removeExchangeListener(final ExchangeListener exchangeListener) {
        eventListenerList.remove(ExchangeListener.class, exchangeListener);
    }
}
