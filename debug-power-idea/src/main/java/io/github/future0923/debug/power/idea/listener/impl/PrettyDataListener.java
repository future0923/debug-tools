package io.github.future0923.debug.power.idea.listener.impl;

import io.github.future0923.debug.power.idea.listener.BaseDataListener;
import io.github.future0923.debug.power.idea.listener.event.PrettyDataEvent;
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
