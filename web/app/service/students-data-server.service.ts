import {Injectable} from '@angular/core';
import {Student} from '../students/student';
import {Http, Response} from '@angular/http';
import {Observable} from "rxjs/Rx";


@Injectable()
export class StudentsDataServerService {
  constructor(private http: Http){}
  getStudentsData(){
    let studentArray:Student[];
    return this.http.get('http://localhost:8080/student')
      .map(res => res.json());

  }

  getStudent(id:number){
    let student:Student;
    return this.http.get('http://localhost:8080/student/'+id)
      .map((res:Response) => {
        if (res) {
          if (res.status === 200) {
            return res.json()
          }
          if (res.status === 204){
            return null;
          }
        }
      })
      .catch((error: any) => {
        if (error.status === 500) {
          return Observable.throw(new Error(error.status));
        }
        else if (error.status === 400) {
          return Observable.throw(new Error(error.status));
        }
        else if (error.status === 409) {
          return Observable.throw(new Error(error.status));
        }
        else if (error.status === 406) {
          return Observable.throw(new Error(error.status));
        }
        return error;
      })
    ;


  }

  addStudent(student:Student){

  }
}
