package team.delete.scheduling_system.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

/**
 * 错误详情类
 *
 * @author patrick_star
 * @version 1.1
 */
@Data
@Builder
public final class ErrorDetail {
    private Integer code;
    private String message;
    private final String requestId;
    private final String path;
    private final Instant timestamp;
    private final Object data;
}
