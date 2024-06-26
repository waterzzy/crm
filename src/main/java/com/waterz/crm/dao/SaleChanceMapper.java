package com.waterz.crm.dao;

import com.waterz.crm.base.BaseMapper;
import com.waterz.crm.vo.SaleChance;

public interface SaleChanceMapper extends BaseMapper<SaleChance,Integer> {

    // 多条件查询的接口不需要单独定义（由于多个模块涉及到多条件查询，所以将对应的多条件查询定义在父类中）

}