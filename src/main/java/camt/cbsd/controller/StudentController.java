package camt.cbsd.controller;

import camt.cbsd.entity.Student;
import camt.cbsd.services.StudentService;
import camt.cbsd.services.StudentServiceImpl;
import org.apache.commons.io.FilenameUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response.ResponseBuilder;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Component
@Path("/student")
@ConfigurationProperties(prefix = "server")
public class StudentController {
    StudentService studentService;
    String imageServerDir;
    String imageUrl;
    String baseUrl;

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setImageServerDir(String imageServerDir) {
        this.imageServerDir = imageServerDir;
    }

    @Autowired
    public void setStudentService(StudentService studentService) {
        this.studentService = studentService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStudents() {

        List<Student> students = studentService.getStudents();
        return Response.ok(students).build();
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStudent(@PathParam("id") long id) {
        Student student = studentService.findById(id);
        if (student != null)
            return Response.ok(student).build();
        else
            //http code 204
            return Response.status(Response.Status.NO_CONTENT).build();

    }

    @OPTIONS
    public Response checkOK(){
        return Response.ok().build();
    }
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON})
    public Response uploadOnlyStudent(Student student) {

        studentService.addStudent(student);
        return Response.ok().entity(student).build();

    }

    @POST
    @Path("/image")
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    @Produces({MediaType.TEXT_PLAIN})
    public Response uploadImage(@FormDataParam("file") InputStream fileInputStream,
                                 @FormDataParam("file") FormDataContentDisposition cdh) throws IOException {
        try {
            BufferedImage img = ImageIO.read(fileInputStream);
            String oldFilename = cdh.getFileName();
            String ext = FilenameUtils.getExtension(oldFilename);
            String newFilename = Integer.toString(LocalTime.now().hashCode(), 16) + Integer.toString(oldFilename.hashCode(), 16) + "." + ext;
            File targetFile = Files.createFile(Paths.get(imageServerDir + newFilename)).toFile();
            ImageIO.write(img, ext, targetFile);

            return Response.ok(baseUrl + imageUrl + newFilename).build();
        }catch(NullPointerException e){
            return Response.status(202).build();
        }
    }


    @POST
    @Path("/student")
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadStudent(@FormDataParam("file") InputStream fileInputStream,
                                  @FormDataParam("file") FormDataContentDisposition cdh,
                                  @FormDataParam("student") FormDataBodyPart dataBodyPart) throws Exception {

        BufferedImage img = ImageIO.read(fileInputStream);

        dataBodyPart.setMediaType(MediaType.APPLICATION_JSON_TYPE);
        Student student = dataBodyPart.getValueAs(Student.class);
        studentService.addStudent(student,cdh.getFileName(),img);

        return Response.ok(student).build();
    }

    @GET
    @Path("/images/{fileName}")
    @Produces({"image/png", "image/jpg", "image/gif"})
    public Response getStuentImage(@PathParam("fileName") String filename) {
        File file = Paths.get(imageServerDir + filename).toFile();
        if (file.exists()) {
            ResponseBuilder responseBuilder = Response.ok((Object) file);
            responseBuilder.header("Content-Disposition", "attachment; filename=" + filename);
            return responseBuilder.build();
        }else{
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
