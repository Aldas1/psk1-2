package com.university.rest;

import com.university.entity.Course;
import com.university.entity.Faculty;
import com.university.service.CourseService;
import com.university.service.FacultyService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

import java.net.URI;
import java.util.List;

@Path("/courses")
@RequestScoped
public class CourseResource {

    @Inject
    private CourseService courseService;

    @Inject
    private FacultyService facultyService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Course> getAllCourses() {
        return courseService.getAllCoursesJpa();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCourseById(@PathParam("id") Long id) {
        Course course = courseService.getCourseByIdJpa(id);
        if (course == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Course with ID " + id + " not found")
                    .build();
        }
        return Response.ok(course).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createCourse(CourseDTO courseDTO) {
        try {
            Course course = convertDtoToEntity(courseDTO, null);

            courseService.saveCourseJpa(course);

            URI location = UriBuilder.fromResource(CourseResource.class)
                    .path("/{id}")
                    .resolveTemplate("id", course.getId())
                    .build();

            return Response.created(location).entity(course).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error creating course: " + e.getMessage())
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCourse(@PathParam("id") Long id, CourseDTO courseDTO) {
        Course existingCourse = courseService.getCourseByIdJpa(id);
        if (existingCourse == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Course with ID " + id + " not found")
                    .build();
        }

        try {
            Course course = convertDtoToEntity(courseDTO, id);

            courseService.saveCourseJpa(course);

            return Response.ok(course).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error updating course: " + e.getMessage())
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteCourse(@PathParam("id") Long id) {
        Course existingCourse = courseService.getCourseByIdJpa(id);
        if (existingCourse == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Course with ID " + id + " not found")
                    .build();
        }

        try {
            courseService.deleteCourseJpa(id);
            return Response.noContent().build();
        } catch (Exception e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("Cannot delete course with ID " + id + ". It may have related students.")
                    .build();
        }
    }

    private Course convertDtoToEntity(CourseDTO dto, Long id) {
        Course course = new Course();
        if (id != null) {
            course.setId(id);
        }

        course.setCourseCode(dto.getCourseCode());
        course.setTitle(dto.getTitle());
        course.setCredits(dto.getCredits());

        if (dto.getFacultyId() != null) {
            Faculty faculty = facultyService.getFacultyByIdJpa(dto.getFacultyId());
            if (faculty == null) {
                throw new IllegalArgumentException("Faculty with ID " + dto.getFacultyId() + " not found");
            }
            course.setFaculty(faculty);
        }

        return course;
    }

    // DTO for Course to avoid circular references in JSON
    public static class CourseDTO {
        private String courseCode;
        private String title;
        private Integer credits;
        private Long facultyId;

        // Getters and setters
        public String getCourseCode() { return courseCode; }
        public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public Integer getCredits() { return credits; }
        public void setCredits(Integer credits) { this.credits = credits; }
        public Long getFacultyId() { return facultyId; }
        public void setFacultyId(Long facultyId) { this.facultyId = facultyId; }
    }
}