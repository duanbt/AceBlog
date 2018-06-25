/**
 * 注册用户 JS.
 */

$(function () {

    $("#registerForm").ajaxForm({
        url:"/register",
        type:"POST",
        beforeSend: function(request) {
            // 获取 CSRF Token
            var csrfToken = $("meta[name='_csrf']").attr("content");
            var csrfHeader = $("meta[name='_csrf_header']").attr("content");
            request.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token
        },
        success: function (data) {
            if (data.success) {
                var user = data.body;
                var result = data.message + "<br>您的账号为：" + "<span class='text-dark h5'>" + user.username;
                $("#registerResult").html(result);
                $("#registerResultModel").modal('show');
            } else {
                toastr.warning(data.message);
            }
        },
        error: function () {
            toastr.error("error!");
        }
    });
    //注册
    // $("#registerBtn").click(function () {
    //    $.ajax({
    //        url:"/register",
    //        type:"POST",
    //        data:$("#registerForm").serialize(),
    //        beforeSend: function(request) {
    //            // 获取 CSRF Token
    //            var csrfToken = $("meta[name='_csrf']").attr("content");
    //            var csrfHeader = $("meta[name='_csrf_header']").attr("content");
    //            request.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token
    //        },
    //        success: function (data) {
    //            if (data.success) {
    //                var user = data.body;
    //                var result = data.message + "<br>您的账号为：" + "<span class='text-dark h5'>" + user.username;
    //                $("#registerResult").html(result);
    //                $("#registerResultModel").modal('show');
    //            } else {
    //                toastr.warning(data.message);
    //            }
    //        },
    //        error: function () {
    //            toastr.error("error!");
    //        }
    //    })
    // });


    $("#loginBtn").click(function () {
        window.location.href = "/login";
    })
});