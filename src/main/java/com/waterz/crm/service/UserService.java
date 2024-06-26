package com.waterz.crm.service;


import com.waterz.crm.base.BaseService;
import com.waterz.crm.dao.UserMapper;
import com.waterz.crm.dao.UserRoleMapper;
import com.waterz.crm.model.UserModel;
import com.waterz.crm.utils.AssertUtil;
import com.waterz.crm.utils.Md5Util;
import com.waterz.crm.utils.PhoneUtil;
import com.waterz.crm.utils.UserIDBase64;
import com.waterz.crm.vo.User;
import com.waterz.crm.vo.UserRole;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class UserService extends BaseService<User,Integer> {
    @Resource
    private UserMapper userMapper;

    @Resource
    private UserRoleMapper userRoleMapper;

    /*
    * 用户登录
    * */
    public UserModel userLogin(String userName,String userPwd){
        // 参数校验
        checkLoginParams(userName,userPwd);

        User user = userMapper.queryUserByName(userName);
        // 判断用户是否存在
        AssertUtil.isTrue(user == null, "用户姓名不存在");
        // 检查密码是否正确
        checkUserPwd(userPwd,user.getUserPwd());

        // 返回构建用户对象
        return buildUserInfo(user);
    }

    /*
    * 修改密码的参数校验
    *
    * */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updatePassWord(Integer userId,String oldPwd, String newPwd, String repeatPwd){
        // 通过用户ID查询用户记录，返回用户对象
        User user = userMapper.selectByPrimaryKey(userId);
        // 判断用户对象是否存在
        AssertUtil.isTrue(user == null,"待更新记录不存在！");
        //参数校验
        checkPasswordParams(user,oldPwd,newPwd,repeatPwd);
        //设置用户的新密码
        user.setUserPwd(Md5Util.encode(newPwd));

        // 执行跟新，判断受影响的行数
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user)<1,"修改密码失败");
    }

    /*
    * 密码的校验
    * */
    private void checkPasswordParams(User user, String oldPwd, String newPwd, String repeatPwd) {
        // 判断原始密码是否为空
        AssertUtil.isTrue(StringUtils.isBlank(oldPwd), "原始密码不能为空！");
        // 判断原始密码是否正确（查询的用户对象中的用户密码是否与原始密码一致）
        AssertUtil.isTrue(!(user.getUserPwd().equals(Md5Util.encode(oldPwd))), "原始密码不正确！");
        // 判断新密码是否为空
        AssertUtil.isTrue(StringUtils.isBlank(newPwd), "新密码不能为空！");
        // 判断型密码是否和原密码一致（不可以）
        AssertUtil.isTrue(oldPwd.equals(newPwd), "新密码不能与原始密码相同！");

        // 判断确认密码是否为空
        AssertUtil.isTrue(StringUtils.isBlank(repeatPwd), "请输⼊确认密码！");
        // 新密码要与确认密码保持⼀致
        AssertUtil.isTrue(!(newPwd.equals(repeatPwd)), "新密码与确认密码不⼀致！");

    }

    /*
    * 构建需要返回给客户端的用户对象
    * */
    private UserModel buildUserInfo(User user) {
        UserModel userModel = new UserModel();
//        userModel.setUserId(user.getId());
        // 设置加了密的用户ID
        userModel.setUserIdStr(UserIDBase64.encoderUserID(user.getId()));
        userModel.setUserName(user.getUserName());
        userModel.setTrueName(user.getTrueName());
        return userModel;
    }

    /*
    * 密码判断
    *   先将客户端传递的密码加密，再与数据库中查到的密码作比较
    * */
    private void checkUserPwd(String userPwd, String pwd) {
        // 将客户端传递的密码加密
        userPwd = Md5Util.encode(userPwd);
        // 判断密码是否相等
        AssertUtil.isTrue(!userPwd.equals(pwd),"用户密码不正确");
    }
    /*
    * 参数判断
    *
    * */
    private void checkLoginParams(String userName, String userPwd) {
        // 验证用户姓名
        AssertUtil.isTrue(StringUtils.isBlank(userName),"用户姓名不能为空");
        // 验证用户密码
        AssertUtil.isTrue(StringUtils.isBlank(userName),"用户密码不能为空");
    }

    /**
     * 查询所有的销售
     * @return
     */
    public List<Map<String,Object>> queryAllSales(){
        return userMapper.queryAllSales();
    }


    /**
     * 添加⽤户
     *  1. 参数校验
     *      ⽤户名 ⾮空 唯⼀性
     *      邮箱 ⾮空
     *      ⼿机号 ⾮空 格式合法
     *  2. 设置默认参数
     *      isValid 1
     *      creteDate 当前时间
     *      updateDate 当前时间
     *      userPwd 123456 -> md5加密
     *  3. 执⾏添加，判断结果
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addUser(User user){
        // 1.参数校验
        checkUserParams(user.getUserName(),user.getEmail(),user.getPhone(),null);
        // 2.设置参数的默认值
        user.setIsValid(1);
        user.setCreateDate(new Date());
        user.setUpdateDate(new Date());
        // 设置默认密码
        user.setUserPwd(Md5Util.encode("123456"));
        // 3.执⾏添加，判断结果
        AssertUtil.isTrue(userMapper.insertSelective(user) < 1,"用户添加失败！");


        // 用户角色关联
        /**
         * 用户id
         *  useId
         * 角色id
         *  roleId
         */
        relationUserRole(user.getId(),user.getRoleIds());
    }
    /**
     * 用户角色关联
     *
     *  添加操作
     *      原始角色不存在
     *          1.不添加新的角色记录   不操作用户角色表
     *          2.添加新的角色记录     给指定用户绑定相关的角色记录
     *  更新操作
     *      原始角色不存在
     *          1.不添加新的角色记录   不操作用户角色表
     *          2.添加新的角色记录     给指定用户绑定相关的角色记录
     *      原始角色存在
     *          1.添加新的角色记录     判断已有的角色记录不添加，添加没有的角色记录
     *          2.清空所有的角色记录     删除用户绑定的角色记录
     *          3.移除部分角色记录      删除不存在的角色记录，存在的角色的记录保留
     *          4.移除部分角色，添加新角色      删除不存在的角色记录，存在的角色记录保留，添加新的角色
     *
     *
     *  如何进行角色分配：
     *      先把所有角色删除，然后再添加角色记录。
     *
     *  删除操作
     *      将指定用户的绑定的角色记录删除
     */
    private void relationUserRole(Integer userId, String roleIds) {

        //通过用户ID查询角色记录
        Integer count = userRoleMapper.countUserRoleByUserId(userId);
        // 判断角色记录是否存在
        if(count > 0){
            // 如果存在记录，则删除该用户对应的角色记录
            AssertUtil.isTrue(userRoleMapper.deleteUserRoleByUserId(userId) != count,"用户角色分配失败! ");
        }
        // 判断角色Id是否存在，如果存在，则添加该用户对应的角色记录
        if(StringUtils.isNotBlank(roleIds)){
            //  将用户角色数据设置到集合中，执行批量添加
            List<UserRole> userRoleList = new ArrayList<>();
            // 将角色ID字符串转换成数组
            String[] roleIdsArray = roleIds.split(",");
            //  遍历数组，得到对应的用户角色对象，并设置到集合中
            for(String roleId:roleIdsArray){
                UserRole userRole = new UserRole();
                userRole.setRoleId(Integer.parseInt(roleId));
                userRole.setUserId(userId);
                userRole.setCreateDate(new Date());
                userRole.setUpdateDate(new Date());
                //  设置到集合中
                userRoleList.add((userRole));
            }
            //  批量添加用户角色记录
            AssertUtil.isTrue(userRoleMapper.insertBatch(userRoleList)!= userRoleList.size(),"用户角色分配失败！");

        }

    }

    /**
     * 更新用户
     * @param user
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateUser(User user){
        // 1. 参数校验

        // 判断对象是否存在
        AssertUtil.isTrue(user.getId() == null, "待更新记录不存在！");
        // 通过id查询⽤户对象
        User temp = userMapper.selectByPrimaryKey(user.getId());
        // 判断是否存在
        AssertUtil.isTrue(temp == null, "待更新记录不存在！");
        // 验证参数
        checkUserParams(user.getUserName(),user.getEmail(),user.getPhone(),temp.getId());
        // 2. 设置默认参数
        user.setUpdateDate(new Date());
        // 3. 执⾏更新，判断结果
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user) != 1, "⽤户更新失败！");


        // 用户角色关联
        /**
         * 用户id
         *  useId
         * 角色id
         *  roleId
         */
        relationUserRole(user.getId(),user.getRoleIds());
    }


    /**
     * 参数校验
     * @param userName
     * @param email
     * @param phone
     */
    private void checkUserParams(String userName, String email, String phone,Integer userId) {
        // 验证⽤户名是否存在
        AssertUtil.isTrue(StringUtils.isBlank(userName),"用户名不能为空！");
        //  判断用户名的唯一性
        //  通过用户名查询用户对象
        User temp = userMapper.queryUserByName(userName);
        //  如果用户对象为空，则表示用户名可以用；如果用户对象不为空，则表示用户名不可用
        //  如果是添加操作，数据库中无数据，只要通过名称查到数据，则表示用户名被占用
        // 如果是修改操作，数据库是有数据的，查询到⽤户记录就是当前要修改的记录本身就表示可⽤，否则不可⽤
        // 数据存在，且不是当前要修改的⽤户记录，则表示其他⽤户占⽤了该⽤户名
        AssertUtil.isTrue(null != temp && !(temp.getId().equals(userId)), "该⽤户已存在！");
        //邮箱 非空
        AssertUtil.isTrue(StringUtils.isBlank(email), "用户邮箱地址不能为空！");
        //手机号 非空
        AssertUtil.isTrue(StringUtils.isBlank(phone), "用户手机号码不能为空！");
        //手机号 格式判断
        AssertUtil.isTrue(!PhoneUtil.isMobile(phone), "⼿机号码格式不正确！");

    }

    /**
     * 删除用户
     * @param ids
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteByIds(Integer[] ids) {
        // 判断ids是否为空，长度是否大于0
        AssertUtil.isTrue(ids == null || ids.length == 0,"待删除记录不存在");
        // 执行删除操作，判断受影响的行数
        AssertUtil.isTrue(userMapper.deleteBatch(ids) != ids.length,"⽤户记录删除失败!");

        // 遍历用户ID的数组
        for(Integer userId: ids){
            // 通过用户ID查询对应的用户角色记录
            Integer count = userRoleMapper.countUserRoleByUserId(userId);
            // 判断用户角色记录是否存在
            if(count > 0){
                // 通过用户ID删除对应的用户角色记录
                AssertUtil.isTrue(userRoleMapper.deleteUserRoleByUserId(userId) != count,"删除用户失败！");
            }
        }
    }






}
