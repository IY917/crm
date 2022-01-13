
layui.use(['form','jquery','jquery_cookie','layer'], function () {
    var form = layui.form,
        layer = layui.layer,
        $ = layui.jquery,
        $ = layui.jquery_cookie($);

    //监听提交
    form.on('submit(saveBtn)', function(data){
        //layer.msg(JSON.stringify(data.field));

        var fieldData = data.field;

        //发送ajax
        $.ajax({
            type:"post",
            url:ctx+"/user/updatePwd",
            data:{
                "oldPassword":fieldData.old_password,
                "newPassword":fieldData.new_password,
                "confirmPassword":fieldData.again_password,
            },
            dataType:"json",
            success:function (data){
                //ResultInfo
                if(data.code==200){
                    layer.msg("修改密码成功了，系统三秒钟后退出！",function (){
                        //清空Cookie
                        $.removeCookie("userIdStr",{domain:"localhost",path:"/crm"});
                        $.removeCookie("userName",{domain:"localhost",path:"/crm"});
                        $.removeCookie("trueName",{domain:"localhost",path:"/crm"});
                        //跳转页面
                        window.parent.location.href=ctx+"/index";
                    });
                }else {
                    //失败
                    layer.msg(data.msg);
                }
            }
        });

        //阻止表单跳转
        return false;
    });
});
