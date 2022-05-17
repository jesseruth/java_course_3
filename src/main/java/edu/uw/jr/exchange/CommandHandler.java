package edu.uw.jr.exchange;

import edu.uw.ext.framework.exchange.StockExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;

public class CommandHandler implements Runnable {
    final static Logger logger = LoggerFactory.getLogger(CommandHandler.class);


    private final StockExchange realExchange;
    private final Socket sock;

    /**
     * Constructor
     *
     * @param sock         the socket for communication with the client
     * @param realExchange the "real" exchange to dispatch commands to
     */
    public CommandHandler(final Socket sock,
                          final StockExchange realExchange) {
        this.realExchange = realExchange;
        this.sock = sock;
        logger.info("New Command Handler with Exchange: {} and Sock is: {}", this.realExchange.isOpen(), this.sock.isClosed());

    }

    /**
     * Processes the command.
     */
    @Override
    public void run() {

    }
}
