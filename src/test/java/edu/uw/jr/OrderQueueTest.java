package edu.uw.jr;


import edu.uw.ext.framework.broker.OrderQueue;
import edu.uw.ext.framework.order.Order;
import edu.uw.ext.framework.order.StopBuyOrder;
import edu.uw.ext.framework.order.StopSellOrder;
import edu.uw.jr.broker.SimpleOrderQueue;
import test.AbstractOrderQueueTest;

import java.util.Comparator;
import java.util.function.BiPredicate;

/**
 * Concrete subclass of AbstractQueueTest, provides implementations of the
 * createStopBuyOrderQueue, createStopSellOrderQueue and createAnyOrderQueue
 * methods which create instances of "my" OrderQueue implementation class, using
 * "my" Comparator implementations.
 */
public class OrderQueueTest extends AbstractOrderQueueTest {
    /**
     * Creates an instance of "my" OrderQueue implementation class, using
     * an instance of "my" implementation of Comparator that is intended to
     * order StopBuyOrders.
     *
     * @param filter the OrderDispatch filter to be used
     * @return a new OrderQueue instance
     */
    @Override
    protected final OrderQueue<Integer, StopBuyOrder> createStopBuyOrderQueue(
            final BiPredicate<Integer, StopBuyOrder> filter) {
        return new SimpleOrderQueue<>(0, (t, o) -> o.getPrice() <= t, Comparator.comparing(StopBuyOrder::getPrice));
    }

    /**
     * Creates an instance of "my" OrderQueue implementation class, using
     * an instance of "my" implementation of Comparator that is intended to
     * order StopSellOrders.
     *
     * @param filter the OrderDispatch filter to be used
     * @return a new OrderQueue instance
     */
    @Override
    protected final OrderQueue<Integer, StopSellOrder> createStopSellOrderQueue(
            final BiPredicate<Integer, StopSellOrder> filter) {
        return new SimpleOrderQueue<>(0, (t, o) -> o.getPrice() <= t, Comparator.comparing(StopSellOrder::getPrice).reversed());
    }

    /**
     * Creates an instance of "my" OrderQueue implementation class, the queue
     * will order the Orders according to their natural ordering.
     *
     * @param filter the OrderDispatch filter to be used
     * @return a new OrderQueue instance
     */
    @Override
    protected final OrderQueue<Boolean, Order> createAnyOrderQueue(
            final BiPredicate<Boolean, Order> filter) {
        return new SimpleOrderQueue<Boolean, Order>(true, (Boolean t, Order o) -> t);
    }
}