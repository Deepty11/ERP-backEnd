package com.example.ERPSpringBootBackEnd.dto.requestDto;

import com.example.ERPSpringBootBackEnd.model.LeaveApplication;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class UserDto {
    private long id;

    @NotNull
    private String firstName;

    @NotBlank
    private String lastName;

    private String fullName;

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @NotNull
    private String role;
    private String birthDate;
    private String gender;
    private String religion;
    private ContactInfoDto contactInfoDto;
    private EmergencyContactInfoDto emergencyContactInfoDto;
    private JobProfileDto jobProfileDto;
    private List<LeaveApplicationDto> leaveApplicationDtos;
    private FileEntityDto fileEntityDto;


    // TODO: Add these fields later
//    @OneToOne
//    @JoinColumn(name = "user_document_id")
//    private UserDocument document;
//
//    @OneToMany(mappedBy = "user")
//    private List<LeaveInfo> leaveInfo;

    public UserDto(
            String firstName,
            String lastName,
            String username,
            String password,
            String role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public UserDto(long id,
                   String firstName,
                   String lastName,
                   String username,
                   String role) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = firstName + " " + lastName;
        this.username = username;
        this.role = role;
    }

    /**
     * To fetch user List from DB and to pass to react
     */
    public UserDto(String firstName,
                   String lastName,
                   String username,
                   String role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.role = role;
    }

    public UserDto(String firstName,
                   String lastName,
                   String username,
                   String password,
                   String role,
                   String birthDate,
                   String gender,
                   String religion,
                   ContactInfoDto contactInfoDto,
                   EmergencyContactInfoDto emergencyContactInfoDto,
                   JobProfileDto jobProfileDto,
                   List<LeaveApplicationDto> leaveApplicationDtos) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.role = role;
        this.birthDate = birthDate;
        this.gender = gender;
        this.religion = religion;
        this.contactInfoDto = contactInfoDto;
        this.emergencyContactInfoDto = emergencyContactInfoDto;
        this.jobProfileDto = jobProfileDto;
        this.leaveApplicationDtos = leaveApplicationDtos;
    }
}
