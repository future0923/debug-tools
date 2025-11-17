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
package io.github.future0923.debug.tools.idea.utils;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;

public class DebugToolsNotifierUtil {

  private final static String msgPre = "DebugTools: ";

  public static void notifyError(@Nullable Project project,
                                 String content) {
    NotificationGroupManager.getInstance()
            .getNotificationGroup("DebugTools")
            .createNotification(msgPre + content, NotificationType.ERROR)
            .notify(project);
  }

  public static void notifyInfo(@Nullable Project project,
                                 String content) {
    NotificationGroupManager.getInstance()
            .getNotificationGroup("DebugTools")
            .createNotification(msgPre + content, NotificationType.INFORMATION)
            .notify(project);
  }

}