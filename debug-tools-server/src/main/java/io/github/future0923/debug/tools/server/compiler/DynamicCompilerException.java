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
package io.github.future0923.debug.tools.server.compiler;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 编译失败异常
 *
 * @author future0923
 */
public class DynamicCompilerException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private List<Diagnostic<? extends JavaFileObject>> diagnostics;

    public DynamicCompilerException(String message, List<Diagnostic<? extends JavaFileObject>> diagnostics) {
        super(message);
        this.diagnostics = diagnostics;
    }

    public DynamicCompilerException(Throwable cause, List<Diagnostic<? extends JavaFileObject>> diagnostics) {
        super(cause);
        this.diagnostics = diagnostics;
    }

    private List<Map<String, Object>> getErrorList() {
        List<Map<String, Object>> messages = new ArrayList<Map<String, Object>>();
        if (diagnostics != null) {
            for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics) {
                Map<String, Object> message = new HashMap<String, Object>(2);
                message.put("line", diagnostic.getLineNumber());
                message.put("message", diagnostic.getMessage(Locale.US));
                messages.add(message);
            }

        }
        return messages;
    }

    private String getErrors() {
        StringBuilder errors = new StringBuilder();

        for (Map<String, Object> message : getErrorList()) {
            for (Map.Entry<String, Object> entry : message.entrySet()) {
                Object value = entry.getValue();
                if (value != null && !value.toString().isEmpty()) {
                    errors.append(entry.getKey());
                    errors.append(": ");
                    errors.append(value);
                }
                errors.append(" , ");
            }

            errors.append("\n");
        }

        return errors.toString();

    }

    @Override
    public String getMessage() {
        return super.getMessage() + "\n" + getErrors();
    }

}
