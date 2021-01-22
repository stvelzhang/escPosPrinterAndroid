package com.example.print_sdk.enums;

/**
 * Created by stvelzhang
 * on 2021/1/18
 * Name： UNDERLINE_MODE(enum)
 */
public enum UNDERLINE_MODE {
    UNDERLINE_OFF(0x00), UNDERLINE_ON(0x01), UNDERLINE_LARGE(0x02);

    private int iSet;

    UNDERLINE_MODE(int iValue) {
        this.iSet = iValue;
    }

    public int Get() {
        return this.iSet;
    }
}
