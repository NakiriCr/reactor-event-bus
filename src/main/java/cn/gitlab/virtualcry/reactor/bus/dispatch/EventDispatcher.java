package cn.gitlab.virtualcry.reactor.bus.dispatch;

import cn.gitlab.virtualcry.reactor.bus.Event;
import cn.gitlab.virtualcry.reactor.bus.registry.Registry;
import cn.gitlab.virtualcry.reactor.bus.routing.Router;
import lombok.Getter;
import reactor.core.publisher.FluxProcessor;
import reactor.core.scheduler.Schedulers;

import java.util.function.Consumer;

/**
 * Implementation of {@link Dispatcher} that uses {@link FluxProcessor}.
 *
 * @author VirtualCry
 * @since 3.2.2
 */
@Getter
public class EventDispatcher implements Dispatcher<Event<?>> {

    private final FluxProcessor<Event<?>, Event<?>>     dispatcher;
    private final DispatcherSubscriber<Event<?>>        dispatcherSubscriber;


    public EventDispatcher(FluxProcessor<Event<?>, Event<?>> dispatcher,
                           Registry<Object, Consumer<? extends Event<?>>> consumerRegistry,
                           Router router,
                           Consumer<Throwable> dispatchErrorHandler) {
        this(dispatcher, new EventDispatcherSubscriber(consumerRegistry, router, dispatchErrorHandler));
    }

    public EventDispatcher(FluxProcessor<Event<?>, Event<?>> dispatcher,
                           DispatcherSubscriber<Event<?>> subscribers) {
        this.dispatcher = dispatcher;
        this.dispatcherSubscriber = subscribers;

        // start to subscribe.
        startToSubscribe();
    }

    private void startToSubscribe() {
        dispatcher.publishOn(Schedulers.newSingle("EventDispatcher"))
                .doOnNext(dispatcherSubscriber::doOnNext)
                .doOnError(dispatcherSubscriber::doOnError)
                .doOnSubscribe(dispatcherSubscriber::doOnSubscribe)
                .doOnComplete(dispatcherSubscriber::doOnComplete)
                .doOnCancel(dispatcherSubscriber::doOnCancel)
        .subscribe();
    }

    @Override
    public void onNext(Event<?> ev) {
        dispatcher.onNext(ev);
    }

    @Override
    public void onComplete() {
        dispatcher.onComplete();
    }

    @Override
    public void onCancel() {
        dispatcher.dispose();
    }
}
