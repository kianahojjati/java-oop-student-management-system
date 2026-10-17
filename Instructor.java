package edu.portfolio.model;
public final class Instructor extends Person {
    public Instructor(String id, String name) { super(id, name); }
    @Override public String getRole() { return "Instructor"; }
}
