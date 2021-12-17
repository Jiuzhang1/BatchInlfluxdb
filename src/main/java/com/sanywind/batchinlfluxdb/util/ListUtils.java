package com.sanywind.batchinlfluxdb.util;

import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


/**
 * 查找list集合中,存储的对象的,属性值的方法
 * @author Administrator
 *
 */
@Component
public class ListUtils {

    /**
     *
     * @param list:要查找的list
     * @param Property:根据list中的对象的哪一个属性来查找,
     * @param value:要查找的值为什么
     * @param clazz:list中存储的类的字节码
     * @return
     */
    public static<T> List<T> containsByProperty(List<T> list,String Property,Object value,Class<T> clazz){

        List<T> list1 = new ArrayList<>();

        try {
            //根据字节码获得该类的某个属性(Property)
            Field field = clazz.getDeclaredField(Property);
            //设置访问权限
            field.setAccessible(true);
            //遍历list集合
            for (T t : list) {
                //根据对象获得其属性得值(获取t对象的属性值)
                Object obj = field.get(t);
                //如果相等
                if (value.equals(obj)) {
                    //将该对象存储到list1中
                    list1.add(t);
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
        }

        //将list返回
        return list1;
    }
}


