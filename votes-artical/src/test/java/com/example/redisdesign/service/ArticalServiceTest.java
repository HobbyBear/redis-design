package com.example.redisdesign.service;

import com.example.redisdesign.entity.ArticalRecommand;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.DecimalFormat;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ArticalServiceTest {

    @Autowired
    private ArticalService articalService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Test
    public void putArtical() {
        articalService.putArtical("test", "test", 1L, "test");
    }

    @Test
    public void voteArtical() {
        String d =  new DecimalFormat("0").format(redisTemplate.opsForZSet().score("score:","1"));
        articalService.voteArtical(2L, 1L);
        String d2 =  new DecimalFormat("0").format(redisTemplate.opsForZSet().score("score:","1"));
        Assert.assertEquals(Long.valueOf(d2).longValue(), Long.valueOf(d) + 1);
    }

    @Test
    public void getArticalList() {
        List<ArticalRecommand> recommandList = articalService.getArticalList(1,null,1);
        System.out.println(recommandList);
    }

}