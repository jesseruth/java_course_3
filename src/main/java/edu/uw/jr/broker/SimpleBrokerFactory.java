package edu.uw.jr.broker;

import edu.uw.ext.framework.account.AccountManager;
import edu.uw.ext.framework.broker.Broker;
import edu.uw.ext.framework.exchange.StockExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * BrokerFactory implementation that returns a SimpleBroker.
 *
 * @author Jesse Ruth
 */
public class SimpleBrokerFactory implements edu.uw.ext.framework.broker.BrokerFactory {
    final static Logger logger = LoggerFactory.getLogger(SimpleBrokerFactory.class);

    /**
     * Instantiates a new SimpleBroker.
     *
     * @param name           the broker's name
     * @param accountManager the account manager to be used by the broker
     * @param exchange       the exchange to be used by the broker
     * @return a newly created SimpleBroker instance
     */
    @Override
    public Broker newBroker(final String name, final AccountManager accountManager, final StockExchange exchange) {
        logger.info("FACTORY: Create new Simple Broker");
        return new SimpleBroker(name, accountManager, exchange);
    }
}
