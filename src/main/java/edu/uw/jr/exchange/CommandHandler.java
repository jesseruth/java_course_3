package edu.uw.jr.exchange;

import edu.uw.ext.framework.exchange.StockExchange;
import edu.uw.ext.framework.exchange.StockQuote;
import edu.uw.ext.framework.order.MarketBuyOrder;
import edu.uw.ext.framework.order.MarketSellOrder;
import edu.uw.ext.framework.order.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.Optional;

import static edu.uw.jr.exchange.ExchangeConstants.*;

/**
 * An instance of this class is dedicated to executing commands received.
 *
 * @author Jesse Ruth
 */
public class CommandHandler implements Runnable {
    final static Logger logger = LoggerFactory.getLogger(CommandHandler.class);

    /**
     * The `real` exchage this adapter delegates to
     */
    private final StockExchange realExchange;
    /**
     * The socket the command was received on.
     */
    private final Socket socket;

    /**
     * Constructor
     *
     * @param socket       the socket for communication with the client
     * @param realExchange the "real" exchange to dispatch commands to
     */
    public CommandHandler(final Socket socket,
                          final StockExchange realExchange) {
        this.realExchange = realExchange;
        this.socket = socket;
        logger.info("New Command Handler with Exchange: {} and Sock is: {}", this.realExchange.isOpen(), this.socket.isClosed());

    }

    /**
     * Processes the command.
     */
    @Override
    public void run() {
        try (
                final Socket localSocket = socket;
                final InputStream inputStream = localSocket.getInputStream();
                final Reader reader = new InputStreamReader(inputStream, ENCODING);
                final BufferedReader br = new BufferedReader(reader);

                final OutputStream outputStream = localSocket.getOutputStream();
                final Writer writer = new OutputStreamWriter(outputStream, ENCODING);
                final PrintWriter printWriter = new PrintWriter(writer, true);
        ) {

            String message = br.readLine();
            if (null == message) {
                message = "";
            }
            logger.info("Received command message {}", message);
            final String[] elements = message.split(ELEMENT_DELIMITER);
            final String cmd = elements[CMD_ELEMENT];

            logger.info("Processing Command {}", cmd);

            switch (cmd) {
                case GET_STATE_CMD:
                    doGetState(printWriter);
                    break;
                case GET_TICKERS_CMD:
                    doGetTickers(printWriter);
                    break;
                case GET_QUOTE_CMD:
                    doGetQuote(elements, printWriter);
                    break;
                case EXECUTE_TRADE_CMD:
                    doExecuteTrade(elements, printWriter);
                    break;
                default:
                    logger.error("Unrecognized command {}", cmd);
                    break;
            }


        } catch (final IOException e) {
            logger.error("error sending response", e);

        }
    }

    /**
     * Process the GET_QUOTE_CMD
     *
     * @param elements    the command elements
     * @param printWriter sink for the response string
     */
    private void doGetQuote(final String[] elements, final PrintWriter printWriter) {
        String ticker = elements[QUOTE_CMD_TICKER_ELEMENT];
        final Optional<StockQuote> stockQuoteOptional = realExchange.getQuote(ticker);
        stockQuoteOptional.ifPresentOrElse(quote -> printWriter.println(quote.getPrice()), () -> printWriter.println(INVALID_STOCK));
    }

    /**
     * Process the EXECUTE_TRADE_CMD
     *
     * @param elements    the command elements
     * @param printWriter sink for the response string
     */
    private void doExecuteTrade(final String[] elements, final PrintWriter printWriter) {
        if (realExchange.isOpen()) {
            final String orderType = elements[EXECUTE_TRADE_CMD_TYPE_ELEMENT];
            final String accountID = elements[EXECUTE_TRADE_CMD_ACCOUNT_ELEMENT];
            final String ticker = elements[EXECUTE_TRADE_CMD_TICKER_ELEMENT];
            final String shares = elements[EXECUTE_TRADE_CMD_SHARES_ELEMENT];
            int qty = -1;

            try {
                qty = Integer.parseInt(shares);
            } catch (final NumberFormatException nfe) {
                logger.warn("string to int conversion failed: {}", shares, nfe);
            }

            Order order;
            if (BUY_ORDER.equals(orderType)) {
                order = new MarketBuyOrder(accountID, qty, ticker);
            } else {
                order = new MarketSellOrder(accountID, qty, ticker);
            }
            int price = realExchange.executeTrade(order);
            printWriter.println(price);
        } else {
            printWriter.println(0);
        }

    }

    /**
     * Process the GET_TICKERS_CMD
     *
     * @param printWriter sink for the response string
     */
    private void doGetTickers(PrintWriter printWriter) {
        final String[] tickers = realExchange.getTickers();
        final String tickerString = String.join(ELEMENT_DELIMITER, tickers);
        printWriter.println(tickerString);
    }

    /**
     * Process the GET_STATE_CMD
     *
     * @param printWriter sink for the response string
     */
    private void doGetState(final PrintWriter printWriter) {
        final String response = realExchange.isOpen() ? OPEN_STATE : CLOSED_STATE;
        printWriter.println(response);
    }
}
