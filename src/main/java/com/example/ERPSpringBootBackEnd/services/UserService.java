package com.example.ERPSpringBootBackEnd.services;

import ch.qos.logback.core.util.StringUtil;
import com.example.ERPSpringBootBackEnd.dto.requestDto.*;
import com.example.ERPSpringBootBackEnd.enums.DBState;
import com.example.ERPSpringBootBackEnd.enums.Gender;
import com.example.ERPSpringBootBackEnd.enums.Religion;
import com.example.ERPSpringBootBackEnd.mapper.UserMapper;
import com.example.ERPSpringBootBackEnd.model.*;
import com.example.ERPSpringBootBackEnd.repositories.FileEntityRepository;
import com.example.ERPSpringBootBackEnd.repositories.UserRepository;
import com.example.ERPSpringBootBackEnd.utils.DateUtils;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobProfileService jobProfileService;

    @Autowired
    private ContactInfoService contactInfoService;

    @Autowired
    private EmergencyContactInfoService emergencyContactInfoService;

    @Autowired
    private FileEntityRepository fileEntityRepository;

    @Transactional
    public DBState save(UserDto userDto) {
        if (userRepository.existsUserByUserName(userDto.getUsername())) {
            System.out.println("User with the username already exists");
            return DBState.ALREADY_EXIST;
        }

        Users users = UserMapper.toUser(userDto);

        if (userDto.getContactInfoDto() != null) {
            users.setContactInfo(contactInfoService.save(userDto.getContactInfoDto()));
        }

        if (userDto.getJobProfileDto() != null) {
            JobProfile jobProfile = jobProfileService.save(userDto.getJobProfileDto());
            users.setJobProfile(jobProfile);
        }

        userRepository.save(users);
        return DBState.SAVED;
    }

    public List<UserDto> getAllUsers() {
        List<Users> users = userRepository.findAll();
        return UserMapper.toListOfUserDto(users);
    }

    public Users getUserById(long id) {
        return userRepository.findById(id)
                .orElse(null);
    }

    public Users getUserByUsername(String username) {
        return userRepository.findUserByUserName(username).get();
    }

    public UserDto getUserDtoByUsername(String username) {
        Users users = getUserByUsername(username);
        return Objects.isNull(users) ? null : new UserDto(
                users.getId(),
                users.getFirstName(),
                users.getLastName(),
                users.getUsername(),
                users.getRole().toString());
    }

    public UserDto getUserDetailsForId(long id) {
        Users users = userRepository.findById(id).orElse(null);
        return UserMapper.toUserDto(users);
    }

    public Users updateUserDetailsForId(long id, UserDto userDto) {
        Users usersFromDB = getUserById(id);
        if(Objects.isNull(usersFromDB)) {
            return null;
        }

        usersFromDB.setFirstName(userDto.getFirstName());
        usersFromDB.setLastName(userDto.getLastName());
        usersFromDB.setGender(Gender.getGender(userDto.getGender()));
        usersFromDB.setReligion(Religion.getReligion(userDto.getReligion()));
        if(StringUtil.notNullNorEmpty(userDto.getBirthDate())) {
            usersFromDB.setBirthDate(DateUtils.parseDate(userDto.getBirthDate()));
        }

        // update contactInfo
        if(Objects.nonNull(userDto.getContactInfoDto())) {
            ContactInfoDto contactInfoDto = userDto.getContactInfoDto();

            ContactInfo contactInfo = null;
            if(Objects.isNull(usersFromDB.getContactInfo())) {
                contactInfo = contactInfoService.save(contactInfoDto);
            } else {
                contactInfo = contactInfoService.update(usersFromDB.getContactInfo().getId(), contactInfoDto);
            }

            if(Objects.isNull(contactInfo)) {
                return null;
            }

            usersFromDB.setContactInfo(contactInfo);
        }

        // update emergencyContactInfo
        if(Objects.nonNull(userDto.getEmergencyContactInfoDto())) {
            EmergencyContactInfoDto emergencyContactInfoDto = userDto.getEmergencyContactInfoDto();

            EmergencyContactInfo emergencyContactInfo = null;
            if(Objects.isNull(usersFromDB.getEmergencyContact())) {
                emergencyContactInfo = emergencyContactInfoService.save(emergencyContactInfoDto);
            } else {
                emergencyContactInfo = emergencyContactInfoService.update(usersFromDB.getEmergencyContact().getId(), emergencyContactInfoDto);
            }

            if(Objects.isNull(emergencyContactInfo)) {
                return null;
            }

            usersFromDB.setEmergencyContact(emergencyContactInfo);
        }

        // update jobProfile
        if(Objects.nonNull(userDto.getJobProfileDto())) {
            JobProfileDto jobProfileDto = userDto.getJobProfileDto();

            JobProfile jobProfile = null;
            if(Objects.isNull(usersFromDB.getEmergencyContact())) {
                jobProfile = jobProfileService.save(jobProfileDto);
            } else {
                jobProfile = jobProfileService.update(usersFromDB.getJobProfile().getId(), jobProfileDto);
            }

            if(Objects.isNull(jobProfile)) {
                return null;
            }

            usersFromDB.setJobProfile(jobProfile);
        }

        usersFromDB.setId(id);
        return userRepository.save(usersFromDB);
    }

    public void deleteUserById(long id) {
        if(!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found with the Id" + id);
        }

        userRepository.deleteById(id);
    }

    public Users getCurrentUser() {
        Object principle = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String username = (String) principle;
        return getUserByUsername(username);
    }

    public List<Users> getAllUsersByIds(List<Long> ids) {
        return userRepository.findAllById(ids);
    }

    public UserDto uploadProfilePicture(long id, FileEntityDto fileEntityDto) throws IOException {
        Optional<Users> optional = userRepository.findById(id);

        if(optional.isEmpty()) {
            return null;
        }

        Users usersFromDB = optional.get();

        if(Objects.nonNull(usersFromDB.getProfilePicture())) {
           FileEntity fileEntityFromDB = usersFromDB.getProfilePicture();
           fileEntityFromDB.setFileName(fileEntityDto.getFileName());
           fileEntityFromDB.setDocument(Base64.getDecoder().decode(fileEntityDto.getData()));
           fileEntityRepository.save(fileEntityFromDB);
           usersFromDB.setProfilePicture(fileEntityFromDB);

        } else {
            FileEntity newProfilePicture = new FileEntity();
            newProfilePicture.setFileName(fileEntityDto.getFileName());
            newProfilePicture.setDocument(Base64.getDecoder().decode(fileEntityDto.getData()));
            fileEntityRepository.save(newProfilePicture);
            usersFromDB.setProfilePicture(newProfilePicture);
        }

        Users users = userRepository.save(usersFromDB);

        return UserMapper.toUserDto(users);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findUserByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    }
}