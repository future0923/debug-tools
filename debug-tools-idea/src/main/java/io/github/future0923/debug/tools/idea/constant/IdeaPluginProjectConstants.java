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
