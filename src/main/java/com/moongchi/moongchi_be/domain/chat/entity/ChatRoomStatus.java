package com.moongchi.moongchi_be.domain.chat.entity;

public enum ChatRoomStatus {
    RECRUITING("모집중"),
    RECRUITED ("모집완료"),
    PAYING ("구매중"),
    PURCHASED ("구매완료"),
    COMPLETED ("공구완료");

    private final String korean;


    ChatRoomStatus(String korean) {
        this.korean = korean;
    }
    public String getKorean() {
        return korean;
    }

    @Override
    public String toString() {
        return name() + " (" + korean + ")";
    }
}
