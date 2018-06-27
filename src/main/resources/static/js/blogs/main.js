/**
 * 博客管理 JS.
 */

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
                getBlog(pageIndex, pageSize);
                _pageSize = pageSize;
                _pageIndex = pageIndex;
            }
        });
    }

//初始化分页条
    initPageTool();


//根据用户名、当前页码、页面大小获取用户
    function getBlog(pageIndex, pageSize) {
        $.ajax({
            url: "/manageBlog",
            contentType: "application/json",
            data: {
                "async": true,
                "pageIndex": pageIndex,
                "pageSize": pageSize,
                "username":$("#searchName").val(),
                "title": $("#searchTitle").val()
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
        })
    }


//搜索
    $("#searchBtn").click(function () {
        getBlog(1, _pageSize);
    });

    // 处理删除博客事件
    $("#mainContainer").on("click", ".blog-delete-blog", function () {
        $.ajax({
            url: '/manageBlog/' + $(this).attr("blogId"),
            type: 'DELETE',
            beforeSend: function (request) {
                // 获取 CSRF Token
                var csrfToken = $("meta[name='_csrf']").attr("content");
                var csrfHeader = $("meta[name='_csrf_header']").attr("content");
                request.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token
            },
            success: function (data) {
                if (data.success) {
                    getBlog(_pageIndex, _pageSize);
                    toastr.success("删除成功");
                } else {
                    toastr.warning(data.message);
                }
            },
            error: function () {
                toastr.error("error!");
            }
        });
    });
});