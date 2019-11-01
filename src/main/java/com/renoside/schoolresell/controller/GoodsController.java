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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class GoodsController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GoodsRepository goodsRepository;
    @Autowired
    private GoodsImgsRepository goodsImgsRepository;
    @Autowired
    private TypesController typesController;

    /**
     * 上架商品
     *
     * @param token     提供令牌
     * @param userId    提供用户ID
     * @param goodsImgs 提供商品图片
     * @param goods     提供商品实体
     * @return 返回上架信息
     */
    @PostMapping("/goods/{userId}")
    public String upShelf(@RequestHeader("token") String token,
                          @PathVariable("userId") String userId,
                          @RequestParam("goodsImgs") String goodsImgs,
                          @RequestParam("goodsType") String goodsType,
                          Goods goods) {
        if (UserController.checkUser(userRepository, userId, token)) {
            goods.setGoodsId(UserController.createId());
            goods.setUserId(userId);
            goods.setGoodsStatus(Goods.STATUS_SELLING);
            Goods result = goodsRepository.save(goods);
            typesController.insertGoodsToTypes(result.getGoodsId(), Integer.parseInt(goodsType));
            String[] imgsArray = goodsImgs.split("\\,");
            for (int i = 0; i < imgsArray.length; i++) {
                GoodsImgs imgSave = new GoodsImgs();
                imgSave.setGoodsId(result.getGoodsId());
                imgSave.setGoodsImg(imgsArray[i]);
                goodsImgsRepository.save(imgSave);
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("goodsId", result.getGoodsId());
            jsonObject.put("userId", result.getUserId());
            return jsonObject.toJSONString();
        } else {
            throw new UnauthorizedException();
        }
    }

    /**
     * 获取商品列表
     *
     * @return 返回所有在售商品
     */
    @GetMapping("/goods")
    public String getAllGoods() {
        List<Goods> goodsList = goodsRepository.findByGoodsStatus(Goods.STATUS_SELLING);
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

    /**
     * 根据关键字对商品进行模糊查询
     *
     * @param keyCode
     * @return
     */
    @GetMapping("/goods/{keyCode}/searchGoodsByKey")
    public String getGoodsByKey(@PathVariable("keyCode") String keyCode) {
        List<Goods> goodsList = goodsRepository.findGoodsByGoodsNameLike("%" + keyCode + "%");
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

    /**
     * 获取商品详情
     *
     * @param goodsId 提供商品ID
     * @return 返回商品详细信息
     */
    @GetMapping("/goods/{goodsId}")
    public String getGoodsInfo(@PathVariable("goodsId") String goodsId) {
        Goods goodsInfo = goodsRepository.findById(goodsId).get();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("goodsId", goodsInfo.getGoodsId());
        jsonObject.put("sellerId", goodsInfo.getUserId());
        List<GoodsImgs> goodsImgsList = goodsImgsRepository.findByGoodsId(goodsId);
        Map[] imgsArray = new Map[goodsImgsList.size()];
        for (int i = 0; i < goodsImgsList.size(); i++) {
            Map<String, Object> imgsMap = new HashMap<>();
            imgsMap.put("goodsImg", goodsImgsList.get(i).getGoodsImg());
            imgsArray[i] = imgsMap;
        }
        jsonObject.put("goodsImgs", imgsArray);
        jsonObject.put("goodsName", goodsInfo.getGoodsName());
        jsonObject.put("goodsDescription", goodsInfo.getGoodsDescription());
        jsonObject.put("goodsPrice", goodsInfo.getGoodsPrice());
        jsonObject.put("goodsLikes", goodsInfo.getGoodsLikes());
        jsonObject.put("goodsPhone", goodsInfo.getGoodsPhone());
        jsonObject.put("goodsAddress", goodsInfo.getGoodsAddress());
        return jsonObject.toJSONString();
    }

    /**
     * 查看用户自己上架商品
     *
     * @param token  提供令牌
     * @param userId 提供用户ID
     * @return 返回上架商品
     */
    @GetMapping("/shelves/{userId}")
    public String getPutGoods(@RequestHeader("token") String token,
                              @PathVariable("userId") String userId) {
        if (UserController.checkUser(userRepository, userId, token)) {
            List<Goods> goodsList = goodsRepository.findByUserId(userId);
            Map[] goodsArray = new Map[goodsList.size()];
            for (int i = 0; i < goodsList.size(); i++) {
                Map<String, Object> goodsMap = new HashMap<>();
                goodsMap.put("goodsId", goodsList.get(i).getGoodsId());
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
            JSONObject jsonObject = new JSONObject(resultList);
            return jsonObject.toJSONString();
        } else {
            throw new UnauthorizedException();
        }
    }

    /**
     * 修改商品信息
     *
     * @param token     提供令牌
     * @param goodsId   提供商品ID
     * @param goodsImgs 提供商品图片地址
     * @param goods     提供商品实体
     * @return 返回商品ID和操作信息
     */
    @PutMapping("/goods/{goodsId}")
    public String updateGoodsInfo(@RequestHeader("token") String token,
                                  @PathVariable("goodsId") String goodsId,
                                  @RequestParam("goodsImgs") String goodsImgs,
                                  @RequestParam("goodsType") String goodsType,
                                  Goods goods) {
        Goods goodsInfo = goodsRepository.findById(goodsId).get();
        if (UserController.checkUser(userRepository, goodsInfo.getUserId(), token)) {
            if (goods.getGoodsName() != null && !goods.getGoodsName().equals(""))
                goodsInfo.setGoodsName(goods.getGoodsName());
            if (goods.getGoodsDescription() != null && !goods.getGoodsDescription().equals(""))
                goodsInfo.setGoodsDescription(goods.getGoodsDescription());
            if (goods.getGoodsPrice() != null && !goods.getGoodsPrice().equals(""))
                goodsInfo.setGoodsPrice(goods.getGoodsPrice());
            Goods result = goodsRepository.save(goodsInfo);
            typesController.updateGoodsToTypes(result.getGoodsId(), Integer.parseInt(goodsType));
            if (!goodsImgs.isEmpty()) {
                String[] imgsArray = goodsImgs.split("\\,");
                List<GoodsImgs> goodsImgsList = goodsImgsRepository.findByGoodsId(result.getGoodsId());
                for (int i = 0; i < goodsImgsList.size(); i++) {
                    goodsImgsRepository.deleteById(goodsImgsList.get(i).getPriKey());
                }
                for (int i = 0; i < imgsArray.length; i++) {
                    GoodsImgs imgSave = new GoodsImgs();
                    imgSave.setGoodsId(result.getGoodsId());
                    imgSave.setGoodsImg(imgsArray[i]);
                    goodsImgsRepository.save(imgSave);
                }
            } else {
                List<GoodsImgs> goodsImgsList = goodsImgsRepository.findByGoodsId(result.getGoodsId());
                for (int i = 0; i < goodsImgsList.size(); i++) {
                    goodsImgsRepository.deleteById(goodsImgsList.get(i).getPriKey());
                }
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("goodsId", result.getGoodsId());
            jsonObject.put("operation", "更新商品信息成功");
            return jsonObject.toJSONString();
        } else {
            throw new UnauthorizedException();
        }
    }

    /**
     * 根据商品ID下架商品
     *
     * @param goodsId 提供商品ID
     * @return 返回操作信息
     */
    @DeleteMapping("/goods/{goodsId}")
    public String deleteGoods(@RequestHeader("token") String token,
                              @PathVariable("goodsId") String goodsId) {
        if (UserController.checkUser(userRepository, goodsRepository.findById(goodsId).get().getUserId(), token)) {
            goodsRepository.deleteById(goodsId);
            typesController.deleteGoodsFromTypes(goodsId);
            List<GoodsImgs> goodsImgsList = goodsImgsRepository.findByGoodsId(goodsId);
            for (int i = 0; i < goodsImgsList.size(); i++) {
                goodsImgsRepository.deleteById(goodsImgsList.get(i).getPriKey());
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("goodsId", goodsId);
            jsonObject.put("operation", "成功下架商品");
            return jsonObject.toJSONString();
        } else {
            throw new UnauthorizedException();
        }
    }
}
