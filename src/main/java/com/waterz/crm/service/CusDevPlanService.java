package com.waterz.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.waterz.crm.base.BaseService;
import com.waterz.crm.dao.CusDevPlanMapper;
import com.waterz.crm.dao.SaleChanceMapper;
import com.waterz.crm.query.CusDevPlanQuery;
import com.waterz.crm.utils.AssertUtil;
import com.waterz.crm.vo.CusDevPlan;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class CusDevPlanService extends BaseService<CusDevPlan,Integer> {

    @Resource
    private CusDevPlanMapper cusDevPlanMapper;

    @Resource
    private SaleChanceMapper saleChanceMapper;

    /**
     * 多条件查询计划项列表  客户开发计划
     *
     * @param cusDevPlanQuery
     * @return
     */
    public Map<String, Object> queryCusDevPlansByParams(CusDevPlanQuery cusDevPlanQuery) {
        Map<String, Object> map = new HashMap<>();
        PageHelper.startPage(cusDevPlanQuery.getPage(), cusDevPlanQuery.getLimit());
        PageInfo<CusDevPlan> pageInfo = new PageInfo<>(selectByParams(cusDevPlanQuery));
        map.put("code", 0);
        map.put("msg", "success");
        map.put("count", pageInfo.getTotal());
        map.put("data", pageInfo.getList());

        return map;
    }

    /**
     * 添加计划项
     * 1. 参数校验
     * 营销机会ID ⾮空 记录必须存在
     * 计划项内容 ⾮空
     * 计划项时间 ⾮空
     * 2. 设置参数默认值
     * is_valid
     * crateDate
     * updateDate
     * 3. 执⾏添加，判断结果
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addCusDevPlan(CusDevPlan cusDevPlan){
        // 1. 参数校验
        checkCusDevPlanParams(cusDevPlan);

        //2. 设置参数默认值
        // 是否有效  默认有效
        cusDevPlan.setIsValid(1);
        // 创建时间
        cusDevPlan.setCreateDate(new Date());
        // 修改时间
        cusDevPlan.setUpdateDate(new Date());

        //3.执行更新操作  判断受影响的行数
        AssertUtil.isTrue(cusDevPlanMapper.insertSelective(cusDevPlan) != 1,"计划项数据添加失败！");
    }

    /**
     * 更新计划项
     * 1.参数校验
     *  id ⾮空 记录存在
     *  营销机会id ⾮空 记录必须存在
     *  计划项内容 ⾮空
     *  计划项时间 ⾮空
     * 2.参数默认值设置
     *  updateDate
     * 3.执⾏更新 判断结果
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateCusDevPlan(CusDevPlan cusDevPlan) {
        // 1. 参数校验
        AssertUtil.isTrue(null == cusDevPlan.getId() || cusDevPlanMapper.selectByPrimaryKey(cusDevPlan.getId()) == null,"数据异常，请重试！");
        checkCusDevPlanParams(cusDevPlan);

        // 2.设置默认值    修改时间
        cusDevPlan.setUpdateDate(new Date());

        //3.执行更新操作  判断受影响的行数
        AssertUtil.isTrue(cusDevPlanMapper.updateByPrimaryKeySelective(cusDevPlan) != 1,"计划项数据更新失败！");

    }


    /**
     * 参数校验
     */
    private void checkCusDevPlanParams(CusDevPlan cusDevPlan) {
        //营销机会ID ⾮空   数据必须存在
        Integer sId = cusDevPlan.getSaleChanceId();
        AssertUtil.isTrue(null == sId || saleChanceMapper.selectByPrimaryKey(sId) == null, "数据异常，请重试！");

        //计划项内容 ⾮空
        AssertUtil.isTrue(StringUtils.isBlank(cusDevPlan.getPlanItem()),"计划项内容不能为空！");

        //计划项时间 ⾮空
        AssertUtil.isTrue(null == cusDevPlan.getPlanDate(), "计划时间不能为空！");
    }

    /**
     * 删除计划项
     *
     * @param id
     */
    public void deleteCusDevPlan(Integer id) {
        //判断id是否为空，且数据存在
        AssertUtil.isTrue(null==id ,"待删除记录不存在!");
        // 通过ID查询计划项对象
        CusDevPlan cusDevPlan =selectByPrimaryKey(id);
        //设置无效记录
        cusDevPlan.setIsValid(0);
        cusDevPlan.setUpdateDate(new Date());
        // 执行更新操作
        AssertUtil.isTrue(cusDevPlanMapper.updateByPrimaryKeySelective(cusDevPlan) != 1,"计划项记录删除失败!");
    }
}
