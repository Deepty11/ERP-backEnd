package com.example.ERPSpringBootBackEnd.services;

import com.example.ERPSpringBootBackEnd.dto.requestDto.LeaveApplicationDto;
import com.example.ERPSpringBootBackEnd.enums.LeaveStatus;
import com.example.ERPSpringBootBackEnd.enums.LeaveType;
import com.example.ERPSpringBootBackEnd.exception.APIException;
import com.example.ERPSpringBootBackEnd.model.LeaveApplication;
import com.example.ERPSpringBootBackEnd.model.Users;
import com.example.ERPSpringBootBackEnd.repositories.LeaveApplicationRepository;
import com.example.ERPSpringBootBackEnd.utils.DateUtils;
import com.example.ERPSpringBootBackEnd.mapper.LeaveApplicationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class LeaveApplicationService {
    @Autowired
    private LeaveApplicationRepository repository;

    @Autowired
    private UserService userService;

    public final int ALLOWED_SICK_LEAVE = 3;
    public final int ALLOWED_CASUAL_LEAVE = 14;

    public LeaveApplication save(LeaveApplicationDto leaveApplicationDto) {
        Users users = userService.getUserByUsername(
                leaveApplicationDto.getUserDto().getUsername());

        if (Objects.isNull(users)) {
            return null;
        }

        LeaveApplication leaveApplication = LeaveApplication.builder()
                .id(leaveApplicationDto.getId())
                .leaveType(LeaveType.getLeaveType(leaveApplicationDto.getLeaveType()))
                .description(leaveApplicationDto.getDescription())
                .fromDate(DateUtils.parseDate(leaveApplicationDto.getFromDate()))
                .toDate(DateUtils.parseDate(leaveApplicationDto.getToDate()))
                .status(LeaveStatus.getLeaveStatus(leaveApplicationDto.getStatus()))
                .users(users)
                .build();
        return saveLeaveApplication(leaveApplication);
    }

    public LeaveApplication saveLeaveApplication(LeaveApplication leaveApplication) {
        return repository.save(leaveApplication);
    }

    public List<LeaveApplicationDto> getAllLeaveApplication() {
        return LeaveApplicationMapper.toListOfLeaveApplicationDto(repository.findAll());
    }

    public List<LeaveApplicationDto> getAllSickLeavesByUserId(long userId) {
        return getAllLeaves(userId, LeaveType.SICK);
    }

    public List<LeaveApplicationDto> getAllCasualLeavesByUserId(long userId) {
        return getAllLeaves(userId, LeaveType.CASUAL);
    }

    public List<LeaveApplicationDto> getAllLeaves(long userId, LeaveType leaveType) {
        Users user = userService.getUserById(userId);
        if (Objects.isNull(user)) {
            throw new APIException("No user Found", HttpStatus.NOT_FOUND.value());
        }

        System.out.println(leaveType.toString());

        List<LeaveApplication> casualLeaveApplications = repository.findByUsersAndLeaveType(user, leaveType);
        if (Objects.isNull(casualLeaveApplications)) {
            throw new APIException("No Casual Leave For this User", HttpStatus.NOT_FOUND.value());
        }

        return LeaveApplicationMapper.toListOfLeaveApplicationDto(casualLeaveApplications);
    }

    public int getNumberOfLeaves(List<LeaveApplicationDto> leaveApplicationDtos) {
        return leaveApplicationDtos.size();
    }

    public int getRemainingSickLeaves(long userId) {
        return ALLOWED_SICK_LEAVE - getNumberOfLeaves(getAllSickLeavesByUserId(userId));
    }

    public int getRemainingCasualLeaves(long userId) {
        return ALLOWED_CASUAL_LEAVE - getNumberOfLeaves(getAllCasualLeavesByUserId(userId));
    }

    public List<LeaveApplicationDto> getApplicationsByUserId(long userId) {
        Users user = userService.getUserById(userId);

        if (Objects.isNull(user)) {
            throw new APIException("No User Found", HttpStatus.NOT_FOUND.value());
        }

        List<LeaveApplication> leaveApplications = repository.findByUsers(user);

        if (Objects.isNull(leaveApplications)) {
            throw new APIException("No Leave Application Found for this user", HttpStatus.NOT_FOUND.value());
        }

        return LeaveApplicationMapper.toListOfLeaveApplicationDto(leaveApplications);
    }

    public Optional<LeaveApplication> getLeaveApplicationById(long id) {
        return repository.findById(id);
    }
}
