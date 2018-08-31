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
<pre>
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
</pre>

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
