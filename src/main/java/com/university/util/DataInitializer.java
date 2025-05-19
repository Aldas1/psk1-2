package com.university.util;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
@Startup
public class DataInitializer {
    private static final Logger logger = Logger.getLogger(DataInitializer.class.getName());

    @PersistenceContext
    private EntityManager em;

    private boolean initialized = false;

    @PostConstruct
    @Transactional
    public void init() {
        try {
            if (!initialized) {
                // Check if tables already exist
                if (!tablesExist()) {
                    logger.info("Initializing database schema and test data...");

                    // Use native SQL to create tables and insert data
                    executeScript();

                    initialized = true;
                    logger.info("Database initialization completed successfully");
                } else {
                    logger.info("Database already initialized, skipping initialization");
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error initializing database", e);
        }
    }

    private boolean tablesExist() {
        try {
            // Try to query a table to see if it exists
            em.createNativeQuery("SELECT 1 FROM faculty LIMIT 1").getResultList();
            return true;
        } catch (Exception e) {
            // Table doesn't exist
            return false;
        }
    }

    @Transactional
    private void executeScript() {
        try {
            // First, drop tables if they exist (in correct order)
            em.createNativeQuery("DROP TABLE IF EXISTS student_course CASCADE").executeUpdate();
            em.createNativeQuery("DROP TABLE IF EXISTS student CASCADE").executeUpdate();
            em.createNativeQuery("DROP TABLE IF EXISTS course CASCADE").executeUpdate();
            em.createNativeQuery("DROP TABLE IF EXISTS faculty CASCADE").executeUpdate();

            // Create tables
            em.createNativeQuery(
                    "CREATE TABLE faculty (" +
                            "    id SERIAL PRIMARY KEY," +
                            "    name VARCHAR(100) NOT NULL," +
                            "    department VARCHAR(100)," +
                            "    version BIGINT DEFAULT 0" +
                            ")"
            ).executeUpdate();

            em.createNativeQuery(
                    "CREATE TABLE course (" +
                            "    id SERIAL PRIMARY KEY," +
                            "    course_code VARCHAR(20) NOT NULL UNIQUE," +
                            "    title VARCHAR(100) NOT NULL," +
                            "    credits INTEGER," +
                            "    faculty_id INTEGER REFERENCES faculty(id)," +
                            "    version BIGINT DEFAULT 0" +
                            ")"
            ).executeUpdate();

            em.createNativeQuery(
                    "CREATE TABLE student (" +
                            "    id SERIAL PRIMARY KEY," +
                            "    student_id VARCHAR(20) NOT NULL UNIQUE," +
                            "    first_name VARCHAR(50) NOT NULL," +
                            "    last_name VARCHAR(50) NOT NULL," +
                            "    email VARCHAR(100)," +
                            "    version BIGINT DEFAULT 0" +
                            ")"
            ).executeUpdate();

            em.createNativeQuery(
                    "CREATE TABLE student_course (" +
                            "    student_id INTEGER REFERENCES student(id) ON DELETE CASCADE," +
                            "    course_id INTEGER REFERENCES course(id) ON DELETE CASCADE," +
                            "    PRIMARY KEY (student_id, course_id)" +
                            ")"
            ).executeUpdate();

            // Insert sample data
            em.createNativeQuery(
                    "INSERT INTO faculty (name, department, version) VALUES " +
                            "('Faculty of Science', 'Computer Science', 0)," +
                            "('Faculty of Arts', 'Music', 0)," +
                            "('Faculty of Engineering', 'Civil Engineering', 0)"
            ).executeUpdate();

            em.createNativeQuery(
                    "INSERT INTO course (course_code, title, credits, faculty_id, version) VALUES " +
                            "('CS101', 'Introduction to Programming', 6, 1, 0)," +
                            "('CS202', 'Algorithms and Data Structures', 6, 1, 0)," +
                            "('MUS101', 'Music Theory', 4, 2, 0)," +
                            "('CE101', 'Engineering Mechanics', 6, 3, 0)"
            ).executeUpdate();

            em.createNativeQuery(
                    "INSERT INTO student (student_id, first_name, last_name, email, version) VALUES " +
                            "('S001', 'John', 'Doe', 'john.doe@example.com', 0)," +
                            "('S002', 'Jane', 'Smith', 'jane.smith@example.com', 0)," +
                            "('S003', 'Alice', 'Johnson', 'alice.johnson@example.com', 0)"
            ).executeUpdate();

            em.createNativeQuery(
                    "INSERT INTO student_course (student_id, course_id) VALUES " +
                            "(1, 1), (1, 2), (2, 3), (3, 4), (2, 1)"
            ).executeUpdate();

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error executing database initialization script", e);
            throw e;
        }
    }
}