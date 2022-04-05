package edu.uw.jr.dao;

import edu.uw.ext.framework.account.Account;
import edu.uw.ext.framework.account.AccountException;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Class for serializing instances of classes that implement the Account interface.
 *
 * @author Jesse Ruth
 */
public class AccountSer {
    /**
     * Reads an Account object from an input stream.
     *
     * @param in the input stream to read from
     * @return the Account object read from stream
     * @throws AccountException if an error occurs reading from stream or instantiating the object
     */
    public static Account read(InputStream in) throws AccountException {
        return null;
    }

    /**
     * Writes an Account object to an output stream.
     *
     * @param out  the output stream to write to
     * @param acct the Account object to write
     * @throws AccountException if an error occurs writing to stream
     */
    public static void write(DataOutputStream out,
                             Account acct)
            throws AccountException {
        try {
            out.writeUTF(acct.getEmail());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
