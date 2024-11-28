package com.exam_jee.ds.payload.request;

import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class SignupRequest {

    private String username;
    private String email;
    private String password;



}
