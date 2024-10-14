package io.github.future0923.debug.tools.idea.model;

import java.util.Objects;

/**
 * 参数数据信息
 *
 * @author future0923
 */
public class ParamDataItem {

    protected String name;

    protected String qualifiedName;

    protected String param;

    public ParamDataItem() {
    }

    public ParamDataItem(String name, String qualifiedName, String param) {
        this.name = name;
        this.qualifiedName = qualifiedName;
        this.param = param;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    public void setQualifiedName(String qualifiedName) {
        this.qualifiedName = qualifiedName;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }


    public String methodName() {
        return Objects.isNull(qualifiedName) ? null : qualifiedName.substring(qualifiedName.lastIndexOf("#") + 1);
    }
}