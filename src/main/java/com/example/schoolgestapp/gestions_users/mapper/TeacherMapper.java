package com.example.schoolgestapp.gestions_users.mapper;

import com.example.schoolgestapp.entity.Teacher;
import com.example.schoolgestapp.gestions_users.dto.TeacherDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TeacherMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "name", expression = "java(teacher.getUser() != null ? teacher.getUser().getFirstName() + \" \" + teacher.getUser().getLastName() : null)")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "phone", source = "user.phone")
    @Mapping(target = "departmentName", source = "department.name")
    TeacherDTO toDto(Teacher teacher);
}
