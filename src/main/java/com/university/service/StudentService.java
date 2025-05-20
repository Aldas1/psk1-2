package com.university.service;

import com.university.dao.jpa.StudentJpaDao;
import com.university.dao.mybatis.StudentMyBatisDao;
import com.university.entity.Student;
import com.university.mybatis.entity.StudentMB;
import jakarta.ejb.Stateless;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceUnit;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.logging.Logger;

@Stateless
public class StudentService {
    private static final Logger logger = Logger.getLogger(StudentService.class.getName());

    @PersistenceContext
    private EntityManager em;

    @Inject
    private StudentJpaDao studentJpaDao;

    @Inject
    private StudentMyBatisDao studentMyBatisDao;

    
    public List<Student> getAllStudentsJpa() {
        return studentJpaDao.getAllStudents();
    }

    public Student getStudentByIdJpa(Long id) {
        return studentJpaDao.getStudentById(id);
    }

    @Transactional
    public void saveStudentJpa(Student student) {
        studentJpaDao.saveStudent(student);
    }

    @Transactional
    public void deleteStudentJpa(Long id) {
        studentJpaDao.deleteStudent(id);
    }

    @Transactional
    public void enrollStudentInCourseJpa(Long studentId, Long courseId) {
        studentJpaDao.enrollStudentInCourse(studentId, courseId);
    }

    @Transactional
    public void removeStudentFromCourseJpa(Long studentId, Long courseId) {
        studentJpaDao.removeStudentFromCourse(studentId, courseId);
    }

    public List<Student> getStudentsByCourseIdJpa(Long courseId) {
        return studentJpaDao.getStudentsByCourseId(courseId);
    }

    /**
     * Simulates a concurrent modification - this will be called by the OptimisticLockingDemoService
     */
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void simulateConcurrentModification(Long studentId) {
        try {
            
            Student student = em.find(Student.class, studentId);
            if (student != null) {
                student.setFirstName(student.getFirstName() + " - Modified by concurrent transaction");
                em.flush(); 
                logger.info("Successfully modified student in concurrent transaction: " + student.getFirstName());
            }
        } catch (Exception e) {
            logger.warning("Error in concurrent modification simulation: " + e.getMessage());
            throw e;
        }
    }

    
    public List<StudentMB> getAllStudentsMyBatis() {
        return studentMyBatisDao.getAllStudents();
    }

    public StudentMB getStudentByIdMyBatis(Long id) {
        return studentMyBatisDao.getStudentById(id);
    }

    @Transactional
    public void saveStudentMyBatis(StudentMB student) {
        studentMyBatisDao.saveStudent(student);
    }

    @Transactional
    public void deleteStudentMyBatis(Long id) {
        studentMyBatisDao.deleteStudent(id);
    }

    @Transactional
    public void enrollStudentInCourseMyBatis(Long studentId, Long courseId) {
        studentMyBatisDao.enrollStudentInCourse(studentId, courseId);
    }

    @Transactional
    public void removeStudentFromCourseMyBatis(Long studentId, Long courseId) {
        studentMyBatisDao.removeStudentFromCourse(studentId, courseId);
    }

    public List<StudentMB> getStudentsByCourseIdMyBatis(Long courseId) {
        return studentMyBatisDao.getStudentsByCourseId(courseId);
    }
}