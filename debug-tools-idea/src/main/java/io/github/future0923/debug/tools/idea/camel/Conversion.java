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
package io.github.future0923.debug.tools.idea.camel;

import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Character.isLowerCase;
import static java.lang.Character.isUpperCase;


/**
 * @author future0923
 */
public class Conversion {

    private static final String CONVERSION_CAMEL_CASE = "camelCase";
    private static final String CONVERSION_LOWER_SNAKE_CASE = "snake_case";
    private static final String CONVERSION_PASCAL_CASE = "CamelCase";
    private static final String CONVERSION_UPPER_SNAKE_CASE = "SNAKE_CASE";
    private static final String CONVERSION_KEBAB_CASE = "kebab-case";
    private static final String CONVERSION_SPACE_CASE = "space case";
    private static final String CONVERSION_PASCAL_SPACE_CASE = "Camel Case";

    private static final List<String> conversionList = Arrays.asList("kebab-case", "SNAKE_CASE", "CamelCase", "camelCase", "snake_case", "space case", "Camel Case");

    @NotNull
    static String transform(String text,
                            boolean usePascalCaseWithSpace,
                            boolean useSpaceCase,
                            boolean useKebabCase,
                            boolean useUpperSnakeCase,
                            boolean usePascalCase,
                            boolean useCamelCase,
                            boolean useLowerSnakeCase) {
        String newText, appendText = "";
        boolean repeat = true;
        int iterations = 0;
        String next = null;

        Pattern p = Pattern.compile("^\\W+");
        Matcher m = p.matcher(text);
        if (m.find()) {
            appendText = m.group(0);
        }
        //remove all special chars
        text = text.replaceAll("^\\W+", "");

        do {
            newText = text;
            boolean isLowerCase = text.equals(text.toLowerCase());
            boolean isUpperCase = text.equals(text.toUpperCase());

            if (isLowerCase && text.contains("_")) {
                // snake_case to space case
                if (next == null) {
                    next = getNext(CONVERSION_LOWER_SNAKE_CASE);
                } else {
                    if (next.equals(CONVERSION_SPACE_CASE)) {
                        repeat = !useSpaceCase;
                        next = getNext(CONVERSION_SPACE_CASE);
                    }
                }
                newText = text.replace('_', ' ');

            } else if (isLowerCase && text.contains(" ")) {
                // space case to Camel Case
                if (next == null) {
                    next = getNext(CONVERSION_SPACE_CASE);
                } else {
                    newText = WordUtils.capitalize(text);
                    if (next.equals(CONVERSION_PASCAL_SPACE_CASE)) {
                        repeat = !usePascalCaseWithSpace;
                        next = getNext(CONVERSION_PASCAL_SPACE_CASE);
                    }
                }

            } else if (isUpperCase(text.charAt(0)) && isLowerCase(text.charAt(1)) && text.contains(" ")) {
                // Camel Case to kebab-case
                if (next == null) {
                    next = getNext(CONVERSION_PASCAL_SPACE_CASE);
                } else {
                    newText = text.toLowerCase().replace(' ', '-');
                    if (next.equals(CONVERSION_KEBAB_CASE)) {
                        repeat = !useKebabCase;
                        next = getNext(CONVERSION_KEBAB_CASE);
                    }
                }

            } else if (isLowerCase && text.contains("-") || (isLowerCase && !text.contains(" "))) {
                // kebab-case to SNAKE_CASE
                if (next == null) {
                    next = getNext(CONVERSION_KEBAB_CASE);
                } else {
                    newText = text.replace('-', '_').toUpperCase();
                    if (next.equals(CONVERSION_UPPER_SNAKE_CASE)) {
                        repeat = !useUpperSnakeCase;
                        next = getNext(CONVERSION_UPPER_SNAKE_CASE);
                    }
                }

            } else if ((isUpperCase && text.contains("_")) || (isLowerCase && !text.contains("_") && !text.contains(" ")) || (isUpperCase && !text.contains(" "))) {
                // SNAKE_CASE to PascalCase
                if (next == null) {
                    next = getNext(CONVERSION_UPPER_SNAKE_CASE);
                } else {
                    newText = Conversion.toCamelCase(text.toLowerCase());
                    if (next.equals(CONVERSION_PASCAL_CASE)) {
                        repeat = !usePascalCase;
                        next = getNext(CONVERSION_PASCAL_CASE);
                    }
                }

            } else if (!isUpperCase && text.substring(0, 1).equals(text.substring(0, 1).toUpperCase()) && !text.contains("_")) {
                // PascalCase to camelCase
                if (next == null) {
                    next = getNext(CONVERSION_PASCAL_CASE);
                } else {
                    newText = text.substring(0, 1).toLowerCase() + text.substring(1);
                    if (next.equals(CONVERSION_CAMEL_CASE)) {
                        repeat = !useCamelCase;
                        next = getNext(CONVERSION_CAMEL_CASE);
                    }
                }
            } else {
                // camelCase to snake_case
                if (next == null) {
                    next = getNext(CONVERSION_CAMEL_CASE);
                } else {
                    newText = Conversion.toSnakeCase(text);
                    if (next.equals(CONVERSION_LOWER_SNAKE_CASE)) {
                        repeat = !useLowerSnakeCase;
                        next = getNext(CONVERSION_LOWER_SNAKE_CASE);
                    }
                }
            }
            if (iterations++ > 20) {
                repeat = false;
            }
            text = newText;
        } while (repeat);

        return appendText + newText;
    }

    private static String getNext(String conversion) {
        int index = conversionList.indexOf(conversion) + 1;
        if (index < conversionList.size()) {
            return conversionList.get(index);
        } else {
            return conversionList.get(0);
        }
    }

    /**
     * Convert a string (CamelCase) to snake_case
     *
     * @param in CamelCase string
     * @return snake_case String
     */
    private static String toSnakeCase(String in) {
        in = in.replaceAll(" +", "");
        StringBuilder result = new StringBuilder("" + Character.toLowerCase(in.charAt(0)));
        for (int i = 1; i < in.length(); i++) {
            char c = in.charAt(i);
            if (isUpperCase(c)) {
                result.append("_").append(Character.toLowerCase(c));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    /**
     * Convert a string (snake_case) to CamelCase
     *
     * @param in snake_case String
     * @return CamelCase string
     */
    private static String toCamelCase(String in) {
        StringBuilder camelCased = new StringBuilder();
        String[] tokens = in.split("_");
        for (String token : tokens) {
            if (!token.isEmpty()) {
                camelCased.append(token.substring(0, 1).toUpperCase()).append(token.substring(1));
            } else {
                camelCased.append("_");
            }
        }
        return camelCased.toString();
    }
}