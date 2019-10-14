package cn.gitlab.virtualcry.reactor.bus.support.loadBalance;

/**
 * A helper class for creating a new {@link LoadBalance}.
 *
 * @author VirtualCry
 * @since 3.2.2
 */
public abstract class LoadBalances {

    private LoadBalances() {
    }

    public static LoadBalance create(LoadBalanceStrategy strategy) {
        switch (strategy) {
            case ROUND_ROBIN:
                return new RoundRobinLoadBalance();
            case RANDOM:
                return new RandomLoadBalance();
            default:
                return new NonLoadBalance();
        }
    }
}
