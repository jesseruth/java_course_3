package edu.uw.jr.account;

import edu.uw.ext.framework.account.AccountManager;
import edu.uw.ext.framework.account.AccountManagerFactory;
import edu.uw.ext.framework.dao.AccountDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple implementation of the AccountManagerFactory that instantiates the SimpleAccountManager.
 *
 * @author Jesse Ruth
 */
public class SimpleAccountManagerFactory implements AccountManagerFactory {
    static Logger logger = LoggerFactory.getLogger(SimpleAccountManagerFactory.class);

    /**
     * Instantiates a new SimpleAccountManager instance.
     *
     * @param accountDao the data access object to be used by the account manager
     * @return a newly instantiated SimpleAccountManager
     */
    @Override
    public AccountManager newAccountManager(AccountDao accountDao) {
        logger.info("Creating a new AccountManager");
        return new SimpleAccountManager(accountDao);
    }
}
