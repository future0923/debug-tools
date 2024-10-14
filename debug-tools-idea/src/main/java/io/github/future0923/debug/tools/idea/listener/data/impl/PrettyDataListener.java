package io.github.future0923.debug.tools.idea.listener.data.impl;

import io.github.future0923.debug.tools.idea.listener.data.BaseDataListener;
import io.github.future0923.debug.tools.idea.listener.data.event.PrettyDataEvent;
import io.github.future0923.debug.tools.idea.ui.main.MainJsonEditor;

/**
 * @author future0923
 */
public class PrettyDataListener extends BaseDataListener<PrettyDataEvent> {

    private final MainJsonEditor jsonEditor;

    public PrettyDataListener(MainJsonEditor jsonEditor) {
        this.jsonEditor = jsonEditor;
    }

    @Override
    public void onEvent(PrettyDataEvent event) {
        jsonEditor.prettyJsonText();
    }

}
