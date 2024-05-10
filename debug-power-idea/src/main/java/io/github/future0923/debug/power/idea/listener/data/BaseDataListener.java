package io.github.future0923.debug.power.idea.listener.data;

import io.github.future0923.debug.power.idea.listener.data.event.DataEvent;

/**
 * @author future0923
 */
public abstract class BaseDataListener<E extends DataEvent> implements DataListener {

    @Override
    public void event(DataEvent event) {
        onEvent((E) event);
    }

    public abstract void onEvent(E event);
}
