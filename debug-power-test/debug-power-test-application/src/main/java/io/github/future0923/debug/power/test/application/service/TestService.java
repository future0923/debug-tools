package io.github.future0923.debug.power.test.application.service;

import io.github.future0923.debug.power.test.application.dao.TestDao;
import io.github.future0923.debug.power.test.application.dao.UserDao;
import io.github.future0923.debug.power.test.application.domain.TestEnum;
import io.github.future0923.debug.power.test.application.domain.dto.MoreDTO;
import io.github.future0923.debug.power.test.application.domain.dto.TestDTO;
import io.github.future0923.debug.power.test.application.domain.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

@Service
public class TestService {

    @Autowired
    private TestDao testDao;

    @Autowired
    private Test1Service test1Service;

    @Autowired
    private UserDao userDao;

    public void test() {
        System.out.println(1);
    }

    public void test(String[] args) {
        System.out.println(1);
    }

    public String test(String name) {
        return testDao.getByNameAndAge(name);
    }

    public String test(String name, Integer age) {
        return "name = " + name + ", age = " + age;
    }

    public void test(Test1Service test1Service) {
        test1Service.test();
    }

    public Integer test(BiFunction<Integer, Integer, Integer> function) {
        Integer apply = function.apply(1, 2);
        System.out.println(apply);
        return apply;
    }

    public void test(TestDTO dto) {
        System.out.println(dto);
    }


    public void test(String name,
                     Integer age,
                     TestEnum testEnum,
                     Test1Service test1Service,
                     BiFunction<Integer, Integer, Integer> function,
                     TestDTO dto) {
        System.out.println("name = " + name + ", age = " + age);
        System.out.println(testEnum);
        test1Service.test();
        Integer apply = function.apply(1, 2);
        System.out.println(apply);
        System.out.println(dto);
    }

    public void test(MoreDTO dto) {
        System.out.println(dto);
    }

    public void test(List<String> obj) {
        obj.forEach(System.out::println);
    }

    public void test(Collection<Long> obj) {
        obj.forEach(System.out::println);
    }

    public void test(Map<Integer, Long> map) {
        System.out.println(1);
    }

    public User testDao(Integer id) {
        User user = userDao.selectById(id);
        System.out.println("user = " + user);
        return user;
    }

    public void test(LocalDateTime localDateTime, LocalDate localDate, Date date) {
        System.out.println("localDateTime = " + localDateTime + ", localDate = " + localDate + ", date = " + date);
    }
}
