package com.example.ERPSpringBootBackEnd.mapper;

import ch.qos.logback.core.util.StringUtil;
import com.example.ERPSpringBootBackEnd.dto.requestDto.UserDto;
import com.example.ERPSpringBootBackEnd.enums.Gender;
import com.example.ERPSpringBootBackEnd.enums.Religion;
import com.example.ERPSpringBootBackEnd.enums.Role;
import com.example.ERPSpringBootBackEnd.model.User;
import com.example.ERPSpringBootBackEnd.utils.DateUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.text.ParseException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class UserMapper {
    public static User toUser(UserDto userDto) {
        User user = new User();
        user.setUserName(userDto.getUsername());
        user.setPassword(new BCryptPasswordEncoder().encode(userDto.getPassword()));
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setGender(Gender.getGender(userDto.getGender()));
        user.setReligion(Religion.getReligion(userDto.getReligion()));
        user.setRole(Role.getRole(userDto.getRole()));

        if (StringUtil.notNullNorEmpty(userDto.getBirthDate())) {
            System.out.println("Birthdate: " + userDto.getBirthDate());
            try {
                user.setBirthDate(DateUtils.convertToDate(userDto.getBirthDate()));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }

        return user;
    }

    public static UserDto toUserDto(User user) {
        return Objects.isNull(user)
                ? null
                : UserDto.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .role(user.getRole().getName())
                .birthDate(Objects.isNull(user.getBirthDate()) ? null : DateUtils.formatDate(user.getBirthDate()))
                .gender(Objects.isNull(user.getGender()) ? null : user.getGender().getName())
                .religion(Objects.isNull(user.getReligion()) ? null :user.getReligion().getName())
                .jobProfileDto(Objects.isNull(user.getJobProfile()) ? null : JobProfileMapper.toDto(user.getJobProfile()))
                .contactInfoDto(Objects.isNull(user.getContactInfo()) ? null :ContactInfoMapper.toDto(user.getContactInfo()))
                .emergencyContactInfoDto(Objects.isNull(user.getEmergencyContact()) ? null :EmergencyContactInfoMapper.toDto(user.getEmergencyContact()))
                .leaveApplicationDtos(LeaveApplicationMapper.toListOfLeaveApplicationDto(user.getLeaveApplications()))
                .build();
    }

    public static List<UserDto> toListOfUserDto(List<User> users) {
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