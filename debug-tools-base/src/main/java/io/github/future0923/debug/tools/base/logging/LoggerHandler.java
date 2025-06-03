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
package io.github.future0923.debug.tools.base.logging;

import io.github.future0923.debug.tools.base.hutool.core.util.ClassUtil;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Simple handler to log to output stream (default is system.out).
 */
public class LoggerHandler {

    // stream to receive the log
    PrintStream outputStream;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    /**
     * Setup custom stream (default is System.out).
     *
     * @param outputStream custom stream
     */
    public void setPrintStream(PrintStream outputStream) {
        this.outputStream = outputStream;
    }

    // print a message to System.out and optionally to custom stream
    protected void printMessage(String message) {
        String log = "DebugTools: " + sdf.format(new Date()) + " " + message;
        System.out.println(log);
        if (outputStream != null)
            outputStream.println(log);
    }

    public void print(Class<?> clazz, Logger.Level level, String message, Throwable throwable, Object... args) {

        // replace {} in string with actual parameters
        String messageWithArgs = message;
        for (Object arg : args) {
            int index = messageWithArgs.indexOf("{}");
            if (index >= 0) {
                messageWithArgs = messageWithArgs.substring(0, index) + String.valueOf(arg) + messageWithArgs.substring(index + 2);
            }
        }

        StringBuilder stringBuffer = new StringBuilder();
        if (level.equals(Logger.Level.TRACE)) {
            stringBuffer.append("  ").append(ColorConsole.getBlue(level.name()));
        } else if (level.equals(Logger.Level.DEBUG)) {
            stringBuffer.append("  ").append(ColorConsole.getBlueGreen(level.name()));
        } else if (level.equals(Logger.Level.INFO)) {
            stringBuffer.append("   ").append(ColorConsole.getGreen(level.name()));
        } else if (level.equals(Logger.Level.WARNING)) {
            stringBuffer.append(ColorConsole.getYellow(level.name()));
        } else if (level.equals(Logger.Level.RELOAD)) {
            stringBuffer.append(" ").append(ColorConsole.getPurple(level.name()));
        } else if (level.equals(Logger.Level.ERROR)) {
            stringBuffer.append("  ").append(ColorConsole.getRed(level.name()));
        }
        stringBuffer.append(" ");
        stringBuffer.append("[").append(Thread.currentThread().getName()).append("]");
        stringBuffer.append(" ");
        stringBuffer.append(ColorConsole.getBlueGreen(ClassUtil.getShortClassName(clazz.getName())));
        stringBuffer.append(" ");

        // 获取当前堆栈跟踪信息
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        // 查找第一个非CustomLogger类的方法调用
        StackTraceElement caller = null;
        for (StackTraceElement element : stackTrace) {
            if (element.getClassName().equals(clazz.getName())) {
                caller = element;
                break;
            }
        }
        if (caller != null) {
            stringBuffer.append(ColorConsole.getRed(String.valueOf(caller.getLineNumber())));
            stringBuffer.append(" : ");
        }
        stringBuffer.append(messageWithArgs);

        if (throwable != null) {
            stringBuffer.append("\n");
            stringBuffer.append(formatErrorTrace(throwable));
        }

        printMessage(stringBuffer.toString());
    }

    private String formatErrorTrace(Throwable throwable) {
        StringWriter errors = new StringWriter();
        throwable.printStackTrace(new PrintWriter(errors));
        return errors.toString();
    }

    public void setDateTimeFormat(String dateTimeFormat) {
        sdf = new SimpleDateFormat(dateTimeFormat);
    }
}
