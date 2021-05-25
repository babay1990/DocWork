package com.shpaginWork.docWork.enums;

public enum Position {
    DIRECTOR,
    DEPUTY_DIRECTOR,
    DEPARTMENT_HEAD,
    SENIOR_SPECIALICST,
    SPECIALIST;

    public static Position[] getList() {
        Position[] list;
        list = Position.values();
        return list;
    }
}
