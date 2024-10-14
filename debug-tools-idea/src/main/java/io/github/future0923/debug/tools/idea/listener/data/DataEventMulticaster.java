package io.github.future0923.debug.tools.idea.listener.data;

import io.github.future0923.debug.tools.idea.listener.data.event.DataEvent;

/**
 * @author future0923
 */
public interface DataEventMulticaster {

    void addListener(DataListener listener);

    void removeListener(DataListener listener);

    void multicastEvent(DataEvent event);

}
