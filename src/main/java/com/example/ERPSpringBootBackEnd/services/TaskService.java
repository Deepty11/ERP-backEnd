package com.example.ERPSpringBootBackEnd.services;

import ch.qos.logback.core.util.StringUtil;
import com.example.ERPSpringBootBackEnd.dto.requestDto.TaskDto;
import com.example.ERPSpringBootBackEnd.dto.requestDto.UserDto;
import com.example.ERPSpringBootBackEnd.enums.TaskPriority;
import com.example.ERPSpringBootBackEnd.enums.TaskStatus;
import com.example.ERPSpringBootBackEnd.mapper.TaskMapper;
import com.example.ERPSpringBootBackEnd.mapper.UserMapper;
import com.example.ERPSpringBootBackEnd.model.Task;
import com.example.ERPSpringBootBackEnd.model.Users;
import com.example.ERPSpringBootBackEnd.repositories.TaskRepository;
import com.example.ERPSpringBootBackEnd.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserService userService;

    public Task save(TaskDto taskDto) {
        Task task = TaskMapper.toTask(taskDto);

        System.out.println("LLLLL task" + task.getStatus());

        if (StringUtil.notNullNorEmpty(taskDto.getStartDate())) {
            task.setDueDate(DateUtils.parseDate(taskDto.getStartDate()));
        }

        if (StringUtil.notNullNorEmpty(taskDto.getDueDate())) {
            task.setDueDate(DateUtils.parseDate(taskDto.getDueDate()));
        }

        if (Objects.nonNull(taskDto.getAssignees())) {
            task.setAssignees(getUsers(taskDto.getAssignees()));
        }

        if (Objects.nonNull(taskDto.getReportedBy())) {
            task.setReportedBy(userService.getUserById(taskDto.getReportedBy().getId()));
        } else {
            task.setReportedBy(userService.getCurrentUser());
        }
        return taskRepository.save(task);
    }

    private List<Users> getUsers(List<UserDto> userDtoList) {
        if(userDtoList.isEmpty()) {
            return null;
        }

        List<Long> userIds = userDtoList.stream().map(UserDto::getId).toList();
        return userService.getAllUsersByIds(userIds);
    }

    public List<TaskDto> getAllTaskDtos() {
        List<Task> tasks = taskRepository.findAll();

        return tasks.stream().map(this::mapToTaskDto).toList();
    }

    public TaskDto mapToTaskDto(Task task) {
        TaskDto taskDto = TaskMapper.toTaskDto(task);

        if(Objects.nonNull(task.getReportedBy())) {
            taskDto.setReportedBy(UserMapper.toUserDto(userService.getUserById(task.getReportedBy().getId())));
        } else {
            // set reported By to the logged in user
            Users currentUsers = userService.getCurrentUser();
            taskDto.setReportedBy(UserMapper.toUserDto(currentUsers));
        }

        if(Objects.nonNull(task.getAssignees())) {
            taskDto.setAssignees(UserMapper.toListOfUserDto(task.getAssignees()));
        }

        return taskDto;
    }

    public TaskDto getTaskDetailsById(long id) {
        Optional<Task> optional = taskRepository.findById(id);

        if(optional.isEmpty()) {
            return null;
        }

        Task taskFromDB = optional.get();
        return mapToTaskDto(taskFromDB);
    }

    public Task updateTask(long id, TaskDto taskDto) {
        Optional<Task> optional = taskRepository.findById(id);

        if(optional.isEmpty()) {
            return null;
        }

        Task taskFromDB = optional.get();
        System.out.println("LLL");
        System.out.println(taskFromDB);
        taskFromDB.setTitle(taskDto.getTitle());
        taskFromDB.setDescription(taskDto.getDescription());
        taskFromDB.setReportedBy(userService.getUserByUsername(
                taskDto.getReportedBy().getUsername()));
        taskFromDB.setStatus(TaskStatus.getTaskStatus(taskDto.getStatus()));
        taskFromDB.setPriority(TaskPriority.getTaskPriority(taskDto.getPriority()));
        taskFromDB.setTaskAllowance(taskFromDB.getTaskAllowance());

        if(StringUtil.notNullNorEmpty(taskDto.getStartDate())) {
            taskFromDB.setStartDate(DateUtils.parseDate(taskDto.getStartDate()));
        }

        if(StringUtil.notNullNorEmpty(taskDto.getDueDate())) {
            taskFromDB.setDueDate(DateUtils.parseDate(taskDto.getDueDate()));
        }

        if(Objects.nonNull(taskDto.getAssignees())) {
            taskFromDB.setAssignees(getUsers(taskDto.getAssignees()));
        }

        return taskRepository.save(taskFromDB);
    }
}
