package com.yjxxt.crm.controller;

import com.yjxxt.crm.base.BaseController;
import com.yjxxt.crm.base.ResultInfo;
import com.yjxxt.crm.bean.SaleChance;
import com.yjxxt.crm.query.SaleChanceQuery;
import com.yjxxt.crm.service.SaleChanceService;
import com.yjxxt.crm.service.UserService;
import com.yjxxt.crm.utils.LoginUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("sale_chance")
public class SaleChanceController extends BaseController {
    @Autowired
    private SaleChanceService saleChanceService;

    @Autowired
    private UserService userService;

    @RequestMapping("index")
    public String index(){
        return "saleChance/sale_chance";
    }

    @RequestMapping("addOrUpdateDialog")
    public String addOrUpdate(Integer id, Model model){
        if(id!=null){
            SaleChance saleChance = saleChanceService.selectByPrimaryKey(id);
            model.addAttribute("saleChance",saleChance);
        }
        return "saleChance/add_update";
    }

    /**
     * 查询
     * @param saleChanceQuery
     * @return
     */
    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> list(SaleChanceQuery saleChanceQuery){
        //调用方法获取数据
        Map<String, Object> map = saleChanceService.querySaleChanceByParams(saleChanceQuery);
        //map——>json
        //转发
        return map;
    }

    /**
     * 添加
     * @param req
     * @param saleChance
     * @return
     */
    @RequestMapping("add")
    @ResponseBody
    public ResultInfo add(HttpServletRequest req, SaleChance saleChance){
        //查询用户id
        int userId = LoginUserUtil.releaseUserIdFromCookie(req);
        String trueName = userService.selectByPrimaryKey(userId).getTrueName();
        //创建人
        saleChance.setCreateMan(trueName);
        //添加操作
        saleChanceService.addSaleChance(saleChance);
        return success("添加成功");
    }

    /**
     * 修改
     * @param saleChance
     * @return
     */
    @RequestMapping("update")
    @ResponseBody
    public ResultInfo update(SaleChance saleChance){
        //修改操作
        saleChanceService.changeSaleChance(saleChance);
        return success("修改成功");
    }

    /**
     * 删除
     * @param ids
     * @return
     */
    @RequestMapping("dels")
    @ResponseBody
    public ResultInfo deletes(Integer[] ids){
        //批量删除操作
        saleChanceService.removeSaleChanceIds(ids);
        return success("删除成功");
    }
}
