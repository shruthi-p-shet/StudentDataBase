package com.jsp.StudentManagementSystem.service;

import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.jsp.StudentManagementSystem.dto.MessageData;
import com.jsp.StudentManagementSystem.dto.StudentRequest;
import com.jsp.StudentManagementSystem.dto.StudentResponse;
import com.jsp.StudentManagementSystem.entity.Student;
import com.jsp.StudentManagementSystem.utility.ResponseStructure;

public interface StudentService {
	public ResponseEntity<ResponseStructure<StudentResponse>> saveStudent(StudentRequest studentRequest);
	public ResponseEntity<ResponseStructure<Student>> updateStudent(Student student,int studentId);
	public ResponseEntity<ResponseStructure<Student>> deleteStudent(int studentId);
	public ResponseEntity<ResponseStructure<Student>> findStudentById(int studentId);
	public ResponseEntity<ResponseStructure<List<Student>>> findAllStudent();

	public ResponseEntity<ResponseStructure<StudentResponse>> findByEmail(String studentEmail);
	ResponseEntity<String> extractDataFromExcel(MultipartFile file) throws IOException;
	ResponseEntity<String> writeToExcel(String filePath) throws IOException;

	public ResponseEntity<String> sendMail(MessageData messageData);

	public ResponseEntity<String> sendMimeMessage(MessageData messageData) throws MessagingException; 
}
