package io.github.future0923.debug.power.idea.listener.data.impl;

import io.github.future0923.debug.power.idea.listener.data.BaseDataListener;
import io.github.future0923.debug.power.idea.listener.data.event.PrettyDataEvent;
import io.github.future0923.debug.power.idea.ui.JsonEditor;

/**
 * @author future0923
 */
public class PrettyDataListener extends BaseDataListener<PrettyDataEvent> {

    private final JsonEditor jsonEditor;

    public PrettyDataListener(JsonEditor jsonEditor) {
        this.jsonEditor = jsonEditor;
    }

    @Override
    public void onEvent(PrettyDataEvent event) {
        jsonEditor.prettyJsonText();
    }

}
