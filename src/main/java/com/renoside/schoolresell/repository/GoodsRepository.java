package com.renoside.schoolresell.repository;

import com.renoside.schoolresell.entity.Goods;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GoodsRepository extends JpaRepository<Goods, String> {

    List<Goods> findByUserId(String userId);

    List<Goods> findByGoodsStatus(int goodsStatus);
}
