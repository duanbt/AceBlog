$(function () {

    //菜单事件
    $(".blog-menu .list-group-item").click(function () {
        var url = $(this).attr("url");

        //移除其他菜单项的active样式，再添加当前点击项的active样式
        $(".blog-menu .list-group-item").removeClass("active");
        $(this).addClass("active");

        //加载点击模块的页面到右侧区域
        $.ajax({
            url: url,
            success: function (data) {
                if (data.success != null) {
                    $("#rightContainer").html(data.message);
                } else {
                    $("#rightContainer").html(data);
                }
            },
            error: function () {
                toastr.error("error");
            }
        });
    });
    //选中菜单第一项并触发点击
    $(".blog-menu .list-group-item:first").trigger("click");
});