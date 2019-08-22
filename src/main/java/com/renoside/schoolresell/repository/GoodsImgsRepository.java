package com.renoside.schoolresell.repository;

import com.renoside.schoolresell.entity.GoodsImgs;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GoodsImgsRepository extends JpaRepository<GoodsImgs, String> {

    List<GoodsImgs> findByGoodsId(String goodsId);
}
