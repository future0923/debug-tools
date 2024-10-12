package io.github.future0923.debug.power.server.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author future0923
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServerConfig {

    private String applicationName;

    private int tcpPort;

    private int httpPort;
}
