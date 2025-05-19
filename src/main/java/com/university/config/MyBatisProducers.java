package com.university.config;

import com.university.mybatis.mapper.CourseMapper;
import com.university.mybatis.mapper.FacultyMapper;
import com.university.mybatis.mapper.StudentMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

@ApplicationScoped
public class MyBatisProducers {

    @Inject
    @Named("sqlSessionFactory")
    private SqlSessionFactory sqlSessionFactory;

    @Produces
    @ApplicationScoped
    public CourseMapper produceCourseMapper() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        return sqlSession.getMapper(CourseMapper.class);
    }

    @Produces
    @ApplicationScoped
    public FacultyMapper produceFacultyMapper() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        return sqlSession.getMapper(FacultyMapper.class);
    }

    @Produces
    @ApplicationScoped
    public StudentMapper produceStudentMapper() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        return sqlSession.getMapper(StudentMapper.class);
    }
}