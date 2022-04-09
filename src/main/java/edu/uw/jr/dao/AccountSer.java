package edu.uw.jr.dao;

import edu.uw.ext.framework.account.Account;
import edu.uw.ext.framework.account.AccountException;
import edu.uw.jr.account.SimpleAccountFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Objects;

/**
 * Class for serializing instances of classes that implement the Account interface.
 *
 * @author Jesse Ruth
 */
public class AccountSer {
    final static Logger logger = LoggerFactory.getLogger(AccountSer.class);
    final static SimpleAccountFactory simpleAccountFactory = new SimpleAccountFactory();

    /**
     * Reads an Account object from an input stream.
     *
     * @param in the input stream to read from
     * @return the Account object read from stream
     * @throws AccountException if an error occurs reading from stream or instantiating the object
     */
    public static Account read(final DataInputStream in) throws AccountException {
        try {
            final String email = in.readUTF();
            final String name = in.readUTF();
            final String phone = in.readUTF();
            final String fullName = in.readUTF();
            final int balance = in.readInt();
            final int passwordHashLength = in.readInt();
            final byte[] passwordHash = in.readNBytes(passwordHashLength);
            logger.info("Email: {}", email);
            final Account account = simpleAccountFactory.newAccount(name, passwordHash, balance);
            account.setFullName(fullName);
            account.setPhone(phone);
            account.setEmail(email);
            return account;
        } catch (IOException e) {
            final String message = "unable to read account";
            logger.error(message, e);
            throw new AccountException(message);
        }
    }

    /**
     * Writes an Account object to an output stream.
     *
     * @param out  the output stream to write to
     * @param acct the Account object to write
     * @throws AccountException if an error occurs writing to stream
     */
    public static void write(final DataOutputStream out,
                             final Account acct)
            throws AccountException {
        try {
            out.writeUTF(Objects.toString(acct.getEmail(), ""));
            out.writeUTF(Objects.toString(acct.getName(), ""));
            out.writeUTF(Objects.toString(acct.getPhone(), ""));
            out.writeUTF(Objects.toString(acct.getFullName(), ""));
            out.writeInt(acct.getBalance());
            out.writeInt(acct.getPasswordHash().length);
            out.write(acct.getPasswordHash());
            out.flush();
        } catch (IOException e) {
            final String message = String.format("unable to write account %s", acct);
            logger.error(message, e);
            throw new AccountException(message);
        }
    }

}
