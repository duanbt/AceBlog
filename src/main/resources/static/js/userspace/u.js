/*
*个人主页 JS.
* */

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
                getBlogsByName(pageIndex, pageSize);
                _pageSize = pageSize;
                _pageIndex = pageIndex;
            }
        });
    }

    //初始化分页条
    initPageTool();

    //根据用户账号、关键字、页面索引、页面大小获取博客列表
    function getBlogsByName(pageIndex, pageSize) {

        $.ajax({
            url: "/u/" + _username + "/blogs",
            contentType: 'application/json',
            data: {
                "async": true,
                "order": $("#userspaceNavtab .active").data("order"),
                "pageIndex": pageIndex,
                "pageSize": pageSize,
                "catalog": catalogId,
                "keyword": $("#keyword").val()
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
    $("#searchBlogs").click(function () {
        catalogId = null;
        getBlogsByName(1, _pageSize);
    });

    //个人主页 最热/最新切换
    $("#userspaceNavtab .nav-link").click(function () {
        //清空搜索框
        $("#keyword").val('');
        //清空分类
        catalogId = null;
        var url = $(this).attr("url");
        $("#userspaceNavtab .nav-link").removeClass("active");
        $(this).addClass("active");

        $.ajax({
            url: url + '&async=true',
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


    });

    var catalogId;//存储分类id ,用于点击分类，加载文章时传入后台
    //根据分类查询
    $(".blog-content-container").on("click", ".blog-query-by-catalog", function () {
        //清空最新最热
        $("#userspaceNavtab .nav-link").removeClass("active");
        //清空关键字
        $("#keyword").val("");
        catalogId = $(this).attr("catalogId");
        getBlogsByName(1, _pageSize);
    });


    // 获取分类列表方法
    function getCatalogs(username) {
        $.ajax({
            url: '/catalogs',
            type: 'GET',
            data: {"username": username},
            success: function (data) {
                if (data.success != null) {
                    $("#catalogMain").html(data.message);
                } else {
                    $("#catalogMain").html(data);
                }
            },
            error: function () {
                toastr.error("error!");
            }
        });
    }

    //获取分类列表
    getCatalogs(_username);


    // 获取新增分类的页面
    $(".blog-content-container").on("click", ".blog-add-catalog", function () {
        $.ajax({
            url: '/catalogs/edit',
            type: 'GET',
            success: function (data) {
                if (data.success != null) {
                    $("#catalogFormContainer").html(data.message);
                } else {
                    $("#catalogFormContainer").html(data);
                }
            },
            error: function () {
                toastr.error("error!");
            }
        });
    });

    // 获取编辑某个分类的页面
    $(".blog-content-container").on("click", ".blog-edit-catalog", function () {

        $.ajax({
            url: '/catalogs/edit/' + $(this).attr('catalogId'),
            type: 'GET',
            success: function (data) {
                if(data.success != null){
                    $("#catalogFormContainer").html(data.message);
                }else {
                    $("#catalogFormContainer").html(data);
                }
            },
            error: function () {
                toastr.error("error!");
            }
        });
    });

    // 提交分类
    $("#submitEditCatalog").click(function () {
        $.ajax({
            url: '/catalogs',
            type: 'POST',
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify({
                "username": _username,
                "catalog": {
                    "id": $('#catalogId').val(),
                    "name": $('#catalogName').val()
                }
            }),
            beforeSend: function (request) {
                // 获取 CSRF Token
                var csrfToken = $("meta[name='_csrf']").attr("content");
                var csrfHeader = $("meta[name='_csrf_header']").attr("content");
                request.setRequestHeader(csrfHeader, csrfToken); // 添加 CSRF Token
            },
            success: function (data) {
                if (data.success) {
                    toastr.success("分类保存成功");
                    // 成功后，刷新列表
                    getCatalogs(_username);
                } else {
                    toastr.warning(data.message);
                }
            },
            error: function () {
                toastr.error("error!");
            }
        });
    });

    // 删除分类
    $(".blog-content-container").on("click", ".blog-delete-catalog", function () {
        $.ajax({
            url: '/catalogs/' + $(this).attr('catalogId') + '?username=' + _username,
            type: 'DELETE',
            beforeSend: function (request) {
                // 获取 CSRF Token
                var csrfToken = $("meta[name='_csrf']").attr("content");
                var csrfHeader = $("meta[name='_csrf_header']").attr("content");
                request.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token
            },
            success: function (data) {
                if (data.success) {
                    toastr.success(data.message);
                    // 成功后，刷新列表
                    getCatalogs(_username);
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
