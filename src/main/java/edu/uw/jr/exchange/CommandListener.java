package edu.uw.jr.exchange;

import edu.uw.ext.framework.exchange.StockExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Accepts connections and passes them to a CommandHandler, for the reading and processing of commands.
 *
 * @author Jesse Ruth
 */
public class CommandListener implements Runnable {
    final static Logger logger = LoggerFactory.getLogger(CommandListener.class);
    /**
     * Command Port
     */
    final private int commandPort;
    /**
     * Socket the Thread listens on
     */
    private ServerSocket serverSocket;
    /**
     * The 'real' exchange this adapter delegates to
     */
    final private StockExchange realExchange;

    /**
     * Executor used to execute the client requests
     */
    final private ExecutorService requestExecutor = Executors.newCachedThreadPool();

    @Override
    public void run() {
        logger.info("RUN: Server is accepting connections on port {}", commandPort);
        try (ServerSocket localServerSock = new ServerSocket(commandPort)) {
            serverSocket = localServerSock;
            while (!serverSocket.isClosed()) {
                Socket socket = null;
                try {
                    socket = serverSocket.accept();
                    logger.info("Accepted Connection: {}:{}", socket.getLocalAddress(), socket.getLocalPort());
                } catch (final SocketException se) {
                    if (serverSocket != null && !serverSocket.isClosed()) {
                        logger.warn("Error accepting connections", se);
                    }
                }

                if (socket == null) {
                    continue;
                }
                requestExecutor.execute(new CommandHandler(socket, realExchange));
            }
        } catch (final IOException ex) {
            logger.error("error starting", ex);
        } finally {
            terminate();
        }
    }

    /**
     * Constructor
     *
     * @param commandPort  the listening port
     * @param realExchange the "real" exchange
     */
    public CommandListener(final int commandPort,
                           final StockExchange realExchange) {
        this.commandPort = commandPort;
        this.realExchange = realExchange;

    }

    /**
     * Terminates this thread gracefully
     */
    public void terminate() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                logger.info("closing server socket");
                serverSocket.close();
            }
            if (!requestExecutor.isShutdown()) {
                requestExecutor.shutdown();
                requestExecutor.awaitTermination(1L, TimeUnit.SECONDS);
            }

        } catch (final IOException ioex) {
            logger.info("Error closing listening socket", ioex);
        } catch (final InterruptedException iex) {
            logger.info("Interrupted awaiting executor termination", iex);
        }
    }
}
