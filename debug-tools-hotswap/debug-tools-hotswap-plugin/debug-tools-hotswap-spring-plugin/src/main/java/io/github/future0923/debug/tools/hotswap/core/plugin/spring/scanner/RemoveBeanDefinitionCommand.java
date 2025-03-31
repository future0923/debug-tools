package io.github.future0923.debug.tools.hotswap.core.plugin.spring.scanner;

import io.github.future0923.debug.tools.hotswap.core.annotation.FileEvent;
import io.github.future0923.debug.tools.hotswap.core.command.EventMergeableCommand;
import io.github.future0923.debug.tools.hotswap.core.watch.WatchFileEvent;

/**
 * @author future0923
 */
public class RemoveBeanDefinitionCommand extends EventMergeableCommand<RemoveBeanDefinitionCommand> {

    private final WatchFileEvent event;

    public RemoveBeanDefinitionCommand(WatchFileEvent event) {
        this.event = event;
    }

    @Override
    public void executeCommand() {
        if (isDeleteEvent()) {
            if (event.isFile() && event.getURI().toString().endsWith(".class")) {
                // 删除了class文件，卸载bean
                if (FileEvent.DELETE.equals(event.getEventType())) {
                    ClassPathBeanDefinitionScannerAgent.removeBeanDefinitionByFilePath(event.getURI().getPath());
                }
            }
        }
    }

    @Override
    protected WatchFileEvent event() {
        return event;
    }
}
