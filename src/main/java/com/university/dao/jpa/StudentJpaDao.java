package com.university.dao.jpa;

import com.university.entity.Course;
import com.university.entity.Student;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.logging.Logger;
import java.util.Set;
import java.util.HashSet;

@ApplicationScoped
public class StudentJpaDao {
    private static final Logger logger = Logger.getLogger(StudentJpaDao.class.getName());

    @PersistenceContext
    private EntityManager em;

    public List<Student> getAllStudents() {
        return em.createQuery("SELECT s FROM Student s", Student.class).getResultList();
    }

    public Student getStudentById(Long id) {
        try {
            return em.createQuery(
                            "SELECT s FROM Student s LEFT JOIN FETCH s.courses WHERE s.id = :id",
                            Student.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (Exception e) {
            logger.warning("Error fetching student by id " + id + ": " + e.getMessage());
            return null;
        }
    }

    @Transactional
    public void saveStudent(Student student) {
        try {
            if (student.getId() == null) {
                // For new entities, explicitly set version to null
                student.setVersion(null);

                // Clear any existing associations that might cause problems
                if (student.getCourses() != null && !student.getCourses().isEmpty()) {
                    Set<Course> courses = new HashSet<>(student.getCourses());
                    student.setCourses(new HashSet<>());

                    // First persist the student
                    em.persist(student);
                    em.flush();

                    // Now re-add the courses
                    for (Course course : courses) {
                        Course managedCourse = em.find(Course.class, course.getId());
                        student.addCourse(managedCourse);
                    }
                } else {
                    em.persist(student);
                }
            } else {
                em.merge(student);
            }
            em.flush();
        } catch (OptimisticLockException e) {
            logger.warning("OptimisticLockException caught in saveStudent: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.severe("Error in saveStudent: " + e.getMessage());
            throw e;
        }
    }

    @Transactional
    public void deleteStudent(Long id) {
        Student student = em.find(Student.class, id);
        if (student != null) {
            em.remove(student);
        }
    }

    @Transactional
    public void enrollStudentInCourse(Long studentId, Long courseId) {
        Student student = em.find(Student.class, studentId);
        Course course = em.find(Course.class, courseId);

        if (student != null && course != null) {
            student.addCourse(course);
        }
    }

    @Transactional
    public void removeStudentFromCourse(Long studentId, Long courseId) {
        Student student = em.find(Student.class, studentId);
        Course course = em.find(Course.class, courseId);

        if (student != null && course != null) {
            student.removeCourse(course);
        }
    }

    public List<Student> getStudentsByCourseId(Long courseId) {
        return em.createQuery(
                        "SELECT s FROM Student s JOIN s.courses c WHERE c.id = :courseId",
                        Student.class)
                .setParameter("courseId", courseId)
                .getResultList();
    }
}