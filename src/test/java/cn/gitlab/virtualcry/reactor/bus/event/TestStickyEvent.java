package cn.gitlab.virtualcry.reactor.bus.event;

import cn.gitlab.virtualcry.reactor.bus.Event;
import cn.gitlab.virtualcry.reactor.bus.StickyEvent;
import lombok.Builder;
import lombok.Getter;

/**
 * Somethings
 *
 * @author VirtualCry
 */
@Builder @Getter
public class TestStickyEvent extends StickyEvent {

    private String content;
}
