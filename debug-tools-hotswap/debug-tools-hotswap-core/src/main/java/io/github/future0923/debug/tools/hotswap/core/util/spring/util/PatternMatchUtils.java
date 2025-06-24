/*
 * Copyright (C) 2024-2025 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package io.github.future0923.debug.tools.hotswap.core.util.spring.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Utility methods for simple pattern matching, in particular for Spring's
 * typical "xxx*", "*xxx" and "*xxx*" pattern styles.
 *
 * @author Juergen Hoeller
 * @since 2.0
 */
public abstract class PatternMatchUtils {

    private final static Map<String, Pattern> patterns = new HashMap<>();

    public static boolean regexMatch(String pattern, String str) {
        if (StringUtils.isEmpty(pattern)) {
            return true;
        }

        Pattern p = patterns.get(pattern);
        if (p == null) {
            p = Pattern.compile(pattern);
            patterns.put(pattern, p);
        }
        boolean matched = p.matcher(str).matches();
        return matched;
    }

    /**
     * Match a String against the given pattern, supporting the following simple
     * pattern styles: "xxx*", "*xxx", "*xxx*" and "xxx*yyy" matches (with an
     * arbitrary number of pattern parts), as well as direct equality.
     * 
     * @param pattern
     *            the pattern to match against
     * @param str
     *            the String to match
     * @return whether the String matches the given pattern
     */
    public static boolean simpleMatch(String pattern, String str) {
        if (pattern == null || str == null) {
            return false;
        }
        int firstIndex = pattern.indexOf('*');
        if (firstIndex == -1) {
            return pattern.equals(str);
        }
        if (firstIndex == 0) {
            if (pattern.length() == 1) {
                return true;
            }
            int nextIndex = pattern.indexOf('*', firstIndex + 1);
            if (nextIndex == -1) {
                return str.endsWith(pattern.substring(1));
            }
            String part = pattern.substring(1, nextIndex);
            if ("".equals(part)) {
                return simpleMatch(pattern.substring(nextIndex), str);
            }
            int partIndex = str.indexOf(part);
            while (partIndex != -1) {
                if (simpleMatch(pattern.substring(nextIndex), str.substring(partIndex + part.length()))) {
                    return true;
                }
                partIndex = str.indexOf(part, partIndex + 1);
            }
            return false;
        }
        return (str.length() >= firstIndex && pattern.substring(0, firstIndex).equals(str.substring(0, firstIndex)) && simpleMatch(pattern.substring(firstIndex), str.substring(firstIndex)));
    }

    /**
     * Match a String against the given patterns, supporting the following
     * simple pattern styles: "xxx*", "*xxx", "*xxx*" and "xxx*yyy" matches
     * (with an arbitrary number of pattern parts), as well as direct equality.
     * 
     * @param patterns
     *            the patterns to match against
     * @param str
     *            the String to match
     * @return whether the String matches any of the given patterns
     */
    public static boolean simpleMatch(String[] patterns, String str) {
        if (patterns != null) {
            for (String pattern : patterns) {
                if (simpleMatch(pattern, str)) {
                    return true;
                }
            }
        }
        return false;
    }

}