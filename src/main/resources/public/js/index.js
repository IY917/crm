
layui.use(['form','jquery','jquery_cookie','layer'], function () {
    var form = layui.form,
        layer = layui.layer,
        $ = layui.jquery,
        $ = layui.jquery_cookie($);

    //监听提交
    form.on('submit(login)', function(data){
        //layer.msg(JSON.stringify(data.field));

        var fieldData = data.field;

        if(fieldData.username=='undefined' || fieldData.username.trim()==''){
            layer.msg("用户名不能为空");
            return false;
        }
        if(fieldData.password=='undefined' || fieldData.password.trim()==''){
            layer.msg("密码不能为空");
            return false;
        }

        //发送ajax
        $.ajax({
            type:"post",
            url:ctx+"/user/login",
            data:{
                userName:fieldData.username,
                userPwd:fieldData.password
            },
            dataType:"json",
            success:function (data){
                //ResultInfo
                if(data.code==200){
                    //成功
                    // layer.msg("登录成功！");
                    //跳转
                    // window.location.href=ctx+"/main";

                    layer.msg("登录成功！",function (){
                        //将用户的数据存储到Cookie
                        $.cookie("userIdStr",data.result.userIdStr);
                        $.cookie("userName",data.result.userName);
                        $.cookie("trueName",data.result.trueName);

                        if($("input[type='checkbox']").is(":checked")){
                            //将用户的数据存储到Cookie,并保存7天
                            $.cookie("userIdStr",data.result.userIdStr,{expires:7});
                            $.cookie("userName",data.result.userName,{expires:7});
                            $.cookie("trueName",data.result.trueName,{expires:7});
                        }

                        //跳转页面
                        window.location.href=ctx+"/main";
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