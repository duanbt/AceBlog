/**
 * 用户管理 JS.
 */




$(function () {

    //分页条
    function initPageTool(){
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
                getUserByName(pageIndex, pageSize);
                _pageSize = pageSize;
                _pageIndex = pageIndex;
            }
        });
    }
//初始化分页条
    initPageTool();


//根据用户名、当前页码、页面大小获取用户
    function getUserByName(pageIndex, pageSize) {
        $.ajax({
            url: "/users",
            contentType: "application/json",
            data: {
                "async": true,
                "pageIndex": pageIndex,
                "pageSize": pageSize,
                "name": $("#searchName").val()
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
    $("#searchNameBtn").click(function () {
        getUserByName(1, _pageSize);
    });

//获取添加用户的界面
    $("#addUser").click(function () {
        $.ajax({
            url: "/users/add",
            success: function (data) {
                if (data.success != null) {
                    $("#userFormContainer").html(data.message);
                } else {
                    $("#userFormContainer").html(data);
                }
            },
            error: function (data) {
                toastr.error("error!")
            }
        })
    });

//提交新增或保存表单,同时清空表单
    $("#submitEdit").click(function () {
        $.ajax({
            url: "/users",
            type: "POST",
            data: $("#userForm").serialize(),
            success: function (data) {
                if (data.success) {
                    $("#userForm")[0].reset();
                    //重新加载表格
                    getUserByName(_pageIndex, _pageSize);
                    toastr.success("保存成功");
                } else {
                    toastr.warning(data.message);
                }
            },
            error: function () {
                toastr.error("error!");
            }
        })
    });

//获取编辑用户的界面
    $("#mainContainer").on("click", ".blog-edit-user", function () {
        $.ajax({
            url: "users/edit/" + $(this).attr("userId"),
            success: function (data) {
                if (data.success != null) {
                    $("#userFormContainer").html(data.message);
                } else {
                    $("#userFormContainer").html(data);
                }
            },
            error: function () {
                toastr.error("error!");
            }
        });
    });

//删除用户
    $("#mainContainer").on("click", ".blog-delete-user", function () {
        $.ajax({
            url: "/users/" + $(this).attr("userId"),
            type: 'DELETE',
            beforeSend: function (request) {
                // 获取 CSRF Token
                var csrfToken = $("meta[name='_csrf']").attr("content");
                var csrfHeader = $("meta[name='_csrf_header']").attr("content");
                request.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token
            },
            success: function (data) {
                if (data.success) {
                    //重新加载表格
                    getUserByName(_pageIndex, _pageSize);
                    toastr.success("删除成功");
                } else {
                    console.log("删除失败");
                    toastr.warning(data.message);
                }
            },
            error: function () {
                toastr.error("error!");
            }
        });

    });

});




