package com.renoside.schoolresell.repository;

import com.renoside.schoolresell.entity.GoodsType;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;

public interface GoodsTypeRepository extends JpaRepository<GoodsType, String> {

    @Transactional
    void deleteByGoodsId(String goodsId);

    GoodsType findByGoodsId(String goodsId);

    List<GoodsType> findByGoodsType(int goodsType);
}
