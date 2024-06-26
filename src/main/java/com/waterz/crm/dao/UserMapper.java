package com.waterz.crm.dao;

import com.waterz.crm.base.BaseMapper;
import com.waterz.crm.vo.User;

import java.util.List;
import java.util.Map;

public interface UserMapper extends BaseMapper<User,Integer> {
    // 根据⽤户名查询⽤户对象
    User queryUserByName(String userName);

    List<Map<String,Object>> queryAllSales();
}