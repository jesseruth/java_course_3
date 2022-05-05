package edu.uw.jr.concurrentbroker;

import edu.uw.jr.broker.SimpleOrderManager;

import java.util.concurrent.Executor;

/**
 * Maintains queues to different types of orders and requests the execution of orders when price conditions
 * allow their execution.
 *
 * @author Jesse Ruth
 */
public class ExecutorOrderManager extends SimpleOrderManager {

    /**
     * Constructor
     *
     * @param stockTickerSymbol the ticker symbol of the stock this instance is manage orders for
     * @param price             the current price of stock to be managed
     * @param executor          the executor to be used to process this queues orders
     */
    public ExecutorOrderManager(String stockTickerSymbol, int price, Executor executor) {
        super(stockTickerSymbol, price);
    }
}
