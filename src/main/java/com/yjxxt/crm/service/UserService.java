package com.yjxxt.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yjxxt.crm.base.BaseService;
import com.yjxxt.crm.bean.User;
import com.yjxxt.crm.bean.UserRole;
import com.yjxxt.crm.mapper.UserMapper;
import com.yjxxt.crm.mapper.UserRoleMapper;
import com.yjxxt.crm.model.UserModel;
import com.yjxxt.crm.query.UserQuery;
import com.yjxxt.crm.utils.AssertUtil;
import com.yjxxt.crm.utils.Md5Util;
import com.yjxxt.crm.utils.PhoneUtil;
import com.yjxxt.crm.utils.UserIDBase64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service
public class UserService extends BaseService<User,Integer> {
    @Resource
    private UserMapper userMapper;

    @Resource
    private UserRoleMapper userRoleMapper;

    /**
     * 登录
     * @param userName
     * @param userPwd
     * @return
     */
    public UserModel userLogin(String userName, String userPwd){
        //校验用户密码
        checkUserLoginParem(userName,userPwd);
        //用户不存在
        User temp = userMapper.selectUserByName(userName);
        AssertUtil.isTrue(temp==null,"用户不存在");
        //密码输入错误
        checkUserPwd(userPwd,temp.getUserPwd());

        return builderUserInfo(temp);
    }

    /**
     * 校验用户密码
     * @param userName
     * @param userPwd
     */
    private void checkUserLoginParem(String userName, String userPwd) {
        //用户名不能为空
        AssertUtil.isTrue(StringUtils.isBlank(userName),"用户名不能为空");
        //密码不能为空
        AssertUtil.isTrue(StringUtils.isBlank(userPwd),"密码不能为空");
    }

    /**
     * 验证密码
     * @param userPwd
     * @param userPwd1
     */
    private void checkUserPwd(String userPwd, String userPwd1) {
        //对输入的密码加密
        userPwd= Md5Util.encode(userPwd);
        //加密的密码和数据中的密码对比
        AssertUtil.isTrue(!userPwd.equals(userPwd1),"密码输入错误");
    }

    /**
     * 构建返回目标对象
     * @param user
     * @return
     */
    private UserModel builderUserInfo(User user) {
        UserModel userModel=new UserModel();
        //对用户ID进行加密
        userModel.setUserIdStr(UserIDBase64.encoderUserID(user.getId()));
        userModel.setUserName(user.getUserName());
        userModel.setTrueName(user.getTrueName());
        return userModel;
    }


    /**
     * 修改密码
     * @param userId
     * @param oldPassword
     * @param newPassword
     * @param confirmPassword
     */
    public void updateUserPassword(Integer userId,String oldPassword,String newPassword,String confirmPassword){
        //确认用户登录了才可修改
        User user = userMapper.selectByPrimaryKey(userId);
        //密码校验
        checkPasswordParams(user,oldPassword,newPassword,confirmPassword);
        //设置新密码
        user.setUserPwd(Md5Util.encode(newPassword));
        //确认密码是否修改成功
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user)<1,"修改失败");
    }

    /**
     * 验证用户密码修改参数
     * @param user
     * @param oldPassword
     * @param newPassword
     * @param confirmPassword
     */
    private void checkPasswordParams(User user, String oldPassword, String newPassword, String confirmPassword) {
        //确认用户登录了才可修改
        AssertUtil.isTrue(user==null,"用户未登录或不存在");
        //原始密码非空
        AssertUtil.isTrue(StringUtils.isBlank(oldPassword),"请输入原始密码！");
        //原始密码是否正确
        AssertUtil.isTrue(!user.getUserPwd().equals(Md5Util.encode(oldPassword)),"原始密码不正确！");
        //新密码非空
        AssertUtil.isTrue(StringUtils.isBlank(newPassword),"请输入新密码！");
        //新密码不能和原始密码一致
        AssertUtil.isTrue(newPassword.equals(oldPassword),"新密码不能与原始密码相同！");
        //确认密码非空
        AssertUtil.isTrue(StringUtils.isBlank(confirmPassword),"请输入确认密码！");
        //确认密码和新密码一致
        AssertUtil.isTrue(!confirmPassword.equals(newPassword),"新密码与确认密码不一致！");
    }

    /**
     * 查询所有销售人员信息
     * @return
     */
    public List<Map<String,Object>> querySales(){
        return userMapper.selectSales();
    }



    /**
     * 用户模块 列表查询
     * @param userQuery
     * @return
     */
    public Map<String,Object> queryUserByParams(UserQuery userQuery){
        //实例化
        Map<String,Object> map=new HashMap<>();
        //分页
        PageHelper.startPage(userQuery.getPage(),userQuery.getLimit());
        PageInfo<User> plist=new PageInfo<>(selectByParams(userQuery));
        //准备数据
        map.put("code",0);
        map.put("msg","success");
        map.put("count",plist.getTotal());
        map.put("data",plist.getList());
        //转发
        return map;
    }

    /**
     * 添加
     * 1、验证
     * 2、设默认值
     *      是否有效、创建时间、修改时间、密码加密
     * 3、是否成功
     * @param user
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addUser(User user){
        //校验
        checkParams(user.getUserName(),user.getEmail(),user.getPhone());
        //用户名唯一
        User temp=userMapper.selectUserByName(user.getUserName());
        AssertUtil.isTrue(temp!=null,"用户已存在");
        //设默认值
        user.setIsValid(1);
        user.setCreateDate(new Date());
        user.setUpdateDate(new Date());
        user.setUserPwd(Md5Util.encode("123456"));
        //是否成功
        //AssertUtil.isTrue(insertSelective(user)<1,"添加失败");
        AssertUtil.isTrue(insertHasKey(user)<1,"添加失败");

        relationUserRole(user.getId(),user.getRoleIds());
    }

    /**
     * 操作中间表
     * @param userId
     * @param roleIds
     */
    private void relationUserRole(Integer userId, String roleIds) {
        //准备集合存储对象
        List<UserRole> urlist=new ArrayList<>();
        AssertUtil.isTrue(StringUtils.isBlank(roleIds),"请选择角色信息");

        //统计当前用户有多少角色
        int count=userRoleMapper.countUserRoleNum(userId);
        if(count>0){
            //删除原有数据
            AssertUtil.isTrue(userRoleMapper.deleteUserRoleByUserId(userId)!=count,"用户角色删除失败");
        }

        String[] roleIdStr = roleIds.split(",");
        //遍历
        for (String rid:roleIdStr) {
            //准备对象
            UserRole userRole=new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(Integer.parseInt(rid));
            userRole.setCreateDate(new Date());
            userRole.setUpdateDate(new Date());
            //存放到集合
            urlist.add(userRole);
        }
        //批量添加
        AssertUtil.isTrue(userRoleMapper.insertBatch(urlist)!=urlist.size(),"用户角色分配失败");
    }

    /**
     * 方法：验证用户名、邮箱、手机号
     *      （只做非空判断）
     * @param userName
     * @param email
     * @param phone
     */
    private void checkParams(String userName, String email, String phone) {
        AssertUtil.isTrue(StringUtils.isBlank(userName),"用户名不能为空！");
        AssertUtil.isTrue(StringUtils.isBlank(email),"邮箱不能为空！");
        AssertUtil.isTrue(StringUtils.isBlank(phone),"手机号不能为空！");
        AssertUtil.isTrue(!PhoneUtil.isMobile(phone),"请输入合法的手机号");
    }

    /**
     * 根据用户id 修改
     * @param user
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void changeUser(User user){
        //根据id获取用户信息
        User temp = userMapper.selectByPrimaryKey(user.getId());
        AssertUtil.isTrue(temp==null,"待修改记录不存在");
        //校验
        checkParams(user.getUserName(),user.getEmail(),user.getPhone());
        //修改时，出现用户名已存在问题
        User temp2=userMapper.selectUserByName(user.getUserName());
        AssertUtil.isTrue(temp2!=null && !temp2.getId().equals(user.getId()),"用户已存在");
        //设默认值
        user.setUpdateDate(new Date());
        //是否成功
        AssertUtil.isTrue(updateByPrimaryKeySelective(user)<1,"修改失败");

        relationUserRole(user.getId(),user.getRoleIds());
    }

    /**
     * 批量删除
     * @param ids
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void removeUser(Integer [] ids){
        AssertUtil.isTrue(ids==null || ids.length==0,"请选择要删除的数据");

        //遍历
        for (Integer userId:ids) {
            //统计当前用户有多少角色
            int count=userRoleMapper.countUserRoleNum(userId);
            if(count>0){
                //删除原有数据
                AssertUtil.isTrue(userRoleMapper.deleteUserRoleByUserId(userId)!=count,"用户角色删除失败");
            }
        }

        AssertUtil.isTrue(userMapper.deleteBatch(ids)<1,"删除失败");
    }
}
