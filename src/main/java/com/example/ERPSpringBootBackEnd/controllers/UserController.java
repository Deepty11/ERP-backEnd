package com.example.ERPSpringBootBackEnd.controllers;

import com.example.ERPSpringBootBackEnd.dto.requestDto.FileEntityDto;
import com.example.ERPSpringBootBackEnd.dto.requestDto.UserDto;
import com.example.ERPSpringBootBackEnd.dto.responseDto.ErrorResponseDto;
import com.example.ERPSpringBootBackEnd.dto.responseDto.SuccessResponseDto;
import com.example.ERPSpringBootBackEnd.enums.DBState;
import com.example.ERPSpringBootBackEnd.model.Users;
import com.example.ERPSpringBootBackEnd.services.UserService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/users")
    @RolesAllowed({"ADMIN"})
    public ResponseEntity<List<UserDto>> getAllUser() {
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok().body(users);
    }

    @PostMapping("/add-user")
    @RolesAllowed({"ADMIN"})
    public ResponseEntity<?> createNewUser(@Valid @RequestBody UserDto userDto) {

        DBState state = userService.save(userDto);

        return state.equals(DBState.ALREADY_EXIST)
                ? ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                        new ErrorResponseDto(
                                "User Already exist",
                                new Date().getTime(),
                                null))
                : ResponseEntity.ok().body(
                        new SuccessResponseDto(
                            "Saved Successfully",
                                    new Date().getTime()));
    }

    @GetMapping("/loggedInUser")
    public ResponseEntity<UserDto> getLoggedInUser(@RequestParam String username) {
        return ResponseEntity.ok().body(userService.getUserDtoByUsername(username));
    }

    @GetMapping("/getUserDetails")
    public ResponseEntity<?> getUserDetails(@RequestParam long id) {
        UserDto userDto = userService.getUserDetailsForId(id);
        return ResponseEntity.ok().body(userDto);
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateUserDetails(@RequestParam long id,
                                               @RequestBody UserDto userDto) {
        Users updatedUsersDetails = userService.updateUserDetailsForId(id, userDto);
        if(Objects.isNull(updatedUsersDetails)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ErrorResponseDto(
                            "User not available",
                            new Date().getTime(),
                            null)
            );
        }

       return ResponseEntity.ok().body(
               new SuccessResponseDto(
                       "User details updated successfully",
                       new Date().getTime()));
    }

    @DeleteMapping("/delete")
    @RolesAllowed({"ADMIN"})
    public ResponseEntity<?> deleteById(@RequestParam long id) {
        try {
            userService.deleteUserById(id);
            return ResponseEntity.ok().body(
                    new ErrorResponseDto(
                            "User is deleted successfully",
                            new Date().getTime(),
                            null));
        } catch(Error error) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ErrorResponseDto(
                            error.getMessage(),
                            new Date().getTime(),
                            null));
        }
    }

    @PostMapping("/upload/profile-picture")
    @RolesAllowed({"ADMIN", "USER"})
    public ResponseEntity<?> uploadProfilePicture(@RequestParam long id, @RequestBody FileEntityDto fileEntityDto) throws IOException {
        UserDto userDto = userService.uploadProfilePicture(id, fileEntityDto);
        if(Objects.nonNull(userDto)) {
            return ResponseEntity.ok().body(userDto);
        }

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponseDto(
                        "No user found with the id",
                        new Date().getTime(),
                        null));
    }


}