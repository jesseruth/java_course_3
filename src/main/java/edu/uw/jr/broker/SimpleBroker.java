package edu.uw.jr.broker;

import edu.uw.ext.framework.account.Account;
import edu.uw.ext.framework.account.AccountException;
import edu.uw.ext.framework.account.AccountManager;
import edu.uw.ext.framework.broker.Broker;
import edu.uw.ext.framework.broker.BrokerException;
import edu.uw.ext.framework.broker.OrderManager;
import edu.uw.ext.framework.exchange.ExchangeEvent;
import edu.uw.ext.framework.exchange.ExchangeListener;
import edu.uw.ext.framework.exchange.StockExchange;
import edu.uw.ext.framework.exchange.StockQuote;
import edu.uw.ext.framework.order.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * An implementation of the Broker interface, provides a full implementation less the creation of the order
 * manager and market queue.
 *
 * @author Jesse Ruth
 */
public class SimpleBroker implements Broker, ExchangeListener {
    final static Logger logger = LoggerFactory.getLogger(SimpleBroker.class);
    private final String brokerName;

    /**
     * The market order queue.
     */
    protected edu.uw.ext.framework.broker.OrderQueue<Boolean, Order> marketOrders;
    final StockExchange exchg;
    final AccountManager acctMgr;

    /**
     * Constructor for sub classes
     *
     * @param brokerName name of the broker
     * @param exchg      the stock exchange to be used by the broker
     * @param acctMgr    the account manager to be used by the broker
     */
    protected SimpleBroker(final String brokerName,
                           final StockExchange exchg,
                           final AccountManager acctMgr) {
        this.brokerName = brokerName;
        logger.info("SimpleBroker constructor protected brokerName :{}", brokerName);
        this.acctMgr = acctMgr;
        this.exchg = exchg;
    }

    /**
     * Constructor
     *
     * @param brokerName name of the broker
     * @param acctMgr    the account manager to be used by the broker
     * @param exchg      the stock exchange to be used by the broker
     */
    public SimpleBroker(final String brokerName,
                        final AccountManager acctMgr,
                        final StockExchange exchg) {
        this.brokerName = brokerName;
        logger.info("SimpleBroker constructor public brokerName :{}", brokerName);
        StopBuyOrderDispatchFilter stopBuyOrderDispatchFilter = new StopBuyOrderDispatchFilter();
        // marketOrders = new SimpleOrderQueue<Boolean, Order>(10, stopBuyOrderDispatchFilter);
        this.acctMgr = acctMgr;
        this.exchg = exchg;
    }

    /**
     * Execute an order with the exchange, satisfies the Consumer<Order> functional interface.
     *
     * @param order the order to execute
     */
    protected void executeOrder(final Order order) {
        logger.info("SimpleBroker executeOrder");
    }

    /**
     * Fetch the stock list from the exchange and initialize an order manager for each stock.
     * Only to be used during construction.
     */
    protected final void initializeOrderManagers() {
        logger.info("SimpleBroker initializeOrderManagers");
    }

    /**
     * Create an appropriate order manager for this broker. Only to be used during construction.
     *
     * @param ticker       the ticker symbol of the stock
     * @param initialPrice current price of the stock
     * @return a new OrderManager for the specified stock
     */
    protected OrderManager createOrderManager(final String ticker, final int initialPrice) {
        logger.info("SimpleBroker createOrderManager {} initial price {}", ticker, initialPrice);
        return null;
    }


    /**
     * Get the name of the broker.
     *
     * @return the name of the broker
     */
    @Override
    public String getName() {
        logger.info("SimpleBroker getName");
        return null;
    }


    /**
     * Create an account with the broker.
     *
     * @param username the user or account name for the account
     * @param password the password for the new account
     * @param balance  the initial account balance in cents
     * @return the new account
     * @throws BrokerException if unable to create account
     */
    @Override
    public Account createAccount(final String username, final String password, final int balance) throws BrokerException {
        logger.info("SimpleBroker createAccount {} with balance {}", username, balance);
        try {
            Account account = acctMgr.createAccount(username, password, balance);
            return account;
        } catch (AccountException e) {
            e.printStackTrace();
            throw new BrokerException("Unable to create account :(");
        }
    }

    /**
     * Delete an account with the broker.
     *
     * @param username the user or account name for the account
     * @throws BrokerException if unable to delete account
     */
    @Override
    public void deleteAccount(final String username) throws BrokerException {
        logger.info("SimpleBroker deleteAccount {}", username);
        try {
            acctMgr.deleteAccount(username);
        } catch (AccountException e) {
            e.printStackTrace();
            throw new BrokerException("Unable to delete account");
        }
    }

    /**
     * Locate an account with the broker. The username and password are first verified and the account is returned.
     *
     * @param username the user or account name for the account
     * @param password the password for the new account
     * @return the account
     * @throws BrokerException username and password are invalid
     */
    @Override
    public Account getAccount(final String username, final String password) throws BrokerException {
        logger.info("SimpleBroker getAccount {}", username);
        try {
            if (acctMgr.validateLogin(username, password)) {
                Account account = acctMgr.getAccount(username);
                return account;
            } else {
                throw new BrokerException("Username and password are invalid");
            }
        } catch (AccountException e) {
            e.printStackTrace();
            throw new BrokerException("Unable to get account");
        }
    }


    /**
     * Get a price quote for a stock.
     *
     * @param symbol the stocks ticker symbol
     * @return optional containing quote, or empty if unable to obtain quote
     */
    @Override
    public Optional<StockQuote> requestQuote(final String symbol) {
        logger.info("SimpleBroker requestQuote {}", symbol);
        Optional<StockQuote> stockQuote = this.exchg.getQuote(symbol);
        if (stockQuote.isPresent()) {
            logger.info("Got quote of {} for {}", stockQuote.get().getPrice(), symbol);

        } else {
            logger.info("No results for {}", symbol);

        }
        return stockQuote;
    }

    /**
     * Place an order with the broker.
     *
     * @param order the order being placed with the broker
     */
    @Override
    public void placeOrder(final MarketBuyOrder order) {
        logger.info("SimpleBroker placeOrder MarketBuyOrder");
        exchg.executeTrade(order);
    }

    /**
     * Place an order with the broker.
     *
     * @param order the order being placed with the broker
     */
    @Override
    public void placeOrder(final MarketSellOrder order) {
        logger.info("SimpleBroker placeOrder MarketSellOrder");
        exchg.executeTrade(order);
    }

    /**
     * @param order the order being placed with the broker
     * @throws BrokerException
     */
    @Override
    public void placeOrder(final StopBuyOrder order) throws BrokerException {
        logger.info("SimpleBroker placeOrder StopBuyOrder");
//        exchg.executeTrade(order);
    }

    /**
     * @param order the order being placed with the broker
     * @throws BrokerException
     */
    @Override
    public void placeOrder(final StopSellOrder order) throws BrokerException {
        logger.info("SimpleBroker placeOrder StopSellOrder");
//        exchg.executeTrade(order);
    }

    @Override
    public void close() throws BrokerException {
        logger.info("SimpleBroker close close close close");
    }

    /**
     * Upon the exchange opening sets the market dispatch filter threshold and processes any available orders.
     *
     * @param exchangeEvent the exchange (open) event
     */
    @Override
    public void exchangeOpened(final ExchangeEvent exchangeEvent) {
        logger.info("SimpleBroker exchangeOpened");
    }

    /**
     * Upon the exchange opening sets the market dispatch filter threshold.
     *
     * @param exchangeEvent the exchange (closed) event
     */
    @Override
    public void exchangeClosed(final ExchangeEvent exchangeEvent) {
        logger.info("SimpleBroker exchangeClosed");
    }

    /**
     * Upon the exchange opening sets the market dispatch filter threshold and processes any available orders.
     *
     * @param exchangeEvent the price change event
     */
    @Override
    public void priceChanged(final ExchangeEvent exchangeEvent) {
        logger.info("SimpleBroker priceChanged");
    }
}
