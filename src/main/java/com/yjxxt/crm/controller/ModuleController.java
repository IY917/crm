package com.yjxxt.crm.controller;

import com.yjxxt.crm.base.BaseController;
import com.yjxxt.crm.base.ResultInfo;
import com.yjxxt.crm.dto.TreeDto;
import com.yjxxt.crm.service.ModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("module")
public class ModuleController extends BaseController {

    @Autowired
    private ModuleService moduleService;

    @RequestMapping("index")
    public String index(){
        return "module/module";
    }

    /**
     * 查询当前角色拥有的资源信息
     * @param roleId
     * @return
     */
    @RequestMapping("findModules")
    @ResponseBody
    public List<TreeDto> findModules(Integer roleId){
        return moduleService.findModulesByRoleId(roleId);
    }

    /**
     * 资源模块 列表查询展示
     * @return
     */
    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> list(){
        return moduleService.queryModules();
    }

}
