layui.use(['table','layer'],function(){
    var layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        table = layui.table;
    /**
     * 用户列表展示
     */
    var  tableIns = table.render( {
        elem: '#userList',
        url : ctx+'/user/list',
        cellMinWidth : 95,
        page : true,
        height : "full-125",
        limits : [10,15,20,25],
        limit : 10,
        toolbar: "#toolbarDemo",
        id : "userListTable",
        cols : [[
            {type: "checkbox", fixed:"left", width:50},
            {field: "id", title:'编号',fixed:"true", width:80},
            {field: 'userName', title: '用户名', minWidth:50, align:"center"},
            {field: 'email', title: '用户邮箱', minWidth:100, align:'center'},
            {field: 'phone', title: '用户电话', minWidth:100, align:'center'},
            {field: 'trueName', title: '真实姓名', align:'center'},
            {field: 'createDate', title: '创建时间', align:'center',minWidth:150},
            {field: 'updateDate', title: '更新时间', align:'center',minWidth:150},
            {title: '操作', minWidth:150, templet:'#userListBar',fixed:"right",align:"center"}
        ]]
    });

    /*实现搜索功能，页面重载*/
    $(".search_btn").click(function () {
        tableIns.reload({
            where: { //设定异步数据接口的额外参数，任意设
                userName: $("input[name=userName]").val()
                ,email: $("input[name=email]").val()
                ,phone: $("input[name=phone]").val()
            }
            ,page: {
                curr: 1 //重新从第 1 页开始
            }
        });
    });


    //头部工具栏触发事件
    table.on('toolbar(users)', function (obj) {
        var checkStatus = table.checkStatus(obj.config.id);
        switch (obj.event) {
            case 'add':
                //layer.msg('添加');
                addOrUpdateUserDialog();
                break;
            case 'del':
                //layer.msg('删除');
                deleteUser(checkStatus.data);
                break;
        };
    });

    /**
     * 添加修改函数
     * @param userId
     */
    function addOrUpdateUserDialog(userId) {
        var title = "<h3>用户模块——添加</h3>";
        var url = ctx + "/user/addOrUpdateUser";
        //根据id判断是添加页面还是修改页面 （非空即为true）
        if (userId) {
            title = "<h3>用户模块——修改</h3>";
            url = url + "?id=" + userId;
        }
        /*弹出层*/
        layui.layer.open({
            title: title,
            content: url,
            type: 2,  //iframe
            area: ["650px", "400px"],
            maxmin: true
        });
    }

    /**
     * 删除函数
     * @param data
     */
    function deleteUser(data) {
        if (data.length == 0) {
            layer.msg("请选择要删除的数据");
            return;
        }

        layer.confirm("你确定要删除这些数据吗?", {
            btn: ["确认", "取消"],
        }, function (index) {
            //关闭询问框
            layer.close(index);
            //收集数据
            var ids = [];
            for (var x in data) {
                ids.push(data[x].id);
            }
            //发送ajax请求
            $.post(ctx + "/user/dels",{"ids": ids.toString()},function (result) {
                if (result.code == 200) {
                    layer.msg("删除成功", {icon: 5});
                    //重新加载
                    tableIns.reload();
                } else {
                    layer.msg(result.msg);
                }
            },"json");
        });
    }

    //行内工具条事件
    table.on('tool(users)', function (obj) {
        var data = obj.data; //获得当前行数据
        var layEvent = obj.event; //获得 lay-event 对应的值（也可以是表头的 event 参数对应的值）
        var tr = obj.tr; //获得当前行 tr 的 DOM 对象（如果有的话）

        if (layEvent === 'del') { //删除
            layer.confirm("你确定要删除这些数据吗?", {
                btn: ["确认", "取消"],
            }, function (index) {
                //关闭询问框
                layer.close(index);
                //发送ajax请求
                //发送ajax请求
                $.post(ctx + "/user/dels",{"ids": data.id},function (result) {
                    if (result.code == 200) {
                        layer.msg("删除成功", {icon: 5});
                        //重新加载
                        tableIns.reload();
                    } else {
                        layer.msg(result.msg);
                    }
                },"json");
            });
        } else if (layEvent === 'edit') { //编辑
            //添加、编辑共用一个页面
            addOrUpdateUserDialog(data.id);
        }
    });
});