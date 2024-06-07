package io.github.future0923.debug.power.idea.constant;

import io.github.future0923.debug.power.base.constants.ProjectConstants;

import java.util.regex.Pattern;

/**
 * @author future0923
 */
public interface IdeaPluginProjectConstants {

    String AGENT_JAR_PATH = "/lib/debug-power-agent-" + ProjectConstants.VERSION + ".jar";

    String AGENT_TMP_PREFIX = "debug-power-agent-";

    Pattern AGENT_TMP_REGEX = Pattern.compile("-javaagent:[^\\s]*" + AGENT_TMP_PREFIX + "[^\\s]*\\.jar");

    String PARAM_FILE = "/.idea/DebugPower/agent.json";

    String TOOL_WINDOW_ID = "DebugPower";
}
