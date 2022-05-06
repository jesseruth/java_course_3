package edu.uw.jr.concurrentbroker;

import edu.uw.ext.framework.order.StopBuyOrder;
import edu.uw.ext.framework.order.StopSellOrder;
import edu.uw.jr.broker.SimpleOrderManager;
import edu.uw.jr.broker.SimpleOrderQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.concurrent.Executor;

/**
 * Maintains queues to different types of orders and requests the execution of orders when price conditions
 * allow their execution.
 *
 * @author Jesse Ruth
 */
public class ExecutorOrderManager extends SimpleOrderManager {
    /**
     * This logger.
     */
    final static Logger logger = LoggerFactory.getLogger(ExecutorOrderManager.class);


    /**
     * Constructor
     *
     * @param stockTickerSymbol the ticker symbol of the stock this instance is manage orders for
     * @param price             the current price of stock to be managed
     * @param executor          the executor to be used to process this queues orders
     */
    public ExecutorOrderManager(String stockTickerSymbol, int price, Executor executor) {
        super(stockTickerSymbol);
        logger.info("Create stopBuyOrderQueue & stopSellOrderQueue");

        stopBuyOrderQueue = new ExecutorOrderQueue<>(price,
                (t, o) -> {
                    logger.info("Create stopBuyOrderQueue lambda {} <= {}", o.getPrice(), t);
                    return o.getPrice() <= t;
                },
                Comparator.comparing(StopBuyOrder::getPrice),
                executor);
        stopSellOrderQueue = new ExecutorOrderQueue<>(price,
                (t, o) -> {
                    logger.info("Create stopSellOrderQueue lambda {} <= {}", o.getPrice(), t);
                    return o.getPrice() >= t;
                },
                Comparator.comparing(StopSellOrder::getPrice).reversed(),
                executor);
    }
}
