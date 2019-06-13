package com.example.redisdesign.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 推荐文章实体类
 * @author xch
 * @since 2019/6/13 13:55
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArticalRecommandVo {

    /**
     * 文章id
     */
    private Long id;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 封面图
     */
    private String img;

    /**
     * 投票数
     */
    private Long votes;

    /**
     * 时间
     */
    private String time;

    /**
     * 文章链接
     */
    private String link;
    /**
     * 文章作者id
     */
    private Long userId;

}
