package io.github.future0923.debug.power.test.application.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @author future0923
 */
public interface BaseMapperPlus<T> extends BaseMapper<T> {

    int insertBatchSomeColumn(List<T> entityList);
}
