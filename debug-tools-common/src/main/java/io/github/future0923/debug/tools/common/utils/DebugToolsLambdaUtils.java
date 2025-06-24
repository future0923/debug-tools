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
package io.github.future0923.debug.tools.common.utils;

import io.github.future0923.debug.tools.base.logging.Logger;
import pl.joegreen.lambdaFromString.LambdaCreationException;
import pl.joegreen.lambdaFromString.LambdaFactory;
import pl.joegreen.lambdaFromString.TypeReference;

import java.lang.reflect.Type;

/**
 * @author future0923
 */
public class DebugToolsLambdaUtils {

    private static final Logger log = Logger.getLogger(DebugToolsLambdaUtils.class);

    private static final LambdaFactory lambdaFactory = LambdaFactory.get();

    public static <T> T createLambda(String value, Type parameterType){
        try {
            return lambdaFactory.createLambda(value, new TypeReference<T>(parameterType){});
        } catch (LambdaCreationException e) {
            log.error("生成lambda表达式失败", e);
            return null;
        }
    }
}
