package edu.uw.jr.account;

import edu.uw.ext.framework.account.CreditCard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A straight forward implementation of the CreditCard interface as a JavaBean.
 *
 * @author Jesse Ruth
 */
public class SimpleCreditCard implements CreditCard {
    final static Logger logger = LoggerFactory.getLogger(SimpleCreditCard.class);

    private String issuer;
    private String type;
    private String holder;
    private String accountNumber;
    private String expirationDate;

    /**
     * No parameter constructor, required by JavaBeans.
     */
    public SimpleCreditCard() {
    }

    /**
     * Gets the card issuer.
     *
     * @return the card issuer.
     */
    @Override
    public String getIssuer() {
        return issuer;
    }

    /**
     * Sets the card issuer.
     *
     * @param issuer the card issuer.
     */
    @Override
    public void setIssuer(final String issuer) {
        logger.info("Set Issuer: {}", issuer);
        this.issuer = issuer;
    }

    /**
     * Gets the card type.
     *
     * @return the card type.
     */
    @Override
    public String getType() {
        return type;
    }

    /**
     * Sets the card type.
     *
     * @param type the card type.
     */
    @Override
    public void setType(final String type) {
        this.type = type;
    }

    /**
     * Gets the card holder's name.
     *
     * @return the card holder's name.
     */
    @Override
    public String getHolder() {
        return holder;
    }

    /**
     * Sets the card holder's name.
     *
     * @param holder the card holder's name.
     */
    @Override
    public void setHolder(final String holder) {
        this.holder = holder;
    }

    /**
     * Gets the card account number.
     *
     * @return the card account number.
     */
    @Override
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the card account number.
     *
     * @param accountNumber the card account number.
     */
    @Override
    public void setAccountNumber(final String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * Gets the card expiration date.
     *
     * @return the card expiration date.
     */
    @Override
    public String getExpirationDate() {
        return expirationDate;
    }

    /**
     * Sets the card expiration date.
     *
     * @param expirationDate the card expiration date.
     */
    @Override
    public void setExpirationDate(final String expirationDate) {
        this.expirationDate = expirationDate;
    }
}
