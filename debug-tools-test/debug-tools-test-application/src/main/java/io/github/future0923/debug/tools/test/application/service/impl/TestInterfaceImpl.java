package io.github.future0923.debug.tools.test.application.service.impl;

import io.github.future0923.debug.tools.test.application.dao.UserDao;
import io.github.future0923.debug.tools.test.application.domain.entity.User;
import io.github.future0923.debug.tools.test.application.service.TestInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author future0923
 */
@Service
public class TestInterfaceImpl implements TestInterface {

    @Autowired
    private UserDao userDao;

    @Override
    public void test(String name) {
        List<User> users = userDao.selectByName(name);
        users.forEach(System.out::println);
    }
}
