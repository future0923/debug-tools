/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.future0923.debug.tools.base.hutool.core.bean.copier;

import java.lang.reflect.Type;

/**
 * JSON自定义转换扩展接口,因core模块无法直接调用json模块而创建,
 * 使用此接口避免使用反射调用toBean方法而性能太差。
 *
 * @author mkeq
 * @since 5.8.22
 */
public interface IJSONTypeConverter {

	/**
	 * 转为实体类对象
	 *
	 * @param <T>  Bean类型
	 * @param type {@link Type}
	 * @return 实体类对象
	 * @since 3.0.8
	 */
	<T> T toBean(Type type);

}
