package com.yjxxt.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yjxxt.crm.base.BaseService;
import com.yjxxt.crm.bean.SaleChance;
import com.yjxxt.crm.query.SaleChanceQuery;
import com.yjxxt.crm.utils.AssertUtil;
import com.yjxxt.crm.utils.PhoneUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class SaleChanceService extends BaseService<SaleChance,Integer> {
    /**
     * 条件查询——>列表
     *
     * code:
     * msg:
     * count:
     * data
     */
    public Map<String,Object> querySaleChanceByParams(SaleChanceQuery saleChanceQuery){
        //实例化
        Map<String,Object> map=new HashMap<>();
        //实例化分页单位
        PageHelper.startPage(saleChanceQuery.getPage(),saleChanceQuery.getLimit());
        //开始分页
        PageInfo<SaleChance> plist=new PageInfo<>(selectByParams(saleChanceQuery));
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
     * @param saleChance
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addSaleChance(SaleChance saleChance){
        //验证
        checkSaleChanceParma(saleChance.getCustomerName(),saleChance.getLinkMan(),saleChance.getLinkPhone());
        //设定默认值
        //state（0--未分配，1--已经分配了）
        //devResult(0--未开发，1-开发中，2-开发成功了，3,-开发失败)
        //未分配
        if(StringUtils.isBlank(saleChance.getAssignMan())){
            saleChance.setState(0);
            saleChance.setDevResult(0);
        }
        //已分配
        if(!StringUtils.isBlank(saleChance.getAssignMan())){
            saleChance.setState(1);
            saleChance.setDevResult(1);
            saleChance.setAssignTime(new Date()); //分配时间
        }
        //创建时间、更新时间、是否有效
        saleChance.setCreateDate(new Date());
        saleChance.setUpdateDate(new Date());
        saleChance.setIsValid(1);
        //是否添加成功
        AssertUtil.isTrue(insertSelective(saleChance)<1,"添加失败");
    }

    /**
     * 验证方法
     * @param customerName 客户名
     * @param linkMan  联系人
     * @param linkPhone  联系电话
     */
    private void checkSaleChanceParma(String customerName, String linkMan, String linkPhone) {
        //客户名非空
        AssertUtil.isTrue(StringUtils.isBlank(customerName),"请输入客户名称");
        //联系人非空
        AssertUtil.isTrue(StringUtils.isBlank(linkMan),"请输入联系人");
        //联系电话 非空，11位的合法的手机号
        AssertUtil.isTrue(StringUtils.isBlank(linkPhone),"请输入联系人电话");
        AssertUtil.isTrue(!PhoneUtil.isMobile(linkPhone),"请输入合法的手机号");
    }

    /**
     * 修改
     * @param saleChance
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void changeSaleChance(SaleChance saleChance){
        SaleChance temp = selectByPrimaryKey(saleChance.getId());
        AssertUtil.isTrue(temp==null,"待修改记录不存在");
        //验证
        checkSaleChanceParma(saleChance.getCustomerName(),saleChance.getLinkMan(),saleChance.getLinkPhone());
        //设定默认值
        //未分配
        if(StringUtils.isBlank(temp.getAssignMan()) && StringUtils.isNotBlank(saleChance.getAssignMan())){
            saleChance.setState(1);
            saleChance.setDevResult(1);
            saleChance.setAssignTime(new Date());  //分配时间
        }
        //已分配
        if(StringUtils.isNotBlank(temp.getAssignMan()) && StringUtils.isBlank(saleChance.getAssignMan())){
            saleChance.setState(0);
            saleChance.setDevResult(0);
            saleChance.setAssignTime(null);
            saleChance.setAssignMan("");
        }
        //更新时间
        saleChance.setUpdateDate(new Date());
        //是否添加成功
        AssertUtil.isTrue(updateByPrimaryKeySelective(saleChance)<1,"修改失败");
    }

    /**
     * 批量删除
     * @param ids
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void removeSaleChanceIds(Integer [] ids){
        AssertUtil.isTrue(ids==null && ids.length==0,"请选择要删除的数据");
        AssertUtil.isTrue(deleteBatch(ids)!=ids.length,"批量删除失败");
    }
}
