package com.waterz.crm.dao;

import com.waterz.crm.base.BaseMapper;
import com.waterz.crm.vo.Role;

import java.util.List;
import java.util.Map;

public interface RoleMapper extends BaseMapper<Role,Integer> {

    // 查询所有的角色列表 只需要id和roleName
    public List<Map<String,Object>> queryAllRoles(Integer userId);

    // 通过角色名称查询角色记录
   public Role selectByRoleName(String roleName);
}