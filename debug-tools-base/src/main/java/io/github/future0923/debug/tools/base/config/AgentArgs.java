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
package io.github.future0923.debug.tools.base.config;

import io.github.future0923.debug.tools.base.enums.PrintSqlType;
import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.base.utils.DebugToolsProperties;
import io.github.future0923.debug.tools.base.utils.DebugToolsStringUtils;
import lombok.Data;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.Properties;

/**
 * Agent可传入的参数
 *
 * @author future0923
 */
@Data
public class AgentArgs {

    private static final Logger logger = Logger.getLogger(AgentArgs.class);

    /**
     * 日志等级
     */
    private Logger.Level logLevel;

    /**
     * 是否启动Server. true | false
     */
    private String server;

    /**
     * 监听的TCP端口 (server=true时才生效)
     */
    private String tcpPort;

    /**
     * 监听的HTTP端口 (server=true时才生效)
     */
    private String httpPort;

    /**
     * 附着的应用名称
     */
    private String applicationName;

    /**
     * 是否打印执行的SQL语句 {@link PrintSqlType}
     */
    private String printSql;

    /**
     * 是否开启热重载/热部署. true | false
     */
    private String hotswap;

    /**
     * 热重载/热部署时禁用的插件名集合
     */
    private String disabledPlugins;

    /**
     * 外部配置文件路径
     */
    private String propertiesFilePath;

    /**
     * 本地是否开启自动附着. true | false
     */
    private String autoAttach;

    /**
     * 将agent上的string参数转为AgentArgs对象
     *
     * @param agentArgs key1=value1,key2=value2
     * @return AgentArgs
     */
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
        config.loadConfigurationFile();
        return config;
    }

    /**
     * 将AgentArgs对象转为agent参数字符串
     *
     * @param config AgentArgs对象
     * @return agent参数字符串 key1=value1,key2=value2
     */
    public static String format(AgentArgs config) {
        StringBuilder argsBuilder = new StringBuilder();
        Field[] fields = config.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.getName().equals("logger")) {
                continue;
            }
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

    /**
     * 将当前对象转为agent参数字符串
     *
     * @return agent参数字符串 key1=value1,key2=value2
     */
    public String format() {
        return format(this);
    }

    /**
     * 载入外部文件，相同时优先级低于agent参数
     */
    private void loadConfigurationFile() {
        try {
            String externalPropertiesFile = propertiesFilePath;
            if (DebugToolsStringUtils.isNotBlank(externalPropertiesFile)) {
                URL configurationURL = DebugToolsStringUtils.resourceNameToURL(externalPropertiesFile);
                Properties properties = new DebugToolsProperties();
                properties.load(configurationURL.openStream());
                if (DebugToolsStringUtils.isBlank(hotswap)) {
                    hotswap = properties.getProperty("hotswap", "true");
                }
                if (DebugToolsStringUtils.isBlank(server)) {
                    server = properties.getProperty("server", "true");
                }
                if (DebugToolsStringUtils.isBlank(printSql)) {
                    printSql = properties.getProperty("printSql", PrintSqlType.NO.getType());
                }
                if (DebugToolsStringUtils.isBlank(applicationName)) {
                    applicationName = properties.getProperty("applicationName");
                }
                if (DebugToolsStringUtils.isBlank(tcpPort)) {
                    tcpPort = properties.getProperty("tcpPort", "12345");
                }
                if (DebugToolsStringUtils.isBlank(httpPort)) {
                    httpPort = properties.getProperty("httpPort", "22222");
                }
            }
        } catch (Exception e) {
            logger.error("Error while loading external properties file " + propertiesFilePath, e);
        }
    }
}
