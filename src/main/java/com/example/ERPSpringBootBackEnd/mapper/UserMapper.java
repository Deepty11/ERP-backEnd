package com.example.ERPSpringBootBackEnd.mapper;

import ch.qos.logback.core.util.StringUtil;
import com.example.ERPSpringBootBackEnd.dto.requestDto.UserDto;
import com.example.ERPSpringBootBackEnd.enums.Gender;
import com.example.ERPSpringBootBackEnd.enums.Religion;
import com.example.ERPSpringBootBackEnd.enums.Role;
import com.example.ERPSpringBootBackEnd.model.Users;
import com.example.ERPSpringBootBackEnd.utils.DateUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.text.ParseException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class UserMapper {
    public static Users toUser(UserDto userDto) {
        Users users = new Users();
        users.setUserName(userDto.getUsername());
        if(!Objects.isNull(userDto.getPassword())) {
            users.setPassword(new BCryptPasswordEncoder().encode(userDto.getPassword()));
        }

        users.setFirstName(userDto.getFirstName());
        users.setLastName(userDto.getLastName());
        users.setGender(Gender.getGender(userDto.getGender()));
        users.setReligion(Religion.getReligion(userDto.getReligion()));
        users.setRole(Role.getRole(userDto.getRole()));

        if (StringUtil.notNullNorEmpty(userDto.getBirthDate())) {
            System.out.println("Birthdate: " + userDto.getBirthDate());
            try {
                users.setBirthDate(DateUtils.convertToDate(userDto.getBirthDate()));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }

        if(!Objects.isNull(userDto.getContactInfoDto())) {
            users.setContactInfo(ContactInfoMapper.toContactInfo(userDto.getContactInfoDto()));
        }

        if(!Objects.isNull(userDto.getJobProfileDto())) {
            users.setJobProfile(JobProfileMapper.toJobProfile(userDto.getJobProfileDto()));
        }

        if(!Objects.isNull(userDto.getEmergencyContactInfoDto())) {
            users.setEmergencyContact(EmergencyContactInfoMapper.toEmergencyContactInfo(userDto.getEmergencyContactInfoDto()));
        }

        if(!Objects.isNull(userDto.getLeaveApplicationDtos())) {
            users.setLeaveApplications(
                    LeaveApplicationMapper.toListOfLeaveApplication(
                            userDto.getLeaveApplicationDtos()));
        }

        return users;
    }

    public static UserDto toUserDto(Users users) {
        return Objects.isNull(users)
                ? null
                : UserDto.builder()
                .firstName(users.getFirstName())
                .lastName(users.getLastName())
                .fullName(users.getFullName())
                .username(users.getUsername())
                .role(users.getRole().name())
                .birthDate(Objects.isNull(users.getBirthDate()) ? null : DateUtils.formatDate(users.getBirthDate()))
                .gender(Objects.isNull(users.getGender()) ? null : users.getGender().name())
                .religion(Objects.isNull(users.getReligion()) ? null : users.getReligion().name())
                .jobProfileDto(Objects.isNull(users.getJobProfile()) ? null : JobProfileMapper.toDto(users.getJobProfile()))
                .contactInfoDto(Objects.isNull(users.getContactInfo()) ? null :ContactInfoMapper.toDto(users.getContactInfo()))
                .emergencyContactInfoDto(Objects.isNull(users.getEmergencyContact()) ? null :EmergencyContactInfoMapper.toDto(users.getEmergencyContact()))
                .leaveApplicationDtos(LeaveApplicationMapper.toListOfLeaveApplicationDto(users.getLeaveApplications()))
                .fileEntityDto(FileEntityMapper.toFileEntityDto(users.getProfilePicture()))
                .build();
    }

    public static List<UserDto> toListOfUserDto(List<Users> users) {
        return users
                .stream()
                .map(user -> new UserDto(
                        user.getId(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getUsername(),
                        user.getRole().getName())).collect(Collectors.toList());
    }
}
