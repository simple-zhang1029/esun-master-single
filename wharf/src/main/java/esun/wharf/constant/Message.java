package esun.wharf.constant;

//信息码枚举类
public enum Message {
    QUERY_ERROR("query_error","请求数据库失败"),
    //基础数据消息枚举
    BASE_GET_ERROR("base_get_error","获取基础数据失败"),
    BASE_GET_SUCCESS("base_get_success","获取基础数据成功"),
    BASE_ADD_ERROR("base_add_error","添加基础数据失败"),
    BASE_ADD_SUCCESS("base_add_success","添加基础数据成功"),
    BASE_DELETE_ERROR("base_delete_error","删除基础数据失败"),
    BASE_DELETE_SUCCESS("base_delete_success","基础数据删除成功"),
    BASE_UPDATE_ERROR("base_update_error","更新基础数据失败"),
    BASE_UPDATE_SUCCESS("base_update_success","更新基础数据成功"),
    BASE_IS_EXIST("base_is_exist","基础数据已存在"),
    BASE_NOT_EXIST("base_not_exits","基础数据不存在"),
    //发货信息消息枚举
    DELIVERY_GET_ERROR("delivery_get_error","获取发货信息失败"),
    DELIVERY_GET_SUCCESS("delivery_get_success","获取发货信息成功"),
    DELIVERY_ADD_ERROR("delivery_add_error","添加发货信息失败"),
    DELIVERY_ADD_SUCCESS("delivery_add_success","添加发货信息成功"),
    DELIVERY_DELETE_ERROR("delivery_delete_error","删除发货信息失败"),
    DELIVERY_DELETE_SUCCESS("delivery_delete_success","发货信息删除成功"),
    DELIVERY_UPDATE_ERROR("delivery_update_error","更新发货信息失败"),
    DELIVERY_UPDATE_SUCCESS("delivery_update_success","更新发货信息成功"),
    DELIVERY_IS_EXIST("delivery_is_exist","发货信息已存在"),
    DELIVERY_NOT_EXIST("delivery_not_exits","发货信息不存在"),
    //收货信息消息枚举
    RECEIVING_GET_ERROR("receiving_get_error","获取收货信息失败"),
    RECEIVING_GET_SUCCESS("receiving_get_success","获取收货信息成功"),
    RECEIVING_ADD_ERROR("receiving_add_error","添加收货信息失败"),
    RECEIVING_ADD_SUCCESS("receiving_add_success","添加收货信息成功"),
    RECEIVING_DELETE_ERROR("receiving_delete_error","删除收货信息失败"),
    RECEIVING_DELETE_SUCCESS("receiving_delete_success","收货信息删除成功"),
    RECEIVING_UPDATE_ERROR("receiving_update_error","更新收货信息失败"),
    RECEIVING_UPDATE_SUCCESS("receiving_update_success","更新收货信息成功"),
    RECEIVING_IS_EXIST("receiving_is_exist","收货信息已存在"),
    RECEIVING_NOT_EXIST("receiving_not_exits","收货信息不存在"),
    //客户信息消息枚举
    CUSTOMER_GET_ERROR("customer_get_error","获取客户信息失败"),
    CUSTOMER_GET_SUCCESS("customer_get_success","获取客户信息成功"),
    CUSTOMER_ADD_ERROR("customer_add_error","添加客户信息失败"),
    CUSTOMER_ADD_SUCCESS("customer_add_success","添加客户信息成功"),
    CUSTOMER_DELETE_ERROR("customer_delete_error","删除客户信息失败"),
    CUSTOMER_DELETE_SUCCESS("customer_delete_success","客户信息删除成功"),
    CUSTOMER_UPDATE_ERROR("customer_update_error","更新客户信息失败"),
    CUSTOMER_UPDATE_SUCCESS("customer_update_success","更新客户信息成功"),
    CUSTOMER_IS_EXIST("customer_is_exist","客户信息已存在"),
    CUSTOMER_NOT_EXIST("customer_not_exits","客户信息不存在"),
    //订单消息枚举
    ORDER_GET_ERROR("order_get_error","获取订单失败"),
    ORDER_GET_SUCCESS("order_get_success","获取订单成功"),
    ORDER_ADD_ERROR("order_add_error","添加订单失败"),
    ORDER_ADD_SUCCESS("order_add_success","添加订单成功"),
    ORDER_DELETE_ERROR("order_delete_error","删除订单失败"),
    ORDER_DELETE_SUCCESS("order_delete_success","订单删除成功"),
    ORDER_UPDATE_ERROR("order_update_error","添加订单失败"),
    ORDER_UPDATE_SUCCESS("order_update_success","添加订单失败"),
    ORDER_IS_EXIST("order_is_exist","订单已存在"),
    ORDER_NOT_EXIST("order_not_exits","订单不存在"),
    //导入发货消息枚举
    EXPORT_DELIVERY_ERROR("export_delivery_error","导入发货信息失败"),
    EXPORT_DELIVERY_SUCCESS("export_delivery_success","导入发货信息成功"),
    //导入收货消息枚举
    EXPORT_RECEIVING_ERROR("export_receiving_error","导入发货信息失败"),
    EXPORT_RECEIVING_SUCCESS("export_receiving_success","导入发货信息成功"),
    //导出发货信息
    DERIVE_DELIVERY_ERROR("derive_delivery_error","导入发货信息失败"),
    DERIVE_DELIVERY_SUCCESS("derive_delivery_success","导入发货信息成功"),
    //导出收货消息枚举
    DERIVE_RECEIVING_ERROR("derive_receiving_error","导入发货信息失败"),
    DERIVE_RECEIVING_SUCCESS("derive_receiving_success","导入发货信息成功"),
    //码头消息
    WHARF_IS_USED("wharf_is_used","码头已被占用"),
    WHARF_NOT_USER("wharf_not_used","码头未被占用");


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
