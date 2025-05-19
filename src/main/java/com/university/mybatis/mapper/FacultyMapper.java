package com.university.mybatis.mapper;

import com.university.mybatis.entity.FacultyMB;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FacultyMapper {
    List<FacultyMB> getAllFaculties();
    FacultyMB getFacultyById(@Param("id") Long id);
    void insertFaculty(FacultyMB faculty);
    void updateFaculty(FacultyMB faculty);
    void deleteFaculty(@Param("id") Long id);
}
