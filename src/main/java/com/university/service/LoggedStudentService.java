package com.university.service;

import com.university.entity.Student;
import com.university.interceptor.Logged;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
@Logged 
public class LoggedStudentService {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public List<Student> getStudentsWithGradeAverage() {
        return em.createQuery("SELECT s FROM Student s", Student.class)
                .getResultList();
    }

    @Transactional
    public Student updateStudentWithLogging(Long id, String firstName, String lastName) {
        Student student = em.find(Student.class, id);
        if (student != null) {
            student.setFirstName(firstName);
            student.setLastName(lastName);
        }
        return student;
    }
}