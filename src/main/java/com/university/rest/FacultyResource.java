package com.university.rest;

import com.university.entity.Faculty;
import com.university.service.FacultyService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

import java.net.URI;
import java.util.List;

@Path("/faculties")
@RequestScoped
public class FacultyResource {

    @Inject
    private FacultyService facultyService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Faculty> getAllFaculties() {
        return facultyService.getAllFacultiesJpa();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFacultyById(@PathParam("id") Long id) {
        Faculty faculty = facultyService.getFacultyByIdJpa(id);
        if (faculty == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Faculty with ID " + id + " not found")
                    .build();
        }
        return Response.ok(faculty).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createFaculty(Faculty faculty) {
        if (faculty.getId() != null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Faculty ID should not be provided for creation")
                    .build();
        }

        facultyService.saveFacultyJpa(faculty);

        URI location = UriBuilder.fromResource(FacultyResource.class)
                .path("/{id}")
                .resolveTemplate("id", faculty.getId())
                .build();

        return Response.created(location).entity(faculty).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateFaculty(@PathParam("id") Long id, Faculty faculty) {
        Faculty existingFaculty = facultyService.getFacultyByIdJpa(id);
        if (existingFaculty == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Faculty with ID " + id + " not found")
                    .build();
        }

        faculty.setId(id); // Ensure ID matches path parameter
        facultyService.saveFacultyJpa(faculty);

        return Response.ok(faculty).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteFaculty(@PathParam("id") Long id) {
        Faculty existingFaculty = facultyService.getFacultyByIdJpa(id);
        if (existingFaculty == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Faculty with ID " + id + " not found")
                    .build();
        }

        try {
            facultyService.deleteFacultyJpa(id);
            return Response.noContent().build();
        } catch (Exception e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("Cannot delete faculty with ID " + id + ". It may have related courses.")
                    .build();
        }
    }
}