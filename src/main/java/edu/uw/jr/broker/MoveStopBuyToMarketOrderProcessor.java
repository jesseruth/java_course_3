package edu.uw.jr.broker;

import edu.uw.ext.framework.order.StopBuyOrder;

import java.util.function.Consumer;

/**
 * Use of the class is discouraged, the Consumer<StopBuyOrder> interface and a lambda expression, is sufficient. This
 * class is provided only to illustrate the sufficiency of Consumer, and soothe those uncomfortable with lambda expressions.
 */
public class MoveStopBuyToMarketOrderProcessor implements Consumer<StopBuyOrder> {
    @Override
    public void accept(StopBuyOrder stopBuyOrder) {

    }
}
