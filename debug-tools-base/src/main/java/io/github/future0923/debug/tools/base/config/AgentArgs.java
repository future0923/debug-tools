package io.github.future0923.debug.tools.base.config;

import io.github.future0923.debug.tools.base.utils.DebugToolsStringUtils;
import lombok.Data;

import java.lang.reflect.Field;

/**
 * @author future0923
 */
@Data
public class AgentArgs {

    private String tcpPort;

    private String httpPort;

    private String applicationName;

    public static AgentArgs parse(String agentArgs) {
        AgentArgs config = new AgentArgs();
        if (DebugToolsStringUtils.isNotBlank(agentArgs)) {
            String[] argsArray = agentArgs.split(",");
            for (String arg : argsArray) {
                String[] keyValue = arg.split("=");
                if (keyValue.length == 2) {
                    try {
                        Field field = AgentArgs.class.getDeclaredField(keyValue[0]);
                        field.setAccessible(true);
                        field.set(config, keyValue[1]);
                    } catch (Exception ignored) {
                    }
                }
            }
        }
        return config;
    }

    public static String format(AgentArgs config) {
        StringBuilder argsBuilder = new StringBuilder();
        Field[] fields = config.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object value = field.get(config);
                if (value != null) {
                    if (argsBuilder.length() > 0) {
                        argsBuilder.append(",");
                    }
                    argsBuilder.append(field.getName()).append("=").append(value);
                }
            } catch (Exception ignored) {
            }
        }
        return argsBuilder.toString();
    }

    public String format() {
        return format(this);
    }
}
