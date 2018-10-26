package com.liuyiling.microservice.api.model;

import javax.validation.constraints.NotNull;

/**
 * @author liuyiling
 * @date on 2018/10/26
 */
public class Member {
    @NotNull(message = "{member.mid.notnull.error}")
    private String member;
    @NotNull(message = "{member.mid.email.error}")
    private String email;

    public String getMember() {
        return member;
    }

    public void setMember(String member) {
        this.member = member;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
