package edu.uw.jr.concurrentbroker;

import edu.uw.ext.framework.account.AccountManager;
import edu.uw.ext.framework.broker.Broker;
import edu.uw.ext.framework.broker.BrokerFactory;
import edu.uw.ext.framework.exchange.StockExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * BrokerFactory implementation which creates a ExecutorBroker.
 *
 * @author Jesse Ruth
 */
public class ExecutorBrokerFactory implements BrokerFactory {
    /**
     * This logger.
     */
    final static Logger logger = LoggerFactory.getLogger(ExecutorBrokerFactory.class);

    /**
     * Instantiates a new ExecutorBroker.
     *
     * @param name           the broker's name
     * @param accountManager the account manager to be used by the broker
     * @param stockExchange  the exchange to be used by the broker
     * @return a newly created ExecutorBroker instance
     */
    @Override
    public Broker newBroker(final String name, final AccountManager accountManager, final StockExchange stockExchange) {
        logger.info("XXX-XXX ExecutorBrokerFactory XXXXXX");
        return new ExecutorBroker(name, accountManager, stockExchange);
    }
}
