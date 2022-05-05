package edu.uw.jr.concurrentbroker;

import edu.uw.ext.framework.account.AccountManager;
import edu.uw.ext.framework.broker.Broker;
import edu.uw.ext.framework.broker.BrokerFactory;
import edu.uw.ext.framework.exchange.StockExchange;

/**
 * BrokerFactory implementation which creates a ThreadedBroker.
 *
 * @author Jesse Ruth
 */
public class ThreadedBrokerFactory implements BrokerFactory {
    /**
     * Instantiates a new ThreadedBroker.
     *
     * @param name           the broker's name
     * @param accountManager the account manager to be used by the broker
     * @param stockExchange  the exchange to be used by the broker
     * @return a newly created ThreadedBroker instance
     */
    @Override
    public Broker newBroker(final String name, final AccountManager accountManager, final StockExchange stockExchange) {
        return new ThreadedBroker(name, accountManager, stockExchange);
    }
}
