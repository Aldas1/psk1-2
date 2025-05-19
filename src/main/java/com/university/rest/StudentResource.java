package com.university.rest;

import com.university.entity.Student;
import com.university.service.StudentService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/students")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StudentResource {

    @Inject
    private StudentService studentService;

    @GET
    public Response getAllStudents() {
        List<Student> students = studentService.getAllStudentsJpa();
        return Response.ok(students).build();
    }

    @GET
    @Path("/{id}")
    public Response getStudentById(@PathParam("id") Long id) {
        Student student = studentService.getStudentByIdJpa(id);

        if (student == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Student with ID " + id + " not found")
                    .build();
        }

        return Response.ok(student).build();
    }

    @POST
    public Response createStudent(Student student) {
        try {
            student.setId(null); // Ensure we're creating a new record
            studentService.saveStudentJpa(student);
            return Response.status(Response.Status.CREATED)
                    .entity(student)
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error creating student: " + e.getMessage())
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response updateStudent(@PathParam("id") Long id, Student student) {
        try {
            Student existingStudent = studentService.getStudentByIdJpa(id);

            if (existingStudent == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Student with ID " + id + " not found")
                        .build();
            }

            // Update the existing student with new values
            student.setId(id);
            studentService.saveStudentJpa(student);

            return Response.ok(student).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error updating student: " + e.getMessage())
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteStudent(@PathParam("id") Long id) {
        try {
            Student existingStudent = studentService.getStudentByIdJpa(id);

            if (existingStudent == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Student with ID " + id + " not found")
                        .build();
            }

            studentService.deleteStudentJpa(id);

            return Response.noContent().build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error deleting student: " + e.getMessage())
                    .build();
        }
    }
}