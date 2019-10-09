package cn.gitlab.virtualcry.reactor.bus.event;

import lombok.Getter;

/**
 * Somethings
 *
 * @author VirtualCry
 */
@Getter
public class TestEvent {

    private String content;

    public TestEvent(String content) {
        this.content = content;
    }
}
