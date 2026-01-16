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
package io.github.future0923.debug.tools.idea.constant;

import io.github.future0923.debug.tools.base.constants.ProjectConstants;

import java.util.regex.Pattern;

/**
 * @author future0923
 */
public interface IdeaPluginProjectConstants {

    String AGENT_JAR_PATH = "/lib/debug-tools-agent-" + ProjectConstants.VERSION + ".jar";

    String AGENT_TMP_PREFIX = "debug-tools-agent-";

    Pattern AGENT_TMP_REGEX = Pattern.compile("-javaagent:[^\\s]*" + AGENT_TMP_PREFIX + "[^\\s]*\\.jar");

    String PARAM_FILE = "/.idea/DebugTools/agent.json";

    String METHOD_AROUND_DIR = "/.idea/DebugTools/MethodAround/";

    String IGNORE_STATIC_FIELD_DIR = "/.idea/DebugTools/IgnoreStaticField/";

    String IGNORE_SQL_CONFIG_DIR = "/.idea/DebugTools/IgnoreSqlConfig/";

    String TOOL_WINDOW_ID = "DebugTools";

    String SCRATCH_PATH = "/debug-tools-plugins";

    String ROOT_TYPE_ID = "DebugToolsPlugin";

    String ROOT_TYPE_DISPLAY_NAME = "Debug Tools Plugins";

    String GROOVY_CONSOLE_FILE = "groovy/console.groovy";
}
