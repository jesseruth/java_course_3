package edu.uw.jr.dao;

import edu.uw.ext.framework.account.AccountException;
import edu.uw.ext.framework.account.CreditCard;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * Class for serializing instances of classes that implement the CreditCard interface. Implemented using a simple
 * text file, one property per line.
 *
 * @author Jesse Ruth
 */
public class CreditCardSer {
    /**
     * Writes a CreditCard object to an output stream.
     *
     * @param out the output stream to write to
     * @param cc  the CreditCard object to write
     * @throws UnsupportedEncodingException if ISO-8859-1 is not supported
     */
    public static void write(OutputStream out,
                             CreditCard cc)
            throws UnsupportedEncodingException {

    }

    /**
     * Reads a CreditCard object from an input stream.
     *
     * @param in the input stream to read from
     * @return the CreditCard object read from stream
     * @throws AccountException             if an error occurs reading from stream or instantiating the object
     * @throws UnsupportedEncodingException if ISO-8859-1 is not supported
     */
    public static CreditCard read(InputStream in)
            throws AccountException,
            UnsupportedEncodingException {
        return null;
    }
}
