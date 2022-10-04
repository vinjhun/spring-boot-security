package com.security.myapp.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.LowerCaseStrategy.class)
public class ApiErrorResponse implements Serializable {

    private int status;
    private String statusCode;
}
