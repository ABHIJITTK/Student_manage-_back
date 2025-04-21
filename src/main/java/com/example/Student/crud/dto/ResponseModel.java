package com.example.Student.crud.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseModel {

    private int HTTP_status_code;
    private HttpStatus HTTP_Status;
    private Object data;
    private String message;
}
