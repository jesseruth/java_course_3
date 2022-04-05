package edu.uw.jr.account;

import edu.uw.ext.framework.account.Account;
import edu.uw.ext.framework.account.AccountException;
import edu.uw.ext.framework.account.AccountFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of AccountFactory that creates SimpleAccount instances.
 *
 * @author Jesse Ruth
 */
public class SimpleAccountFactory implements AccountFactory {
    static Logger logger = LoggerFactory.getLogger(SimpleAccountFactory.class);

    /**
     * Instantiations an instance of SimpleAccount.
     *
     * @param accountName    the account name
     * @param hashedPassword the password hash
     * @param initialBalance the balance
     * @return the newly instantiated account, or null if unable to instantiate the account
     */
    @Override
    public Account newAccount(final String accountName, final byte[] hashedPassword, int initialBalance) {
        if (initialBalance < 1000 * 100) return null;
        Account account = new SimpleAccount();
        account.setBalance(initialBalance);
        try {
            account.setName(accountName);
        } catch (AccountException e) {
            logger.error("Name too short :(", e);
            return null;
        }
        account.setPasswordHash(hashedPassword);
        return account;
    }
}
