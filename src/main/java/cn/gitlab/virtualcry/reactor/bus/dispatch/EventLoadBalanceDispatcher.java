package cn.gitlab.virtualcry.reactor.bus.dispatch;

import cn.gitlab.virtualcry.reactor.bus.Event;
import cn.gitlab.virtualcry.reactor.bus.support.loadBalance.LoadBalance;
import cn.gitlab.virtualcry.reactor.bus.support.loadBalance.LoadBalanceStrategy;
import cn.gitlab.virtualcry.reactor.bus.support.loadBalance.LoadBalances;

import java.util.List;

/**
 * Dispatcher with load balance. Decide which {@link Dispatcher} will publish the {@link Event}
 * according to the {@link LoadBalanceStrategy}.
 *
 * @author VirtualCry
 * @since 3.2.2
 */
public class EventLoadBalanceDispatcher<E extends Event<?>> implements Dispatcher<E> {

    private final List<Dispatcher<E>>                   dispatchers;
    private final LoadBalance                           loadBalance;


    public EventLoadBalanceDispatcher(List<Dispatcher<E>> dispatchers,
                                      LoadBalanceStrategy loadBalanceStrategy) {
        this.dispatchers = dispatchers;
        this.loadBalance = LoadBalances.create(loadBalanceStrategy);
    }


    @Override
    public void onNext(E ev) {
        loadBalance.get(dispatchers, ev.getKey()).onNext(ev);
    }

    @Override
    public void onComplete() {
        dispatchers.forEach(Dispatcher::onComplete);
    }

    @Override
    public void onCancel() {
        dispatchers.forEach(Dispatcher::onCancel);
    }
}
