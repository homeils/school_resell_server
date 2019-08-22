package com.renoside.schoolresell.repository;

import com.renoside.schoolresell.entity.Indent;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;

public interface IndentRepository extends JpaRepository<Indent, String> {

    List<Indent> findByBuyerId(String buyerId);

    @Transactional
    void deleteByGoodsId(String goodsId);
}
