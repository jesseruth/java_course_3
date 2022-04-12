package edu.uw.jr.account;

import edu.uw.ext.framework.account.*;
import edu.uw.ext.framework.order.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the Account interface as a JavaBean.
 *
 * @author Jesse Ruth
 */
public class SimpleAccount implements Account {
    public static final int MIN_ACCT_LEN = 8;
    public static final int MIN_ACCOUNT_BALANCE = 1000 * 100;
    static Logger logger = LoggerFactory.getLogger(SimpleAccount.class);
    private String accountName;
    private byte[] passwordHash;
    private int balance;
    private Address address;
    private String phone;
    private String email;
    private AccountManager accountManager;
    private CreditCard creditCard;
    private String fullName;
    private Boolean accountManagerSet = false;

    /**
     * No parameter constructor, required by JavaBeans.
     */
    SimpleAccount() {
    }

    /**
     * Constructor, validates length of account name and the initial balance.
     *
     * @param acctName     account name
     * @param passwordHash password hash
     * @param balance      initial balance
     * @throws AccountException if the account name is too short or balance too low
     */
    SimpleAccount(final String acctName, final byte[] passwordHash, final int balance) throws AccountException {
        this.setName(acctName);
        if (balance >= MIN_ACCOUNT_BALANCE) {
            this.setBalance(balance);
        } else {
            throw new AccountException("Name must be at least 8 characters in length");
        }
        this.passwordHash = passwordHash;
    }

    /**
     * Get the account name.
     *
     * @return the account name.
     */
    @Override
    public String getName() {
        return accountName;
    }

    /**
     * Sets the account name. The name will be checked for minimum length MIN_ACCT_LEN. This operation is not
     * generally used but is provided for JavaBean conformance.
     *
     * @param accountName the value to be set for the account name
     * @throws AccountException if the account name is to short
     */
    @Override
    public void setName(final String accountName) throws AccountException {
        if (accountName.length() >= MIN_ACCT_LEN) {
            this.accountName = accountName;
        } else {
            throw new AccountException("Name must be at least 8 characters in length");
        }
    }

    /**
     * Gets the hashed password.
     *
     * @return the hashed password
     */
    @Override
    public byte[] getPasswordHash() {
        return passwordHash;
    }

    /**
     * Sets the hashed password.
     *
     * @param bytes the hashed password.
     */
    @Override
    public void setPasswordHash(final byte[] bytes) {
        this.passwordHash = bytes;
    }

    /**
     * Gets the account balance.
     *
     * @return the current balance of the account
     */
    @Override
    public int getBalance() {
        return balance;
    }


    /**
     * Sets the account balance.
     *
     * @param balance the account balance.
     */
    @Override
    public void setBalance(final int balance) {
        this.balance = balance;
    }

    /**
     * Gets the full name of the account holder.
     *
     * @return the account holders full name
     */
    @Override
    public String getFullName() {
        return fullName;
    }

    /**
     * Sets the full name of the account holder.
     *
     * @param fullName the account holders full name
     */
    @Override
    public void setFullName(final String fullName) {
        this.fullName = fullName;
    }

    /**
     * Gets the account address.
     *
     * @return the accounts address
     */
    @Override
    public Address getAddress() {
        return this.address;
    }

    /**
     * Sets the account address.
     *
     * @param address the address for the account
     */
    @Override
    public void setAddress(final Address address) {
        this.address = address;
    }

    /**
     * Gets the phone number.
     *
     * @return the phone number
     */
    @Override
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the account phone number.
     *
     * @param phone value for the account phone number
     */
    @Override
    public void setPhone(final String phone) {
        this.phone = phone;
    }

    /**
     * Gets the email address.
     *
     * @return the email address
     */
    @Override
    public String getEmail() {
        return email;
    }

    /**
     * Sets the account email address.
     *
     * @param email the email address
     */
    @Override
    public void setEmail(final String email) {
        this.email = email;
    }

    /**
     * Gets the account credit card.
     *
     * @return the credit card
     */
    @Override
    public CreditCard getCreditCard() {
        return creditCard;
    }

    /**
     * Sets the account credit card.
     *
     * @param creditCard the value to be set for the credit card
     */
    @Override
    public void setCreditCard(final CreditCard creditCard) {
        this.creditCard = creditCard;
    }

    /**
     * Sets the account manager responsible for persisting/managing this account. This may be invoked exactly once on
     * any given account, any subsequent invocations should be ignored.
     *
     * @param accountManager the account manager
     */
    @Override
    public void registerAccountManager(final AccountManager accountManager) {
        if (!accountManagerSet) {
            this.accountManager = accountManager;
            this.accountManagerSet = true;
        }
    }

    /**
     * Incorporates the effect of an order in the balance. Increments or decrements the account balance by the
     * execution price * number of shares in the order and then persists the account, using the account manager.
     *
     * @param order          the order to be reflected in the account
     * @param executionPrice the price the order was executed at
     */
    @Override
    public void reflectOrder(final Order order, final int executionPrice) {
        logger.info("reflect order {} \n At Price {}", order, executionPrice);
        balance += order.valueOfOrder(executionPrice);
        try {
            accountManager.persist(this);
        } catch (AccountException e) {
            logger.error("Unable to persist account", e);
        }
    }
}
