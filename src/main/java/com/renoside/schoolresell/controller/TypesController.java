package com.renoside.schoolresell.controller;

import com.alibaba.fastjson.JSONObject;
import com.renoside.schoolresell.entity.Goods;
import com.renoside.schoolresell.entity.GoodsImgs;
import com.renoside.schoolresell.entity.GoodsType;
import com.renoside.schoolresell.repository.GoodsImgsRepository;
import com.renoside.schoolresell.repository.GoodsRepository;
import com.renoside.schoolresell.repository.GoodsTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class TypesController {

    @Autowired
    private GoodsRepository goodsRepository;
    @Autowired
    private GoodsImgsRepository goodsImgsRepository;
    @Autowired
    private GoodsTypeRepository goodsTypeRepository;

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 插入一条规定类型的商品
     *
     * @param goodsId
     * @param goodsType
     */
    public void insertGoodsToTypes(String goodsId, int goodsType) {
        GoodsType oneType = new GoodsType();
        oneType.setTypeId(UserController.createId());
        oneType.setGoodsId(goodsId);
        oneType.setGoodsType(goodsType);
        logger.info("oneType======================"+oneType.toString());
        goodsTypeRepository.save(oneType);
    }

    /**
     * 根据goodsId删除一条类型数据
     *
     * @param goodsId
     */
    public void deleteGoodsFromTypes(String goodsId) {
        goodsTypeRepository.deleteByGoodsId(goodsId);
    }

    /**
     * 更新商品类型
     *
     * @param goodsId
     * @param newType
     */
    public void updateGoodsToTypes(String goodsId, int newType) {
        GoodsType existType = goodsTypeRepository.findByGoodsId(goodsId);
        existType.setGoodsType(newType);
        goodsTypeRepository.save(existType);
    }

    /**
     * 返回所有指定类型的商品
     * @param goodsType
     * @return
     */
    @GetMapping("/types/{goodsType}")
    public String searchGoodsFromTypes(@PathVariable("goodsType")String goodsType) {
        List<GoodsType> goodsTypeList = goodsTypeRepository.findByGoodsType(Integer.parseInt(goodsType));
        List<Goods> goodsList = new ArrayList<>();
        for (int i = 0; i < goodsTypeList.size(); i++) {
            Goods goods = goodsRepository.findById(goodsTypeList.get(i).getGoodsId()).get();
            goodsList.add(goods);
        }
        Map[] goodsArray = new Map[goodsList.size()];
        for (int i = 0; i < goodsList.size(); i++) {
            Map<String, Object> goodsMap = new HashMap<>();
            goodsMap.put("goodsId", goodsList.get(i).getGoodsId());
            goodsMap.put("sellerId", goodsList.get(i).getUserId());
            List<GoodsImgs> goodsImgsList = goodsImgsRepository.findByGoodsId(goodsList.get(i).getGoodsId());
            Map[] imgsArray = new Map[goodsImgsList.size()];
            for (int j = 0; j < goodsImgsList.size(); j++) {
                Map<String, Object> imgsMap = new HashMap<>();
                imgsMap.put("goodsImg", goodsImgsList.get(j).getGoodsImg());
                imgsArray[j] = imgsMap;
            }
            goodsMap.put("goodsImgs", imgsArray);
            goodsMap.put("goodsName", goodsList.get(i).getGoodsName());
            goodsMap.put("goodsDescription", goodsList.get(i).getGoodsDescription());
            goodsMap.put("goodsPrice", goodsList.get(i).getGoodsPrice());
            goodsMap.put("goodsLikes", goodsList.get(i).getGoodsLikes());
            goodsMap.put("goodsStatus", goodsList.get(i).getGoodsStatus());
            goodsArray[i] = goodsMap;
        }
        Map<String, Object> resultList = new HashMap<>();
        resultList.put("goods", goodsArray);
        System.out.println(resultList.toString());
        JSONObject jsonObject = new JSONObject(resultList);
        return jsonObject.toJSONString();
    }
}
