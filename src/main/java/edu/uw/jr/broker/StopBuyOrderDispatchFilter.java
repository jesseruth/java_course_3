package edu.uw.jr.broker;

import edu.uw.ext.framework.order.StopBuyOrder;

import java.util.function.BiPredicate;

/**
 * Use of the class is discouraged, the BiPredicate<Integer, StopBuyOrder> interface and a lambda expression, is
 * sufficient. This class is provided only to illustrate the sufficiency of BiPredicate, and soothe those uncomfortable with lambda expressions.
 */
public class StopBuyOrderDispatchFilter implements BiPredicate<Integer, StopBuyOrder> {
    @Override
    public boolean test(Integer integer, StopBuyOrder stopBuyOrder) {
        return false;
    }

    @Override
    public BiPredicate<Integer, StopBuyOrder> and(BiPredicate<? super Integer, ? super StopBuyOrder> other) {
        return BiPredicate.super.and(other);
    }

    @Override
    public BiPredicate<Integer, StopBuyOrder> negate() {
        return BiPredicate.super.negate();
    }

    @Override
    public BiPredicate<Integer, StopBuyOrder> or(BiPredicate<? super Integer, ? super StopBuyOrder> other) {
        return BiPredicate.super.or(other);
    }
}
