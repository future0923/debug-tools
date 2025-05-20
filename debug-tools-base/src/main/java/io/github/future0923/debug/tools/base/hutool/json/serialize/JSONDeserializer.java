package io.github.future0923.debug.tools.base.hutool.json.serialize;

import io.github.future0923.debug.tools.base.hutool.json.JSON;

/**
 * JSON反序列话自定义实现类
 * 
 * @author Looly
 *
 * @param <T> 反序列化后的类型
 */
@FunctionalInterface
public interface JSONDeserializer<T> {
	
	/**
	 * 反序列化，通过实现此方法，自定义实现JSON转换为指定类型的逻辑
	 * 
	 * @param json {@link JSON}
	 * @return 目标对象
	 */
	T deserialize(JSON json);
}
