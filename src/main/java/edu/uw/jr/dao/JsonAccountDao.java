package edu.uw.jr.dao;

import com.fasterxml.jackson.databind.module.SimpleModule;
import edu.uw.ext.framework.account.Account;
import edu.uw.ext.framework.account.AccountException;
import edu.uw.ext.framework.account.Address;
import edu.uw.ext.framework.account.CreditCard;
import edu.uw.ext.framework.dao.AccountDao;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uw.jr.account.SimpleAccount;
import edu.uw.jr.account.SimpleAddress;
import edu.uw.jr.account.SimpleCreditCard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * An AccountDao that persists the account information using JSON.
 *
 * @author Jesse Ruth
 */
public class JsonAccountDao implements AccountDao, AutoCloseable {
    final static Logger logger = LoggerFactory.getLogger(JsonAccountDao.class);

    private final String DIRECTORY = "target/accounts";
    private final String FILE_NAME = "%s.json";
    private final File accountDirectory = new File(DIRECTORY);
    private ObjectMapper objectMapper;

    /**
     * Creates an instance of this class and loads the account data.
     */
    public JsonAccountDao() {
        logger.info("New JsonAccountDao");
        final SimpleModule simpleModule = new SimpleModule();
        simpleModule.addAbstractTypeMapping(Account.class, SimpleAccount.class);
        simpleModule.addAbstractTypeMapping(Address.class, SimpleAddress.class);
        simpleModule.addAbstractTypeMapping(CreditCard.class, SimpleCreditCard.class);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(simpleModule);
    }


    /**
     * Lookup an account based on username.
     *
     * @param accountName the name of the desired account
     * @return the account if located otherwise null
     */
    @Override
    public Account getAccount(String accountName) {
        final List<String> contents = List.of(Objects.requireNonNull(accountDirectory.list()));
        logger.info("getAccount directory contents {}", contents);
        final String fileName = String.format(FILE_NAME, accountName);
        if (contents.contains(fileName)) {
            logger.info("Json File Exist: {}", accountName);
            try {
                final SimpleAccount account = objectMapper.readValue(new File(accountDirectory, fileName), SimpleAccount.class);
                return account;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Adds or updates an account.
     *
     * @param account the account to add/update
     * @throws AccountException if operation fails
     */
    @Override
    public void setAccount(Account account) throws AccountException {
        if (accountDirectory.exists() || accountDirectory.mkdirs()) {
            try {
                final String fileName = String.format(FILE_NAME, account.getName());
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(accountDirectory, fileName), account);
            } catch (IOException e) {
                final String message = "Unable to create account";
                e.printStackTrace();
                throw new AccountException(message);
            }
        }
    }

    /**
     * Remove the account.
     *
     * @param accountName the name of the account to remove
     * @throws AccountException if operation fails
     */
    @Override
    public void deleteAccount(String accountName) throws AccountException {
        final List<String> contents = List.of(Objects.requireNonNull(accountDirectory.list()));
        logger.info("Attempt to delete Account {}", accountName);
        final String fileName = String.format(FILE_NAME, accountName);

        if (contents.contains(fileName)) {
            logger.info("Found Account {}", fileName);
            final File file = new File(accountDirectory, fileName);
            final boolean result = file.delete();
            if (result) {
                logger.info("Deleted Account {}", accountName);
            } else {
                throw new AccountException("Unable to delete " + accountName);
            }
        }
    }

    /**
     * Remove all accounts. This is primarily available to facilitate testing.
     */
    @Override
    public void reset() {
        try {
            final List<File> contents = List.of(Objects.requireNonNull(accountDirectory.listFiles()));
            contents.forEach(File::delete);
        } catch (final Exception e) {
            final String message = "Unable to reset Directory";
            logger.error(message, e);
        }
    }

    /**
     * Close the DAO.
     */
    @Override
    public void close() {
        logger.info("Close the file handler");
    }
}
