layui.use(['table','layer'],function(){
    var layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        table = layui.table;

    /**
     * 加载数据表格
     */
        //用户列表展示
    var tableIns = table.render({
            id : "userTable",
            elem: '#userList',  // 表格绑定的ID
            url : ctx+'/user/list',  // 访问数据的地址
            cellMinWidth : 95,
            page : true,   // 开启分页
            height : "full-125",
            limits : [10,15,20,25],
            limit : 10,  // 默认每页显示数量
            toolbar: "#toolbarDemo",
            cols : [[
                {type: "checkbox", fixed:"center"},
                {field: "id", title:'编号',fixed:"true"},
                {field: 'userName', title: '用户名称',align:"center"},
                {field: 'trueName', title: '真实姓名',  align:'center'},
                {field: 'email', title: '用户邮箱', align:'center'},
                {field: 'phone', title: '用户号码', align:'center'},

                {field: 'createDate', title: '创建时间', align:'center'},
                {field: 'updateDate', title: '修改时间', align:'center'},

                {title: '操作', templet:'#userListBar',fixed:"right",align:"center", minWidth:150}
            ]]
        });

    /**
     * 搜索按钮点击事件
     */
    $(".search_btn").click(function (){
        /**
         * 表格重载
         *  多条件查询
         */
        tableIns.reload({
            // 设置需要传递给后端的参数
            where:{ // 设定异步数据接口的额外参数， 任意设置
                // 通过文本框，设置传递的参数
                userName: $("[name='userName']").val(),  // 用户名称
                email: $("[name='email']").val(),  // 邮箱
                phone: $("[name='phone']").val()  // 手机号
            },
            page:{
                curr: 1 // 从第一页开始
            }
        });
    });

    /**
     * 监听头部工具栏
     */
    table.on('toolbar(users)',function (data){
        console.log(data);

        if(data.event == "add"){ // 添加用户

            // 打开添加/修改用户对话框
            openAddOrUpdateUserDialog();

        }else if(data.event == "del"){ //
            // 获取被选中的数据 信息
            var checkStatus = table.checkStatus(data.config.id);

            // 删除多个用户记录
            deleteUsers(checkStatus.data);
        }
    });

    /**
     * 删除多条用户记录
     * @param userData
     */
    function deleteUsers(userData){
        // 判断用户是否选择了要删除的记录
        if(userData.length == 0){
            layer.msg("请选择要删除的记录",{icon:5});
            return;
        }
        // 选文用户是否确认删除
        layer.confirm('确定删除选中的机会数据？', {
            btn: ['确定','取消'] //按钮
        }, function(index){
            layer.close(index);
            var ids= "ids=";
            for(var i=0;i<userData.length;i++){
                if(i<userData.length-1){
                    ids=ids+userData[i].id+"&ids=";
                }else {
                    ids=ids+userData[i].id
                }
            }
            $.ajax({
                type:"post",
                url:ctx+"/user/delete",
                data:ids,
                dataType:"json",
                success:function (result) {
                    if(result.code==200){
                        layer.msg("删除成功！",{icon:6});
                        tableIns.reload();

                    }else{
                        layer.msg(result.msg, {icon: 5});
                    }
                }
            })
        });
    }

    /**
     * 监听行工具栏
     */
    table.on('tool(users)',function (data){

        if(data.event == "edit"){ // 更新用户

            // 打开添加/修改用户对话框
            openAddOrUpdateUserDialog(data.data.id);

        }else if(data.event == "del"){ //

            // 删除单条用户记录
            deleteUser(data.data.id);
        }

    });

    /**
     * 删除单条用户记录
     * @param id
     */
    function deleteUser(id){
        layer.confirm('确定删除当前数据？', {icon: 3, title: "用户管理"}, function (index) {

            layer.close(index);

            $.ajax({
                type: "post",
                url: ctx + "/user/delete",
                data: {
                    ids: id
                },
                success: function (result) {
                    if (result.code == 200) {
                        layer.msg("删除成功！", {icon: 6});
                        tableIns.reload();
                    } else {
                        layer.msg(result.msg, {icon: 5});
                    }
                }
            });
        });
    }


    function openAddOrUpdateUserDialog(id){
        var title = "<h3>用户管理 - 添加用户</h3>";
        var url = ctx + "/user/toAddOrUpdateUserPage";
        // 判断id是否为空，如果为空，则为添加操作，否则是修改操作
        if(id != null && id != ''){
            title = "<h3>用户管理 - 更新用户</h3>";
            url += "?id="+id; // 传递主键，查询数据
        }
        // iframe层
        layui.layer.open({
            //
            type: 2,
            //
            title: title,
            //
            area: ['650px','400px'],
            //
            content: url,
            //
            maxmin: true
        });
    }

});