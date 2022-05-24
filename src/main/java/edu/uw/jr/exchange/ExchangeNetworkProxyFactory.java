package edu.uw.jr.exchange;

import edu.uw.ext.framework.exchange.NetworkExchangeProxyFactory;
import edu.uw.ext.framework.exchange.StockExchange;

/**
 * Factory for new NetworkExchangeProxys
 *
 * @author Jesse Ruth
 */
public class ExchangeNetworkProxyFactory implements NetworkExchangeProxyFactory {
    /**
     * Factory method to create a new ExchangeNetworkProxy
     *
     * @param eventIpAddress The multicast address to connect to
     * @param evenPort       the multicast port to connect to
     * @param cmdIpAddress   the address the exchange accepts request on
     * @param cmdPort        the port the exchange accepts request on
     * @return a new ExchangeNetworkProxy or null if there is an error
     */
    @Override
    public StockExchange newProxy(final String eventIpAddress, final int evenPort, final String cmdIpAddress, final int cmdPort) {
        return new ExchangeNetworkProxy(eventIpAddress, evenPort, cmdIpAddress, cmdPort);
    }
}
