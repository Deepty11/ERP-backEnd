package com.example.ERPSpringBootBackEnd.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum LeaveStatus {
    APPROVED("Approved"),
    DECLINED("Declined"),
    PENDING("Pending"),
    DELETED("Deleted");

    String title;

    LeaveStatus(String title) {
        this.title = title;
    }

    public static LeaveStatus getLeaveStatus(String statusString) {
        return Arrays.stream(LeaveStatus.values())
                .filter((leaveStatus -> leaveStatus.title.equalsIgnoreCase(statusString)))
                .findFirst()
                .orElse(null);
    }
}
