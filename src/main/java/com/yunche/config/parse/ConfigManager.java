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
