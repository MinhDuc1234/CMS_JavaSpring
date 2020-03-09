package com.eureka.service.Enum;

import lombok.Getter;

@Getter
public enum Action {

    CREATE(1l), READ(2l), READ_FULL(4l), UPDATE(10l), UPDATE_FULL(20l), DELETE(34l), DELETE_FULL(68l);

    private final Long val;

    private Action(Long val) {
        this.val = val;
    }

}