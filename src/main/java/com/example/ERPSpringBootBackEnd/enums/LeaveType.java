package com.example.ERPSpringBootBackEnd.enums;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public enum LeaveType {
    CASUAL("Casual"),
    SICK("Sick");

    private String fullString;

    LeaveType(String fullString) {
        this.fullString = fullString;
    }

    public static List<String> getLeaveTypeList() {
        List<String> leaveTypeList = new ArrayList<>();
        for (LeaveType l: LeaveType.values()) {
            leaveTypeList.add(l.fullString);
        }

        return leaveTypeList;
    }

    public static LeaveType getLeaveType( String leaveTypeString) {
        switch (leaveTypeString) {
            case "Sick": return SICK;
            default: return CASUAL;
        }
    }

}
