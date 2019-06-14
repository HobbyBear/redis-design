package com.example.redisdesign.service;

import com.example.redisdesign.entity.ArticalRecommandVo;
import com.example.redisdesign.utils.LongUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
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
    private HashOperations<String,String,String> hashOperations;

    @Autowired
    private ValueOperations<String,String> valueOperations;

    @Autowired
    private ZSetOperations<String,String> zSetOperations;

    @Autowired
    private SetOperations<String,String> setOperations;

    /**
     * 发布一篇新文章，这里省略文章内容之类的代码，直接考虑的是推荐文章列表部分。
     *
     * @param title
     * @param link
     * @param userId
     */
    public void putArtical(String title, String link, Long userId, String img) {
        Long articalId = valueOperations.increment("artical:");
        long time = System.currentTimeMillis();
        hashOperations.put("artical:" + articalId, "link", link);
        hashOperations.put("artical:" + articalId, "userId", String.valueOf(userId));
        hashOperations.put("artical:" + articalId, "img", img);
        hashOperations.put("artical:" + articalId, "title", title);
        hashOperations.put("artical:" + articalId, "time", String.valueOf(time));
        hashOperations.put("artical:" + articalId, "votes", "0");
        //时间排序
        zSetOperations.add("time:", String.valueOf(articalId), time);
        //投票数排序,最开始投票为0
        zSetOperations.add("score:", String.valueOf(articalId), time);
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
        Long res = setOperations.add("votes:" + articalId, String.valueOf(userId));
        if (res != null && res == 1) {
            //文章票数加1
            hashOperations.increment("artical:" + articalId, "votes", 1);
            //投票排序改变
           zSetOperations.incrementScore("score:", String.valueOf(articalId), 1d);
        }

    }

    /**
     * 根据搜索条件返回推荐列表
     *
     * @param page
     * @param searchItem time ,score 排序条件,默认为score排序
     * @param size
     */
    public List<ArticalRecommandVo> getArticalList(int page, String searchItem, int size) {
        int start = (page - 1) * size;
        int end = start + size - 1;
        Set<String> articals;
        if (searchItem != null && searchItem.equals("time")) {
            articals = zSetOperations.reverseRange("time:", start, end);
        } else {
            articals = zSetOperations.reverseRange("score:", start, end);
        }
        List<ArticalRecommandVo> recommandList = new ArrayList<>();
        Optional.ofNullable(articals).ifPresent(articalIds -> {
                    for (String articalId : articalIds) {
                        ArticalRecommandVo articalRecommand = new ArticalRecommandVo();
                        articalRecommand.setId(Long.valueOf(articalId));
                        articalRecommand.setImg(String.valueOf(hashOperations.get("artical:" + articalId, "img")));
                        articalRecommand.setVotes(LongUtil.parse(hashOperations.get("artical:" + articalId, "votes")));
                        articalRecommand.setLink(String.valueOf(hashOperations.get("artical:" + articalId, "link")));
                        articalRecommand.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(
                                LongUtil.parse(hashOperations.get("artical:" + articalId, "time"))
                        )));
                        articalRecommand.setTitle(String.valueOf(hashOperations.get("artical:" + articalId, "title")));
                        articalRecommand.setUserId(LongUtil.parse(hashOperations.get("artical:" + articalId, "userId")));
                        recommandList.add(articalRecommand);
                    }
                }
        );
        return recommandList;
    }


}
