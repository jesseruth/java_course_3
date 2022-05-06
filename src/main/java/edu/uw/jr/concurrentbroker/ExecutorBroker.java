package edu.uw.jr.concurrentbroker;

import edu.uw.ext.framework.account.AccountManager;
import edu.uw.ext.framework.broker.BrokerException;
import edu.uw.ext.framework.broker.OrderManager;
import edu.uw.ext.framework.exchange.StockExchange;
import edu.uw.jr.broker.SimpleBroker;
import edu.uw.jr.broker.SimpleOrderQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * An extension of AbstractBroker that uses a ExecutorOrderManager and ExecutorOrderQueue for the market order queue.
 *
 * @author Jesse Ruth
 */
public class ExecutorBroker extends SimpleBroker {
    /**
     * This logger.
     */
    final static Logger logger = LoggerFactory.getLogger(ExecutorBroker.class);

    private final Executor executor;

    /**
     * Constructor
     *
     * @param brokerName     name of the broker
     * @param accountManager the account manager to be used by the broker
     * @param stockExchange  the stock exchange to be used by the broker
     */
    public ExecutorBroker(final String brokerName, final AccountManager accountManager, final StockExchange stockExchange) {
        super(brokerName, stockExchange, accountManager);
        logger.info("XXX - Construct ExecutorBroker for {}", brokerName);
        executor = Executors.newSingleThreadExecutor();

        marketOrders = new ExecutorOrderQueue<>(stockExchange.isOpen(), (t, o) -> {
            logger.info("** SimpleOrderQueue lambda is open: {}", t);
            return t;
        }, executor);

        marketOrders.setConsumer(this::executeOrder);

        initializeOrderManagers();
        stockExchange.addExchangeListener(this);
    }

    @Override
    public synchronized void close() throws BrokerException {
        logger.info("XXX - ExecutorBroker close close close close");

        super.close();
    }

    /**
     * Create an appropriate order manager for this broker.
     *
     * @param ticker       the ticker symbol of the stock
     * @param initialPrice current price of the stock
     * @return a new OrderManager for the specified stock
     */
    @Override
    protected final synchronized OrderManager createOrderManager(final String ticker,
                                                                 final int initialPrice) {
        logger.info("XXX - ExecutorBroker createOrderManager for {} @ {}", ticker, initialPrice);
        return new ExecutorOrderManager(ticker, initialPrice, executor);
    }
}
