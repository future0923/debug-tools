package io.github.future0923.debug.power.idea.listener.data.impl;

import io.github.future0923.debug.power.idea.listener.data.BaseDataListener;
import io.github.future0923.debug.power.idea.listener.data.event.ExampleDataEvent;
import io.github.future0923.debug.power.idea.ui.main.MainJsonEditor;

/**
 * @author future0923
 */
public class SimpleDataListener extends BaseDataListener<ExampleDataEvent> {

    private final MainJsonEditor jsonEditor;

    public SimpleDataListener(MainJsonEditor jsonEditor) {
        this.jsonEditor = jsonEditor;
    }

    @Override
    public void onEvent(ExampleDataEvent event) {
        jsonEditor.regenerateJsonText(event.getType());
    }

}
