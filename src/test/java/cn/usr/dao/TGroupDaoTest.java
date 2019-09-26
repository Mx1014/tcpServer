package cn.usr.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * @Package: cn.usr.dao
 * @Description: TODO
 * @author: Rock 【shizhiyuan@usr.cn】
 * @Date: 2018-12-25 11:29
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TGroupDaoTest {

    @Autowired
    TGroupDao tGroupDao;

    @Test
    public void queryTargetGroupIdsByDid() {
        List<String> strings = tGroupDao.queryTargetGroupIdsByDid("00008318000000000017");
        System.out.println(strings);


    }

    @Test
    public void queryDidListByGroupIds() {
        List<String> strings = tGroupDao.queryTargetGroupIdsByDid("00008318000000000017");
        Set<String> strings1 = tGroupDao.queryDidListByGroupIds(strings);
        System.out.println(strings1.size());

    }
}