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
