package com.renoside.schoolresell.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.renoside.schoolresell.entity.EaseMob;
import com.renoside.schoolresell.entity.User;
import com.renoside.schoolresell.exception.UnauthorizedException;
import com.renoside.schoolresell.repository.EaseMobRepository;
import com.renoside.schoolresell.repository.UserRepository;

import okhttp3.*;
import okhttp3.RequestBody;
import org.apache.tomcat.util.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EaseMobRepository easeMobRepository;

    long lastTime = System.currentTimeMillis();
    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 用户登录/注册
     *
     * @param loginName     提供用户登录账号
     * @param loginPassword 提供用户登录密码
     * @return 返回用户ID和令牌或者返回登陆信息
     */
    @PostMapping("/login")
    public String updateUserInfo(@RequestParam("loginName") String loginName,
                                 @RequestParam("loginPassword") String loginPassword) {
        logger.info("loginName=" + loginName + "  loginPassword=" + loginPassword + "的用户请求登录");
        JSONObject jsonObject = new JSONObject();
        if (!userRepository.existsByLoginName(loginName)) {
            /**
             * 新用户登录
             */
            User login = new User();
            login.setUserId(createId());
            login.setToken(createUUID());
            login.setLoginName(loginName);
            login.setLoginPassword(loginPassword);
            User result = userRepository.save(login);
            jsonObject.put("userId", result.getUserId());
            jsonObject.put("token", result.getToken());
            jsonObject.put("easemob_token", getEaseMobToken(easeMobRepository));
            jsonObject.put("message", "新用户注册");
            return jsonObject.toJSONString();
        } else if (userRepository.existsByLoginName(loginName) &&
                userRepository.findByLoginName(loginName).getLoginPassword().equals(loginPassword)) {
            /**
             * 老用户登录
             */
            User result = userRepository.findByLoginName(loginName);
            jsonObject.put("userId", result.getUserId());
            jsonObject.put("token", result.getToken());
            jsonObject.put("message", "老用户登录");
            jsonObject.put("easemob_token", getEaseMobToken(easeMobRepository));
            return jsonObject.toJSONString();
        } else {
            /**
             * 登陆出错
             */
            throw new UnauthorizedException();
        }
    }

    /**
     * 查询用户信息
     *
     * @param token  提供令牌
     * @param userId 提供用户ID
     * @return 返回用户详细信息
     */
    @GetMapping("/user/{userId}")
    public String getUserInfo(@RequestHeader("token") String token,
                              @PathVariable("userId") String userId) {
        logger.info("查询" + "userId=" + userId + "的用户的详细信息");
        User result = userRepository.findById(userId).get();
        JSONObject jsonObject = new JSONObject();
        if (result.getToken().equals(token)) {
            jsonObject.put("userId", result.getUserId());
            jsonObject.put("userImg", result.getUserImg());
            jsonObject.put("userName", result.getUserName());
            jsonObject.put("userDescription", result.getUserDescription());
            jsonObject.put("userPhone", result.getUserPhone());
            jsonObject.put("userAddress", result.getUserAddress());
            return jsonObject.toJSONString();
        } else {
            throw new UnauthorizedException();
        }
    }

    /**
     * 修改除用户ID、令牌、头像、用户登录账号和用户登陆密码以外的用户信息
     *
     * @param token  提供令牌
     * @param userId 提供用户ID
     * @param user   提供用户对象
     * @return 返回用户操作信息
     */
    @PutMapping("/user/{userId}")
    public String updateUserInfo(@RequestHeader("token") String token,
                                 @PathVariable("userId") String userId,
                                 User user) {
        logger.info("更新" + "userId=" + userId + "的用户的基本信息");
        User userInfo = userRepository.findById(userId).get();
        JSONObject jsonObject = new JSONObject();
        if (userInfo.getToken().equals(token)) {
            user.setUserId(userInfo.getUserId());
            user.setToken(userInfo.getToken());
            user.setUserImg(userInfo.getUserImg());
            user.setLoginName(userInfo.getLoginName());
            user.setLoginPassword(userInfo.getLoginPassword());
            User result = userRepository.save(user);
            jsonObject.put("userId", result.getUserId());
            jsonObject.put("operation", "更新用户数据");
            return jsonObject.toJSONString();
        } else {
            throw new UnauthorizedException();
        }
    }

    /**
     * 更新用户头像
     *
     * @param token   提供令牌
     * @param userId  提供用户ID
     * @param userImg 提供新头像的URL
     * @return 返回用户操作信息
     */
    @PutMapping("/user/{userId}/img")
    public String updateUserImg(@RequestHeader("token") String token,
                                @PathVariable("userId") String userId,
                                @RequestParam("userImg") String userImg) {
        logger.info("更新" + "userId=" + userId + "的用户的头像");
        User userInfo = userRepository.findById(userId).get();
        JSONObject jsonObject = new JSONObject();
        if (userInfo.getToken().equals(token)) {
            userInfo.setUserImg(userImg);
            User result = userRepository.save(userInfo);
            jsonObject.put("userId", result.getUserId());
            jsonObject.put("operation", "更新用户头像");
            return jsonObject.toJSONString();
        } else {
            throw new UnauthorizedException();
        }
    }

    /**
     * 修改用户登录账号
     *
     * @param token     提供令牌
     * @param userId    提供用户ID
     * @param loginName 提供新的用户登录账号
     * @return 返回用户操作信息
     */
    @PutMapping("/user/{userId}/loginName")
    public String updateUserLoginName(@RequestHeader("token") String token,
                                      @PathVariable("userId") String userId,
                                      @RequestParam("loginName") String loginName) {
        logger.info("更新" + "userId=" + userId + "的用户的登陆账号");
        User userInfo = userRepository.findById(userId).get();
        JSONObject jsonObject = new JSONObject();
        if (userRepository.existsByLoginName(loginName)) {
            jsonObject.put("error", "该用户已存在");
            return jsonObject.toJSONString();
        }
        if (userInfo.getToken().equals(token) && !userRepository.existsByLoginName(loginName)) {
            userInfo.setLoginName(loginName);
            User result = userRepository.save(userInfo);
            jsonObject.put("userId", result.getUserId());
            jsonObject.put("operation", "更新用户登录账号");
            return jsonObject.toJSONString();
        } else {
            throw new UnauthorizedException();
        }
    }

    /**
     * 修改用户登陆密码
     *
     * @param token         提供令牌
     * @param userId        提供用户ID
     * @param loginPassword 提供新的用户登录密码
     * @return 返回用户操作信息
     */
    @PutMapping("/user/{userId}/loginPassword")
    public String updateUserLoginPassword(@RequestHeader("token") String token,
                                          @PathVariable("userId") String userId,
                                          @RequestParam("loginPassword") String loginPassword) {
        logger.info("更新" + "userId=" + userId + "的用户的登陆密码");
        User userInfo = userRepository.findById(userId).get();
        JSONObject jsonObject = new JSONObject();
        if (userInfo.getToken().equals(token)) {
            userInfo.setLoginPassword(loginPassword);
            User result = userRepository.save(userInfo);
            jsonObject.put("userId", result.getUserId());
            jsonObject.put("operation", "更新用户登录密码");
            return jsonObject.toJSONString();
        } else {
            throw new UnauthorizedException();
        }
    }

    /**
     * 永久注销用户
     *
     * @param token  提供令牌
     * @param userId 提供用户ID
     * @return 返回用户操作信息
     */
    @DeleteMapping("/user/{userId}")
    public String deleteUser(@RequestHeader("token") String token,
                             @PathVariable("userId") String userId) {
        logger.info("删除" + "userId=" + userId + "的用户");
        User userInfo = userRepository.findById(userId).get();
        JSONObject jsonObject = new JSONObject();
        if (userInfo.getToken().equals(token)) {
            jsonObject.put("userId", userId);
            userRepository.deleteById(userId);
            jsonObject.put("operation", "删除用户");
            return jsonObject.toJSONString();
        } else {
            throw new UnauthorizedException();
        }
    }

    /**
     * 创建随机ID
     *
     * @return 生成用户ID
     */
    public static String createId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 15);
    }

    /**
     * 使用UUID创建token
     *
     * @return 生成token
     */
    public static String createUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * 认证用户是否合法
     *
     * @param userId 提供用户ID
     * @param token  提供令牌
     * @return 返回是否合法
     */
    public static boolean checkUser(UserRepository userRepository, String userId, String token) {
        User userInfo = userRepository.findById(userId).get();
        if (userInfo.getToken().equals(token)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取环信Api的Token
     *
     * @param easeMobRepository
     * @return
     */
    public String getEaseMobToken(EaseMobRepository easeMobRepository) {
        logger.info("easemob: " + "上一次请求环信token时间: " + lastTime);
        long nowTime = System.currentTimeMillis();
        logger.info("easemob: " + "本次请求token时间: " + nowTime);
        long intervalTime = nowTime - lastTime;
        logger.info("easemob: " + "请求间隔（秒）: " + intervalTime / 1000);
        if ((intervalTime / 1000 / 3600 / 24) >= 20) {
            easeMobRepository.deleteAll();
            lastTime = nowTime;
        }
        List<EaseMob> easeMob = easeMobRepository.findAll();
        if (easeMob == null || easeMob.size() == 0) {
            /**
             * 数据库没有token，发送请求获取token
             */
            logger.info("easemob: " + "向环信请求token");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("grant_type", "client_credentials");
            jsonObject.put("client_id", "YXA6hHqwzWVlQbmIXg-VH68v0Q");
            jsonObject.put("client_secret", "YXA62na7-kJ4SkBtTITfAkbhXf0C0t0");
            MediaType Json = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(Json, jsonObject.toJSONString());
            Request request = new Request.Builder()
                    .url("http://a1.easemob.com/1100191016019699/renoside/token")
                    .addHeader("Content-Type", "application/json")
                    .post(requestBody)
                    .build();
            OkHttpClient okHttpClient = new OkHttpClient();
            Response response = null;
            try {
                response = okHttpClient.newCall(request).execute();
                logger.info("easemob: " + response.code());
                String easeMobResult = response.body().string();
                logger.info("easemob: " + easeMobResult);
                JSONObject result = JSON.parseObject(easeMobResult);
                EaseMob saveDate = new EaseMob();
                saveDate.setApplication(result.getString("application"));
                saveDate.setAccessToken(result.getString("access_token"));
                saveDate.setExpiresIn("expires_in");
                easeMobRepository.save(saveDate);
                return result.getString("access_token");
            } catch (IOException e) {
                e.printStackTrace();
                return "";
            } finally {
                if (response != null)
                    response.close();
            }
        } else {
            /**
             * 数据库中有token，直接返回token
             */
            logger.info("easemob: " + "请求已有的环信token");
            return easeMob.get(0).getAccessToken();
        }
    }
}
