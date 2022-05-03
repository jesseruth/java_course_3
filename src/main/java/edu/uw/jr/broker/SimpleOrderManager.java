package edu.uw.jr.broker;

import edu.uw.ext.framework.broker.OrderManager;
import edu.uw.ext.framework.broker.OrderQueue;
import edu.uw.ext.framework.order.StopBuyOrder;
import edu.uw.ext.framework.order.StopSellOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.function.Consumer;

/**
 * Maintains queues to different types of orders and requests the execution of orders when price conditions allow
 * their execution.
 *
 * @author Jesse Ruth
 */
public class SimpleOrderManager implements OrderManager {
    /**
     * This logger.
     */
    final static Logger logger = LoggerFactory.getLogger(SimpleOrderManager.class);

    /**
     * Symbol this manages.
     */
    private final String stockSymbol;

    /**
     * Queue for stop buy orders
     */
    protected OrderQueue<Integer, StopBuyOrder> stopBuyOrderQueue;
    /**
     * Queue for stop sell orders
     */
    protected OrderQueue<Integer, StopSellOrder> stopSellOrderQueue;

    /**
     * Constructor. Constructor to be used by sub classes to finish initialization.
     *
     * @param stockTickerSymbol the ticker symbol of the stock this instance is manage orders for
     */
    protected SimpleOrderManager(final String stockTickerSymbol) {
        this.stockSymbol = stockTickerSymbol;
        logger.info("Construct Protected SimpleOrderManager with {}", stockTickerSymbol);
    }

    /**
     * Constructor
     *
     * @param stockTickerSymbol the ticker symbol of the stock this instance is manage orders for
     * @param price             the current price of stock to be managed
     */
    public SimpleOrderManager(final String stockTickerSymbol, final int price) {
        this(stockTickerSymbol);
        logger.info("Create stopBuyOrderQueue & stopSellOrderQueue");

        stopBuyOrderQueue = new SimpleOrderQueue<>(price,
                (t, o) -> {
                    logger.info("Create stopBuyOrderQueue lambda {} <= {}", o.getPrice(), t);
                    return o.getPrice() <= t;
                },
                Comparator.comparing(StopBuyOrder::getPrice));
        stopSellOrderQueue = new SimpleOrderQueue<>(price,
                (t, o) -> {
                    logger.info("Create stopSellOrderQueue lambda {} <= {}", o.getPrice(), t);
                    return o.getPrice() >= t;
                },
                Comparator.comparing(StopSellOrder::getPrice).reversed());
    }

    /**
     * Gets the stock ticker symbol for the stock managed by this stock manager.
     *
     * @return the stock ticker symbol
     */
    @Override
    public String getSymbol() {
        logger.info("Get symbol {}", this.stockSymbol);

        return this.stockSymbol;
    }

    /**
     * Respond to a stock price adjustment by setting threshold on dispatch filters.
     *
     * @param price the new price
     */
    @Override
    public void adjustPrice(final int price) {
        logger.info("Adjust Threshold price {}", price);
        stopBuyOrderQueue.setThreshold(price);
        stopSellOrderQueue.setThreshold(price);
    }

    /**
     * Queue a stop buy order.
     *
     * @param stopBuyOrder the order to be queued
     */
    @Override
    public void queueOrder(final StopBuyOrder stopBuyOrder) {
        logger.info("Queue a stop buy order.");

        stopBuyOrderQueue.enqueue(stopBuyOrder);
    }

    /**
     * Queue a stop sell order.
     *
     * @param stopSellOrder the order to be queued
     */
    @Override
    public void queueOrder(final StopSellOrder stopSellOrder) {
        logger.info("Queue a stop sell order.");
        stopSellOrderQueue.enqueue(stopSellOrder);
    }

    /**
     * Registers the processor to be used during buy order processing. This will be passed on to the order
     * queues as the dispatch callback.
     *
     * @param processor - the callback to be registered
     */
    @Override
    public void setBuyOrderProcessor(final Consumer<StopBuyOrder> processor) {
        logger.info("setBuyOrderProcessor");
        stopBuyOrderQueue.setConsumer(processor);
    }

    /**
     * Registers the processor to be used during sell order processing. This will be passed on to the order queues as
     * the dispatch callback.
     *
     * @param processor - the callback to be registered
     */
    @Override
    public void setSellOrderProcessor(final Consumer<StopSellOrder> processor) {
        logger.info("setSellOrderProcessor");
        stopSellOrderQueue.setConsumer(processor);
    }
}
