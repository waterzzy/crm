package com.waterz.crm.controller;

import com.waterz.crm.base.BaseController;
import com.waterz.crm.base.ResultInfo;
import com.waterz.crm.query.CusDevPlanQuery;
import com.waterz.crm.service.CusDevPlanService;
import com.waterz.crm.service.SaleChanceService;
import com.waterz.crm.vo.CusDevPlan;
import com.waterz.crm.vo.SaleChance;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RequestMapping("cus_dev_plan")
@Controller
public class CusDevPlanController extends BaseController {

    @Resource
    private SaleChanceService saleChanceService;

    @Resource
    private CusDevPlanService cusDevPlanService;

    /*
    * 进入客服开发计划页面
    * */
    @RequestMapping("index")
    public String index(){
        return "cusDevPlan/cus_dev_plan";
    }

    /*
    * 打开计划项开发与详情页面
    *
    * */
    @RequestMapping("toCusDevPlanPage")
    public String toCusDevPlanPage(Integer sid, HttpServletRequest request){
        //通过id查询营销机会对象
        SaleChance saleChance = saleChanceService.selectByPrimaryKey(sid);
        //将对象设置到请求域中
        request.setAttribute("saleChance",saleChance);

        return "cusDevPlan/cus_dev_plan_data";

    }

    /**
     * 客户开发计划项数据列表
     * @param cusDevPlanQuery
     * @return
     */
    @RequestMapping("list")
    @ResponseBody
    public Map<String, Object> queryCusDevPlanByParams (CusDevPlanQuery cusDevPlanQuery) {
        return cusDevPlanService.queryCusDevPlansByParams(cusDevPlanQuery);
    }

    /**
     * 添加计划项
     * @param cusDevPlan
     * @return
     */
    @PostMapping("add")
    @ResponseBody
    public ResultInfo addCusDevPlan(CusDevPlan cusDevPlan){
        cusDevPlanService.addCusDevPlan(cusDevPlan);
        return success("计划项添加成功！");
    }

    /**
     * 更新计划项
     * @param cusDevPlan
     * @return
     */
    @PostMapping("update")
    @ResponseBody
    public ResultInfo updateCusDevPlan(CusDevPlan cusDevPlan){
        cusDevPlanService.updateCusDevPlan(cusDevPlan);
        return success("计划项更新成功！");
    }

    /**
     * 删除计划项
     * @param id
     * @return
     */
    @PostMapping("delete")
    @ResponseBody
    public ResultInfo deleteCusDevPlan(Integer id){
        cusDevPlanService.deleteCusDevPlan(id);
        return success("计划项更新成功！");
    }

    /**
     * 进入添加或修改计划项
     * @return
     */
    @RequestMapping("toAddOrUpdateCusDevPlanPage")
    public String toAddOrUpdateCusDevPlanPage(HttpServletRequest request,Integer sId,Integer id){
        // 将营销机会ID设置到请求
        request.setAttribute("sId",sId);
        // 通过计划项Id查询记录
        CusDevPlan cusDevPlan = cusDevPlanService.selectByPrimaryKey(id);

        request.setAttribute("cusDevPlan",cusDevPlan);
        return "cusDevPlan/add_update";
    }




}
