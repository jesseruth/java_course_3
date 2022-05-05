package edu.uw.jr.concurrentbroker;

import edu.uw.ext.framework.account.AccountManager;
import edu.uw.ext.framework.broker.BrokerException;
import edu.uw.ext.framework.broker.OrderManager;
import edu.uw.ext.framework.exchange.StockExchange;
import edu.uw.jr.broker.SimpleBroker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    /**
     * Constructor
     *
     * @param brokerName     name of the broker
     * @param accountManager the account manager to be used by the broker
     * @param stockExchange  the stock exchange to be used by the broker
     */
    public ExecutorBroker(final String brokerName, final AccountManager accountManager, final StockExchange stockExchange) {
        super(brokerName, accountManager, stockExchange);
    }

    @Override
    public void close() throws BrokerException {
        logger.info("SimpleBroker close close close close");
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
    protected OrderManager createOrderManager(final String ticker,
                                              final int initialPrice) {
        return super.createOrderManager(ticker, initialPrice);

    }
}
