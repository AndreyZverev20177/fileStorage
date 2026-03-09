// dto/LoginRequest.java
package com.filestorage.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}