package com.cytyk.wordserver.db.mapper;

import com.cytyk.wordserver.db.entity.UserDO;

/**
 * @author yuankai
 * @date 2021/9/15 11:39
 */
public interface UserMapper {

    /**
     * 新增用户
     *
     * @param userDO 用户
     * @return 1
     */
    int add(UserDO userDO);

    /**
     * 修改用户
     *
     * @param userDo 用户
     * @return 1
     */
    int update(UserDO userDo);

    /**
     * 查询用户
     *
     * @param id id
     * @return 用户
     */
    UserDO get(Integer id);
}
