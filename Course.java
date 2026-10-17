package edu.portfolio.model;
public final class Course {
    private final String code;
    private final String title;
    private final int capacity;
    private final int credits;
    private final Instructor instructor;
    public Course(String code, String title, int capacity, int credits, Instructor instructor) {
        if (code == null || code.isBlank() || title == null || title.isBlank())
            throw new IllegalArgumentException("Course code/title required");
        if (capacity <= 0 || credits <= 0) throw new IllegalArgumentException("Capacity and credits must be positive");
        if (instructor == null) throw new IllegalArgumentException("Instructor required");
        this.code = code.trim().toUpperCase(java.util.Locale.ROOT);
        this.title = title.trim(); this.capacity = capacity; this.credits = credits; this.instructor = instructor;
    }
    public String getCode() { return code; }
    public String getTitle() { return title; }
    public int getCapacity() { return capacity; }
    public int getCredits() { return credits; }
    public Instructor getInstructor() { return instructor; }
    @Override public String toString() { return code + " - " + title + " (capacity " + capacity + ")"; }
}
