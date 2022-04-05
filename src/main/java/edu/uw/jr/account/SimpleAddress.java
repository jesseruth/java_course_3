package edu.uw.jr.account;

import edu.uw.ext.framework.account.Address;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A straight forward implementation of the Address interface as a JavaBean.
 *
 * @author Jesse Ruth
 */
public class SimpleAddress implements Address {
    static Logger logger = LoggerFactory.getLogger(Address.class);

    private String streetAddress;
    private String city;
    private String state;
    private String zipCode;

    /**
     * Gets the street address.
     *
     * @return the street address.
     */
    @Override
    public String getStreetAddress() {
        return streetAddress;
    }

    /**
     * Sets the street address.
     *
     * @param streetAddress the street address.
     */
    @Override
    public void setStreetAddress(final String streetAddress) {
        logger.info("Set streetAddres {}", streetAddress);
        this.streetAddress = streetAddress;
    }

    /**
     * Gets the city.
     *
     * @return the city.
     */
    @Override
    public String getCity() {
        return city;
    }

    /**
     * Sets the city.
     *
     * @param city the city.
     */
    @Override
    public void setCity(final String city) {
        this.city = city;
    }

    /**
     * Gets the state.
     *
     * @return the state.
     */
    @Override
    public String getState() {
        return state;
    }

    /**
     * Sets the state.
     *
     * @param state the state.
     */
    @Override
    public void setState(final String state) {
        this.state = state;
    }

    /**
     * Gets the ZIP code.
     *
     * @return the ZIP code.
     */
    @Override
    public String getZipCode() {
        return zipCode;
    }

    /**
     * Sets the ZIP code.
     *
     * @param zipCode the ZIP code.
     */
    @Override
    public void setZipCode(final String zipCode) {
        this.zipCode = zipCode;
    }
}
