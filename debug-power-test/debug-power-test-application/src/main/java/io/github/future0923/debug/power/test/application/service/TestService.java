package io.github.future0923.debug.power.test.application.service;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.future0923.debug.power.test.application.dao.TestDao;
import io.github.future0923.debug.power.test.application.dao.UserDao;
import io.github.future0923.debug.power.test.application.domain.TestEnum;
import io.github.future0923.debug.power.test.application.domain.dto.DealFilesHandoverCheckReq;
import io.github.future0923.debug.power.test.application.domain.dto.MoreDTO;
import io.github.future0923.debug.power.test.application.domain.dto.TestDTO;
import io.github.future0923.debug.power.test.application.domain.entity.User;
import io.github.future0923.debug.power.test.application.dto.PageR;
import io.github.future0923.debug.power.test.application.dto.ProfitBatchVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
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

    public String insertBatchSomeColumn() {
        User user1 = new User();
        user1.setName("1");
        user1.setAge(1);
        User user2 = new User();
        user2.setName("2");
        user2.setAge(2);
        userDao.insertBatchSomeColumn(Arrays.asList(user1, user2));
        return null;
    }

    public void insertBatch() {
        User user1 = new User();
        user1.setName("1");
        user1.setAge(1);
        User user2 = new User();
        user2.setName("2");
        user2.setAge(2);
    }

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

    public List<User> testNull() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getName, "a");
        wrapper.eq(User::getVersion, null);
        return userDao.selectList(wrapper);
    }

    public void test(LocalDateTime localDateTime, LocalDate localDate, Date date) {
        System.out.println("localDateTime = " + localDateTime + ", localDate = " + localDate + ", date = " + date);
    }

    private void testPrivate() {
        System.out.println("testPrivate");
    }

    public void test(DealFilesHandoverCheckReq req) {
        System.out.println(req);
    }

    public void exampleMethod(Map<String, Map<String, Map<Integer, String>>> param) {
        // Do something
    }

    public void test(HttpServletRequest request, HttpServletResponse response) {

    }

    public String test(File file) {
        String absolutePath = file.getAbsolutePath();
        System.out.println(absolutePath);
        return absolutePath;
    }

    public String test(Class<?> clz) {
        return clz.getName();
    }

    public PageR<ProfitBatchVO> page() {
        String json = "{\"code\":200,\"message\":\"操作成功\",\"data\":[{\"id\":1823615117445890049,\"type\":1,\"settlementBatchNo\":\"2024002\",\"startTime\":1719763200000,\"endTime\":1723564800000,\"profitTime\":1723651199000,\"status\":1,\"statusName\":\"进行中\",\"createBy\":1762736500306149377,\"createTime\":1723618700000,\"updateBy\":1823237365605113857,\"updateTime\":1724062252000,\"version\":4,\"butPcPerm\":[80000]},{\"id\":1,\"type\":1,\"settlementBatchNo\":\"2024001\",\"startTime\":1717171200000,\"endTime\":1719676800000,\"performanceTime\":1719763199000,\"profitTime\":1719763199000,\"carryTime\":1719763199000,\"status\":3,\"statusName\":\"已结束\",\"createBy\":0,\"updateBy\":0,\"updateTime\":1723618700000,\"version\":52,\"butPcPerm\":[]}],\"total\":2,\"subTotal\":0}";
        PageR<ProfitBatchVO> bean = JSONUtil.toBean(json, new TypeReference<PageR<ProfitBatchVO>>() {
        }, true);
        return bean;
        /*ProfitBatchVO vo1 = new ProfitBatchVO();
        vo1.setId(0L);
        vo1.setType(0);
        vo1.setSettlementBatchNo("");
        vo1.setStartTime(LocalDate.now());
        vo1.setEndTime(LocalDate.now());
        vo1.setPerformanceTime(LocalDateTime.now());
        vo1.setProfitTime(LocalDateTime.now());
        vo1.setCarryTime(LocalDateTime.now());
        vo1.setStatus(0);
        vo1.setStatusName("");
        vo1.setId(0L);
        vo1.setCreateBy(0L);
        vo1.setCreateByName("");
        vo1.setCreateTime(LocalDateTime.now());
        vo1.setUpdateBy(0L);
        vo1.setUpdateByName("");
        vo1.setUpdateTime(LocalDateTime.now());
        vo1.setVersion(0);
        vo1.setButPerm(new HashSet<Integer>());
        vo1.setButPcPerm(new HashSet<Integer>());
        return PageR.pageResp(1, 1, Arrays.asList(vo1));*/
    }
}
