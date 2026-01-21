package com.example.schoolgestapp.repository;

import com.example.schoolgestapp.entity.Attendance;
import com.example.schoolgestapp.entity.Planning;
import com.example.schoolgestapp.entity.Student;
import com.example.schoolgestapp.entity.enums.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IAttendance extends JpaRepository<Attendance, Long> {
    List<Attendance> findByStudent(Student student);
    List<Attendance> findByPlanning(Planning planning);
    Optional<Attendance> findByStudentAndPlanning(Student student, Planning planning);
    long countByStudentAndStatusAndJustificationStatus(Student student, AttendanceStatus status, com.example.schoolgestapp.entity.enums.JustificationStatus jStatus);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(a) FROM Attendance a WHERE a.planning.teacher.id = :teacherId")
    long countByTeacherId(@org.springframework.data.repository.query.Param("teacherId") Long teacherId);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(a) FROM Attendance a WHERE a.planning.teacher.id = :teacherId AND a.status = com.example.schoolgestapp.entity.enums.AttendanceStatus.PRESENT")
    long countPresentByTeacherId(@org.springframework.data.repository.query.Param("teacherId") Long teacherId);
}
