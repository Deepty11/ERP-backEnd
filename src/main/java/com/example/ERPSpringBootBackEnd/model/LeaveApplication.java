package com.example.ERPSpringBootBackEnd.model;

import com.example.ERPSpringBootBackEnd.dto.requestDto.LeaveApplicationDto;
import com.example.ERPSpringBootBackEnd.enums.LeaveStatus;
import com.example.ERPSpringBootBackEnd.enums.LeaveType;
import com.example.ERPSpringBootBackEnd.utils.DateUtils;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LeaveApplication implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private LeaveType leaveType;

    private String description;

    @Enumerated(EnumType.STRING)
    private LeaveStatus status;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date fromDate;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date toDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users users;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;

    @Version
    private int version = 0;

    public long getNumberOfDays() {
        return DateUtils.getIntervalInDays(this.fromDate, this.toDate);
    }

    public String getDateRangeString() {
        return DateUtils.getDateRangeString(this.fromDate, this.toDate);
    }

    public String getRequestedDate() {
        return DateUtils.formatDate(this.created);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LeaveApplication)) return false;
        LeaveApplication leaveInfo = (LeaveApplication) o;
        return id == leaveInfo.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public LeaveApplication(LeaveApplicationDto leaveApplicationDto) {
        this.leaveType = LeaveType.getLeaveType(leaveApplicationDto.getLeaveType());
        this.description = leaveApplicationDto.getDescription();
        this.status = LeaveStatus.getLeaveStatus(leaveApplicationDto.getStatus());
        this.fromDate = DateUtils.parseDate(leaveApplicationDto.getFromDate());
        this.toDate = DateUtils.parseDate(leaveApplicationDto.getToDate());
    }
}


