package edu.uw.jr.dao;

import edu.uw.ext.framework.account.Account;
import edu.uw.ext.framework.account.AccountException;

import edu.uw.ext.framework.account.Address;
import edu.uw.ext.framework.account.CreditCard;
import edu.uw.ext.framework.dao.AccountDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;
import java.util.Objects;

/**
 * An AccountDao that persists the account information in a file.
 *
 * @author Jesse Ruth
 */
public class FileAccountDao implements AccountDao, AutoCloseable {
    final static Logger logger = LoggerFactory.getLogger(FileAccountDao.class);
    final String DIRECTORY = "target/accounts";
    final String ACCOUNT_FILE = "accounts.data";
    final String ADDRESS_FILE = "address.data";
    final String CREDIT_CARD_FILE = "credit_card.data";
    final File directoryPath = new File(DIRECTORY);

    // function to delete subdirectories and files
    private boolean deleteDirectory(File file) {
        // store all the paths of files and folders present
        // inside directory
        for (File subfile : Objects.requireNonNull(file.listFiles())) {

            // if it is a subfolder,e.g Rohan and Ritik,
            // recursiley call function to empty subfolder
            if (subfile.isDirectory()) {
                deleteDirectory(subfile);
            }

            // delete files and empty subfolders
            subfile.delete();
        }
        return file.delete();
    }

    /**
     * Lookup an account in the HashMap based on username.
     *
     * @param accountName the name of the desired account
     * @return the account if located otherwise null
     */
    @Override
    public Account getAccount(final String accountName) {
        final List<String> contents = List.of(Objects.requireNonNull(directoryPath.list()));
        logger.info("getAccount directory contents {}", contents);
        if (contents.contains(accountName)) {
            final File accountDir = new File(DIRECTORY, accountName);
            logger.info("Directory Exists {}", accountName);
            try (final DataInputStream dataInputStream = new DataInputStream(new FileInputStream(new File(accountDir, ACCOUNT_FILE)))) {
                final Account account = AccountSer.read(dataInputStream);
                final Address address = getAddress(accountDir);
                final CreditCard creditCard = getCreditCard(accountDir);
                account.setAddress(address);
                account.setCreditCard(creditCard);
                return account;
            } catch (final FileNotFoundException e) {
                logger.error("File not found", e);
            } catch (final IOException e) {
                logger.error("Unable to open data stream", e);
            } catch (final AccountException e) {
                logger.error("Unable to read account", e);
            }
        }
        return null;
    }

    private Address getAddress(final File accountDir) throws AccountException {
        final File newFile = new File(accountDir, ADDRESS_FILE);
        if (newFile.exists()) {
            try (final DataInputStream dataInputStream = new DataInputStream(new FileInputStream(newFile))) {
                return AddressSer.read(dataInputStream);
            } catch (final IOException e) {
                final String message = String.format("Unable to read file %s", ADDRESS_FILE);
                logger.error(message, e);
                throw new AccountException(message);
            }
        }
        return null;
    }

    private void writeAddress(final File accountDir, final Address address) throws AccountException {
        try (final DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(new File(accountDir, ADDRESS_FILE)))) {
            AddressSer.write(outputStream, address);
        } catch (final IOException e) {
            final String message = String.format("Unable to write file %s", ADDRESS_FILE);
            logger.error(message, e);
            throw new AccountException(message);
        }
    }

    private CreditCard getCreditCard(final File accountDir) throws AccountException {
        final File creditCardFile = new File(accountDir, CREDIT_CARD_FILE);
        if (creditCardFile.exists()) {
            try (final DataInputStream dataInputStream = new DataInputStream(new FileInputStream(creditCardFile))) {
                return CreditCardSer.read(dataInputStream);
            } catch (final IOException e) {
                final String message = String.format("Unable to read file %s", CREDIT_CARD_FILE);
                logger.error(message, e);
                throw new AccountException(message);
            }
        }
        return null;
    }

    private void writeCreditCard(final File accountDir, final CreditCard creditCard) throws AccountException {
        try (final DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(new File(accountDir, CREDIT_CARD_FILE)))) {
            CreditCardSer.write(outputStream, creditCard);
        } catch (final IOException e) {
            final String message = String.format("Unable to write file %s", CREDIT_CARD_FILE);
            logger.error(message, e);
            throw new AccountException(message);
        }
    }

    /**
     * Adds or updates an account.
     *
     * @param account the account to add/update
     * @throws AccountException if operation fails
     */
    @Override
    public void setAccount(final Account account) throws AccountException {
        final File accountDirectory = new File(DIRECTORY, account.getName());
        if (accountDirectory.exists() || accountDirectory.mkdirs()) {
            logger.info("Directory is available {}", accountDirectory);
            try (final DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(new File(accountDirectory, ACCOUNT_FILE)))) {
                AccountSer.write(outputStream, account);
                if (account.getAddress() != null) {
                    writeAddress(accountDirectory, account.getAddress());
                } else {
                    final File addressFile = new File(accountDirectory, ADDRESS_FILE);
                    if (addressFile.exists()) addressFile.delete();
                }
                if (account.getCreditCard() != null) {
                    writeCreditCard(accountDirectory, account.getCreditCard());
                } else {
                    final File creditCardFile = new File(accountDirectory, CREDIT_CARD_FILE);
                    if (creditCardFile.exists()) creditCardFile.delete();
                }
            } catch (final IOException e) {
                final String message = String.format("Unable to write file %s", ACCOUNT_FILE);
                logger.error(message, e);
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
    public void deleteAccount(final String accountName) throws AccountException {
        final List<String> contents = List.of(Objects.requireNonNull(directoryPath.list()));
        logger.info("Attempt to delete Account {}", accountName);

        if (contents.contains(accountName)) {
            logger.info("Found Account {}", accountName);
            final File file = new File(directoryPath, accountName);
            final boolean result = deleteDirectory(file);
            if (result) {
                logger.info("Deleted Account {}", accountName);
            } else {
                throw new AccountException("Unable to delete " + accountName);
            }
        }
    }

    /**
     * Remove all accounts. This is primarily available to facilitate testing.
     *
     * @throws AccountException if operation fails
     */
    @Override
    public void reset() throws AccountException {
        try {
            final List<File> contents = List.of(Objects.requireNonNull(directoryPath.listFiles()));
            contents.forEach(this::deleteDirectory);
        } catch (final Exception e) {
            final String message = "Unable to reset Directory";
            logger.error(message, e);
            throw new AccountException(message);
        }

    }

    /**
     * Close the DAO.
     *
     * @throws AccountException
     */
    @Override
    public void close() throws AccountException {
        logger.info("Close the file handler");

    }
}
