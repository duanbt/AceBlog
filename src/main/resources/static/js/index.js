$(function () {

//分页条
    function initPageTool() {
        $("#pageTool").Paging({
            pagesize: _pageSize,
            count: _count,
            current: _pageIndex,
            prevTpl: '<i class="fa fa-angle-left"></i>',
            nextTpl: '<i class="fa fa-angle-right"></i>',
            firstTpl: '<i class="fa fa-angle-double-left"></i>',
            lastTpl: '<i class="fa fa-angle-double-right"></i>',
            hash: true,
            callback: function (pageIndex, pageSize, pageCount) {
                getBlogs(pageIndex, pageSize);
                _pageSize = pageSize;
                _pageIndex = pageIndex;
            }
        });
    }

    //初始化分页条
    initPageTool();

    //根据关键字、页面索引、页面大小获取博客列表
    function getBlogs(pageIndex, pageSize) {

        $.ajax({
            url: "/blogs",
            contentType: 'application/json',
            data: {
                "async": true,
                "order": $("#headerNav .nav-item .active").data("order"),
                "pageIndex": pageIndex,
                "pageSize": pageSize,
                "keyword": $("#indexKeyword").val()
            },
            success: function (data) {
                if (data.success != null) {
                    $("#mainContainer").html(data.message);
                } else {
                    $("#mainContainer").html(data);
                }
                initPageTool();

            },
            error: function () {
                toastr.error("error!");
            }
        });
    }

    //关键字搜索
    $("#indexSearch").click(function () {
        getBlogs(1, _pageSize);
    });

    //最新/最热切换
    $("#headerNav .nav-item .nav-link").click(function () {
        //清空搜索框
        $("#indexKeyword").val('');

        var url = $(this).attr("url");

        $("#headerNav .nav-item .nav-link").removeClass("active");
        $(this).addClass("active");

        $.ajax({
            url: url + '&async=true',
            success: function (data) {
                if (data.success != null) {
                    $("#mainContainer").html(data.message);
                } else {
                    $("#mainContainer").html(data);
                }
            },
            error: function () {
                toastr.error("error!");
            }
        });


    })


});
