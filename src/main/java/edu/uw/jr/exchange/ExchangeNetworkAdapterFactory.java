package edu.uw.jr.exchange;

import edu.uw.ext.framework.exchange.ExchangeAdapter;
import edu.uw.ext.framework.exchange.NetworkExchangeAdapterFactory;
import edu.uw.ext.framework.exchange.StockExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.UnknownHostException;

/**
 * Factory for new Exchange Network Adapters
 *
 * @author Jesse Ruth
 */
public class ExchangeNetworkAdapterFactory implements NetworkExchangeAdapterFactory {
    final static Logger logger = LoggerFactory.getLogger(ExchangeNetworkAdapterFactory.class);

    /**
     * Instantiates an ExchangeNetworkAdapter
     * @param stockExchange underlying real exchange
     * @param multicastIP the multicast IP address used to distribute events
     * @param multicastPort the port used to distribute events
     * @param commandPort the listening port used to accept command requests
     * @return a newly instantiated ExchangeNetworkAdapter, or null, if instantiation fails
     */
    @Override
    public ExchangeAdapter newAdapter(final StockExchange stockExchange, final String multicastIP, final int multicastPort, final int commandPort) {
        ExchangeAdapter exchangeAdapter = null;
        logger.info("ExchangeNetworkAdapterFactory new ADAPTER");
        try {
            exchangeAdapter =  new ExchangeNetworkAdapter(stockExchange, multicastIP, multicastPort, commandPort);
        } catch (final UnknownHostException e) {
            logger.error("Unable to resolve multicast address", e);
        }
        return exchangeAdapter;
    }
}
