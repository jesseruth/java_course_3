package edu.uw.jr.dao;

import edu.uw.ext.framework.account.AccountException;
import edu.uw.ext.framework.account.Address;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Class for serializing instances of classes that implement the Address interface. Implementation uses a
 * Properties file.
 *
 * @author Jesse Ruth
 */
public class AddressSer {
    /**
     * Writes an Address object to an output stream.
     *
     * @param out  the output stream to write to
     * @param addr the Address object to write
     * @throws AccountException if an error occurs writing to stream
     */
    public static void write(final OutputStream out,
                             final Address addr)
            throws AccountException {

    }

    /**
     * Reads an Address object from an input stream.
     *
     * @param in the input stream to read from
     * @return the Address object read from stream
     * @throws AccountException if an error occurs reading from stream or instantiating the object
     */
    public static Address read(InputStream in)
            throws AccountException {
        return null;
    }
}
