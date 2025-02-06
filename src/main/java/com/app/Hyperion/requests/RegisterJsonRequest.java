package com.app.Hyperion.requests;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class RegisterJsonRequest {
    String username;
    String password;
    String name;
    String lastName;
    String email;
    String rol;
}
