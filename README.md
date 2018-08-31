# 手动创建一个SpringIoC容器（代码内容来源于：<a href="https://www.cnblogs.com/fingerboy/p/5425813.html">深入理解Spring</a>）

## 原理：Spring中的IoC实现机制原理就是工厂模式加反射机制
## 需要知识点：
- dom4j解析xml文件
- xpath表达式(用于解析xml中的文件)
- java反射机制
## 项目采用IDEA+MAVEN
## 项目包结构：
<p><img src="https://github.com/xiaxinandye/SpringIoC/blob/master/viewImage/1.PNG"></p>

## 依赖文件：BeanUtils工具包、dom4j工具包
## application.xml(用来存储对象的元数据，用于通过反射时创建对象。)
  <?xml version="1.0" encoding="UTF-8" ?>
<beans>
    <bean name="student" class="com.yunche.entity.Student" scope="prototype">
        <property name="name" value="小李"></property>
    </bean>

    <bean name="teacher" class="com.yunche.entity.Teacher" scope="prototype">
        <property name="student" ref="student"></property>
    </bean>

    <bean name="person" class="com.yunche.entity.Person">
        <property name="teacher" ref="teacher"></property>
        <property name="Student" ref="student"></property>
    </bean>

</beans>

## Student.java(学生实体类)
<pre>
  public class Student
  {
      private String name;
  
      public String getName()
      {
          return name;
      }

      public void setName(String name)
      {
          this.name = name;
      }
  }
</pre>

## Teacher.java(教师实体类)
<pre>
public class Teacher
{
    private Student student;

    public Student getStudent()
    {
        return student;
    }

    public void setStudent(Student student)
    {
        this.student = student;
    }
}
</pre>
## Person.java
<pre>
package com.yunche.entity;

/**
 * @ClassName: Person
 * @Description:
 * @author: yunche
 * @date: 2018/08/30
 */
public class Person
{
    private Student student;
    private Teacher teacher;

    public Student getStudent()
    {
        return student;
    }

    public void setStudent(Student student)
    {
        this.student = student;
    }

    public Teacher getTeacher()
    {
        return teacher;
    }

    public void setTeacher(Teacher teacher)
    {
        this.teacher = teacher;
    }
}

</pre>

## Bean.java(用于封装applicationContext.xml中的bean标签信息)
<pre>
package com.yunche.config;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: Bean
 * @Description: 用于封装Bean标签信息的Bean类
 * @author: yunche
 * @date: 2018/08/30
 */
public class Bean
{
    private String name;
    private String className;
    private String scope = "singleton";
    private List<Property> properties = new ArrayList<Property>();

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getClassName()
    {
        return className;
    }

    public void setClassName(String className)
    {
        this.className = className;
    }

    public String getScope()
    {
        return scope;
    }

    public void setScope(String scope)
    {
        this.scope = scope;
    }

    public List<Property> getProperties()
    {
        return properties;
    }

    public void setProperties(List<Property> properties)
    {
        this.properties = properties;
    }
}
</pre>

## Property.java(用于封装bean子标签property信息)
<pre>
package com.yunche.config;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: Bean
 * @Description: 用于封装Bean标签信息的Bean类
 * @author: yunche
 * @date: 2018/08/30
 */
public class Bean
{
    private String name;
    private String className;
    private String scope = "singleton";
    private List<Property> properties = new ArrayList<Property>();

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getClassName()
    {
        return className;
    }

    public void setClassName(String className)
    {
        this.className = className;
    }

    public String getScope()
    {
        return scope;
    }

    public void setScope(String scope)
    {
        this.scope = scope;
    }

    public List<Property> getProperties()
    {
        return properties;
    }

    public void setProperties(List<Property> properties)
    {
        this.properties = properties;
    }
}
</pre>
## ConfigManager.java(将applicationContext.xml中的标签数据信息封装到Bean类与Property类中)
<pre>
package com.yunche.config.parse;

import com.yunche.config.Bean;
import com.yunche.config.Property;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: ConfigManager
 * @Description:
 * @author: yunche
 * @date: 2018/08/30
 */
public class ConfigManager
{
    private static Map<String, Bean> map = new HashMap<>();

    /**
     * 读取配置文件并返回读取结果
     *
     * @param path xpath
     * @return
     */
    public static Map<String, Bean> getConfig(String path)
    {
        /**
         * dom4j的实现
         *    1.创建解析器
         *    2.加载配置文件，得到document对象
         *    3.定义小path表达式，取出所有Bean元素
         *    4.对Bean元素继续遍历
         *      4.1将Bean元素的name/class属性封装到bean类属性中
         *      4.2获得bean下的所有property子元素
         *      4.3将属性name/value/ref封装到Property类中
         *    5.将property对象分装到bean对象中
         *    6.将bean对象封装到Map集合中，返回map
         */

        //1.创建解析器
        SAXReader reader = new SAXReader();
        //2.加载配置文件，得到document对象
        InputStream is = ConfigManager.class.getResourceAsStream(path);
        Document doc = null;
        try
        {
            doc = reader.read(is);
        }
        catch (DocumentException e)
        {
            e.printStackTrace();
            throw new RuntimeException("请检查你的xml配置是否配置正确");
        }
        //3.定义xpath表达式，取出所有Bean元素
        String xpath = "//bean";

        //4.对Bean元素继续遍历
        List<Element> list = doc.selectNodes(xpath);
        if(list!=null)
        {
            //将Bean元素的name/class属性封装到bean类属性中
            for(Element bean : list)
            {
                Bean b = new Bean();
                String name = bean.attributeValue("name");
                String clazz = bean.attributeValue("class");
                String scope = bean.attributeValue("scope");
                b.setName(name);
                b.setClassName(clazz);
                if(scope!=null)
                {
                    b.setScope(scope);
                }
                //获得bean下所有的property子元素
                List<Element> children = bean.elements("property");

                //将属性name/value/ref封装到Property类中
                if(children!=null)
                {
                    for(final Element child : children)
                    {
                        Property property = new Property()
                        {
                            {
                                setName(child.attributeValue("name"));
                                setValue(child.attributeValue("value"));
                                setRef(child.attributeValue("ref"));
                            }
                        };
                        //将property对象分装到bean对象中
                        b.getProperties().add(property);
                    }
                }
                //将bean对象分装到Map集合中，返回map
                map.put(name, b);

            }


        }

        return map;
    }
}
</pre>
## BeanFactory接口（提供一个方法--用于根据对象名获取对象）
<pre>
package com.yunche.main;

/**
 * @ClassName: BeanFactory
 * @Description: 工厂Bean
 * @author: yunche
 * @date: 2018/08/31
 */
public interface BeanFactory
{
    Object getBean(String name);
}
</pre>

## ClassPathXmlApplicationContext类(主要生成IoC容器--存储对象)
<pre>
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
</pre>

## TestBean(测试类)
<pre>
package com.yunche.main;
import com.yunche.entity.Person;
import com.yunche.entity.Student;
import org.junit.Test;

/**
 * @ClassName: TestBean
 * @Description:
 * @author: yunche
 * @date: 2018/08/31
 */
public class TestBean
{
    @Test
    public void func1()
    {
        BeanFactory bf=new ClassPathXmlApplicationContext("/applicationContext.xml");

        Person s=(Person)bf.getBean("person");
        Person s1=(Person)bf.getBean("person");
        System.out.println(s==s1);
        System.out.println(s1);
        Student stu1=(Student) bf.getBean("student");
        Student stu2=(Student) bf.getBean("student");
        String name=stu1.getName();
        System.out.println(name);
        System.out.println(stu1==stu2);

    }
}
</pre>
