package com.example.redisdesign.service;

import com.example.redisdesign.BaseTest;
import com.example.redisdesign.entity.ArticalRecommandVo;
import com.example.redisdesign.utils.LongUtil;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;

public class ArticalServiceTest extends BaseTest {

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
        Long d = LongUtil.parse(redisTemplate.opsForZSet().score("score:","1"));
        articalService.voteArtical(2L, 1L);
        Long d2 = LongUtil.parse(redisTemplate.opsForZSet().score("score:","1"));
        Assert.assertEquals(d2.longValue(), d+1);
    }

    @Test
    public void getArticalList() {
        List<ArticalRecommandVo> recommandList = articalService.getArticalList(1,null,1);
        Assert.assertEquals(1,recommandList.size());
    }



}