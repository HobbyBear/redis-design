package com.example.redisdesign.service;

import com.example.redisdesign.entity.ArticalRecommand;
import com.example.redisdesign.utils.LongUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author xch
 * @since 2019/6/13 14:06
 **/
@Service
public class ArticalService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 发布一篇新文章，这里省略文章内容之类的代码，直接考虑的是推荐文章列表部分。
     *
     * @param title
     * @param link
     * @param userId
     */
    public void putArtical(String title, String link, Long userId, String img) {
        Long articalId = redisTemplate.opsForValue().increment("artical:");
        Long time = System.currentTimeMillis();
        redisTemplate.opsForHash().put("artical:" + articalId, "link", link);
        redisTemplate.opsForHash().put("artical:" + articalId, "userId", String.valueOf(userId));
        redisTemplate.opsForHash().put("artical:" + articalId, "img", img);
        redisTemplate.opsForHash().put("artical:" + articalId, "title", title);
        redisTemplate.opsForHash().put("artical:" + articalId, "time", String.valueOf(time));
        redisTemplate.opsForHash().put("artical:" + articalId, "votes", "0");
        //时间排序
        redisTemplate.opsForZSet().add("time:", String.valueOf(articalId), time);
        //投票数排序,最开始投票为0
        redisTemplate.opsForZSet().add("score:", String.valueOf(articalId), time);
        //作者为自己要投一票
        voteArtical(userId, articalId);
    }

    /**
     * 给文章投票
     *
     * @param userId
     * @param articalId
     */
    public void voteArtical(Long userId, Long articalId) {
        //查看该用户是否已经投票，没投票才能发起投票
        Long res = redisTemplate.opsForSet().add("votes:" + articalId, String.valueOf(userId));
        if (res !=null && res == 1) {
            //文章票数加1
            redisTemplate.opsForHash().increment("artical:" + articalId, "votes", 1);
            //投票排序改变
            redisTemplate.opsForZSet().incrementScore("score:", String.valueOf(articalId), 1d);
        }

    }

    /**
     * 根据搜索条件返回推荐列表
     *
     * @param page
     * @param searchItem time ,score 排序条件,默认为score排序
     * @param size
     */
    public List<ArticalRecommand> getArticalList(int page, String searchItem, int size) {
        int start = (page - 1) * size;
        int end = start + size - 1;
        Set<String> articals;
        if (searchItem != null && searchItem.equals("time")) {
            articals = redisTemplate.opsForZSet().reverseRange("time:", start, end);
        } else {
            articals = redisTemplate.opsForZSet().reverseRange("score:", start, end);
        }
        List<ArticalRecommand> recommandList = new ArrayList<>();
        Optional.ofNullable(articals).ifPresent(articalIds -> {
                    for (String articalId : articalIds) {
                        ArticalRecommand.ArticalRecommandBuilder builder = ArticalRecommand.builder();
                        builder.id(Long.valueOf(articalId));
                        builder.img(String.valueOf(redisTemplate.opsForHash().get("artical:" + articalId, "img")));
                        builder.votes(LongUtil.parse(redisTemplate.opsForHash().get("artical:" + articalId, "votes")));
                        builder.link(String.valueOf(redisTemplate.opsForHash().get("artical:" + articalId, "link")));
                        builder.time(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(
                                LongUtil.parse(redisTemplate.opsForHash().get("artical:" + articalId, "time"))
                        )));
                        builder.title(String.valueOf(redisTemplate.opsForHash().get("artical:" + articalId, "title")));
                        builder.userId(LongUtil.parse(redisTemplate.opsForHash().get("artical:" + articalId, "userId")));
                        ArticalRecommand articalRecommand = builder
                                .build();
                        recommandList.add(articalRecommand);
                    }
                }
        );
        return recommandList;
    }


}
