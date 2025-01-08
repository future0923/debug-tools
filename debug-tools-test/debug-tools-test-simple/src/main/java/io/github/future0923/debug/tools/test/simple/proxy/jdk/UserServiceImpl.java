package io.github.future0923.debug.tools.test.simple.proxy.jdk;

/**
 * @author future0923
 */
public class UserServiceImpl implements UserService {

    @Override
    public void addUser(String name) {
        System.out.println("Adding user 33: " + name);
    }
}
