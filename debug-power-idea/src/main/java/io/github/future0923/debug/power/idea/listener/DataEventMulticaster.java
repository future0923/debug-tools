package io.github.future0923.debug.power.idea.listener;

import io.github.future0923.debug.power.idea.listener.event.DataEvent;

/**
 * @author future0923
 */
public interface DataEventMulticaster {

    void addListener(DataListener listener);

    void removeListener(DataListener listener);

    void multicastEvent(DataEvent event);

}
