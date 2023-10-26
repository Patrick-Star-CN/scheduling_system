package team.delete.scheduling_system.constant;

import lombok.Getter;

/**
 * 错误类型枚举类
 *
 * @author patrick_star
 * @version 1.4
 */
@Getter
public enum ErrorCode {
    /** 身份认证失败 */
    NOT_LOGIN(200100, "未登陆"),
    /** 用户已存在 */
    USER_EXISTED(200101, "用户已存在"),
    /** 用户不存在 */
    USER_NOT_EXISTED(200102, "用户不存在"),
    /** 用户名格式错误 */
    USERNAME_FORMAT_ERROR(200103, "用户名格式错误"),
    /** 密码格式错误 */
    PASSWORD_FORMAT_ERROR(200104, "密码格式错误"),
    /** 密码错误 */
    PASSWORD_ERROR(200105, "密码错误"),

    /** 服务器异常 */
    SERVER_ERROR(200300, "服务器异常"),
    /** 参数有误 */
    PARAM_ERROR(200302, "参数有误"),
    /** AccessToken异常 */
    ACCESS_TOKEN_ERROR(200303, "AccessToken异常"),
    /** 图片类型不支持 */
    IMAGE_TYPE_ERROR(200304, "图片类型不支持"),
    /** 上传文件大小不符 */
    IMAGE_SIZE_ERROR(200305, "上传文件大小不符，文件大小不超过 10M"),
    /** 文件类型不支持 */
    FILE_TYPE_ERROR(200306, "文件类型不支持"),
    /** 图片尺寸不符 */
    IMAGE_MEASUREMENT_ERROR(200307, "图片尺寸不符，图像宽高须介于 20 和 10000（像素）之间"),

    /** 非法请求 */
    ILLEGAL_REQUEST(200404, "非法请求"),
    ;




    private final Integer code;
    private final String message;

    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}