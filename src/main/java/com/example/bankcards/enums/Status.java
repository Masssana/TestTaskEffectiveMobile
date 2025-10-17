package com.example.bankcards.enums;

import lombok.Getter;

@Getter
public enum Status {
    ACTIVE("active"), BLOCKED("blocked"), EXPIRED("expired");

    private String status;

    Status(String status) {
        this.status = status;
    }

}
