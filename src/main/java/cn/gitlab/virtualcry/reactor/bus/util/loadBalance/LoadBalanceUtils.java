package cn.gitlab.virtualcry.reactor.bus.util.loadBalance;

/**
 * Somethings
 *
 * @author VirtualCry
 */
public class LoadBalanceUtils {

    public static LoadBalance createLoadBalance(LoadBalanceStrategy strategy) {
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
