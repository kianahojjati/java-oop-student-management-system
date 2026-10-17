package edu.portfolio.model;
public final class Student extends Person {
    public Student(String id, String name) { super(id, name); }
    @Override public String getRole() { return "Student"; }
}
