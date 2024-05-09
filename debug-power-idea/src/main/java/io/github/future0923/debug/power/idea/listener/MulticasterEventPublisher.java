package io.github.future0923.debug.power.idea.listener;

import io.github.future0923.debug.power.idea.listener.event.DataEvent;

/**
 * @author future0923
 */
public class MulticasterEventPublisher {

    private final DataEventMulticaster multicaster = new DefaultDataEventMulticaster();

    public void addListener(DataListener listener) {
        multicaster.addListener(listener);
    }

    public void removeListener(DataListener listener) {
        multicaster.removeListener(listener);
    }

    public void multicastEvent(DataEvent event) {
        multicaster.multicastEvent(event);
    }

}
