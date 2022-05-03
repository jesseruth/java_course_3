package edu.uw.jr.broker;

import edu.uw.ext.framework.account.Account;
import edu.uw.ext.framework.account.AccountException;
import edu.uw.ext.framework.account.AccountManager;
import edu.uw.ext.framework.broker.Broker;
import edu.uw.ext.framework.broker.BrokerException;
import edu.uw.ext.framework.broker.OrderManager;
import edu.uw.ext.framework.broker.OrderQueue;
import edu.uw.ext.framework.exchange.ExchangeEvent;
import edu.uw.ext.framework.exchange.ExchangeListener;
import edu.uw.ext.framework.exchange.StockExchange;
import edu.uw.ext.framework.exchange.StockQuote;
import edu.uw.ext.framework.order.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * An implementation of the Broker interface, provides a full implementation less the creation of the order
 * manager and market queue.
 *
 * @author Jesse Ruth
 */
public class SimpleBroker implements Broker, ExchangeListener {
    /**
     * This logger.
     */
    final static Logger logger = LoggerFactory.getLogger(SimpleBroker.class);
    /**
     * This broker's name
     */
    private final String brokerName;
    /**
     * The stock exchange used by this broker
     */
    private final StockExchange stockExchange;
    /**
     * The account manager used by this broker
     */
    private final AccountManager accountManager;
    /**
     * The market order queue.
     */
    protected OrderQueue<Boolean, Order> marketOrders;
    /**
     * Set of order managers used by the broker
     */
    private HashMap<String, OrderManager> orderManagerMap;

    /**
     * Constructor for sub classes
     *
     * @param brokerName     name of the broker
     * @param stockExchange  the stock exchange to be used by the broker
     * @param accountManager the account manager to be used by the broker
     */
    protected SimpleBroker(final String brokerName,
                           final StockExchange stockExchange,
                           final AccountManager accountManager) {
        this.brokerName = brokerName;
        logger.info("SimpleBroker constructor protected brokerName :{}", brokerName);
        this.accountManager = accountManager;
        this.stockExchange = stockExchange;
    }

    /**
     * Constructor
     *
     * @param brokerName     name of the broker
     * @param accountManager the account manager to be used by the broker
     * @param stockExchange  the stock exchange to be used by the broker
     */
    public SimpleBroker(final String brokerName,
                        final AccountManager accountManager,
                        final StockExchange stockExchange) {
        this(brokerName, stockExchange, accountManager);

        logger.info("SimpleBroker constructor public brokerName :{}", brokerName);
        // StopBuyOrderDispatchFilter stopBuyOrderDispatchFilter = new StopBuyOrderDispatchFilter();
        marketOrders = new SimpleOrderQueue<>(stockExchange.isOpen(), (t, o) -> t);
        marketOrders.setConsumer(this::executeOrder);
        initializeOrderManagers();
        stockExchange.addExchangeListener(this);
    }

    /**
     * Execute an order with the exchange, satisfies the Consumer<Order> functional interface.
     *
     * @param order the order to execute
     */
    protected void executeOrder(final Order order) {
        logger.info("SimpleBroker executeOrder");
        final int sharePrice = stockExchange.executeTrade(order);
        try {
            final Account account = accountManager.getAccount(order.getAccountId());
            account.reflectOrder(order, sharePrice);
        } catch (final AccountException error) {
            logger.error("Unable to update account {}", order.getAccountId());
        }
    }

    /**
     * Fetch the stock list from the exchange and initialize an order manager for each stock.
     * Only to be used during construction.
     */
    protected final void initializeOrderManagers() {
        orderManagerMap = new HashMap<>();
        final Consumer<StopBuyOrder> moveBuy2MarketProc = order -> marketOrders.enqueue(order);
        final Consumer<StopSellOrder> moveSell2MarketProc = order -> marketOrders.enqueue(order);
        logger.info("SimpleBroker initializeOrderManagers");

        Consumer<? super StockQuote> addStockAction = (q) -> {
            final int currentPrice = q.getPrice();
            final String stockTicker = q.getTicker();
            final OrderManager orderManager = createOrderManager(stockTicker, currentPrice);
            orderManager.setBuyOrderProcessor(moveBuy2MarketProc);
            orderManager.setSellOrderProcessor(moveSell2MarketProc);
            orderManagerMap.put(stockTicker, orderManager);
            logger.info("Initialized new order manager for {} @ {}", stockTicker, currentPrice);
        };

        for (String ticker : stockExchange.getTickers()) {
            final Optional<StockQuote> quoteOptional = stockExchange.getQuote(ticker);
            quoteOptional.ifPresentOrElse(addStockAction, () -> logger.info("No price available for {}", ticker));
        }
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
        return new SimpleOrderManager(ticker, initialPrice);
    }


    /**
     * Get the name of the broker.
     *
     * @return the name of the broker
     */
    @Override
    public String getName() {
        checkInvariants();
        logger.info("SimpleBroker getName {}", this.brokerName);
        return this.brokerName;
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
        checkInvariants();
        logger.info("SimpleBroker createAccount {} with balance {}", username, balance);
        try {
            Account account = accountManager.createAccount(username, password, balance);
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
            accountManager.deleteAccount(username);
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
            if (accountManager.validateLogin(username, password)) {
                Account account = accountManager.getAccount(username);
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
        Optional<StockQuote> stockQuote = this.stockExchange.getQuote(symbol);
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
        checkInvariants();
        logger.info("SimpleBroker placeOrder MarketBuyOrder");
        marketOrders.enqueue(order);
    }

    /**
     * Place an order with the broker.
     *
     * @param order the order being placed with the broker
     */
    @Override
    public void placeOrder(final MarketSellOrder order) {
        logger.info("SimpleBroker placeOrder MarketSellOrder");
        checkInvariants();
        marketOrders.enqueue(order);
    }

    private OrderManager orderManagerLookup(final String ticker) throws BrokerException {
        final OrderManager orderManager = orderManagerMap.get(ticker);
        if (orderManager == null) {
            throw new BrokerException(String.format("Stock %s does not exist", ticker));
        }
        return orderManager;
    }

    /**
     * @param order the order being placed with the broker
     * @throws BrokerException
     */
    @Override
    public void placeOrder(final StopBuyOrder order) throws BrokerException {
        logger.info("SimpleBroker placeOrder StopBuyOrder");
        checkInvariants();
        orderManagerLookup(order.getStockTicker()).queueOrder(order);
    }

    /**
     * @param order the order being placed with the broker
     * @throws BrokerException
     */
    @Override
    public void placeOrder(final StopSellOrder order) throws BrokerException {
        logger.info("SimpleBroker placeOrder StopSellOrder");
        checkInvariants();
        orderManagerLookup(order.getStockTicker()).queueOrder(order);
    }

    @Override
    public void close() throws BrokerException {
        logger.info("SimpleBroker close close close close");
        try {
            stockExchange.removeExchangeListener(this);
            accountManager.close();
            orderManagerMap = null;
        } catch (final AccountException e) {
            throw new BrokerException("Closing the broker failed", e);
        }
    }

    /**
     * Upon the exchange opening sets the market dispatch filter threshold and processes any available orders.
     *
     * @param exchangeEvent the exchange (open) event
     */
    @Override
    public void exchangeOpened(final ExchangeEvent exchangeEvent) {
        logger.info("SimpleBroker exchangeOpened");
        checkInvariants();
        marketOrders.setThreshold(Boolean.TRUE);
    }

    /**
     * Upon the exchange opening sets the market dispatch filter threshold.
     *
     * @param exchangeEvent the exchange (closed) event
     */
    @Override
    public void exchangeClosed(final ExchangeEvent exchangeEvent) {
        logger.info("SimpleBroker exchangeClosed");
        checkInvariants();
        marketOrders.setThreshold(Boolean.FALSE);
    }

    /**
     * Upon the exchange opening sets the market dispatch filter threshold and processes any available orders.
     *
     * @param exchangeEvent the price change event
     */
    @Override
    public void priceChanged(final ExchangeEvent exchangeEvent) {
        logger.info("SimpleBroker priceChanged");
        checkInvariants();
        OrderManager orderManager;
        orderManager = orderManagerMap.get(exchangeEvent.getTicker());
        if (orderManager != null) {
            orderManager.adjustPrice(exchangeEvent.getPrice());
        }
    }

    private void checkInvariants() {
        if (brokerName == null ||
                accountManager == null ||
                stockExchange == null ||
                orderManagerMap == null ||
                marketOrders == null) {
            throw new IllegalStateException("Yo broker is Not Configured properly.:(");
        }
    }

}
