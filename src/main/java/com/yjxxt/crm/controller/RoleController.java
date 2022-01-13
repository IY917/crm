package com.yjxxt.crm.controller;

import com.yjxxt.crm.annotation.RequiredPermission;
import com.yjxxt.crm.base.BaseController;
import com.yjxxt.crm.base.ResultInfo;
import com.yjxxt.crm.bean.Role;
import com.yjxxt.crm.query.RoleQuery;
import com.yjxxt.crm.service.PermissionService;
import com.yjxxt.crm.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("role")
public class RoleController extends BaseController {
    @Autowired
    private RoleService roleService;


    @RequestMapping("index")
    public String index(){
        return "role/role";
    }

    @RequestMapping("roleGrant")
    public String roleGrant(Integer roleId,Model model){
        model.addAttribute("roleId",roleId);
        return "role/grant";
    }

    @RequestMapping("addOrUpdate")
    public String AddOrUpdateRole(Integer roleId, Model model){
        if(roleId!=null){
            Role role = roleService.selectByPrimaryKey(roleId);
            model.addAttribute("role",role);
        }
        return "role/add_update";
    }

    /**
     * 查询所有角色信息
     * @return
     */
    @RequestMapping("findRoles")
    @ResponseBody
    public List<Map<String,Object>> findRoles(Integer userId){
        return roleService.findRoles(userId);
    }

    /**
     * 角色 列表查询
     * @param roleQuery
     * @return
     */
    @RequestMapping("list")
    @ResponseBody
    @RequiredPermission(code = "60")
    public Map<String,Object> list(RoleQuery roleQuery){
        return roleService.findRoleByParam(roleQuery);
    }

    /**
     * 添加
     * @param role
     * @return
     */
    @RequestMapping("add")
    @ResponseBody
    public ResultInfo add(Role role){
        roleService.addRole(role);
        return success("添加成功");
    }

    /**
     * 修改
     * @param role
     * @return
     */
    @RequestMapping("update")
    @ResponseBody
    public ResultInfo update(Role role){
        roleService.changeRole(role);
        return success("修改成功");
    }

    /**
     * 角色 删除（单删）
     * @param role
     * @return
     */
    @RequestMapping("delete")
    @ResponseBody
    public ResultInfo delete(Role role){
        roleService.removeRoleById(role);
        return success("删除成功");
    }


    /**
     * 授权
     * @param roleId
     * @param mids
     * @return
     */
    @RequestMapping("addGrant")
    @ResponseBody
    public ResultInfo grant(Integer roleId,Integer[] mids){
        roleService.addGrant(roleId,mids);
        return success("授权成功");
    }
}
