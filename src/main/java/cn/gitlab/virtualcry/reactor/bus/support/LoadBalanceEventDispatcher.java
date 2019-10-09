package cn.gitlab.virtualcry.reactor.bus.support;

import cn.gitlab.virtualcry.reactor.bus.Event;
import cn.gitlab.virtualcry.reactor.bus.util.loadBalance.LoadBalance;
import cn.gitlab.virtualcry.reactor.bus.util.loadBalance.LoadBalanceStrategy;
import cn.gitlab.virtualcry.reactor.bus.util.loadBalance.LoadBalanceUtils;

import java.util.List;

/**
 * Somethings
 *
 * @author VirtualCry
 */
public class LoadBalanceEventDispatcher<E extends Event<?>> implements Dispatcher<E> {

    private final List<Dispatcher<E>>                   dispatchers;
    private final LoadBalance                           loadBalance;


    public LoadBalanceEventDispatcher(List<Dispatcher<E>> dispatchers,
                                      LoadBalanceStrategy loadBalanceStrategy) {
        this.dispatchers = dispatchers;
        this.loadBalance = LoadBalanceUtils.createLoadBalance(loadBalanceStrategy);
    }


    @Override
    public void onNext(E ev) {
        this.loadBalance.get(dispatchers, ev.getKey()).onNext(ev);
    }

    @Override
    public void onComplete() {
        this.dispatchers.forEach(Dispatcher::onComplete);
    }

    @Override
    public void onCancel() {
        this.dispatchers.forEach(Dispatcher::onCancel);
    }
}
