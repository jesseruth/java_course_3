package edu.uw.jr.account;

import edu.uw.ext.framework.account.Account;
import edu.uw.ext.framework.account.AccountException;
import edu.uw.ext.framework.account.AccountManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * A simple account manager that has no persistence, and accepts every login.
 *
 * @author Jesse Ruth
 */
public class SimpleAccountManager implements AccountManager {
    static final Logger logger = LoggerFactory.getLogger(SimpleAccountManager.class);
    private final SimpleAccountFactory simpleAccountFactory = new SimpleAccountFactory();
    private edu.uw.ext.framework.dao.AccountDao dao;

    /**
     * Creates a new Simple account manager using the specified AccountDao for persistence.
     *
     * @param dao the DAO to use for persistence
     */
    public SimpleAccountManager(final edu.uw.ext.framework.dao.AccountDao dao) {
        logger.info("Creates a new Simple account manager");
        this.dao = dao;
    }

    /**
     * Used to persist an account.
     *
     * @param account the account to persist
     * @throws AccountException if operation fails
     */
    @Override
    public void persist(final Account account) throws AccountException {
        dao.setAccount(account);
    }

    /**
     * Lookup an account based on accountName.
     *
     * @param accountName the name of the desired account
     * @return the account if located otherwise null
     * @throws AccountException if operation fails
     */
    @Override
    public Account getAccount(final String accountName) throws AccountException {
        try {
            Account account;
            account = dao.getAccount(accountName);
            if (account != null) account.registerAccountManager(this);
            return account;
        } catch (Exception e) {
            final String message = "Unable to get account";
            logger.error(message, e);
            throw new AccountException(message);
        }
    }

    /**
     * Remove the account.
     *
     * @param accountName the name of the account to remove
     * @throws AccountException if operation fails
     */
    @Override
    public void deleteAccount(final String accountName) throws AccountException {
        dao.deleteAccount(accountName);
    }

    /**
     * Creates an account.
     *
     * @param accountName the name for account to add
     * @param password    the password used to gain access to the account
     * @param balance     the initial balance of the account
     * @return the newly created account
     * @throws AccountException if the account already exists, or account creation fails for any reason
     */
    @Override
    public Account createAccount(final String accountName, final String password, final int balance) throws AccountException {
        Account account = dao.getAccount(accountName);
        if (account != null) {
            throw new AccountException("account already exists");
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] passwordHash = password.getBytes(StandardCharsets.UTF_8);
            md.update(passwordHash);
            account = simpleAccountFactory.newAccount(accountName, passwordHash, balance);
            persist(account);
        } catch (NoSuchAlgorithmException e) {
            logger.error("Unable to store password, no such algorithm", e);
        }
        return account;
    }

    /**
     * Check whether a login is valid. AccountName must exist and password must match.
     *
     * @param accountName name of account the password is to be validated for
     * @param password    password is to be validated
     * @return true if password is valid for account identified by username
     * @throws AccountException if error occurs accessing accounts
     */
    @Override
    public boolean validateLogin(final String accountName, final String password) throws AccountException {
        final Account account = dao.getAccount(accountName);
        if (account == null) return false;
        try {
            final MessageDigest md = MessageDigest.getInstance("SHA1");
            final byte[] passwordHash = password.getBytes(StandardCharsets.UTF_8);
            md.update(passwordHash);
            return Arrays.equals(account.getPasswordHash(), passwordHash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Closes the account manager.
     *
     * @throws AccountException if the DAO can't be closed
     */
    @Override
    public void close() throws AccountException {
        dao.close();
        dao = null;
    }
}
