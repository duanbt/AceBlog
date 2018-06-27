/**
 * 个人设置页 js
 */

$(function () {
    //保存个人设置
    $("#submitBtn").click(function () {
        var url = $("#submitBtn").attr("url");
        $.ajax({
            url: url,
            type: "POST",
            data: $("#userForm").serialize(),
            beforeSend: function (request) {
                // 获取 CSRF Token
                var csrfToken = $("meta[name='_csrf']").attr("content");
                var csrfHeader = $("meta[name='_csrf_header']").attr("content");
                request.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token
            },
            success: function (data) {
                if (data.success) {
                    toastr.success("修改成功");
                } else {
                    toastr.warning(data.message);
                }
            },
            error: function () {
                toastr.error("error!");
            }
        })
    });

    var avatarApi;
    //获取编辑用户头像页面
    $(".blog-edit-avatar").click(function () {
        avatarApi = "/u/" + $(this).attr("username") + "/avatar";
        $.ajax({
            url: avatarApi,
            success: function (data) {
                if(data.success != null){
                    $("#avatarFormContainer").html(data.message);
                }else {
                    $("#avatarFormContainer").html(data);
                }
            },
            error: function () {
                toastr.error("error!");
            }
        });
    });

    /**
     * 将以base64的图片url数据转换为Blob
     * @param urlData 用url方式表示的base64图片数据
     */
    function convertBase64UrlToBlob(urlData) {
        var imgDataType = urlData.split(';')[0];
        var imgType = imgDataType.substring(imgDataType.indexOf(":")+1);//取图片类型  ，类似 image/jpg 等
        var bytes = window.atob(urlData.split(',')[1]);        //去掉url的头，并转换为byte

        //处理异常,将ascii码小于0的转换为大于0
        var ab = new ArrayBuffer(bytes.length);
        var ia = new Uint8Array(ab);
        for (var i = 0; i < bytes.length; i++) {
            ia[i] = bytes.charCodeAt(i);
        }

        return new Blob([ab], {type: imgType});
    }


    //提交用户头像的图片数据
    $("#submitEditAvatar").click(function () {
        var form = $("#avatarForm")[0];
        var formData = new FormData(form);
        var base64Codes = $(".cropped > img").attr("src");
        if (base64Codes) {
            formData.append("file", convertBase64UrlToBlob(base64Codes));
        } else {
            toastr.info("请先裁剪再提交");
            return;
        }

        $.ajax({
            url: _fileServerUrl,
            type: 'POST',
            data: formData,
            cache: false,
            contentType:false,
            processData: false,
            success: function (data) {//文件服务器返回图片地址
                //保存头像Url到数据库
                $.ajax({
                    url: avatarApi,
                    type: 'POST',
                    contentType: "application/json; charset=utf-8",
                    data: JSON.stringify({
                        "id": $("#userId").val(),
                        "avatar": data
                    }),
                    beforeSend: function (request) {
                        // 获取 CSRF Token
                        var csrfToken = $("meta[name='_csrf']").attr("content");
                        var csrfHeader = $("meta[name='_csrf_header']").attr("content");
                        request.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token
                    },
                    success: function (data) {
                        if (data.success) {
                            $(".blog-avatar").attr("src", data.body);
                            $("#avatar-modal").modal('hide');
                            toastr.success("头像修改成功");
                        } else {
                            toastr.warning(data.message);
                        }
                    },
                    error: function () {
                        toastr.error("error!")
                    }
                })
            },
            error: function () {
                toastr.error("error!");
            }
        })
    })
});
