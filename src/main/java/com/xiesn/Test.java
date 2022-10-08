package com.xiesn;

import com.spring.AppConfig;
import com.spring.ApplicationContext;
import com.xiesn.service.StudentService;

public class Test {
    public static void main(String[] args) {
        ApplicationContext applicationContext  = new ApplicationContext(AppConfig.class);


//        UserService userService1 = (UserService) applicationContext. getBean("userService");
//        System.err.println(userService1);

//        userService1.test();




        StudentService studentService = (StudentService) applicationContext. getBean("studentService");
        studentService.say();


    }
}
