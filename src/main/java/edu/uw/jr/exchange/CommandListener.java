package edu.uw.jr.exchange;

import edu.uw.ext.framework.exchange.StockExchange;

/**
 * Accepts connections and passes them to a CommandHandler, for the reading and processing of commands.
 *
 * @author Jesse Ruth
 *
 */
public class CommandListener implements Runnable {
    @Override
    public void run() {

    }

    public CommandListener(final int commandPort,
                            final StockExchange realExchange) {

    }
}
