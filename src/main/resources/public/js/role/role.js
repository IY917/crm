layui.use(['table', 'layer'], function () {
    var layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        table = layui.table;

    //角色列表展示
    var tableIns = table.render({
        elem: '#roleList',
        url: ctx + '/role/list',
        cellMinWidth: 95,
        page: true,
        height: "full-125",
        limits: [10, 15, 20, 25],
        limit: 10,
        toolbar: "#toolbarDemo",
        id: "roleListTable",
        cols: [[
            {type: "checkbox", fixed: "left", width: 50},
            {field: "id", title: '编号', fixed: "true", width: 80},
            {field: 'roleName', title: '角色名', minWidth: 50, align: "center"},
            {field: 'roleRemark', title: '角色备注', minWidth: 100, align: 'center'},
            {field: 'createDate', title: '创建时间', align: 'center', minWidth: 150},
            {field: 'updateDate', title: '更新时间', align: 'center', minWidth: 150},
            {title: '操作', minWidth: 150, templet: '#roleListBar', fixed: "right", align: "center"}
        ]]
    });

    // 多条件搜索
    $(".search_btn").on("click", function () {
        tableIns.reload({
            page: {
                curr: 1 //重新从第 1 页开始
            },
            where: {
                roleName: $("input[name='roleName']").val()
            }
        })
    });


    //头部工具栏触发事件
    table.on('toolbar(roles)', function (obj) {
        var checkStatus = table.checkStatus(obj.config.id);

        switch (obj.event) {
            case 'add':
                //layer.msg('添加');
                addOrUpdateRolePage();
                break;
            case 'grant':
                //授权
                toRoleGrant(checkStatus.data);
                break;
        }
        ;
    });

    /**
     * 添加修改函数
     * @param roleId
     */
    function addOrUpdateRolePage(roleId) {
        var title = "<h3>角色模块——添加</h3>";
        var url = ctx + "/role/addOrUpdate";
        //根据id判断是添加页面还是修改页面 （非空即为true）
        if (roleId) {
            title = "<h3>角色模块——更新</h3>";
            url = url + "?roleId=" + roleId;
        }
        /*弹出层*/
        layui.layer.open({
            title: title,
            content: url,
            type: 2,  //iframe
            area: ["600px", "280px"],
            maxmin: true
        });
    }

    //授权
    function toRoleGrant(datas) {
        if (datas.length == 0) {
            layer.msg("请选择要授权的角色");
            return ;
        }
        if (datas.length > 1) {
            layer.msg("不能批量授权");
            return ;
        }
        var title = "<h3>角色模块——授权</h3>";
        var url = ctx + "/role/roleGrant?roleId=" + datas[0].id;
        layui.layer.open({
            title: title,
            content: url,
            type: 2,  //iframe
            area: ["600px", "280px"],
            maxmin: true
        });

    }

    //行内工具条事件
    table.on('tool(roles)', function (obj) {
        var data = obj.data; //获得当前行数据
        var layEvent = obj.event; //获得 lay-event 对应的值（也可以是表头的 event 参数对应的值）
        var tr = obj.tr; //获得当前行 tr 的 DOM 对象（如果有的话）

        if (layEvent === 'del') { //删除
            layer.confirm("你真的要删除么", function (index) {
                $.post(ctx + "/role/delete", {"id": data.id}, function (result) {
                    if (result.code == 200) {
                        layer.msg("删除成功", {icon: 6});
                        layer.close(index);
                        tableIns.reload();
                    } else {
                        layer.msg(result.msg, {icon: 5});
                    }
                }, "json");
            });
        } else if (layEvent === 'edit') { //编辑
            //添加、编辑共用一个页面
            addOrUpdateRolePage(data.id);
        }
    });
});