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

    public static final ServerConfig DEFAULT = new ServerConfig(50888);

    private int port;
}
