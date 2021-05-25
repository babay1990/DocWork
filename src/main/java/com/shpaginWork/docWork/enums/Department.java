package com.shpaginWork.docWork.enums;

public enum Department {
    DIRECTION,
    BOOKKKEEPING,
    LEGAL_DEPARTMENT,
    CHANCELLERY,
    DEPARTMENT_1,
    DEPARTMENT_2,
    DEPARTMENT_3;

    public static Department[] getList() {
        Department[] list;
        list = Department.values();
        return list;
    }
}
