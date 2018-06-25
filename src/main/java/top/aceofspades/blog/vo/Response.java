package top.aceofspades.blog.vo;

/**
 * 响应 值对象.
 * @author ace
 * @version 1.0
 * @since 2018/6/3 19:07
 */
public class Response {

    private boolean success;//处理是否成功

    private String message;//处理后提示消息

    private Object body;//返回数据

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    public Response(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public Response(boolean success, String message, Object body) {
        this.success = success;
        this.message = message;
        this.body = body;
    }
}
