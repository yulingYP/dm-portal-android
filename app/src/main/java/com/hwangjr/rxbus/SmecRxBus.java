package com.hwangjr.rxbus;

import com.hwangjr.rxbus.entity.EventType;
import com.hwangjr.rxbus.entity.ProducerEvent;
import com.hwangjr.rxbus.entity.SubscriberEvent;
import com.hwangjr.rxbus.finder.Finder;
import com.hwangjr.rxbus.thread.ThreadEnforcer;

import java.util.Map;
import java.util.Set;

/**
 *
 * Created by apple on 2016/11/17.
 */

public final class SmecRxBus  {
    private SmecRxBus(){

    }

    private static Bus sBus;

    /**
     * Get the instance of {@link Bus}
     *
     * @return e
     */
    public static synchronized Bus get() {
        if (sBus == null) {
            sBus = new Bus(ThreadEnforcer.ANY, Bus.DEFAULT_IDENTIFIER, new Finder() {
                @Override
                public Map<EventType, ProducerEvent> findAllProducers(Object listener) {
                    return SmecAnnotatedFinder.findAllProducers(listener);
                }

                @Override
                public Map<EventType, Set<SubscriberEvent>> findAllSubscribers(Object listener) {
                    return SmecAnnotatedFinder.findAllSubscribers(listener);
                }
            });
        }
        return sBus;
    }
}
