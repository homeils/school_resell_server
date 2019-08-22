package com.renoside.schoolresell.controller;

import com.alibaba.fastjson.JSONObject;
import com.renoside.schoolresell.entity.Goods;
import com.renoside.schoolresell.entity.GoodsImgs;
import com.renoside.schoolresell.entity.Indent;
import com.renoside.schoolresell.exception.ForbiddenException;
import com.renoside.schoolresell.exception.UnauthorizedException;
import com.renoside.schoolresell.repository.GoodsImgsRepository;
import com.renoside.schoolresell.repository.GoodsRepository;
import com.renoside.schoolresell.repository.IndentRepository;

import com.renoside.schoolresell.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class IndentController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GoodsRepository goodsRepository;
    @Autowired
    private GoodsImgsRepository goodsImgsRepository;
    @Autowired
    private IndentRepository indentRepository;

    /**
     * 下单
     *
     * @param token   提供令牌
     * @param goodsId 提供商品ID
     * @param buyerId 提供购买用户ID
     * @return 返货买卖家及商品ID
     */
    @PostMapping("/order/{goodsId}")
    public String placeOrder(@RequestHeader("token") String token,
                             @PathVariable("goodsId") String goodsId,
                             @RequestParam("buyerId") String buyerId) {
        if (UserController.checkUser(userRepository, buyerId, token)) {
            Goods goodsInfo = goodsRepository.findById(goodsId).get();
            if (!buyerId.equals(goodsInfo.getUserId()) &&
                    goodsInfo.getGoodsStatus() != Goods.STATUS_PROCESSING &&
                    goodsInfo.getGoodsStatus() != Goods.STATUS_COMPLETED) {
                goodsInfo.setGoodsStatus(Goods.STATUS_PROCESSING);
                Indent indent = new Indent();
                indent.setGoodsId(goodsId);
                indent.setSellerId(goodsInfo.getUserId());
                indent.setBuyerId(buyerId);
                Indent result = indentRepository.save(indent);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("goodsId", result.getGoodsId());
                jsonObject.put("sellerId", result.getSellerId());
                jsonObject.put("buyerId", result.getBuyerId());
                return jsonObject.toJSONString();
            } else {
                throw new ForbiddenException();
            }
        } else {
            throw new UnauthorizedException();
        }
    }

    /**
     * 获取订单
     *
     * @param token   提供令牌
     * @param buyerId 提供用户ID
     * @return 返回订单信息
     */
    @GetMapping("/order/{buyerId}")
    public String getOrder(@RequestHeader("token") String token,
                           @PathVariable("buyerId") String buyerId) {
        if (UserController.checkUser(userRepository, buyerId, token)) {
            List<Indent> indentList = indentRepository.findByBuyerId(buyerId);
            Map[] goodsArray = new Map[indentList.size()];
            for (int i = 0; i < indentList.size(); i++) {
                Goods goodsInfo = goodsRepository.findById(indentList.get(i).getGoodsId()).get();
                Map<String, Object> goodsMap = new HashMap<>();
                goodsMap.put("goodsId", goodsInfo.getGoodsId());
                goodsMap.put("sellerId", goodsInfo.getUserId());
                List<GoodsImgs> goodsImgsList = goodsImgsRepository.findByGoodsId(goodsInfo.getGoodsId());
                Map[] imgsArray = new Map[goodsImgsList.size()];
                for (int j = 0; j < goodsImgsList.size(); j++) {
                    Map<String, Object> imgsMap = new HashMap<>();
                    imgsMap.put("goodsImg", goodsImgsList.get(j).getGoodsImg());
                    imgsArray[j] = imgsMap;
                }
                goodsMap.put("goodsImgs", imgsArray);
                goodsMap.put("goodsName", goodsInfo.getGoodsName());
                goodsMap.put("goodsDescription", goodsInfo.getGoodsDescription());
                goodsMap.put("goodsPrice", goodsInfo.getGoodsPrice());
                goodsMap.put("goodsLikes", goodsInfo.getGoodsLikes());
                goodsMap.put("goodsStatus", goodsInfo.getGoodsStatus());
                goodsMap.put("goodsPhone", goodsInfo.getGoodsPhone());
                goodsMap.put("goodsAddress", goodsInfo.getGoodsAddress());
                goodsArray[i] = goodsMap;
            }
            Map<String, Object> resultList = new HashMap<>();
            resultList.put("goods", goodsArray);
            JSONObject jsonObject = new JSONObject(resultList);
            return jsonObject.toJSONString();
        } else {
            throw new UnauthorizedException();
        }
    }

    /**
     * 确认收货
     *
     * @param token   提供令牌
     * @param goodsId 提供商品ID
     * @param buyerId 提供买家ID
     * @return 返回操作信息
     */
    @PutMapping("/order/{goodsId}")
    public String completeOrder(@RequestHeader("token") String token,
                                @PathVariable("goodsId") String goodsId,
                                @RequestParam("buyerId") String buyerId) {
        Goods goodsInfo = goodsRepository.findById(goodsId).get();
        if (UserController.checkUser(userRepository, buyerId, token) && goodsInfo.getGoodsStatus() == Goods.STATUS_PROCESSING) {
            goodsInfo.setGoodsStatus(Goods.STATUS_COMPLETED);
            Goods result = goodsRepository.save(goodsInfo);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("goodsId", result.getGoodsId());
            jsonObject.put("operation", "确认收货完成");
            return jsonObject.toJSONString();
        } else {
            throw new UnauthorizedException();
        }
    }

    /**
     * 取消订单
     *
     * @param token   令牌
     * @param goodsId 商品ID
     * @param buyerId 买家ID
     * @return 返回操作信息
     */
    @DeleteMapping("/order/{goodsId}")
    public String deleteOrder(@RequestHeader("token") String token,
                              @PathVariable("goodsId") String goodsId,
                              @RequestParam("buyerId") String buyerId) {
        if (UserController.checkUser(userRepository, buyerId, token)) {
            Goods goodsInfo = goodsRepository.findById(goodsId).get();
            goodsInfo.setGoodsStatus(Goods.STATUS_SELLING);
            Goods result = goodsRepository.save(goodsInfo);
            indentRepository.deleteByGoodsId(goodsId);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("goodsId", result.getGoodsId());
            jsonObject.put("operation", "取消订单完成");
            return jsonObject.toJSONString();
        } else {
            throw new UnauthorizedException();
        }
    }
}
