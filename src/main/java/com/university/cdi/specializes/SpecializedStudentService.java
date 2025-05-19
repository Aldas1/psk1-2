package com.university.cdi.specializes;

import com.university.entity.Student;
import com.university.service.StudentService;
import jakarta.enterprise.inject.Specializes;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.logging.Logger;

@Named
@Specializes
public class SpecializedStudentService extends StudentService {

    private static final Logger LOGGER = Logger.getLogger(SpecializedStudentService.class.getName());

    @Override
    public List<Student> getAllStudentsJpa() {
        LOGGER.info("Using SPECIALIZED StudentService to get all students");
        List<Student> students = super.getAllStudentsJpa();
        LOGGER.info("Retrieved " + students.size() + " students");
        return students;
    }

    @Override
    @Transactional
    public void saveStudentJpa(Student student) {
        LOGGER.info("Using SPECIALIZED StudentService to save student: " + student.getFirstName() + " " + student.getLastName());
        super.saveStudentJpa(student);
        LOGGER.info("Student saved successfully");
    }
}