package edu.uw.jr.broker;

import edu.uw.ext.framework.order.StopBuyOrder;
import edu.uw.ext.framework.order.StopSellOrder;

import java.util.function.Consumer;

/**
 * Maintains queues to different types of orders and requests the execution of orders when price conditions allow
 * their execution.
 *
 * @author Jesse Ruth
 */
public class SimpleOrderManager implements edu.uw.ext.framework.broker.OrderManager {
    /**
     * Queue for stop buy orders
     */
    protected edu.uw.ext.framework.broker.OrderQueue<Integer, edu.uw.ext.framework.order.StopBuyOrder> stopBuyOrderQueue;
    /**
     * Queue for stop sell orders
     */
    protected edu.uw.ext.framework.broker.OrderQueue<Integer, edu.uw.ext.framework.order.StopSellOrder> stopSellOrderQueue;

    /**
     * Constructor. Constructor to be used by sub classes to finish initialization.
     *
     * @param stockTickerSymbol the ticker symbol of the stock this instance is manage orders for
     */
    protected SimpleOrderManager(final String stockTickerSymbol) {

    }

    /**
     * Constructor
     *
     * @param stockTickerSymbol the ticker symbol of the stock this instance is manage orders for
     * @param price             the current price of stock to be managed
     */
    public SimpleOrderManager(final String stockTickerSymbol, final int price) {

    }

    /**
     * Gets the stock ticker symbol for the stock managed by this stock manager.
     *
     * @return the stock ticker symbol
     */
    @Override
    public String getSymbol() {
        return null;
    }

    /**
     * Respond to a stock price adjustment by setting threshold on dispatch filters.
     *
     * @param price the new price
     */
    @Override
    public void adjustPrice(final int price) {

    }

    /**
     * Queue a stop buy order.
     *
     * @param stopBuyOrder the order to be queued
     */
    @Override
    public void queueOrder(final StopBuyOrder stopBuyOrder) {

    }

    /**
     * Queue a stop sell order.
     *
     * @param stopSellOrder the order to be queued
     */
    @Override
    public void queueOrder(final StopSellOrder stopSellOrder) {

    }

    /**
     * Registers the processor to be used during buy order processing. This will be passed on to the order
     * queues as the dispatch callback.
     *
     * @param processor - the callback to be registered
     */
    @Override
    public void setBuyOrderProcessor(final Consumer<StopBuyOrder> processor) {

    }

    /**
     * Registers the processor to be used during sell order processing. This will be passed on to the order queues as
     * the dispatch callback.
     *
     * @param processor - the callback to be registered
     */
    @Override
    public void setSellOrderProcessor(final Consumer<StopSellOrder> processor) {

    }
}
