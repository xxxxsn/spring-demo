package com.spring;


import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ApplicationContext {

    private Class configClass;

    //单例池
    private ConcurrentHashMap<String, Object> singletonObjects = new ConcurrentHashMap<>();

    private ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();


    private List<BeanPostProcessor> postProcessorList = new ArrayList<>();

    public ApplicationContext(Class configCalss) {
        //解析配置累
        //Component注解---->扫描路径--->扫描---->BeanDefinitionMap
        scan(configCalss);

        for (String beanName : beanDefinitionMap.keySet()) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if (beanDefinition.getScope().equals("singleton")) {
                //单例bean
                Object bean = createBean(beanName, beanDefinition);
                singletonObjects.put(beanName, bean);
            } else {

            }
        }

    }


    private Object createBean(String beanName, BeanDefinition beanDefinition) {
        Class clazz = beanDefinition.getClazz();
        try {
            Object instance = clazz.getDeclaredConstructor().newInstance();

            //依赖注入
            for (Field declaredField : clazz.getDeclaredFields()) {
                if (declaredField.isAnnotationPresent(Autowired.class)) {

                    //根据名字去找注入的bean对象
                    Object bean = getBean(declaredField.getName());
                    //给属性注入对象
                    declaredField.setAccessible(true);
                    declaredField.set(instance, bean);
                }
            }

            //Aware回调
            if (instance instanceof BeanNameAware) {
                BeanNameAware nameAware = (BeanNameAware) instance;
                nameAware.setBeanName(beanName);
            }


            //执行BeanPostProcessor 初始化之前的方法
            for (BeanPostProcessor beanPostProcessor : postProcessorList) {
                beanPostProcessor.postProcessBeforeInitialization(instance,beanName);
            }

            //初始化回调
            if (instance instanceof InitializxingBean) {
                InitializxingBean initializxingBean = (InitializxingBean) instance;
                try {
                    initializxingBean.afterPropertiesSet();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //执行beanPostProcessor初始化之前的方法
            for (BeanPostProcessor beanPostProcessor : postProcessorList) {
                beanPostProcessor.postProcessAfterInitialization(instance,beanName);
            }


            return instance;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void scan(Class configCalss) {
        this.configClass = configCalss;
        ComponentScan componentScan = (ComponentScan) configClass.getDeclaredAnnotation(ComponentScan.class);
        String path = componentScan.value();

        //扫描=>获取标注Component注解的类
        ClassLoader classLoader = ApplicationContext.class.getClassLoader();
        URL url = classLoader.getResource("com/xiesn/service");

        File file = new File(url.getFile());
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                String name = f.getAbsolutePath();
                if (name.endsWith(".class")) {
                    String className = name.substring(name.indexOf("com"), name.indexOf(".class"));
                    className = className.replace("\\", ".");
                    Class<?> clazz = null;
                    try {
                        clazz = classLoader.loadClass(className);
                        if (clazz.isAnnotationPresent(Component.class)) {
                            //包含Component注解的类的Bean
                            //解析类---> BeanDefinition

                            //当前类是否实现了BeanPostProcessor接口
                            if (BeanPostProcessor.class.isAssignableFrom(clazz)) {
                                BeanPostProcessor beanPostProcessor = (BeanPostProcessor) clazz.getDeclaredConstructor().newInstance();
                                postProcessorList.add(beanPostProcessor);
                            }

                            Component componentAnnotation = clazz.getDeclaredAnnotation(Component.class);
                            String beanName = componentAnnotation.value();


                            BeanDefinition beanDefinitio = new BeanDefinition();
                            beanDefinitio.setClazz(clazz);
                            if (clazz.isAnnotationPresent(Scope.class)) {
                                Scope scopetAnnotation = clazz.getDeclaredAnnotation(Scope.class);
                                beanDefinitio.setScope(scopetAnnotation.value());
                            } else {
                                beanDefinitio.setScope("singleton");
                            }
                            beanDefinitionMap.put(beanName, beanDefinitio);


                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }

    public Object getBean(String beanName) {
        if (beanDefinitionMap.containsKey(beanName)) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            String scope = beanDefinition.getScope();
            //单例bean
            if (scope.equals("singleton")) {
                return singletonObjects.get(beanName);
            }
            //prototype 的bean
            else {
                //创建bean对象
                Object bean = createBean(beanName, beanDefinition);
                return bean;
            }
        } else {
            //不存在的bean
            throw new NullPointerException();
        }
    }
}
