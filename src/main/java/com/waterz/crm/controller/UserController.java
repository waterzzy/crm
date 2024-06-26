package com.waterz.crm.controller;

import com.waterz.crm.base.BaseController;
import com.waterz.crm.base.ResultInfo;
import com.waterz.crm.exceptions.ParamsException;
import com.waterz.crm.model.UserModel;
import com.waterz.crm.query.UserQuery;
import com.waterz.crm.service.SaleChanceService;
import com.waterz.crm.service.UserService;
import com.waterz.crm.utils.LoginUserUtil;
import com.waterz.crm.vo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("user")
public class UserController extends BaseController {
    @Resource
    private UserService userService;



    /*
    * 用户登录
    * */
    @PostMapping("login")
    @ResponseBody
    public ResultInfo userLogin(String userName,String userPwd){
        ResultInfo resultInfo = new ResultInfo();

        // 调用service层登录方法
        UserModel userModel = userService.userLogin(userName,userPwd);

        // 设置ResultInfo的result的值
        resultInfo.setResult(userModel);

       /* // 通过try catch捕获service层异常，如果service抛出异常，则表示登录失败，否则成功
        try {
            // 调用service层登录方法
            UserModel userModel = userService.userLogin(userName,userPwd);

            // 设置ResultInfo的result的值
            resultInfo.setResult(userModel);
        }catch (ParamsException p){
            resultInfo.setCode(p.getCode());
            resultInfo.setMsg(p.getMsg());
            p.printStackTrace();
        }catch (Exception e){
            resultInfo.setCode(500);
            resultInfo.setMsg("登录失败！");
        }*/
        return resultInfo;
    }


    /*
    * 用户修改密码
    * */
    @PostMapping("updatePwd")
    @ResponseBody
    public ResultInfo updateUserPassword(HttpServletRequest request,String oldPassword,String newPassword,String repeatPassword){
        ResultInfo resultInfo = new ResultInfo();

        // 获取cookie中的userId
        Integer userId = LoginUserUtil.releaseUserIdFromCookie(request);
        // 调用service层的修改密码方法
        userService.updatePassWord(userId,oldPassword,newPassword,repeatPassword);

       /* try {
            // 获取cookie中的userId
            Integer userId = LoginUserUtil.releaseUserIdFromCookie(request);
            // 调用service层的修改密码方法
            userService.updatePassWord(userId,oldPassword,newPassword,repeatPassword);
        }catch (ParamsException e) {
            // 设置状态码和提示信息
            resultInfo.setCode(e.getCode());
            resultInfo.setMsg(e.getMsg());
            e.printStackTrace();
        }catch (Exception e){
            resultInfo.setCode(500);
            resultInfo.setMsg("修改密码失败！");
            e.printStackTrace();
        }*/


        return resultInfo;
    }

    /*
    * 进入修改密码的页面
    * */
    @RequestMapping("toPasswordPage")
    public String toPasswordPage(){

        return "user/password";
    }

    /**
     * 查询所有销售人员
     * @return
     */
    @RequestMapping("queryAllSales")
    @ResponseBody
    public List<Map<String,Object>> queryAllSales(){
        return userService.queryAllSales();
    }

    /**
     * 分页多条件查询用户列表
     * @param userQuery
     * @return
     */
    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> selectByParams(UserQuery userQuery){
        return userService.queryByParamsForTable(userQuery);
    }

    /**
     * 进入用户列表页面
     * @return
     */
    @RequestMapping("index")
    public String index(){
        return "user/user";
    }


    /**
     * 更新用户
     * @param user
     * @return
     */
    @PostMapping("update")
    @ResponseBody
    public ResultInfo updateUser(User user){
        userService.updateUser(user);
        return success("用户更新成功！");
    }


    /**
     * 添加用户
     * @param user
     * @return
     */
    @PostMapping("add")
    @ResponseBody
    public ResultInfo addUser(User user){
        userService.addUser(user);
        return success("用户添加成功！");
    }

    /**
     * 删除用户
     * @param ids
     * @return
     */
    @PostMapping("delete")
    @ResponseBody
    public ResultInfo deleteUser(Integer[] ids){

        userService.deleteByIds(ids);

        return success("用户删除成功");
    }

    /**
     * 打开添加或修改用户的页面
     * @return
     */
    @RequestMapping("toAddOrUpdateUserPage")
    public String toAddOrUpdateUserPage(Integer id,HttpServletRequest request){

        // 判断id是否为空不为空白哦是更新
        if(id != null){
            // 通过id查询用户对象
            User user = userService.selectByPrimaryKey(id);
            // 将数据设置到请求域中
            request.setAttribute("userInfo",user);
        }

        return "user/add_update";
    }



}
