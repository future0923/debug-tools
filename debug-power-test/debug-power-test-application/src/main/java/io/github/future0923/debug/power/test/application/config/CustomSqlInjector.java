package io.github.future0923.debug.power.test.application.config;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.extension.injector.methods.InsertBatchSomeColumn;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author future0923
 */
@Configuration
public class CustomSqlInjector extends DefaultSqlInjector {

    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass, TableInfo tableInfo) {
        // 这里很重要，先要通过父类方法，获取到原有的集合，不然会自带的通用方法会失效的
        List<AbstractMethod> methodList = super.getMethodList(mapperClass, tableInfo);
        methodList.add(new InsertBatchSomeColumn(i -> i.getFieldFill() != FieldFill.UPDATE));
        return methodList;
    }
}
