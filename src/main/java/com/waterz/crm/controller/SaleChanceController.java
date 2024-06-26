package com.waterz.crm.controller;

import com.waterz.crm.annoation.RequiredPermission;
import com.waterz.crm.base.BaseController;
import com.waterz.crm.base.ResultInfo;
import com.waterz.crm.enums.StateStatus;
import com.waterz.crm.query.SaleChanceQuery;
import com.waterz.crm.service.SaleChanceService;
import com.waterz.crm.utils.CookieUtil;
import com.waterz.crm.utils.LoginUserUtil;
import com.waterz.crm.vo.SaleChance;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("sale_chance")
public class SaleChanceController extends BaseController {

    @Resource
    private SaleChanceService saleChanceService;

    /**
     * 多条件分⻚查询营销机会 101001
     * 如果flag的值不为空，且值为1，则查询的是客户开发计划：否则查询营销机会数据
     * @param saleChanceQuery
     * @return
     */
    @RequiredPermission(code = "101001")
    @RequestMapping("list")
    @ResponseBody
    public Map<String, Object> querySaleChanceByParams (SaleChanceQuery saleChanceQuery, Integer flag,HttpServletRequest request) {
        // 判断flag值
        if(flag != null && flag == 1){
            //  查询客户开发计划
            //  设置分配状态
            saleChanceQuery.setState(StateStatus.STATED.getType());
            //  设置指派人（当前登录用户的ID)
            //  从cookie中获取当前登录用户的ID
            Integer userId = LoginUserUtil.releaseUserIdFromCookie(request);
            saleChanceQuery.setAssignMan(userId);
        }

        return saleChanceService.querySaleChanceByParams(saleChanceQuery);
    }

    /**
     * 进⼊营销机会⻚⾯  1010
     * @return
     */
    @RequestMapping("index")
    @RequiredPermission(code = "1010")
    public String index() {
        return "saleChance/sale_chance";
    }

    /**
     * 添加营销机会  101002
     * @param request
     * @param saleChance
     * @return
     */
    @RequiredPermission(code = "101002")
    @PostMapping("add")
    @ResponseBody
    public ResultInfo addSaleChance(SaleChance saleChance, HttpServletRequest request){
        // 从cookie中获取⽤户姓名
        String userName = CookieUtil.getCookieValue(request, "userName");
        // 设置营销机会的创建⼈
        saleChance.setCreateMan(userName);
        // 添加营销机会的数据
        saleChanceService.addSaleChance(saleChance);
        return success("营销机会数据添加成功！"); // BaseController中的方法

    }

    /**
     * 机会数据添加与更新表单⻚⾯视图转发
     * id为空 添加操作
     * id⾮空 修改操作
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("addOrUpdateSaleChancePage")
    public String addOrUpdateSaleChancePage(Integer id, Model model){
        // 如果id不为空，表示是修改操作，修改操作需要查询被修改的数据
        if (null != id) {
        // 通过主键查询营销机会数据
            SaleChance saleChance = saleChanceService.selectByPrimaryKey(id);
        // 将数据存到作⽤域中
            model.addAttribute("saleChance", saleChance);
        }
        return "saleChance/add_update";
    }

    /**
     * 更新营销机会数据  101004
     * @param request
     * @param saleChance
     * @return
     */
    @RequiredPermission(code = "101004")
    @PostMapping("update")
    @ResponseBody
    public ResultInfo updateSaleChance(HttpServletRequest request, SaleChance saleChance){
        // 更新营销机会的数据
        saleChanceService.updateSaleChance(saleChance);
        return success("营销机会数据更新成功！");
    }

    /**
     * 删除营销机会数据  101003
     * @param ids
     * @return
     */
    @RequiredPermission(code = "101003")
    @PostMapping("delete")
    @ResponseBody
    public ResultInfo deleteSaleChance (Integer[] ids) {
        // 删除营销机会的数据
        saleChanceService.deleteBatch(ids);
        return success("营销机会数据删除成功！");
    }


    /**
     * 更新营销机会的开发状态
     * @param id
     * @param devResult
     * @return
     */
    @PostMapping("updateSaleChanceDevResult")
    @ResponseBody
    public ResultInfo updateSaleChanceDevResult(Integer id , Integer devResult){
        saleChanceService.updateSaleChanceDevResult(id,devResult);

        return success("开发状态更新成功");

    }
}
