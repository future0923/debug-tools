package io.github.future0923.debug.power.common.utils;

import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.URL;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 类工具类 <br>
 */
public class DebugPowerClassUtils {

	/**
	 * 根据类全路径获取类信息
	 *
	 * @param className 类全路径集合
	 * @return 类信息集合
	 */
	public static Class<?>[] getTypes(List<String> className) {
		return className.stream().map(DebugPowerClassUtils::loadClass).toArray(Class[]::new);
	}

	/**
	 * {@code null}安全的获取对象类型
	 *
	 * @param <T> 对象类型
	 * @param obj 对象，如果为{@code null} 返回{@code null}
	 * @return 对象类型，提供对象如果为{@code null} 返回{@code null}
	 */
	@SuppressWarnings("unchecked")
	public static <T> Class<T> getClass(T obj) {
		return ((null == obj) ? null : (Class<T>) obj.getClass());
	}

	/**
	 * 获得外围类<br>
	 * 返回定义此类或匿名类所在的类，如果类本身是在包中定义的，返回{@code null}
	 *
	 * @param clazz 类
	 * @return 外围类
	 * @since 4.5.7
	 */
	public static Class<?> getEnclosingClass(Class<?> clazz) {
		return null == clazz ? null : clazz.getEnclosingClass();
	}

	/**
	 * 是否为顶层类，即定义在包中的类，而非定义在类中的内部类
	 *
	 * @param clazz 类
	 * @return 是否为顶层类
	 * @since 4.5.7
	 */
	public static boolean isTopLevelClass(Class<?> clazz) {
		if (null == clazz) {
			return false;
		}
		return null == getEnclosingClass(clazz);
	}

	/**
	 * 获取类名
	 *
	 * @param obj      获取类名对象
	 * @param isSimple 是否简单类名，如果为true，返回不带包名的类名
	 * @return 类名
	 * @since 3.0.7
	 */
	public static String getClassName(Object obj, boolean isSimple) {
		if (null == obj) {
			return null;
		}
		final Class<?> clazz = obj.getClass();
		return getClassName(clazz, isSimple);
	}

	/**
	 * 获取类名<br>
	 * 类名并不包含“.class”这个扩展名<br>
	 * 例如：ClassUtil这个类<br>
	 *
	 * <pre>
	 * isSimple为false: "com.xiaoleilu.hutool.util.DebugPowerClassUtils"
	 * isSimple为true: "DebugPowerClassUtils"
	 * </pre>
	 *
	 * @param clazz    类
	 * @param isSimple 是否简单类名，如果为true，返回不带包名的类名
	 * @return 类名
	 * @since 3.0.7
	 */
	public static String getClassName(Class<?> clazz, boolean isSimple) {
		if (null == clazz) {
			return null;
		}
		return isSimple ? clazz.getSimpleName() : clazz.getName();
	}

	/**
	 * 加载类
	 *
	 * @param <T>           对象类型
	 * @param className     类名
	 * @param isInitialized 是否初始化
	 * @return 类
	 */
	@SuppressWarnings("unchecked")
	public static <T> Class<T> loadClass(String className, boolean isInitialized) {
		return (Class<T>) DebugPowerClassLoaderUtils.loadClass(className, isInitialized);
	}

	/**
	 * 加载类并初始化
	 *
	 * @param <T>       对象类型
	 * @param className 类名
	 * @return 类
	 */
	public static <T> Class<T> loadClass(String className) {
		return loadClass(className, true);
	}

	// ---------------------------------------------------------------------------------------------------- Invoke end

	/**
	 * 是否为包装类型
	 *
	 * @param clazz 类
	 * @return 是否为包装类型
	 */
	public static boolean isPrimitiveWrapper(Class<?> clazz) {
		if (null == clazz) {
			return false;
		}
		return BasicType.WRAPPER_PRIMITIVE_MAP.containsKey(clazz);
	}

	/**
	 * 是否为基本类型（包括包装类和原始类）
	 *
	 * @param clazz 类
	 * @return 是否为基本类型
	 */
	public static boolean isBasicType(Class<?> clazz) {
		if (null == clazz) {
			return false;
		}
		return (clazz.isPrimitive() || isPrimitiveWrapper(clazz));
	}

	/**
	 * 是否简单值类型或简单值类型的数组<br>
	 * 包括：原始类型,、String、other CharSequence, a Number, a Date, a URI, a URL, a Locale or a Class及其数组
	 *
	 * @param clazz 属性类
	 * @return 是否简单值类型或简单值类型的数组
	 */
	public static boolean isSimpleTypeOrArray(Class<?> clazz) {
		if (null == clazz) {
			return false;
		}
		return isSimpleValueType(clazz) || (clazz.isArray() && isSimpleValueType(clazz.getComponentType()));
	}

	/**
	 * 是否为简单值类型<br>
	 * 包括：
	 * <pre>
	 *     原始类型
	 *     String、other CharSequence
	 *     Number
	 *     Date
	 *     URI
	 *     URL
	 *     Locale
	 *     Class
	 * </pre>
	 *
	 * @param clazz 类
	 * @return 是否为简单值类型
	 */
	public static boolean isSimpleValueType(Class<?> clazz) {
		return isBasicType(clazz) //
				|| clazz.isEnum() //
				|| CharSequence.class.isAssignableFrom(clazz) //
				|| Number.class.isAssignableFrom(clazz) //
				|| Date.class.isAssignableFrom(clazz) //
				|| clazz.equals(URI.class) //
				|| clazz.equals(URL.class) //
				|| clazz.equals(Locale.class) //
				|| clazz.equals(Class.class)//
				// jdk8 date object
				|| TemporalAccessor.class.isAssignableFrom(clazz); //
	}

	/**
	 * 检查目标类是否可以从原类转化<br>
	 * 转化包括：<br>
	 * 1、原类是对象，目标类型是原类型实现的接口<br>
	 * 2、目标类型是原类型的父类<br>
	 * 3、两者是原始类型或者包装类型（相互转换）
	 *
	 * @param targetType 目标类型
	 * @param sourceType 原类型
	 * @return 是否可转化
	 */
	public static boolean isAssignable(Class<?> targetType, Class<?> sourceType) {
		if (null == targetType || null == sourceType) {
			return false;
		}

		// 对象类型
		if (targetType.isAssignableFrom(sourceType)) {
			return true;
		}

		// 基本类型
		if (targetType.isPrimitive()) {
			// 原始类型
			Class<?> resolvedPrimitive = BasicType.WRAPPER_PRIMITIVE_MAP.get(sourceType);
			return targetType.equals(resolvedPrimitive);
		} else {
			// 包装类型
			Class<?> resolvedWrapper = BasicType.PRIMITIVE_WRAPPER_MAP.get(sourceType);
			return resolvedWrapper != null && targetType.isAssignableFrom(resolvedWrapper);
		}
	}

	/**
	 * 指定类是否为Public
	 *
	 * @param clazz 类
	 * @return 是否为public
	 */
	public static boolean isPublic(Class<?> clazz) {
		if (null == clazz) {
			throw new NullPointerException("Class to provided is null.");
		}
		return Modifier.isPublic(clazz.getModifiers());
	}

	/**
	 * 指定方法是否为Public
	 *
	 * @param method 方法
	 * @return 是否为public
	 */
	public static boolean isPublic(Method method) {
		Assert.notNull(method, "Method to provided is null.");
		return Modifier.isPublic(method.getModifiers());
	}

	/**
	 * 指定类是否为非public
	 *
	 * @param clazz 类
	 * @return 是否为非public
	 */
	public static boolean isNotPublic(Class<?> clazz) {
		return false == isPublic(clazz);
	}

	/**
	 * 是否为JDK中定义的类或接口，判断依据：
	 *
	 * <pre>
	 * 1、以java.、javax.开头的包名
	 * 2、ClassLoader为null
	 * </pre>
	 *
	 * @param clazz 被检查的类
	 * @return 是否为JDK中定义的类或接口
	 * @since 4.6.5
	 */
	public static boolean isJdkClass(Class<?> clazz) {
		final Package objectPackage = clazz.getPackage();
		if (null == objectPackage) {
			return false;
		}
		final String objectPackageName = objectPackage.getName();
		return objectPackageName.startsWith("java.") //
				|| objectPackageName.startsWith("javax.") //
				|| clazz.getClassLoader() == null;
	}

	public enum BasicType {
		BYTE, SHORT, INT, INTEGER, LONG, DOUBLE, FLOAT, BOOLEAN, CHAR, CHARACTER, STRING;

		/** 包装类型为Key，原始类型为Value，例如： Integer.class =》 int.class. */
		public static final Map<Class<?>, Class<?>> WRAPPER_PRIMITIVE_MAP = new ConcurrentHashMap<>(8);
		/** 原始类型为Key，包装类型为Value，例如： int.class =》 Integer.class. */
		public static final Map<Class<?>, Class<?>> PRIMITIVE_WRAPPER_MAP = new ConcurrentHashMap<>(8);

		static {
			WRAPPER_PRIMITIVE_MAP.put(Boolean.class, boolean.class);
			WRAPPER_PRIMITIVE_MAP.put(Byte.class, byte.class);
			WRAPPER_PRIMITIVE_MAP.put(Character.class, char.class);
			WRAPPER_PRIMITIVE_MAP.put(Double.class, double.class);
			WRAPPER_PRIMITIVE_MAP.put(Float.class, float.class);
			WRAPPER_PRIMITIVE_MAP.put(Integer.class, int.class);
			WRAPPER_PRIMITIVE_MAP.put(Long.class, long.class);
			WRAPPER_PRIMITIVE_MAP.put(Short.class, short.class);

			for (Map.Entry<Class<?>, Class<?>> entry : WRAPPER_PRIMITIVE_MAP.entrySet()) {
				PRIMITIVE_WRAPPER_MAP.put(entry.getValue(), entry.getKey());
			}
		}

		/**
		 * 原始类转为包装类，非原始类返回原类
		 * @param clazz 原始类
		 * @return 包装类
		 */
		public static Class<?> wrap(Class<?> clazz){
			if(null == clazz || false == clazz.isPrimitive()){
				return clazz;
			}
			Class<?> result = PRIMITIVE_WRAPPER_MAP.get(clazz);
			return (null == result) ? clazz : result;
		}

		/**
		 * 包装类转为原始类，非包装类返回原类
		 * @param clazz 包装类
		 * @return 原始类
		 */
		public static Class<?> unWrap(Class<?> clazz){
			if(null == clazz || clazz.isPrimitive()){
				return clazz;
			}
			Class<?> result = WRAPPER_PRIMITIVE_MAP.get(clazz);
			return (null == result) ? clazz : result;
		}
	}
}
