package com.university.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.Reader;

@ApplicationScoped
public class MyBatisConfig {

    @Resource(lookup = "java:comp/DefaultDataSource")
    private DataSource dataSource;

    private SqlSessionFactory sqlSessionFactory;

    @PostConstruct
    public void init() {
        try {
            Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader, System.getProperties());
        } catch (IOException e) {
            throw new RuntimeException("Error initializing MyBatis SqlSessionFactory", e);
        }
    }

    @Produces
    @Named("sqlSessionFactory")
    public SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionFactory;
    }
}
