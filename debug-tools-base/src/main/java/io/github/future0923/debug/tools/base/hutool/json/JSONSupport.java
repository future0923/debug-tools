package io.github.future0923.debug.tools.base.hutool.json;

import io.github.future0923.debug.tools.base.hutool.core.bean.BeanUtil;
import io.github.future0923.debug.tools.base.hutool.json.JSON;
import io.github.future0923.debug.tools.base.hutool.json.JSONConverter;
import io.github.future0923.debug.tools.base.hutool.json.JSONString;

/**
 * JSON支持<br>
 * 继承此类实现实体类与JSON的相互转换
 *
 * @author Looly
 */
public class JSONSupport implements JSONString, JSONBeanParser<JSON> {

	/**
	 * JSON String转Bean
	 *
	 * @param jsonString JSON String
	 */
	public void parse(String jsonString) {
		parse(new JSONObject(jsonString));
	}

	/**
	 * JSON转Bean
	 *
	 * @param json JSON
	 */
	@Override
	public void parse(JSON json) {
		final JSONSupport support = JSONConverter.jsonToBean(getClass(), json, false);
		BeanUtil.copyProperties(support, this);
	}

	/**
	 * @return JSON对象
	 */
	public JSONObject toJSON() {
		return new JSONObject(this);
	}

	@Override
	public String toJSONString() {
		return toJSON().toString();
	}

	/**
	 * 美化的JSON（使用回车缩进显示JSON），用于打印输出debug
	 *
	 * @return 美化的JSON
	 */
	public String toPrettyString() {
		return toJSON().toStringPretty();
	}

	@Override
	public String toString() {
		return toJSONString();
	}
}
