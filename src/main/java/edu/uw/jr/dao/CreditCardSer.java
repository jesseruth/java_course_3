package edu.uw.jr.dao;

import edu.uw.ext.framework.account.AccountException;
import edu.uw.ext.framework.account.CreditCard;
import edu.uw.jr.account.SimpleCreditCard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Objects;

/**
 * Class for serializing instances of classes that implement the CreditCard interface. Implemented using a simple
 * text file, one property per line.
 *
 * @author Jesse Ruth
 */
public class CreditCardSer {
    static Logger logger = LoggerFactory.getLogger(CreditCardSer.class);

    /**
     * Writes a CreditCard object to an output stream.
     *
     * @param out the output stream to write to
     * @param cc  the CreditCard object to write
     * @throws UnsupportedEncodingException if ISO-8859-1 is not supported
     */
    public static void write(final DataOutputStream out,
                             final CreditCard cc)
            throws UnsupportedEncodingException, AccountException {
        try {
            out.writeUTF(Objects.toString(cc.getIssuer(), ""));
            out.writeUTF(Objects.toString(cc.getType(), ""));
            out.writeUTF(Objects.toString(cc.getHolder(), ""));
            out.writeUTF(Objects.toString(cc.getAccountNumber(), ""));
            out.writeUTF(Objects.toString(cc.getExpirationDate(), ""));
            out.flush();
        } catch (IOException e) {
            final String message = String.format("unable to write credit card %s", cc);
            logger.error(message, e);
            throw new AccountException(message);
        }
    }

    /**
     * Reads a CreditCard object from an input stream.
     *
     * @param in the input stream to read from
     * @return the CreditCard object read from stream
     * @throws AccountException             if an error occurs reading from stream or instantiating the object
     * @throws UnsupportedEncodingException if ISO-8859-1 is not supported
     */
    public static CreditCard read(final DataInputStream in)
            throws AccountException,
            UnsupportedEncodingException {
        try {
            final String issuer = in.readUTF();
            final String type = in.readUTF();
            final String holder = in.readUTF();
            final String accountNumber = in.readUTF();
            final String expirationDate = in.readUTF();
            final CreditCard creditCard = new SimpleCreditCard();
            creditCard.setIssuer(issuer);
            creditCard.setType(type);
            creditCard.setHolder(holder);
            creditCard.setAccountNumber(accountNumber);
            creditCard.setExpirationDate(expirationDate);

            return creditCard;
        } catch (IOException e) {
            final String message = "unable to read credit card";
            logger.error(message, e);
            throw new AccountException(message);
        }
    }
}
