package com.yjxxt.crm.service;

import com.yjxxt.crm.base.BaseService;
import com.yjxxt.crm.bean.Module;
import com.yjxxt.crm.dto.TreeDto;
import com.yjxxt.crm.mapper.ModuleMapper;
import com.yjxxt.crm.mapper.PermissionMapper;
import com.yjxxt.crm.utils.AssertUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ModuleService extends BaseService<Module,Integer> {

    @Resource
    private ModuleMapper moduleMapper;

    @Resource
    private PermissionMapper permissionMapper;

    /**
     * 查询所有资源信息
     * @return
     */
    public List<TreeDto> findModules(){
        return moduleMapper.selectMoudles();
    }

    /**
     * 查询当前角色拥有的资源信息
     * @param roleId
     * @return
     */
    public List<TreeDto> findModulesByRoleId(Integer roleId){
        //获取所有资源信息
        List<TreeDto> tlist = moduleMapper.selectMoudles();
        //获取当前角色的拥有的资源信息
        List<Integer> roleHasModules=permissionMapper.selectModuleByRoleId(roleId);
        //遍历
        for (TreeDto treeDto: tlist) {
            if(roleHasModules.contains(treeDto.getId())){
                //判断比对，checked=true;
                treeDto.setChecked(true);
            }
        }

        return tlist;
    }


    /**
     * 资源模块 列表查询
     * @return
     */
    public Map<String, Object> queryModules() {
        //准备数据
        Map<String,Object> map=new HashMap<String,Object>();
        //查询所有的资源
        List<Module> mlist=moduleMapper.selectAllModules();
        //准备数据项（未分页）
        map.put("code",0);
        map.put("msg","success");
        map.put("count",mlist.size());
        map.put("data",mlist);
        //返回目标map
        return map;
    }
}
