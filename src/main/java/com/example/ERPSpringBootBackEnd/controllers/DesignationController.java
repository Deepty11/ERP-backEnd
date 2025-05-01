package com.example.ERPSpringBootBackEnd.controllers;

import com.example.ERPSpringBootBackEnd.dto.requestDto.DesignationDto;
import com.example.ERPSpringBootBackEnd.dto.responseDto.SuccessResponseDto;
import com.example.ERPSpringBootBackEnd.exception.APIException;
import com.example.ERPSpringBootBackEnd.mapper.DesignationMapper;
import com.example.ERPSpringBootBackEnd.model.Designation;
import com.example.ERPSpringBootBackEnd.services.DesignationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/designation")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@Tag(
        name = "DesignationController",
        description = "Manages Designations CRUD operations")
public class DesignationController {
    private final DesignationService designationService;

    @PostMapping("/add-designation")
    @RolesAllowed({"ADMIN"})
    @Operation(summary = "Save Designation", description = "Saves new designation")
    public ResponseEntity<Designation> saveDesignation(@RequestBody DesignationDto designationDto) {
        return ResponseEntity.ok().body(designationService.save(designationDto));
    }

    @GetMapping("/designations")
    @RolesAllowed({"ADMIN"})
    @Operation(summary = "Get All designations", description = "Returns all designation information")
    public ResponseEntity<List<DesignationDto>> getDesignationList() {
        return ResponseEntity.ok().body(designationService.getAllDesignations());
    }

    @GetMapping("/designation-details")
    @RolesAllowed({"ADMIN"})
    @Operation(summary = "Get All designations", description = "Returns all user information")
    public ResponseEntity<?> getDesignationById(@RequestParam long id) {
        Optional<Designation> optional = designationService.getDesignationBy(id);
        if(optional.isEmpty()) {
            throw new APIException(
                    "No designation found with this id.",
                    HttpStatus.NOT_FOUND.value());
        }

        return ResponseEntity.ok()
                .body(DesignationMapper
                        .toDto(optional.get()));
    }

    @PostMapping("/edit-designation")
    @RolesAllowed({"ADMIN"})
    @Operation(summary = "Update designation by id", description = "Update designation for given designation id")
    public ResponseEntity<?> updateDesignationById(@RequestParam long id,
                                                   @RequestBody DesignationDto designationDto) {
        Designation designation = designationService.update(id, designationDto);

        if(Objects.isNull(designation)) {
            throw new APIException(
                    "No designation found with this id",
                    HttpStatus.NOT_FOUND.value()
            );
        }

        return ResponseEntity
                .ok()
                .body(new SuccessResponseDto("Designation is updated successfully",
                        new Date().getTime()));

    }
}