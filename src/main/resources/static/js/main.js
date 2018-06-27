$(function () {
    //回到顶部插件
    $.scrollUp({
        animation: 'fade',
        scrollImg: {
            active: true,
            type: 'background',
            src: '/images/top.png'
        }
    });

    //退出登陆
    $("#logout").click(function () {
        $.ajax({
            url: "/logout",
            type: "POST",
            beforeSend: function (request) {
                // 获取 CSRF Token
                var csrfToken = $("meta[name='_csrf']").attr("content");
                var csrfHeader = $("meta[name='_csrf_header']").attr("content");
                request.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token
            },
            success: function () {
                window.location.reload();
            }
        })
    });


    toastr.options = {
        "closeButton": true, //是否显示关闭按钮
        "debug": false, //是否使用debug模式
        "positionClass": "toast-bottom-right",//弹出窗的位置
        "showDuration": "300",//显示的动画时间
        "hideDuration": "300",//消失的动画时间
        "timeOut": "4000", //展现时间
        "extendedTimeOut": "1000",//加长展示时间
        "showEasing": "swing",//显示时的动画缓冲方式
        "hideEasing": "linear",//消失时的动画缓冲方式
        "showMethod": "fadeIn",//显示时的动画方式
        "hideMethod": "fadeOut" //消失时的动画方式
    };


    var tag; //保存当前标签

    //首页分页条
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

    //初始化首页分页条
    if (window.location.pathname.indexOf("/blogs") === 0) {
        initPageTool();
    }

    //根据关键字、页面索引、页面大小获取博客列表
    function getBlogs(pageIndex, pageSize) {
        $.ajax({
            url: "/blogs",
            contentType: 'application/json',
            data: {
                "async": true,
                "order": $("#headerNav .active").data("order"),
                "pageIndex": pageIndex,
                "pageSize": pageSize,
                "keyword": $("#indexKeyword").val(),
                "tag": tag
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
        //清空tag
        tag = null;
        if (window.location.pathname.indexOf("/blogs") !== 0) {//非首页请求，跳转到首页
            window.location.href = '/blogs/?keyword=' + $("#indexKeyword").val();
        } else {
            getBlogs(1, _pageSize);
        }

    });

//最新/最热切换
    $("#headerNav .nav-link").click(function () {
        //清空搜索框
        $("#indexKeyword").val('');
        //清空tag
        tag = null;

        $("#headerNav .nav-link").removeClass("active");
        $(this).addClass("active");

        if (window.location.pathname.indexOf("/blogs") !== 0) {//非首页请求，跳转到首页
            window.location.href = '/blogs/?order=' + $("#headerNav .active").data("order");
        } else {
            getBlogs(1, _pageSize);
        }
    });


    $(".blog-tag").click(function () {
        //清空搜索框
        $("#indexKeyword").val('');
        //清空 顶栏最新/最热选中
        $("#headerNav .nav-link").removeClass("active");
        tag = $(this).attr("tag");

        if (window.location.pathname.indexOf("/blogs") !== 0) {//非首页请求，跳转到首页
            window.location.href = '/blogs/?tag=' + tag;
        } else {
            getBlogs(1, _pageSize);
        }
    })


});