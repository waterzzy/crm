layui.use(['form', 'layer','formSelects'], function () {
    var form = layui.form,
        layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery;
    var formSelects = layui.formSelects;

    /**
     * 添加或更新⽤户
     */
    form.on("submit(addOrUpdateUser)", function (data) {
        // 弹出loading层
        var index = top.layer.msg('数据提交中，请稍候...', {icon: 16, time: false, shade: 0.8});

        // 得到所有表单元素的值
        var formData = data.field;

        var url = ctx + "/user/add";
        if($("input[name='id']").val()){
            url = ctx + "/user/update";
        }
        $.post(url, formData, function (res) {
            if (res.code == 200) {
                setTimeout(function () {
                    // 关闭弹出层（返回值为index的弹出层）
                    top.layer.close(index);
                    top.layer.msg("操作成功！",{icon:6});
                    // 关闭所有ifream层
                    layer.closeAll("iframe");
                    // 刷新⽗⻚⾯
                    parent.location.reload();
                }, 500);
            } else {
                layer.msg(res.msg, {icon: 5});
            }
        });
        return false;
    });


    /**
     * 关闭弹出层
     */
    $("#closeBtn").click(function () {
        // 先得到当前iframe层的索引
        var index = parent.layer.getFrameIndex(window.name);
        // 再执⾏关闭
        parent.layer.close(index);
    });

    /**
     * 加载角色下拉框
     */
    var userId = $("[name = 'id']").val();
    formSelects.config("selectId",{
        type:"post",
        searchUrl:ctx + "/role/queryAllRoles?userId="+userId,
        //⾃定义返回数据中name的key, 默认 name
        keyName: 'roleName',
        //⾃定义返回数据中value的key, 默认 value
        keyVal: 'id'
    },true);
});