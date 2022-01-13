layui.use(['form', 'layer'], function () {
    var form = layui.form,
        layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery;

    /**
     * 监听表单事件
     */
    form.on("submit(addOrUpdateSaleChance)",function (obj){
        // 提交数据时的加载层
        var index = layer.msg("数据提交中,请稍后...",{
            icon:16, // 图标
            time:false, // 不关闭
            shade:0.8 // 设置遮罩的透明度
            });

        var url=ctx+"/sale_chance/add";

        if($("input[name=id]").val()){
            url=ctx+"/sale_chance/update";
        }

        //发送ajax
        $.ajax({
            type:"post",
            url:url,
            data:obj.field,
            dataType:"json",
            success:function (obj){
                if(obj.code==200){
                    layer.msg("添加OK",{icon:6});
                    // 关闭加载层
                    layer.close(index);
                    // 关闭弹出层
                    layer.closeAll("iframe");
                    //刷新页面
                    window.parent.location.reload();
                }else {
                    layer.msg(obj.msg,{icon:5});
                }
            }
        });

        return false;
    })

    /**
     * 取消功能
     */
    $("#closeBtn").click(function (){
        var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
        parent.layer.close(index); //再执行关闭
    });


    /**
     * 添加下拉框
     */
    var assignMan=$("input[name='man']").val();
    $.ajax({
        type: "post",
        url:ctx+"/user/sales",
        dataType:"json",
        success:function (data){
            for (var x in data) {
                if(data[x].id==assignMan){
                    $("#assignMan").append("<option selected value='"+data[x].id+"'>"+data[x].uname+"</option>");
                }else {
                    $("#assignMan").append("<option value='"+data[x].id+"'>"+data[x].uname+"</option>");
                }
            }
            //重新渲染
            layui.form.render("select");
        }
    });
});