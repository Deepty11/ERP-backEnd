package com.example.ERPSpringBootBackEnd.dto.requestDto;
import com.example.ERPSpringBootBackEnd.mapper.UserMapper;
import com.example.ERPSpringBootBackEnd.model.LeaveApplication;
import com.example.ERPSpringBootBackEnd.utils.DateUtils;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LeaveApplicationDto {
    private long id;
    private String leaveType;
    private String description;
    private String fromDate;
    private String toDate;
    private String status;
    private UserDto userDto;
    private String created;

    public LeaveApplicationDto(LeaveApplication leaveApplication) {
        this.id = leaveApplication.getId();
        this.created = DateUtils.formatDate(leaveApplication.getCreated());
        this.fromDate = DateUtils.formatDate(leaveApplication.getFromDate());
        this.toDate = DateUtils.formatDate(leaveApplication.getToDate());
        this.leaveType = leaveApplication.getLeaveType().getTitle();
        this.description = leaveApplication.getDescription();
        this.status = leaveApplication.getStatus().getTitle();
        this.userDto = UserDto.builder()
                .firstName(leaveApplication.getUsers().getFirstName())
                .lastName(leaveApplication.getUsers().getLastName())
                .build();
    }
}
