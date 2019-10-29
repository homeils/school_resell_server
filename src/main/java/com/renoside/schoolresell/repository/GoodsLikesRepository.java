package com.renoside.schoolresell.repository;

import com.renoside.schoolresell.entity.GoodsLikes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface GoodsLikesRepository extends JpaRepository<GoodsLikes, String> {

    List<GoodsLikes> findByUserId(String userId);

    GoodsLikes findByGoodsIdAndUserId(String goodsId, String userId);

    @Modifying
    @Query(value = "delete from goods_likes where goods_id = ?1 and user_id = ?2", nativeQuery = true)
    void deleteByGoodsIdAndUserId(String goodsId, String userId);
}
