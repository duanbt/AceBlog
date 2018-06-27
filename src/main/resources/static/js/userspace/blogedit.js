/**
 * 博客编辑页面 JS.
 */
//设置标签输入框
$("#tagsInput").tagsinput({
    tagClass: 'badge-secondary mb-2',
    maxTags: 5,
    maxChars: 20,
    trimValue: true,
    confirmKeys: [13, 44, 188]
});


$(function () {

    //初始化tui.editor编辑器
    var editor = new tui.Editor({
        el: document.querySelector('#editSection'),
        initialEditType: 'wysiwyg',
        initialValue: _content,
        previewStyle: 'vertical',
        height: '700px',
        language: 'zh_CN',
        exts: ['scrollSync', 'colorSyntax']
    });

    //删除图片上传按钮
    editor._ui.toolbar.buttons[11].remove();

    //初始化下拉选择框
    $('#catalogSelect').chosen();


    //发布博客
    $("#submitBlog").click(function () {
        var mdContent = editor.getMarkdown();
        // var htmlContent = editor.getHtml();

        $.ajax({
            url: '/u/' + $(this).attr("username") + '/blogs/edit',
            type: 'POST',
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify({
                "id": $('#id').val(),
                "title": $("#title").val(),
                "summary": $("#summary").val(),
                "content": mdContent,
                // "htmlContent":htmlContent,
                "catalog": {"id": $("#catalogSelect").val()},
                "tags": $("#tagsInput").val()

            }),
            beforeSend: function (request) {
                // 获取 CSRF Token
                var csrfToken = $("meta[name='_csrf']").attr("content");
                var csrfHeader = $("meta[name='_csrf_header']").attr("content");
                request.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token
            },
            success: function (data) {
                if (data.success) {
                    window.location.href = data.body;
                } else {
                    toastr.warning(data.message);
                }
            },
            error: function () {
                toastr.error("error!");
            }
        })
    });

    //获取新增分类的页面
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
                    toastr.success('分类保存成功');
                    // 成功后，刷新下拉选择框
                    getCatalogsSelect(_username);
                } else {
                    toastr.warning(data.message);
                }
            },
            error: function () {
                toastr.error("error!");
            }
        })
    });

    // 刷新分类下拉选择框方法
    function getCatalogsSelect(username) {
        $.ajax({
            url: '/catalogs/select',
            type: 'GET',
            data: {"username": username},
            success: function (data) {
                $("#catalogSelectContainer").html(data);
                $('#catalogSelect').chosen();
            },
            error: function () {
                toastr.error("error!");
            }
        });
    }
});

