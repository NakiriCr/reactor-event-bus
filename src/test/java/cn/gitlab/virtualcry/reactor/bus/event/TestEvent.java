package cn.gitlab.virtualcry.reactor.bus.event;

import cn.gitlab.virtualcry.reactor.bus.Event;
import lombok.Builder;
import lombok.Getter;

/**
 * Somethings
 *
 * @author VirtualCry
 */
@Builder @Getter
public class TestEvent extends Event {

    private String content;
}
