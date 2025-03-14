package com.example.ERPSpringBootBackEnd.enums;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public enum LeaveType {
    CASUAL("Casual"),
    SICK("Sick");

    private String title;

    LeaveType(String title) {
        this.title = title;
    }

    public static List<String> getLeaveTypeList() {
        List<String> leaveTypeList = new ArrayList<>();
        for (LeaveType l : LeaveType.values()) {
            leaveTypeList.add(l.title);
        }

        return leaveTypeList;
    }

    public static LeaveType getLeaveType(String leaveTypeString) {
        return Arrays.stream(LeaveType.values())
                .filter(leaveType -> leaveType.toString().equals(leaveTypeString))
                .findFirst()
                .orElse(null);
    }

}
