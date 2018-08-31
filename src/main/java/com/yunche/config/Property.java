package com.yunche.config;

/**
 * @ClassName: Property
 * @Description: 用于封装Bean子标签property内容
 * @author: yunche
 * @date: 2018/08/30
 */
public class Property
{
    private String name;
    private String value;
    private String ref;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public String getRef()
    {
        return ref;
    }

    public void setRef(String ref)
    {
        this.ref = ref;
    }
}
