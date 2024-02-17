package ru.job4j.bmb.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.stereotype.Service;
import ru.job4j.bmb.content.Content;

@Service
public class BotCommandHandler implements BeanNameAware {
    private String beanName;

    void receive(Content content) {
        System.out.println(content);
    }

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
