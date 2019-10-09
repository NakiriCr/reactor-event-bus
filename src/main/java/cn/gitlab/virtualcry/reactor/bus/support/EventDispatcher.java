package cn.gitlab.virtualcry.reactor.bus.support;

import cn.gitlab.virtualcry.reactor.bus.Event;
import cn.gitlab.virtualcry.reactor.bus.registry.Registry;
import cn.gitlab.virtualcry.reactor.bus.routing.Router;
import lombok.Getter;
import reactor.core.publisher.FluxProcessor;
import reactor.core.scheduler.Schedulers;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Somethings
 *
 * @author VirtualCry
 */
@Getter
public class EventDispatcher implements Dispatcher<Event<?>> {

    private final FluxProcessor<Event<?>, Event<?>>                   dispatcher;
    private final DispatcherSubscriber<Event<?>>                   dispatcherSubscriber;
    private final BiConsumer<FluxProcessor<Event<?>, Event<?>>,
            DispatcherSubscriber<Event<?>>>                       startToSubscribe = (dispatcher, dispatcherSubscriber) ->
            dispatcher
                    .publishOn(Schedulers.newSingle("EventDispatcher"))
                    .doOnNext(dispatcherSubscriber::doOnNext)
                    .doOnError(dispatcherSubscriber::doOnError)
                    .doOnSubscribe(dispatcherSubscriber::doOnSubscribe)
                    .doOnCancel(dispatcherSubscriber::doOnComplete)
                    .doOnCancel(dispatcherSubscriber::doOnCancel)
            .subscribe();

    public EventDispatcher(FluxProcessor<Event<?>, Event<?>> dispatcher,
                           Registry<Object, PayloadConsumer<?>> consumerRegistry,
                           Router router,
                           Consumer<Throwable> dispatchErrorHandler) {
        this(dispatcher, new EventDispatcherSubscriber(consumerRegistry, router, dispatchErrorHandler));
    }

    public EventDispatcher(FluxProcessor<Event<?>, Event<?>> dispatcher, DispatcherSubscriber<Event<?>> subscribers) {
        this.dispatcher = dispatcher;
        this.dispatcherSubscriber = subscribers;

        // start to subscribe.
        startToSubscribe.accept(dispatcher, dispatcherSubscriber);
    }


    @Override
    public void onNext(Event<?> ev) {
        this.dispatcher.onNext(ev);
    }

    @Override
    public void onComplete() {
        this.dispatcher.onComplete();
    }

    @Override
    public void onCancel() {
        this.dispatcher.dispose();
    }
}
