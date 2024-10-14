package io.github.future0923.debug.tools.idea.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

/**
 * @author future0923
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServerDisplayValue {

    private static final String separator = " ";

    private String key;

    private String value;

    @Override
    public String toString() {
        return key + separator + value;
    }

    public static String display(String key, String value) {
        return key + separator + value;
    }

    public static ServerDisplayValue of(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        String[] split = text.split(separator);
        return new ServerDisplayValue(split[0], split[1]);
    }
}
