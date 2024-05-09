package io.github.future0923.debug.power.common.utils;

import pl.joegreen.lambdaFromString.LambdaCreationException;
import pl.joegreen.lambdaFromString.LambdaFactory;
import pl.joegreen.lambdaFromString.TypeReference;

import java.lang.reflect.Type;

/**
 * @author future0923
 */
public class DebugPowerLambdaUtils {

    private static final LambdaFactory lambdaFactory = LambdaFactory.get();

    public static <T> T createLambda(String value, Type parameterType){
        try {
            return lambdaFactory.createLambda(value, new TypeReference<T>(parameterType){});
        } catch (LambdaCreationException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
