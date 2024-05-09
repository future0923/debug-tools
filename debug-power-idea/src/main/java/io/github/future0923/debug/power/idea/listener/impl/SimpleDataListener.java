package io.github.future0923.debug.power.idea.listener.impl;

import io.github.future0923.debug.power.idea.listener.BaseDataListener;
import io.github.future0923.debug.power.idea.listener.event.SimpleDataEvent;
import io.github.future0923.debug.power.idea.ui.JsonEditor;

/**
 * @author future0923
 */
public class SimpleDataListener extends BaseDataListener<SimpleDataEvent> {

    private final JsonEditor jsonEditor;

    public SimpleDataListener(JsonEditor jsonEditor) {
        this.jsonEditor = jsonEditor;
    }

    @Override
    public void onEvent(SimpleDataEvent event) {
        jsonEditor.regenerateJsonText();
    }

}
