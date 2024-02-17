package ru.job4j.bmb.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.stereotype.Service;

@Service
public class ReminderService implements BeanNameAware {
    private String beanName;

    @PostConstruct
    public void init() {
        System.out.println("Bean is going through @PostConstruct init.");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("Bean will be destroyed via @PreDestroy.");
    }

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
        System.out.println("The name of the bean is: " + beanName);
    }
}