package camt.cbsd.services;

import camt.cbsd.entity.Student;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Profile("secondDataSource")
@Service
public class StudentSecondServiceImpl implements StudentService {
    @Override
    public List<Student> getStudents() {
        List<Student> students = new ArrayList<>();

        Student student = new Student(1,"SE-000","Chartchai","D",
                5.00,"images/temp.gif",true,0,
                "The Special One");
        students.add(student);

        return students;
    }

    @Override
    public Student findById(long id) {
        return null;
    }

    @Override
    public Student addStudent(Student student) {
        return null;
    }

    @Override
    public Student addStudent(Student student, String imageFileName, BufferedImage image) throws IOException {
        return null;
    }


}
