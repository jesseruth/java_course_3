package edu.uw.jr.concurrentbroker;

import edu.uw.ext.framework.broker.OrderQueue;
import edu.uw.ext.framework.order.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

/**
 * An order queue using an executor to dispatch orders.
 *
 * @param <T> the dispatch threshold type
 * @param <E> the order type contained in the queue
 * @author Jesse Ruth
 */
public class ExecutorOrderQueue<T, E extends Order> implements OrderQueue<T, E>, Runnable {
    /**
     * This logger.
     */
    final static Logger logger = LoggerFactory.getLogger(ExecutorOrderQueue.class);

    /**
     * the queue data structure.
     */
    private final SortedSet<E> queue;

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

    final Executor executor;

    /**
     * Constructor
     *
     * @param threshold the initial threshold
     * @param filter    the dispatch filter used to control dispatching from this queue
     * @param cmp       Comparator to be used for ordering
     * @param executor  the executor to be used to process this queues orders
     */
    public ExecutorOrderQueue(final T threshold,
                              final BiPredicate<T, E> filter,
                              final Comparator<E> cmp,
                              final Executor executor) {
        logger.info("New ExecutorOrderQueue");

        this.threshold = threshold;
        this.filter = filter;
        this.executor = executor;

        queue = Collections.synchronizedSortedSet(new TreeSet<>(cmp));
    }

    /**
     * Constructor.
     *
     * @param threshold the initial threshold
     * @param filter    the dispatch filter used to control dispatching from this queue
     * @param executor  the executor to be used to process this queues orders
     */
    public ExecutorOrderQueue(final T threshold,
                              final BiPredicate<T, E> filter,
                              final Executor executor) {
        this(threshold, filter, Comparator.naturalOrder(), executor);
        logger.info("New ExecutorOrderQueue");
    }

    /**
     * Dispatch orders as long as there are dispatchable orders available.
     */
    @Override
    public void run() {
        logger.info("Run ExecutorOrderQueue");
        dispatchOrders();
    }

    /**
     * Adds the specified order to the queue.
     *
     * @param order the order to be added to the queue
     */
    @Override
    public void enqueue(final E order) {
        logger.info("SimpleOrderQueue enqueue order: {}, Ticker: {}", threshold, order.getStockTicker());
        if (queue.add(order)) {
            executor.execute(this);
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
     * Removes the highest dispatchable order in the queue. If there are orders in the queue but they do not meet
     * the dispatch threshold no order will be removed and null will be returned.
     *
     * @return the highest order in the queue, or null if there are no dispatchable orders in the queue
     */
    @Override
    public Optional<E> dequeue() {
        logger.info("dequeue");

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
     * Registers the callback to be invoked during order processing.
     *
     * @param consumer the callback to be registered
     */
    @Override
    public void setConsumer(final Consumer<E> consumer) {
        logger.info("setConsumer");
        this.consumer = consumer;
    }


    /**
     * Adjusts the threshold and dispatches orders.
     *
     * @param threshold the new threshold
     */
    @Override
    public void setThreshold(final T threshold) {
        logger.info("setThreshold");

        this.threshold = threshold;
        executor.execute(this);
    }

    /**
     * Obtains the current threshold value.
     *
     * @return the current threshold
     */
    @Override
    public T getThreshold() {
        logger.info("Run ExecutorOrderQueue");

        return this.threshold;
    }
}
