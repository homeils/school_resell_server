package com.renoside.schoolresell.controller;

import com.alibaba.fastjson.JSONObject;
import com.renoside.schoolresell.entity.Goods;
import com.renoside.schoolresell.entity.GoodsImgs;
import com.renoside.schoolresell.entity.GoodsLikes;
import com.renoside.schoolresell.exception.ForbiddenException;
import com.renoside.schoolresell.exception.UnauthorizedException;
import com.renoside.schoolresell.repository.GoodsImgsRepository;
import com.renoside.schoolresell.repository.GoodsLikesRepository;
import com.renoside.schoolresell.repository.GoodsRepository;
import com.renoside.schoolresell.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class LikesController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GoodsRepository goodsRepository;
    @Autowired
    private GoodsImgsRepository goodsImgsRepository;
    @Autowired
    private GoodsLikesRepository goodsLikesRepository;

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 根据goodsId收藏商品
     *
     * @param token
     * @param goodsId
     * @param userId
     * @return
     */
    @PostMapping("/likes/{goodsId}")
    public String addGoodsLikes(@RequestHeader("token") String token,
                                @PathVariable("goodsId") String goodsId,
                                @RequestParam("userId") String userId) {
        Goods goods = goodsRepository.findById(goodsId).get();
        if (UserController.checkUser(userRepository, userId, token)) {
            if (null != goods && goodsLikesRepository.findByGoodsIdAndUserId(goodsId, userId) == null) {
                goods.setGoodsLikes(goods.getGoodsLikes() + 1);
                Goods result = goodsRepository.save(goods);
                if (null != result) {
                    GoodsLikes goodsLikes = new GoodsLikes();
                    goodsLikes.setLikesId(UserController.createId());
                    goodsLikes.setUserId(userId);
                    goodsLikes.setGoodsId(goodsId);
                    goodsLikesRepository.save(goodsLikes);
                }
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("goodsId", result.getGoodsId());
                jsonObject.put("operation", "收藏商品成功");
                return jsonObject.toJSONString();
            } else {
                throw new ForbiddenException();
            }
        } else {
            throw new UnauthorizedException();
        }
    }

    /**
     * 根据goodsId和userId取消收藏商品
     *
     * @param token
     * @param goodsId
     * @param userId
     * @return
     */
    @DeleteMapping("/likes/{goodsId}")
    public String deleteGoodsLikes(@RequestHeader("token") String token,
                                   @PathVariable("goodsId") String goodsId,
                                   @RequestParam("userId") String userId) {
        Goods goods = goodsRepository.findById(goodsId).get();
        if (UserController.checkUser(userRepository, userId, token)) {
            if (null != goods) {
                if (goodsLikesRepository.findByGoodsIdAndUserId(goodsId, userId) != null) {
                    goods.setGoodsLikes(goods.getGoodsLikes() - 1);
                    goodsRepository.save(goods);
                    goodsLikesRepository.deleteByGoodsIdAndUserId(goodsId, userId);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("operation", "取消收藏商品成功");
                    return jsonObject.toJSONString();
                } else {
                    throw new ForbiddenException();
                }
            } else {
                throw new ForbiddenException();
            }
        } else {
            throw new UnauthorizedException();
        }
    }

    /**
     * 查询用户的所有收藏商品
     *
     * @param userId
     * @return
     */
    @GetMapping("/likes/{userId}")
    public String getAllLikesByUerId(@PathVariable("userId") String userId) {
        List<GoodsLikes> goodsLikesList = goodsLikesRepository.findByUserId(userId);
        List<Goods> goodsList = new ArrayList<>();
        for (int i = 0; i < goodsLikesList.size(); i++) {
            Goods goods = goodsRepository.findById(goodsLikesList.get(i).getGoodsId()).get();
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
