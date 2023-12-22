package com.jsp.StudentManagementSystem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.jsp.StudentManagementSystem.entity.Student;

public interface StudentRepo extends JpaRepository<Student, Integer>{
	//creating abstract method to fetch data from database
	public Student findByStudentEmail(String name);
	
	//creating query to get data from database
	@Query("select s.studentEmail from Student s where s.studentGrade=?1")
	public List<String> getStudentEmailByGrade(String grade);
	
	//retrieving student name based on student id
	@Query("select s.studentName from Student s where s.studentId=?1")
	public List<String> getStudentNameById(int id);
	
	//query to retrieving phone number based on student names
	@Query("select s.studentPhn from Student s where s.studentName=?1")
	public List<Long> getPhnByName(String name);
}

































