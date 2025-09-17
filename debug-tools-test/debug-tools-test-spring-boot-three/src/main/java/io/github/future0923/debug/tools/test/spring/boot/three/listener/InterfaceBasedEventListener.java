/*
 * Copyright (C) 2024-2025 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package io.github.future0923.debug.tools.test.spring.boot.three.listener;

/**
 * @author future0923
 */

import io.github.future0923.debug.tools.test.spring.boot.three.event.ActionEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 实现自定义接口的事件监听器
 */
@Component
public class InterfaceBasedEventListener implements EventHandler {

    @EventListener
    @Async
    @Override
    public void handleEvent(ActionEvent event) {
        System.out.println("[接口监听器] 事件处理: " + event);
        System.out.println("===============================================");
    }
}
