package com.light.rpc.common;

import java.io.Serializable;

public class Request implements Serializable{
    
    private static final long serialVersionUID = 6529430977803351000L;

    private String id;

    private String group;               // 分组名

    private String interfaceName;       // 接口

    private String methodName;          // 方法

    private Class<?>[] parameterTypes;  // 参数类别

    private Object[] parameters;        // 参数

    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getGroup() {
        return group;
    }
    
    public void setGroup(String group) {
        this.group = group;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }
    
    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
    
    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }
    
    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public Object[] getParameters() {
        return parameters;
    }
    
    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }

}
