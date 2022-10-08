package com.xiesn.service;

import com.spring.*;

//@Component("userService")
//@Scope("singleton")
public class UserService implements BeanNameAware , InitializxingBean {



    @Autowired
    private OrderService orderService;


    private String beanName;

    private String hellol;

    public void setHellol(String hellol) {
        this.hellol = hellol;
    }

    public void test(){
        System.err.println(orderService);
        System.err.println(beanName);
        System.err.println(hellol);
    }

    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.err.println("userService......init...........");
    }
}
