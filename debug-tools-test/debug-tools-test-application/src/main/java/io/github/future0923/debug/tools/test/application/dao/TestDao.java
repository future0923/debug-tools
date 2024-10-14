package io.github.future0923.debug.tools.test.application.dao;

import org.springframework.stereotype.Service;

@Service
public class TestDao {

    public String getByNameAndAge(String name) {
        return "name = " + name;
    }
}
