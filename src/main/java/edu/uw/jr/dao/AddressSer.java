package edu.uw.jr.dao;

import edu.uw.ext.framework.account.AccountException;
import edu.uw.ext.framework.account.Address;
import edu.uw.jr.account.SimpleAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Objects;

/**
 * Class for serializing instances of classes that implement the Address interface. Implementation uses a
 * Properties file.
 *
 * @author Jesse Ruth
 */
public class AddressSer {
    final static Logger logger = LoggerFactory.getLogger(AddressSer.class);

    /**
     * Writes an Address object to an output stream.
     *
     * @param out  the output stream to write to
     * @param addr the Address object to write
     * @throws AccountException if an error occurs writing to stream
     */
    public static void write(final DataOutputStream out,
                             final Address addr)
            throws AccountException {
        try {
            out.writeUTF(Objects.toString(addr.getStreetAddress(), ""));
            out.writeUTF(Objects.toString(addr.getCity(), ""));
            out.writeUTF(Objects.toString(addr.getState(), ""));
            out.writeUTF(Objects.toString(addr.getZipCode(), ""));
            out.flush();
        } catch (IOException e) {
            final String message = String.format("unable to write address %s", addr);
            logger.error(message, e);
            throw new AccountException(message);
        }
    }

    /**
     * Reads an Address object from an input stream.
     *
     * @param in the input stream to read from
     * @return the Address object read from stream
     * @throws AccountException if an error occurs reading from stream or instantiating the object
     */
    public static Address read(final DataInputStream in)
            throws AccountException {
        try {
            final String streetAddress = in.readUTF();
            final String city = in.readUTF();
            final String state = in.readUTF();
            final String zipCode = in.readUTF();
            final Address address = new SimpleAddress();
            address.setStreetAddress(streetAddress);
            address.setCity(city);
            address.setState(state);
            address.setZipCode(zipCode);

            return address;
        } catch (IOException e) {
            final String message = "unable to read address";
            logger.error(message, e);
            throw new AccountException(message);
        }
    }
}
