package com.moongchi.moongchi_be.domain.group_boards.enums;

import lombok.Getter;

@Getter
public enum BoardStatus {
    OPEN("모집중"),
    CLOSING_SOON("마감임박"),
    CLOSED("모집마감"),
    COMPLETED("공구성공");

    private String value;

    BoardStatus(String value) {
        this.value = value;
    }
}
