/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.future0923.debug.tools.idea.utils;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;

public class DebugToolsNotifierUtil {

  private final static String msgPre = "DebugToolsTool: ";

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