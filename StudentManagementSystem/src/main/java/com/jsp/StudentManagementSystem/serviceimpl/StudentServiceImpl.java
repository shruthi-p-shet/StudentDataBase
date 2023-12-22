package com.jsp.StudentManagementSystem.serviceimpl;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.jsp.StudentManagementSystem.customexception.StudentNotFoundException;
import com.jsp.StudentManagementSystem.dto.MessageData;
import com.jsp.StudentManagementSystem.dto.StudentRequest;
import com.jsp.StudentManagementSystem.dto.StudentResponse;
import com.jsp.StudentManagementSystem.entity.Student;
import com.jsp.StudentManagementSystem.repository.StudentRepo;
import com.jsp.StudentManagementSystem.service.StudentService;
import com.jsp.StudentManagementSystem.utility.ResponseStructure;

//import jakarta.mail.internet.MimeMessage;

@Service
public class StudentServiceImpl implements StudentService {
	@Autowired
	private StudentRepo repo;
	@Autowired
	private JavaMailSender javaMailSender;

	// inserting student object data to mysql database
	@Override
	public ResponseEntity<ResponseStructure<StudentResponse>> saveStudent(StudentRequest studentRequest) {
		Student student = new Student();
		student.setStudentName(studentRequest.getStudentName());
		student.setStudentEmail(studentRequest.getStudentEmail());
		student.setStudentGrade(studentRequest.getStudentGrade());
		student.setStudentPhn(studentRequest.getStudentPhn());

		Student stud2 = repo.save(student);

		StudentResponse response = new StudentResponse();
		response.setStudentGrade(stud2.getStudentGrade());
		response.setStudentId(stud2.getStudentId());
		response.setStudentName(stud2.getStudentName());

		ResponseStructure<StudentResponse> stucture = new ResponseStructure<StudentResponse>();
		stucture.setData(response);
		stucture.setMsg("data stored successfully!!!!");
		stucture.setStatus(HttpStatus.CREATED.value());
		return new ResponseEntity<ResponseStructure<StudentResponse>>(stucture, HttpStatus.CREATED);
	}

	// updating single entry based on id
	@Override
	public ResponseEntity<ResponseStructure<Student>> updateStudent(Student student, int studentId) {
		Optional<Student> ostud = repo.findById(studentId);
		if (ostud.isPresent()) {
			Student stud2 = ostud.get();

			// student.setStudentId(stud2.getStudentId());

			student.setStudentId(studentId);
			student = repo.save(student);
			ResponseStructure<Student> response = new ResponseStructure<Student>();
			response.setData(stud2);
			response.setMsg("data updated successfully!!!!");
			response.setStatus(HttpStatus.CREATED.value());
			return new ResponseEntity<ResponseStructure<Student>>(response, HttpStatus.OK);
		} else {
			throw new StudentNotFoundException("student not found by id " + studentId);
		}

	}

	// deleting single entry based on student id
	@Override
	public ResponseEntity<ResponseStructure<Student>> deleteStudent(int studentId) {
		Optional<Student> student = repo.findById(studentId);
		if (student.isPresent()) {
			Student delete = student.get();
			repo.delete(delete);
			ResponseStructure<Student> response = new ResponseStructure<Student>();
			response.setData(delete);
			response.setMsg("data stored successfully!!!!");
			response.setStatus(HttpStatus.CREATED.value());
			return new ResponseEntity<ResponseStructure<Student>>(response, HttpStatus.OK);
		} else {
			throw new StudentNotFoundException("failed to delete");
		}

	}

	// fetching student data based on id
	@Override
	public ResponseEntity<ResponseStructure<Student>> findStudentById(int studentId) {
		Optional<Student> optional = repo.findById(studentId);
		if (optional.isPresent()) {
			Student student = optional.get();
			ResponseStructure<Student> response = new ResponseStructure<Student>();
			response.setData(student);
			response.setMsg("data stored successfully!!!!");
			response.setStatus(HttpStatus.CREATED.value());
			return new ResponseEntity<ResponseStructure<Student>>(response, HttpStatus.OK);

		} else {
			throw new StudentNotFoundException("student not found by id " + studentId);
		}
	}

	// fetching all the details of all student
	@Override
	public ResponseEntity<ResponseStructure<List<Student>>> findAllStudent() {
		List<Student> list = repo.findAll();
		ResponseStructure<List<Student>> response = new ResponseStructure<List<Student>>();
		response.setData(list);
		response.setMsg("data stored successfully!!!!");
		response.setStatus(HttpStatus.CREATED.value());
		return new ResponseEntity<ResponseStructure<List<Student>>>(response, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<ResponseStructure<StudentResponse>> findByEmail(String studentEmail) {
		Student student = repo.findByStudentEmail(studentEmail);
		if (student != null) {
			StudentResponse response = new StudentResponse();
			response.setStudentGrade(student.getStudentGrade());
			response.setStudentId(student.getStudentId());
			response.setStudentName(student.getStudentName());

			ResponseStructure<StudentResponse> structure = new ResponseStructure<StudentResponse>();
			structure.setData(response);
			structure.setMsg("data stored successfully!!!!");
			structure.setStatus(HttpStatus.FOUND.value());
			return new ResponseEntity<ResponseStructure<StudentResponse>>(structure, HttpStatus.FOUND);

		} else {
			throw new StudentNotFoundException("student not found by email");
		}
	}

	// inserting the data to mysql database from excel sheet
	@Override
	public ResponseEntity<String> extractDataFromExcel(MultipartFile file) throws IOException {
		XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
		for (Sheet sheet : workbook) { // Sheet must import from ss.model Sheet is interface
			for (Row row : sheet) {

				// if(row.getFirstCellNum()>0) {
				if (row.getRowNum() > 0) {
					if (row != null) {
						String name = row.getCell(0).getStringCellValue();
						String email = row.getCell(1).getStringCellValue();
						long phoneNumber = (long) row.getCell(2).getNumericCellValue();
						String grade = row.getCell(3).getStringCellValue();
						String password = row.getCell(4).getStringCellValue();

						System.out.println(name + ", " + email + ", " + phoneNumber + ", " + grade + ", " + password);

						// in order to save the data into the data base
						Student student = new Student();
						student.setStudentName(name);
						student.setStudentEmail(email);
						// student.setId(0);
						student.setStudentGrade(grade);
						student.setPassword(password);
						student.setStudentPhn(phoneNumber);

						repo.save(student);
					}
				}
			}
		}
		workbook.close();
		return null;
	}

	// extracting the data from database and storing it into excel sheet
	@Override
	public ResponseEntity<String> writeToExcel(String filePath) throws IOException {
		List<Student> students = repo.findAll();

		XSSFWorkbook workBook = new XSSFWorkbook();

		XSSFSheet sheet = workBook.createSheet();

		Row header = sheet.createRow(0);
		header.createCell(0).setCellValue("StudentId");
		header.createCell(1).setCellValue("StudentName");
		header.createCell(2).setCellValue("StudentPhNo");
		header.createCell(3).setCellValue("StudentEmail");
		header.createCell(4).setCellValue("StudentGrade");
		header.createCell(5).setCellValue("StudentPassword");

		int rowNum = 1;
		for (Student std : students) {
			Row row = sheet.createRow(rowNum++);

			row.createCell(0).setCellValue(std.getStudentId());
			row.createCell(1).setCellValue(std.getStudentName());
			row.createCell(2).setCellValue(std.getStudentPhn());
			row.createCell(3).setCellValue(std.getStudentEmail());
			row.createCell(4).setCellValue(std.getStudentGrade());
			row.createCell(5).setCellValue(std.getPassword());
		}
		FileOutputStream outputStream = new FileOutputStream(filePath);
		workBook.write(outputStream);

		workBook.close();
		return new ResponseEntity<String>("Data transferd successfulley ", HttpStatus.OK);
	}

	// Sending text format mail to any given email
	@Override
	public ResponseEntity<String> sendMail(MessageData messageData) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(messageData.getTo()); // taking second one to send multiple mails (var aggs)
		message.setSubject(messageData.getSubject());
		message.setText(messageData.getText() + "\n\n Thamks and Regards\n\n" + messageData.getSenderName() + "\n"
				+ messageData.getSenderAddress());
		message.setSentDate(new Date());

		javaMailSender.send(message);
		return new ResponseEntity<String>("Mail send  successfully!!", HttpStatus.OK);
	}

	// sending html file as mail
	@Override
	public ResponseEntity<String> sendMimeMessage(MessageData messageData) throws MessagingException {
		String body = messageData.getText() + "<br><br>" + "<h4>Thanks and Regards<br>" + messageData.getSenderName()
				+ "<br>" + messageData.getSenderAddress() + "</h4>"
				+ "<img src=\"https://www.jspiders.com/_nuxt/img/logo_jspiders.3b552d0.png\" width=\"250\" height=\"100\">";

		MimeMessage mime = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mime, false);
		helper.setTo(messageData.getTo());
		helper.setSentDate(new Date());
		helper.setText(body, true);
		helper.setSubject(messageData.getSubject());

		javaMailSender.send(mime);
		return new ResponseEntity<String>("mime msg sent successfully!!!", HttpStatus.OK);
	}

}
