package com.renoside.schoolresell.repository;

import com.renoside.schoolresell.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {

    /**
     * 查询是否存在此用户
     *
     * @param loginName 提供登录账号
     * @return 返回是否存在
     */
    boolean existsByLoginName(String loginName);

    /**
     * 根据用户登录账号查询用户实体
     *
     * @param loginName 提供登录账号
     * @return 返回用户实体
     */
    User findByLoginName(String loginName);
}
