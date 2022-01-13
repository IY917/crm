package com.yjxxt.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yjxxt.crm.base.BaseService;
import com.yjxxt.crm.bean.Permission;
import com.yjxxt.crm.bean.Role;
import com.yjxxt.crm.mapper.ModuleMapper;
import com.yjxxt.crm.mapper.PermissionMapper;
import com.yjxxt.crm.mapper.RoleMapper;
import com.yjxxt.crm.query.RoleQuery;
import com.yjxxt.crm.utils.AssertUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service
public class RoleService extends BaseService<Role,Integer> {
    @Resource
    private RoleMapper roleMapper;

    @Resource
    private PermissionMapper permissionMapper;

    @Resource
    private ModuleMapper moduleMapper;

    /**
     * 查询所有角色信息
     * @return
     */
    public List<Map<String,Object>> findRoles(Integer userId){
        return roleMapper.selectRoles(userId);
    }

    /**
     * 角色 列表查询
     * @param roleQuery
     * @return
     */
    public Map<String,Object> findRoleByParam(RoleQuery roleQuery){
        //实例化map
        Map<String,Object> map=new HashMap<>();
        //开启分页
        PageHelper.startPage(roleQuery.getPage(),roleQuery.getLimit());
        PageInfo<Role> rlist=new PageInfo<>(selectByParams(roleQuery));
        //准备数据
        map.put("code",0);
        map.put("msg","success");
        map.put("count",rlist.getTotal());
        map.put("data",rlist.getList());
        //返回目标
        return map;
    }

    /**
     * 角色添加
     *      验证：
     *          1）角色名非空
     *          2）角色名唯一
     *      设默认值：
     *          是否有效、创建时间、更新时间
     *      操作是否成功
     * @param role
     */
    public void addRole(Role role){
        AssertUtil.isTrue(StringUtils.isBlank(role.getRoleName()),"角色名不能为空");
        Role temp=roleMapper.selectRoleByName(role.getRoleName());
        AssertUtil.isTrue(temp!=null,"角色已存在");

        role.setIsValid(1);
        role.setCreateDate(new Date());
        role.setUpdateDate(new Date());

        AssertUtil.isTrue(roleMapper.insertHasKey(role)<1,"添加失败");
    }

    /**
     * 角色修改
     *      验证：
     *          1）根据id验证
     *          2）角色名非空
     *          3）角色名唯一
     *      设默认值：
     *          更新时间
     *      操作是否成功
     * @param role
     */
    public void changeRole(Role role){
        Role temp = roleMapper.selectByPrimaryKey(role.getId());
        AssertUtil.isTrue(temp==null,"待修改记录不存在");

        AssertUtil.isTrue(StringUtils.isBlank(role.getRoleName()),"角色名不能为空");
        Role temp2=roleMapper.selectRoleByName(role.getRoleName());
        AssertUtil.isTrue(temp2!=null && !temp2.getId().equals(role.getId()),"角色已存在");

        role.setUpdateDate(new Date());

        AssertUtil.isTrue(roleMapper.updateByPrimaryKeySelective(role)<1,"修改失败");
    }

    /**
     * 角色 删除（单删）
     *          实际上是把is_valid修改为0（失效）
     * @param role
     */
    public void removeRoleById(Role role) {
        AssertUtil.isTrue(role.getId()==null || selectByPrimaryKey(role.getId())==null,"请选择要修改的数据");
        role.setIsValid(0);
        role.setUpdateDate(new Date());
        AssertUtil.isTrue(roleMapper.updateByPrimaryKeySelective(role)<1,"删除失败");
    }


    /**
     * 授权，
     *      统计当前角色有多少资源，删除，重新添加；
     * @param roleId
     * @param mids
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addGrant(Integer roleId, Integer[] mids) {
        AssertUtil.isTrue(roleId==null|| roleMapper.selectByPrimaryKey(roleId)==null,"请选择角色");
        //统计当前角色的资源数量
        int count=permissionMapper.countRoleModulesByRoleId(roleId);
        if(count>0){
            //删除当前角色的资源信息
            AssertUtil.isTrue(permissionMapper.deleteRoleModuleByRoleId(roleId)!=count,"角色资源分配失败");
        }
        //删除角色的资源信息
        List<Permission> plist=new ArrayList<Permission>();
        if(mids!=null && mids.length>0){
            //遍历mids
            for (Integer mid:mids) {
                //实例化对象
                Permission permission=new Permission();

                permission.setRoleId(roleId);
                permission.setModuleId(mid);
                //权限码
                permission.setAclValue(moduleMapper.selectByPrimaryKey(mid).getOptValue());
                permission.setCreateDate(new Date());
                permission.setUpdateDate(new Date());

                plist.add(permission);
            }
        }
        AssertUtil.isTrue(permissionMapper.insertBatch(plist)!=plist.size(),"授权失败");
    }
}
