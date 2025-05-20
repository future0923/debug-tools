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

    String TOOL_WINDOW_ID = "DebugTools";

    String SCRATCH_PATH = "/debug-tools-plugins";

    String ROOT_TYPE_ID = "DebugToolsPlugin";

    String ROOT_TYPE_DISPLAY_NAME = "Debug Tools Plugins";

    String GROOVY_CONSOLE_FILE = "groovy/console.groovy";
}
