package esun.core.constant;

//信息码枚举类
public enum   Message {
    PASSWORD_NOT_STANDARD("password_not_standard","密码格式不规范"),
    EMAIL_NOT_STANDARD("email_not_standard","邮箱不规范"),
    PASSWORD_IS_NULL("password_is_null","密码为空"),
    QUERY_ERROR("query_error","请求失败"),
    USER_INFO_GET_ERROR("user_info_get_error","获取用户信息失败"),
    USER_INFO_DELETE_ERROR("user_info_delete_error","删除用户信息失败"),
    USER_INFO_UPDATE_ERROR("user_info_update_error","更新用户信息失败"),
    USER_INFO_INSERT_ERROR("user_info_insert_error","插入用户信息失败"),
    USER_INFO_GET_SUCCESS("user_info_get_success","获取用户信息成功"),
    USER_INFO_DELETE_SUCCESS("user_info_delete_success","删除用户信息成功"),
    USER_INFO_UPDATE_SUCCESS("user_info_update_success","更新用户信息成功"),
    USER_INFO_INSERT_SUCCESS("user_info_insert_success","插入用户信息成功"),
    USER_NOT_EXIST("user_not_exist","用户不存在"),
    USER_IS_EXISTED("user_is_existed","用户已存在"),
    USER_NOT_LOGIN("user_not_login","用户未登入"),
    PASSWORD_ERROR("password_error","密码错误"),
    USER_LOGGED("user_logged","用户已登入"),
    LOGIN_SUCCESS("login_success","登入成功"),
    REGISTER_ERROR("register_error","注册失败"),
    REGISTER_SUCCESS("register_success","注册成功"),
    FILE_EXISTED("file_existed","文件已存在"),
    TOKEN_UPDATE_ERROR("token_update_error","更新token失败"),
    TOKEN_IS_NULL("token_is_null","token为空"),
    TOKEN_CHECK_ERROR("token_check_error","token校验错误"),
    TOKEN_CHECK_SUCCESS("token_check_success","token校验成功"),
    ROUTER_CHECK_SUCCESS("router_check_success","路由校验成功"),
    ROUTER_CHECK_ERROR("router_check_error","理由校验失败"),
    ROUTER_GET_ERROR("router_get_error","获取路由表失败"),
    ROUTER_GET_SUCCESS("router_get_success","获取路由表成功"),
    ROUTER_DELETE_ERROR("router_delete_error","删除路由失败"),
    ROUTER_DELETE_SUCCESS("router_delete_success","路由删除成功"),
    ROUTER_ADD_ERROR("router_add_error","路由添加失败"),
    ROUTER_ADD_SUCCESS("router_add_success","路由添加成功"),
    ROUTER_IS_EXIST("router_is_exist","路由已存在"),
    ROUTER_NOT_EXIST("routre_not_exits","路由不存在"),
    UPDATE_PASSWORD_ERROR("update_password_error","更新密码失败"),
    UPDATE_PASSWORD_SUCCESS("update_password_success","更新密码成功"),
    GET_PROJECT_INFO_SUCCESS("get_project_info_success","获取项目信息成功"),
    GET_PROJECT_INFO_ERROR("get_project_info_error","获取项目信息失败"),
    UPLOAD_FTP_FAIL("upload_ftp_fail","上传FTP服务器失败"),
    UPLOAD_FTP_SUCCESS("upload_ftp_success","上传FTP服务器成功"),
    GET_SALT_FAIL("get_salt_fail","获取用户盐失败"),
    GET_SALT_SUCCESS("get_salt_success","获取用户盐成功"),
    PASSWORD_IS_SAME("password_is_same","密码相同");


    //信息码
    private final String code;
    //信息码描述
    private final String description;

    private Message(String code,String description) {
        this.code=code;
        this.description=description;
    }

    public String getCode() {
        return code;
    }
}
