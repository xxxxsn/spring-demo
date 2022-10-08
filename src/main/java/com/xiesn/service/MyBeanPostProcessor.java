package com.xiesn.service;

import com.spring.BeanPostProcessor;
import com.spring.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@Component
public class MyBeanPostProcessor implements BeanPostProcessor {
    /**
     * 初始化之前
     *
     * @param bean
     * @param beanName
     * @return
     * @throws Exception
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws Exception {
        System.err.println("BeforeInitialization..........."+beanName);
        if (beanName.equals("userService")) {
            Class<?> aClass = bean.getClass();
            System.err.println(aClass);
            ((UserService) bean).setHellol("Helllo Spring");
        }
        return bean;
    }

    /**
     * 初始化之后
     *
     * @param bean
     * @param beanName
     * @return
     * @throws Exception
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws Exception {
        System.err.println("AfterInitialization..........."+beanName);
        if(beanName.equals("studentService")){
            //创建代理
            Object proxy = Proxy.newProxyInstance(MyBeanPostProcessor.class.getClassLoader(),
                    bean.getClass().getInterfaces(), new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    //代理逻辑
                    System.err.println("代理逻辑");
                    //原逻辑
                    return method.invoke(bean,args);
                }
            });
            return proxy;
        }
        return bean;
    }
}
