package com.yjxxt.crm.controller;

import com.yjxxt.crm.base.BaseController;
import com.yjxxt.crm.base.ResultInfo;
import com.yjxxt.crm.bean.User;
import com.yjxxt.crm.exceptions.ParamsException;
import com.yjxxt.crm.model.UserModel;
import com.yjxxt.crm.query.UserQuery;
import com.yjxxt.crm.service.UserService;
import com.yjxxt.crm.utils.LoginUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("user")
public class UserController extends BaseController {

    @Autowired(required = false)
    private UserService userService;

    @RequestMapping("toPasswordPage")
    public String updatePwd() {
        return "user/password";
    }

    @RequestMapping("index")
    public String index() {
        return "user/user";
    }

    @RequestMapping("toSettingPage")
    public String setting(HttpServletRequest req) {
        //查看cookie中的用户ID
        int userId = LoginUserUtil.releaseUserIdFromCookie(req);
        //根据id查询用户信息
        User user = userService.selectByPrimaryKey(userId);
        //存储数据
        req.setAttribute("user",user);
        //转发
        return "user/setting";
    }

    @RequestMapping("addOrUpdateUser")
    public String addOrUpdate(Integer id, Model model) {
        if(id!=null){
            User user = userService.selectByPrimaryKey(id);
            model.addAttribute("user",user);
        }
        return "user/add_update";
    }

    /**
     * 登录
     *
     * @param userName
     * @param userPwd
     * @return
     */
    @RequestMapping("login")
    @ResponseBody
    public ResultInfo login(String userName, String userPwd) {
        ResultInfo resultInfo = new ResultInfo();
        UserModel userModel = userService.userLogin(userName, userPwd);
        resultInfo.setResult(userModel);
        return resultInfo;
    }


    /**
     * 修改密码
     *
     * @param req
     * @param oldPassword
     * @param newPassword
     * @param confirmPassword
     * @return
     */
    @RequestMapping("updatePwd")
    @ResponseBody
    public ResultInfo updatePwd(HttpServletRequest req, String oldPassword, String newPassword, String confirmPassword) {
        ResultInfo resultInfo = new ResultInfo();
        //获取Cookie中的userId
        int userId = LoginUserUtil.releaseUserIdFromCookie(req);
        //修改密码操作
        userService.updateUserPassword(userId, oldPassword, newPassword, confirmPassword);
        return resultInfo;
    }

    /**
     * 修改基本资料信息
     * @param user
     * @return
     */
    @RequestMapping("setting")
    @ResponseBody
    public ResultInfo set(User user) {
        ResultInfo resultInfo = new ResultInfo();
        //修改信息
        userService.updateByPrimaryKeySelective(user);
        //返回目标
        return resultInfo;
    }

    /**
     * 查询所有销售人员信息
     * @return
     */
    @RequestMapping("sales")
    @ResponseBody
    public List<Map<String,Object>> findSales(){
        return userService.querySales();
    }

    /**
     * 列表查询
     * @param userQuery
     * @return
     */
    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> findUserByParams(UserQuery userQuery){
        return userService.queryUserByParams(userQuery);
    }

    /**
     * 用户模块 添加
     * @param user
     * @return
     */
    @RequestMapping("add")
    @ResponseBody
    public ResultInfo add(User user) {
        //修改信息
        userService.addUser(user);
        //返回目标
        return success("添加成功");
    }

    /**
     * 用户模块 修改
     * @param user
     * @return
     */
    @RequestMapping("update")
    @ResponseBody
    public ResultInfo update(User user) {
        //修改信息
        userService.changeUser( user);
        //返回目标
        return success("修改成功");
    }

    /**
     * 删除
     * @param ids
     * @return
     */
    @RequestMapping("dels")
    @ResponseBody
    public ResultInfo delete(Integer [] ids) {
        //删除操作
        userService.removeUser(ids);
        //返回目标
        return success("删除成功");
    }
}
