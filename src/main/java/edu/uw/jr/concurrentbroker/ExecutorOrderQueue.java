package edu.uw.jr.concurrentbroker;

import edu.uw.ext.framework.broker.OrderQueue;
import edu.uw.ext.framework.order.Order;

import java.util.Comparator;
import java.util.Optional;
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

    }

    /**
     * Constructor.
     *
     * @param threshold the initial threshold
     * @param filter    the dispatch filter used to control dispatching from this queue
     * @param executor  the executor to be used to process this queues orders
     */
    public ExecutorOrderQueue(final T threshold,
                              final java.util.function.BiPredicate<T, E> filter,
                              final Executor executor) {

    }

    /**
     * Dispatch orders as long as there are dispatchable orders available.
     */
    @Override
    public void run() {

    }

    /**
     * Adds the specified order to the queue.
     *
     * @param order the order to be added to the queue
     */
    @Override
    public void enqueue(final E order) {

    }

    /**
     * Removes the highest dispatchable order in the queue. If there are orders in the queue but they do not meet
     * the dispatch threshold no order will be removed and null will be returned.
     *
     * @return the highest order in the queue, or null if there are no dispatchable orders in the queue
     */
    @Override
    public Optional<E> dequeue() {
        E returnValue = null;
        return Optional.ofNullable(returnValue);
    }

    /**
     * Registers the callback to be invoked during order processing.
     *
     * @param consumer the callback to be registered
     */
    @Override
    public void setConsumer(final Consumer<E> consumer) {

    }


    /**
     * Adjusts the threshold and dispatches orders.
     *
     * @param threshold the new threshold
     */
    @Override
    public void setThreshold(final T threshold) {

    }

    /**
     * Obtains the current threshold value.
     *
     * @return the current threshold
     */
    @Override
    public T getThreshold() {
        return null;
    }
}
