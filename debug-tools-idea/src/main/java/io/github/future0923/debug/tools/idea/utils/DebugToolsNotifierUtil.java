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