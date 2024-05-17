package com.example.back_end.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonPropertyOrder({ "status", "message", "data" })
public class ResponseObject {
    private String status;
    private String message;
    private Object data;
}
