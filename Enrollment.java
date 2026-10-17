package edu.portfolio.model;
public final class Enrollment {
    private final Student student;
    private final Course course;
    private Double grade;
    public Enrollment(Student student, Course course) {
        if (student == null || course == null) throw new IllegalArgumentException("Student and course required");
        this.student = student; this.course = course;
    }
    public Student getStudent() { return student; }
    public Course getCourse() { return course; }
    public Double getGrade() { return grade; }
    public void setGrade(double grade) {
        if (!Double.isFinite(grade) || grade < 0 || grade > 100) throw new IllegalArgumentException("Grade must be 0–100");
        this.grade = grade;
    }
}
