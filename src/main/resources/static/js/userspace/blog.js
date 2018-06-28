$(function () {

    //正文初始化
    $('#editSection').tuiEditor({
        initialValue: _content
    });

    //侧边栏目录插件
    $('#editSection').autoc({
        title: '目录',
        isAnchorsOnly: false,
        hasChapterCodeInDirectory: true
    });


    // 处理删除博客事件
    $(".blog-content-container").on("click", ".blog-delete-blog", function () {
        $.ajax({
            url: _blogUrl,
            type: 'DELETE',
            beforeSend: function (request) {
                // 获取 CSRF Token
                var csrfToken = $("meta[name='_csrf']").attr("content");
                var csrfHeader = $("meta[name='_csrf_header']").attr("content");
                request.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token
            },
            success: function (data) {
                if (data.success) {
                    // 成功后，重定向
                    window.location = data.body;
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


    //点赞
    $(".blog-content-container").on("click", "#submitVote", function () {
        $.ajax({
            url: '/votes',
            type: 'POST',
            data: {"blogId": _blogId},
            beforeSend: function (request) {
                // 获取 CSRF Token
                var csrfToken = $("meta[name='_csrf']").attr("content");
                var csrfHeader = $("meta[name='_csrf_header']").attr("content");
                request.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token
            },
            success: function (data) {
                if (data.success) {
                    $("#submitVote").attr("hidden", "hidden");
                    $("#cancelVote").removeAttr("hidden");
                    $("#cancelVote").attr("voteId", data.body.id);
                    //点赞量表面+1
                    var $voteSize = $(".blog-status-display .fa-thumbs-o-up");
                    $voteSize.text(Number($voteSize.text()) + 1);
                    toastr.success("已赞!")
                }else {
                    toastr.info("登录后才能点赞哦~");
                }
            },
            error: function () {
                toastr.error("error!");
            }
        });
    });

    //取消点赞
    $(".blog-content-container").on("click", "#cancelVote", function () {
        $.ajax({
            url:'/votes/'+$(this).attr("voteId") + '?blogId=' + _blogId,
            type:'DELETE',
            beforeSend:function (request) {
                // 获取 CSRF Token
                var csrfToken = $("meta[name='_csrf']").attr("content");
                var csrfHeader = $("meta[name='_csrf_header']").attr("content");
                request.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token
            },
            success:function (data) {
                if(data.success){
                    $("#cancelVote").attr("hidden", "hidden");
                    $("#submitVote").removeAttr("hidden");
                    //点赞量表面-1
                    var $voteSize = $(".blog-status-display .fa-thumbs-o-up");
                    $voteSize.text(Number($voteSize.text()) - 1);
                    toastr.success("已取消赞");
                }else {
                    toastr.warning(data.message);
                }
            },
            error:function () {
                toastr.error("error!");
            }
        })
    })



});