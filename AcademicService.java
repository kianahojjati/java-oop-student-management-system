package edu.portfolio.service;
import edu.portfolio.exception.RegistrationException;
import edu.portfolio.grading.GradePolicy;
import edu.portfolio.model.*;
import java.util.*;
/** Business rules for registration, grade entry, and weighted GPA. */
public final class AcademicService {
    private final Map<String, Student> students = new LinkedHashMap<>();
    private final Map<String, Instructor> instructors = new LinkedHashMap<>();
    private final Map<String, Course> courses = new LinkedHashMap<>();
    private final Map<String, Enrollment> enrollments = new LinkedHashMap<>();
    private final GradePolicy gradePolicy;
    public AcademicService(GradePolicy gradePolicy) { this.gradePolicy = Objects.requireNonNull(gradePolicy); }
    private static String key(String studentId, String courseCode) { return studentId + "|" + courseCode.toUpperCase(Locale.ROOT); }
    public Student addStudent(String id, String name) {
        Student student = new Student(id, name);
        if (students.putIfAbsent(student.getId(), student) != null) throw new RegistrationException("Student ID exists");
        return student;
    }
    public Instructor addInstructor(String id, String name) {
        Instructor instructor = new Instructor(id, name);
        if (instructors.putIfAbsent(instructor.getId(), instructor) != null) throw new RegistrationException("Instructor ID exists");
        return instructor;
    }
    public Course addCourse(String code, String title, int capacity, int credits, String instructorId) {
        Instructor instructor = instructors.get(instructorId);
        if (instructor == null) throw new RegistrationException("Instructor not found");
        Course course = new Course(code, title, capacity, credits, instructor);
        if (courses.putIfAbsent(course.getCode(), course) != null) throw new RegistrationException("Course already exists");
        return course;
    }
    public Enrollment enroll(String studentId, String courseCode) {
        Student student = students.get(studentId);
        Course course = courses.get(courseCode.toUpperCase(Locale.ROOT));
        if (student == null || course == null) throw new RegistrationException("Student or course not found");
        String enrollmentKey = key(studentId, course.getCode());
        if (enrollments.containsKey(enrollmentKey)) throw new RegistrationException("Student already enrolled");
        if (enrollments.values().stream().filter(e -> e.getCourse().getCode().equals(course.getCode())).count() >= course.getCapacity())
            throw new RegistrationException("Course is full");
        Enrollment enrollment = new Enrollment(student, course);
        enrollments.put(enrollmentKey, enrollment);
        return enrollment;
    }
    public void recordGrade(String studentId, String courseCode, double grade) {
        Enrollment enrollment = enrollments.get(key(studentId, courseCode));
        if (enrollment == null) throw new RegistrationException("Enrollment not found");
        enrollment.setGrade(grade);
    }
    /** Credit-weighted GPA, excluding courses without grades. */
    public OptionalDouble calculateGpa(String studentId) {
        if (!students.containsKey(studentId)) throw new RegistrationException("Student not found");
        double weighted = 0; int totalCredits = 0;
        for (Enrollment enrollment : enrollments.values()) {
            if (enrollment.getStudent().getId().equals(studentId) && enrollment.getGrade() != null) {
                int credits = enrollment.getCourse().getCredits();
                weighted += gradePolicy.gradePoints(enrollment.getGrade()) * credits;
                totalCredits += credits;
            }
        }
        return totalCredits == 0 ? OptionalDouble.empty() : OptionalDouble.of(weighted / totalCredits);
    }
    public Collection<Student> getStudents() { return Collections.unmodifiableCollection(students.values()); }
    public Collection<Instructor> getInstructors() { return Collections.unmodifiableCollection(instructors.values()); }
    public Collection<Course> getCourses() { return Collections.unmodifiableCollection(courses.values()); }
    public Collection<Enrollment> getEnrollments() { return Collections.unmodifiableCollection(enrollments.values()); }
}
