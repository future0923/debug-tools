package io.github.future0923.debug.tools.test.simple.proxy.cglib;

/**
 * @author future0923
 */
public class UserService {

    public void addUser(String name) {
        //System.out.println("Add user: " + name);
        printName(name);
    }

    public void printName(String name) {
        System.out.println("printName: " + name);
    }
}
