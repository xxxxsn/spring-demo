package com.xiesn.service;

import com.spring.Component;

@Component("studentService")
public class StudentServiceImpl implements StudentService{
    @Override
    public void say() {
        System.err.println("say Hello");
    }
}
