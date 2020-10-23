package esun.menu.constant;

//信息码枚举类
public enum Message {
    QUERY_ERROR("query_error","请求失败"),
    MENU_GET_ERROR("menu_get_error","获取菜单失败"),
    MENU_GET_SUCCESS("menu_get_success","获取菜单成功"),
    MENU_ADD_ERROR("menu_add_error","添加菜单失败"),
    MENU_ADD_SUCCESS("menu_add_success","添加菜单成功"),
    MENU_DELETE_ERROR("menu_delete_error","删除菜单失败"),
    MENU_DELETE_SUCCESS("menu_delete_success","菜单删除成功"),
    MENU_UPDATE_ERROR("menu_update_error","添加菜单失败"),
    MENU_UPDATE_SUCCESS("menu_update_success","添加菜单成功"),
    ROLE_GET_ERROR("role_get_error","获取角色失败"),
    ROLE_GET_SUCCESS("role_get_success","获取角色成功"),
    ROLE_ADD_ERROR("role_add_error","添加角色失败"),
    ROLE_ADD_SUCCESS("role_add_success","添加角色成功"),
    ROLE_DELETE_ERROR("role_delete_error","删除角色失败"),
    ROLE_DELETE_SUCCESS("role_delete_success","角色删除成功"),
    ROLE_UPDATE_ERROR("role_update_error","添加角色失败"),
    ROLE_IS_EXIST("role_is_exist","角色已存在"),
    ROLE_NOT_EXIST("role_not_exits","角色不存在"),
    MENU_IS_EXIST("menu_is_exist","菜单已存在"),
    MENU_NOT_EXIST("menu_not_exits","菜单不存在");


    //信息码
    private final String code;
    //信息码描述
    private final String description;

    private Message(String code, String description) {
        this.code=code;
        this.description=description;
    }

    public String getCode() {
        return code;
    }
}
