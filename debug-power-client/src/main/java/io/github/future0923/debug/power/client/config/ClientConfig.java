package io.github.future0923.debug.power.client.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author future0923
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientConfig {

    public static final ClientConfig DEFAULT = new ClientConfig(
            "127.0.0.1",
            50888,
            10
    );

    private String host;

    private int port;

    private int heartbeatInterval;
}
