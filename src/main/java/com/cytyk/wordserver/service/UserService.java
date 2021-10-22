package com.cytyk.wordserver.service;

import com.cytyk.wordserver.controller.vo.UserVO;
import com.cytyk.wordserver.db.entity.UserDO;
import com.cytyk.wordserver.db.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * insert user
 *
 * @author yuankai
 * @date 2020/10/28 9:34
 */
@Slf4j
@Service
public class UserService {

    @Resource
    private UserMapper userMapper;

    public Integer add(UserVO userVO) {
        checkUserInfo(userVO);

        UserDO userDo = new UserDO();
        BeanUtils.copyProperties(userVO, userDo);
        userMapper.add(userDo);

        return userDo.getId();
    }

    private void checkUserInfo(UserVO userVO) {

    }

    public Integer update(UserVO userVO) {
        checkUserInfo(userVO);

        UserDO userDo = new UserDO();
        BeanUtils.copyProperties(userVO, userDo);
        userMapper.update(userDo);
        return userDo.getId();
    }

    public UserVO get(Integer id) {
        UserDO userDO = userMapper.get(id);
        UserVO userVo = new UserVO();
        BeanUtils.copyProperties(userDO, userVo);
        return userVo;
    }
}
