package com.ssafy.yourpilling.common;

public enum PillProductForm {
    CAPSULE("capsule", "캡슐"),
    TABLET("tablet", "정"),
    JELLY("jelly", "젤리"),
    POWDER("powder", "분말"),
    LIQUID("liquid", "액상");

    String english, korean;

    PillProductForm(String english, String korean) {
        this.english = english;
        this.korean = korean;
    }
}
