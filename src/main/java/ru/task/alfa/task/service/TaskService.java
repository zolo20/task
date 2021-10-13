

package ru.task.alfa.task.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class TaskService {
    private String str;

    public TaskService(@Qualifier(value = "test_bean") String str) {
        this.str = str;
    }

    public void doSomething() {

    }
}
