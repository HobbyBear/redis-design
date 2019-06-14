package com.example.redisdesign.dao;

import com.example.redisdesign.entity.ArticalRecommandVo;
import org.springframework.data.repository.CrudRepository;

/**
 * @author xch
 * @since 2019/6/14 8:29
 **/
public interface RecommandArticalDao extends CrudRepository<ArticalRecommandVo,Long> {
}
