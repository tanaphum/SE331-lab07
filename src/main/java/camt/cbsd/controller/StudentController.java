package camt.cbsd.controller;

import camt.cbsd.entity.Student;
import camt.cbsd.services.StudentService;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.List;
import org.apache.commons.io.FilenameUtils;

@Component
@ConfigurationProperties(prefix="server")
@Path("/student")
public class StudentController {
    String imageServerDir;
    StudentService studentService;
    String baseUrl;
    String imageUrl;

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
    public Response getStudents(){

        List<Student> students = studentService.getStudents();
        return Response.ok(students).build();
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStudent(@PathParam("id") long id){
        Student student = studentService.findById(id);
        if (student != null)
            return Response.ok(student).build();
        else
            //http code 204
            return Response.status(Response.Status.NO_CONTENT).build();

    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadOnlyStudent(Student student){
        System.out.println(student);
        return Response.ok().entity(student).build();
    }

    @GET
    @Path("/images/{fileName}")
    @Produces({"image/png","image/jpg","image/gif"})
    public Response getStudentImage(@PathParam("fileName") String filename){
        File file = Paths.get( imageServerDir + filename).toFile();
        Response.ResponseBuilder responseBuilder = Response.ok((Object) file);
        responseBuilder.header("Content-Disposition", "attachment;filename=\"a.jpg\"");
        if(file.exists()) {
            return responseBuilder.build();
        }
        else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    @Path("/images")
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    @Produces({MediaType.TEXT_PLAIN})
    public Response uploadImage(@FormDataParam("file")InputStream fileInputStream,
                                @FormDataParam("file")FormDataContentDisposition cdh)throws IOException{
        BufferedImage img = ImageIO.read(fileInputStream);
        String oldFilename = cdh.getFileName();
        String ext = FilenameUtils.getExtension(oldFilename);
        String newFilename = Integer.toString(LocalTime.now().hashCode(),16) + Integer.toString(oldFilename.hashCode(),16)+ "." +ext;
        File targetFile = Files.createFile(Paths.get(imageServerDir+newFilename)).toFile();
        ImageIO.write(img,ext,targetFile);

        return Response.ok(baseUrl+imageUrl+newFilename).build();

    }
}
