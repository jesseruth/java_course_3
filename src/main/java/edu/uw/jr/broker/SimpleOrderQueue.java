package edu.uw.jr.broker;

import edu.uw.ext.framework.broker.OrderQueue;
import edu.uw.ext.framework.order.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.Optional;
import java.util.TreeSet;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

/**
 * A simple OrderQueue implementation backed by a TreeSet.
 *
 * @param <T> the dispatch threshold type
 * @param <E> the type of order contained in the queue
 * @author Jesse Ruth
 */
public class SimpleOrderQueue<T, E extends Order> implements OrderQueue<T, E> {
    /**
     * This logger.
     */
    final static Logger logger = LoggerFactory.getLogger(SimpleOrderQueue.class);
    /**
     * the queue data structure.
     */
    private final TreeSet<E> queue;

    /**
     * The current threshold.
     */
    private T threshold;

    /**
     * The BiPredicate used to determine if an order is dispatchable.
     */
    private final BiPredicate<T, E> filter;

    /**
     * Consumer used to process elements.
     */
    private Consumer<E> consumer;


    /**
     * Constructor
     *
     * @param threshold the initial threshold
     * @param filter    the dispatch filter used to control dispatching from this queue
     * @param cmp       Comparator to be used for ordering
     */
    public SimpleOrderQueue(final T threshold,
                            final BiPredicate<T, E> filter,
                            final Comparator<E> cmp) {
        logger.info("SimpleOrderQueue constructor Threshold: {}", threshold);

        queue = new TreeSet<>(cmp);
        this.threshold = threshold;
        this.filter = filter;
    }

    /**
     * Constructor
     *
     * @param threshold the initial threshold
     * @param filter    the dispatch filter used to control dispatching from this queue
     */
    public SimpleOrderQueue(final T threshold,
                            final BiPredicate<T, E> filter) {
        this(threshold, filter, Comparator.naturalOrder());
        logger.info("naturalOrder: SimpleOrderQueue constructor Threshold: {}", threshold);

    }

    /**
     * Adds the specified order to the queue. Subsequent to adding the order dispatches any dispatchable orders.
     *
     * @param order the order to be added to the queue
     */
    @Override
    public void enqueue(final E order) {
        logger.info("SimpleOrderQueue enqueue order: {}, Ticker: {}", threshold, order.getStockTicker());
        if (queue.add(order)) {
            dispatchOrders();
        }
    }

    /**
     * Executes callback for each element
     */
    private void dispatchOrders() {
        logger.info("SimpleOrderQueue dispatchOrders");
        Optional<E> opt;
        int counter = 1;
        while ((opt = dequeue()).isPresent()) {
            logger.info("*** SimpleOrderQueue dispatchOrders");
            if (consumer != null) {
                consumer.accept(opt.get());
                logger.info("SimpleOrderQueue dispatchOrder: {}", counter);
                counter++;
            }
        }
    }

    /**
     * Removes the highest dispatchable order in the queue. If there are orders in the queue, but they do not meet the
     * dispatch threshold order will not be removed and null will be returned.
     *
     * @return the first dispatchable order in the queue, or null if there are no dispatchable orders in the queue
     */
    @Override
    public Optional<E> dequeue() {
        logger.info("SimpleOrderQueue dequeue");

        E dispatchable = null;
        if (!queue.isEmpty()) {
            dispatchable = queue.first();
            if (filter.test(threshold, dispatchable)) {
                logger.info("Order is Dispatchable");
                queue.remove(dispatchable);
            } else {
                logger.info("Order is NOT Dispatchable");
                dispatchable = null;
            }
        }
        return Optional.<E>ofNullable(dispatchable);
    }

    /**
     * Registers the consumer to be used during order processing.
     *
     * @param consumer the consumer to be registered
     */
    @Override
    public void setConsumer(Consumer<E> consumer) {
        logger.info("Register a new consumer to be used during order processing.");

        this.consumer = consumer;
    }

    /**
     * Adjusts the threshold and dispatches orders.
     *
     * @param threshold the new threshold
     */
    @Override
    public void setThreshold(T threshold) {
        logger.info("Adjusts the threshold to {} and dispatched orders", threshold);
        this.threshold = threshold;
        dispatchOrders();
    }

    /**
     * Obtains the current threshold value.
     *
     * @return the current threshold
     */
    @Override
    public T getThreshold() {
        logger.info("Obtains the current threshold value of {}", threshold);

        return threshold;
    }
}
