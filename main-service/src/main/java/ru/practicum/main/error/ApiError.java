package ru.practicum.main.error;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiError {
    private String errors;
    private String message;
    private String reason;
    private String status;
    private String timestamp;
}