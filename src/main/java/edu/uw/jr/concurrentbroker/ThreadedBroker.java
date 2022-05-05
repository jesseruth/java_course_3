package edu.uw.jr.concurrentbroker;

import edu.uw.ext.framework.account.AccountManager;
import edu.uw.ext.framework.exchange.StockExchange;
import edu.uw.ext.framework.broker.OrderManager;
import edu.uw.jr.broker.SimpleBroker;

/**
 * An extension of AbstractBroker that uses a ThreadedOrderManager and ThreadeOrderQueue for the market order queue.
 *
 * @author Jesse Ruth
 */
public class ThreadedBroker extends SimpleBroker {
    /**
     * Constructor
     *
     * @param brokerName     name of the broker
     * @param accountManager the account manager to be used by the broker
     * @param stockExchange  the stock exchange to be used by the broker
     */
    public ThreadedBroker(final String brokerName, final AccountManager accountManager, final StockExchange stockExchange) {
        super(brokerName, accountManager, stockExchange);
    }

    /**
     * Create an appropriate order manager for this broker.
     * @param ticker       the ticker symbol of the stock
     * @param initialPrice current price of the stock
     * @return a new OrderManager for the specified stock
     */
    @Override
    protected OrderManager createOrderManager(String ticker, int initialPrice) {
        return null;
    }
}
