package com.example.back_end.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProducListResponse implements Serializable {
    private String status;
    private String message;
    private List<ProductResponse> data;
}
