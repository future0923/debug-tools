package io.github.future0923.debug.tools.client.config;

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

    private String host = "127.0.0.1";

    private int port = 50888;

    private int heartbeatInterval = 60;
}
