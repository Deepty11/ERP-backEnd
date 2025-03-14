package com.example.ERPSpringBootBackEnd.controllers;

import com.example.ERPSpringBootBackEnd.dto.requestDto.LeaveApplicationDto;
import com.example.ERPSpringBootBackEnd.dto.requestDto.LeaveOverviewDto;
import com.example.ERPSpringBootBackEnd.enums.LeaveStatus;
import com.example.ERPSpringBootBackEnd.enums.LeaveType;
import com.example.ERPSpringBootBackEnd.exception.APIException;
import com.example.ERPSpringBootBackEnd.model.LeaveApplication;
import com.example.ERPSpringBootBackEnd.services.LeaveApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/leave")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@Tag(name = "Leave Applications",
        description = "Manages leave application CRUD operations")
public class LeaveApplicationController {
    private final LeaveApplicationService service;

    @PostMapping("/create-application")
    @RolesAllowed({"ADMIN", "USER"})
    @Operation(
            summary = "Save new application",
            description = "Saves new leave application")
    public ResponseEntity<LeaveApplication> save(@RequestBody LeaveApplicationDto leaveApplicationDto) {
       LeaveApplication leaveApplication = service.save(leaveApplicationDto);

       if (Objects.isNull(leaveApplication)) {
           throw new APIException("Not saved", HttpStatus.NOT_MODIFIED.value());
       }

       return ResponseEntity.ok().body(leaveApplication);
    }

    @GetMapping("/leave-applications")
    @RolesAllowed({"ADMIN"})
    @Operation(
            summary = "Get All leave appplications",
            description = "Returns all leave Applications")
    public  ResponseEntity<List<LeaveApplicationDto>> getAllLeaveApplications() {
        return ResponseEntity.ok().body(service.getAllLeaveApplication());
    }

    @GetMapping("/my-leave-applications")
    @RolesAllowed({"ADMIN", "USER"})
    @Operation(
            summary = "Get All leave applications by user id",
            description = "Returns all leave applications for a given user id")
    public  ResponseEntity<List<LeaveApplicationDto>> getAllApplicationsByUserId(@RequestParam long userId) {
        return ResponseEntity.ok().body(service.getApplicationsByUserId(userId));
    }

    @GetMapping("/overview")
    @RolesAllowed({"ADMIN", "USER"})
    @Operation(
            summary = "Get leave overviews",
            description = "Returns leave overviews")
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
    @Operation(
            summary = "Leave application action",
            description = "Updates leave application for a given leaveId and specified action")
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
