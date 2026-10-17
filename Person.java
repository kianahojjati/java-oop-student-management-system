package edu.portfolio.model;
/** Common identity and state shared by people in the system. */
public abstract class Person {
    private final String id;
    private final String name;
    protected Person(String id, String name) {
        if (id == null || id.isBlank() || name == null || name.isBlank())
            throw new IllegalArgumentException("ID and name cannot be blank");
        this.id = id.trim(); this.name = name.trim();
    }
    public String getId() { return id; }
    public String getName() { return name; }
    public abstract String getRole();
    @Override public String toString() { return getRole() + " " + id + " - " + name; }
}
