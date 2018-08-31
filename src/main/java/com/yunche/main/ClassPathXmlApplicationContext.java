package com.yunche.main;

import com.yunche.config.Bean;
import com.yunche.config.Property;
import com.yunche.config.parse.ConfigManager;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: ClassPathXmlApplicationContext
 * @Description:
 * @author: yunche
 * @date: 2018/08/31
 */
public class ClassPathXmlApplicationContext implements BeanFactory
{
    /**
     * 获取读取配置文件中的Map信息
     */
    private Map<String, Bean> map;

    /**
     * 作为IoC容器使用
     */
    private Map<String, Object> context = new HashMap<>();

    public ClassPathXmlApplicationContext(String path)
    {
        map = ConfigManager.getConfig(path);

        //遍历配置，初始化Bean
        for (Map.Entry<String, Bean> en : map.entrySet())
        {
            String beanName = en.getKey();
            Bean  bean = en.getValue();

            Object existBean = context.get(beanName);
            if(existBean == null && bean.getScope().equals("singleton"))
            {
                Object beanObj = createBean(bean);

                context.put(beanName, beanObj);
            }
        }
    }

    /**
     * 通过反射创建对象
     * @param bean 存储元数据的对象
     * @return 返回根据元数据创建的对象
     */
    private Object createBean(Bean bean)
    {
        //创建该类对象
        Class clazz = null;
        try
        {
            clazz = Class.forName(bean.getClassName());
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
            throw new RuntimeException("没有找到该类" + bean.getClassName());
        }

        Object beanObj = null;
        try
        {
            beanObj = clazz.newInstance();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException("没有提供无参构造器");
        }

        //获得bean的属性将其注入
        if (bean.getProperties()!=null)
        {
            for(Property prop : bean.getProperties())
            {
                String name = prop.getName();
                String value = prop.getValue();
                String ref = prop.getRef();

                if(value!=null)
                {
                    Map<String, String[]> parmMap = new HashMap<>();
                    parmMap.put(name, new String[]{value});
                    try
                    {
                        //使用BeanUtils工具类完成属性注入，可以自动完成类型装换
                        BeanUtils.populate(beanObj, parmMap);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        throw new RuntimeException("请检查你的"+name+"属性");
                    }
                }

                if(ref!=null)
                {
                    //如果当前Ioc容器存在该bean，直接设置，否则使用递归，创建该bean对象
                    Object existBean = context.get(prop.getRef());
                    if(null == existBean)
                    {
                        //递归的创建一个bean
                        existBean = createBean(map.get(prop.getRef()));

                        if(map.get(prop.getRef()).getScope().equals("singleton"))
                        {
                            context.put(prop.getRef(), existBean);
                        }
                    }

                    try
                    {
                        BeanUtils.setProperty(beanObj, name, existBean);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        throw new RuntimeException("您的bean属性" + name +"没有对应的set方法");
                    }
                }
            }
        }

        return beanObj;
    }

    @Override
    public Object getBean(String name)
    {
        Object bean = context.get(name);
        //如果为空说明scope不是singleton,需要现场创建
        if(bean == null)
        {
            bean = createBean(map.get(name));
        }
        return bean;
    }
}
