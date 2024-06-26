package com.waterz.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.waterz.crm.base.BaseService;
import com.waterz.crm.dao.SaleChanceMapper;
import com.waterz.crm.enums.DevResult;
import com.waterz.crm.enums.StateStatus;
import com.waterz.crm.query.SaleChanceQuery;
import com.waterz.crm.utils.AssertUtil;
import com.waterz.crm.utils.PhoneUtil;
import com.waterz.crm.vo.SaleChance;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;



@Service
public class SaleChanceService extends BaseService<SaleChance,Integer> {

    @Resource
    private SaleChanceMapper saleChanceMapper;


    /**
     * 多条件分⻚查询营销机会 (BaseService 中有对应的⽅法)
     * @param saleChanceQuery
     * @return map
     */
    public Map<String,Object> querySaleChanceByParams(SaleChanceQuery saleChanceQuery){

        Map<String,Object> map = new HashMap<>();

        // 开启分页
        PageHelper.startPage(saleChanceQuery.getPage(),saleChanceQuery.getLimit());
        // 得到对应分页对象
        PageInfo<SaleChance> pageInfo = new PageInfo<>(saleChanceMapper.selectByParams(saleChanceQuery));

        // 设置map对象
        map.put("code",0);
        map.put("msg", "success");
        map.put("count", pageInfo.getTotal());
        // 设置好分页的列表
        map.put("data", pageInfo.getList());

        return map;
    }

    /**
     * 营销机会数据添加
     * 1.参数校验
     *  customerName:⾮空
     *  linkMan:⾮空
     *  linkPhone:⾮空 11位⼿机号
     * 2.设置相关参数默认值
     *  state:默认未分配 如果选择分配⼈ state 为已分配
     *  assignTime:如果 如果选择分配⼈ 时间为当前系统时间
     *  devResult:默认未开发 如果选择分配⼈devResult为开发中 0-未开发 1-开发中 2-开发成功 3-开发失败
     *  isValid:默认有效数据(1-有效 0-⽆效)
     *  createDate updateDate:默认当前系统时间
     * 3.执⾏添加 判断结果
     */
    /**
     * 营销机会数据添加
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addSaleChance(SaleChance saleChance) {
        // 1.参数校验
        checkSaleChanceParams(saleChance.getCustomerName(),saleChance.getLinkMan(),saleChance.getLinkPhone());

        // 2.设置相关参数默认值
        // isValid是否有效
        saleChance.setIsValid(1);
        //  CreateDate创建时间  默认系统当前
        saleChance.setCreateDate(new Date());
        //  UpdateDate 默认系统当前
        saleChance.setUpdateDate(new Date());

        // 判断是否设置了指派人
        if(StringUtils.isBlank(saleChance.getAssignMan())){
            // 如果为空，则表示未设置指派人
            // state分配状态  （0=未分配，1=已分配  ）
            saleChance.setState(StateStatus.UNSTATE.getType());
            //  assignTime指派时间  设置null
            saleChance.setAssignTime(null);
            //  devResult开发状态 （0=未开发，1=开发中，2=开发成功，3=开发失败） 0=未开放（默认）
            saleChance.setDevResult(DevResult.UNDEV.getStatus());
        }else {
            // 如果不为空
            saleChance.setState(StateStatus.STATED.getType());
            saleChance.setAssignTime(new Date());
            saleChance.setDevResult(DevResult.DEVING.getStatus());
        }
        // 3.执行添加操作，判断受影响的行数
        AssertUtil.isTrue(saleChanceMapper.insertSelective(saleChance) != 1,"添加营销机会失败！");
    }

    /**
     * 营销机会数据更新
     *  1.参数校验
     *      id:记录必须存在
     *      customerName:⾮空
     *      linkMan:⾮空
     *      linkPhone:⾮空，11位⼿机号
     *  2. 设置相关参数值
     *     updateDate:系统当前时间
     *      原始记录 未分配 修改后改为已分配(由分配⼈决定)
     *          state 0->1
     *          assginTime 系统当前时间
     *          devResult 0-->1
     *      原始记录 已分配 修改后 为未分配
     *          state 1-->0
     *          assignTime 待定 null
     *          devResult 1-->0
     * 3.执⾏更新 判断结果
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateSaleChance(SaleChance saleChance){
        // 1.参数校验
        // 通过id查询记录
        SaleChance temp = saleChanceMapper.selectByPrimaryKey(saleChance.getId());
        // 判断是否为空
        AssertUtil.isTrue(null == temp, "待更新记录不存在！");
        // 校验基础参数
        checkSaleChanceParams(saleChance.getCustomerName(), saleChance.getLinkMan(), saleChance.getLinkPhone());

        // 2. 设置相关参数值
        //updateDate更新时间  设置为系统当前时间
        saleChance.setUpdateDate(new Date());
        //assignMan指派人
        // 判断原始数据是否存在
        if(StringUtils.isBlank(temp.getAssignMan())){ // 不存在
            //判断修改后的值是否存在
            if(!StringUtils.isBlank(saleChance.getAssignMan())){
                // assignTime指派时间  设置当前时间
                saleChance.setAssignTime(new Date());
                //分配状态   1=已分配
                saleChance.setState(StateStatus.STATED.getType());
                //开发状态   1=开发中
                saleChance.setDevResult(DevResult.DEVING.getStatus());
            }
        }else { // 存在
            // 判断修改后的值是否存在
            if(StringUtils.isBlank(saleChance.getAssignMan())){ //修改前有值，修改后无值
                // assignTime指派时间  设置为null
                saleChance.setAssignTime(null);
                //分配状态   0=未分配
                saleChance.setState(StateStatus.STATED.getType());
                //开发状态   0=未开发
                saleChance.setDevResult(DevResult.DEVING.getStatus());

            }else {// 修改前有值，修改后有值
                // 判断修改前后是否是同一用户
                if(!saleChance.getAssignMan().equals(temp.getAssignMan())){
                    //更新指派时间
                    saleChance.setAssignTime(new Date());
                }else {
                    // 设置指派时间为修改前的时间
                    saleChance.setAssignTime(temp.getAssignTime());
                }
            }
        }

        /*saleChance.setUpdateDate(new Date());
        if (StringUtils.isBlank(temp.getAssignMan()) && StringUtils.isNotBlank(saleChance.getAssignMan())) {
        // 如果原始记录未分配，修改后改为已分配
            saleChance.setState(StateStatus.STATED.getType());
            saleChance.setAssignTime(new Date());
            saleChance.setDevResult(DevResult.DEVING.getStatus());
        } else if (StringUtils.isNotBlank(temp.getAssignMan()) && StringUtils.isBlank(saleChance.getAssignMan())) {
        // 如果原始记录已分配，修改后改为未分配
            saleChance.setAssignMan("");
            saleChance.setState(StateStatus.UNSTATE.getType());
            saleChance.setAssignTime(null);
            saleChance.setDevResult(DevResult.UNDEV.getStatus());
        }*/

        // 3.执⾏更新 判断结果影响的行数
        AssertUtil.isTrue(saleChanceMapper.updateByPrimaryKeySelective(saleChance) != 1, "营销机会数据 更新失败！");
    }


    /**
     * 基本参数校验
     * @param customerName
     * @param linkMan
     * @param linkPhone
     */
    private void checkSaleChanceParams(String customerName, String linkMan, String linkPhone) {
        // customerName客户名称   非空
        AssertUtil.isTrue(StringUtils.isBlank(customerName), "请输⼊客户名！");
        // linkMan联系人   非空
        AssertUtil.isTrue(StringUtils.isBlank(linkMan), "请输⼊联系⼈！");
        // linkPhone联系号码    非空
        AssertUtil.isTrue(StringUtils.isBlank(linkPhone), "请输⼊⼿机号！");
        // 手机号码格式正确
        AssertUtil.isTrue(!PhoneUtil.isMobile(linkPhone),"⼿机号格式不正确！");
    }

    /**
     * 营销机会数据删除
     * @param ids
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteSaleChance(Integer[] ids){
        // 判断要删除的id是否为空
        AssertUtil.isTrue(null == ids || ids.length < 1, "请选择需要删除的数据！");
        // 删除数据
        AssertUtil.isTrue(saleChanceMapper.deleteBatch(ids) != ids.length, "营销机会数据删除失败！");

    }

    /**
     * 更新营销机会的开发状态
     * @param id
     * @param devResult
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateSaleChanceDevResult(Integer id, Integer devResult) {
        // 判断id是否为空
        AssertUtil.isTrue(null == id,"待更新记录不存在");
        //通过id查询营销机会数据
        SaleChance saleChance = saleChanceMapper.selectByPrimaryKey(id);
        //判断对象是否为空
        AssertUtil.isTrue(null == saleChance,"待更新记录不存在");
        //设置开发状态
        saleChance.setDevResult(devResult);
        //执行更新操作，判断受影响的行数
        AssertUtil.isTrue(saleChanceMapper.updateByPrimaryKeySelective(saleChance) != 1,"开发状态更新失败！");
    }
}
