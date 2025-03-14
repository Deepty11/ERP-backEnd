package com.example.ERPSpringBootBackEnd.controllers;

import com.example.ERPSpringBootBackEnd.dto.requestDto.LeaveApplicationDto;
import com.example.ERPSpringBootBackEnd.dto.requestDto.LeaveOverviewDto;
import com.example.ERPSpringBootBackEnd.enums.LeaveStatus;
import com.example.ERPSpringBootBackEnd.enums.LeaveType;
import com.example.ERPSpringBootBackEnd.exception.APIException;
import com.example.ERPSpringBootBackEnd.model.LeaveApplication;
import com.example.ERPSpringBootBackEnd.services.LeaveApplicationService;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/leave")
public class LeaveApplicationController {
    @Autowired
    private LeaveApplicationService service;

    @PostMapping("/create-application")
    @RolesAllowed({"ADMIN", "USER"})
    public ResponseEntity<LeaveApplication> save(@RequestBody LeaveApplicationDto leaveApplicationDto) {
       LeaveApplication leaveApplication = service.save(leaveApplicationDto);

       if (Objects.isNull(leaveApplication)) {
           throw new APIException("Not saved", HttpStatus.NOT_MODIFIED.value());
       }

       return ResponseEntity.ok().body(leaveApplication);
    }

    @GetMapping("/leave-applications")
    @RolesAllowed({"ADMIN"})
    public  ResponseEntity<List<LeaveApplicationDto>> getAllLeaveApplications() {
        return ResponseEntity.ok().body(service.getAllLeaveApplication());
    }

    @GetMapping("/my-leave-applications")
    @RolesAllowed({"ADMIN", "USER"})
    public  ResponseEntity<List<LeaveApplicationDto>> getAllApplicationsByUserId(@RequestParam long userId) {
        return ResponseEntity.ok().body(service.getApplicationsByUserId(userId));
    }

    @GetMapping("/overview")
    @RolesAllowed({"ADMIN", "USER"})
    public ResponseEntity<LeaveOverviewDto> getLeaveOverview(@RequestParam long userId) {
        LeaveOverviewDto leaveOverviewDto = new LeaveOverviewDto(
                userId,
                service.ALLOWED_CASUAL_LEAVE,
                service.ALLOWED_SICK_LEAVE,
                service.getNumberOfLeaves(service.getAllLeaves(userId, LeaveType.CASUAL)),
                service.getNumberOfLeaves(service.getAllLeaves(userId, LeaveType.SICK)),
                service.getRemainingCasualLeaves(userId),
                service.getRemainingSickLeaves(userId)
                );
        System.out.println("Leave Application Dto: " + leaveOverviewDto);
        return ResponseEntity.ok().body(leaveOverviewDto);
    }

    @GetMapping("/action")
    @RolesAllowed({"ADMIN"})
    public ResponseEntity<String> action(
            @RequestParam long leaveId,
            @RequestParam String action) {

        Optional<LeaveApplication> optional = service.getLeaveApplicationById(leaveId);
        if(optional.isEmpty()) {
            throw new APIException("Leave Application Not Found", HttpStatus.NOT_FOUND.value());
        }

        LeaveApplication leaveApplicationFromDB = optional.get();

        leaveApplicationFromDB.setStatus(switch (action) {
            case "Approve":
                yield LeaveStatus.APPROVED;
            case "Decline":
                yield LeaveStatus.DECLINED;
            case "Delete":
                yield LeaveStatus.DELETED;
            default:
                yield LeaveStatus.PENDING;
        });

        service.saveLeaveApplication(leaveApplicationFromDB);

        return ResponseEntity.ok().body("Leave Application is updated!");
    }
}
