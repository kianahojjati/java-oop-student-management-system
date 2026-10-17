package edu.portfolio.persistence;
import edu.portfolio.model.*;
import edu.portfolio.service.AcademicService;
import edu.portfolio.grading.GradePolicy;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
/** Minimal CSV snapshot format with quoted fields. No third-party libraries. */
public final class CsvStorage {
    private CsvStorage() {}
    private static String esc(String value) { return "\"" + value.replace("\"", "\"\"") + "\""; }
    private static String line(String... cells) { return String.join(",", Arrays.stream(cells).map(CsvStorage::esc).toList()); }
    private static List<String> fields(String line) {
        List<String> out = new ArrayList<>(); StringBuilder cell = new StringBuilder(); boolean quoted = false;
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (ch == '"') { if (quoted && i + 1 < line.length() && line.charAt(i + 1) == '"') { cell.append('"'); i++; } else quoted = !quoted; }
            else if (ch == ',' && !quoted) { out.add(cell.toString()); cell.setLength(0); }
            else cell.append(ch);
        }
        if (quoted) throw new IllegalArgumentException("Unclosed CSV quote");
        out.add(cell.toString()); return out;
    }
    private static List<String> read(Path path) throws IOException {
        return Files.exists(path) ? Files.readAllLines(path, StandardCharsets.UTF_8) : List.of();
    }
    public static void save(AcademicService service, Path dir) throws IOException {
        Files.createDirectories(dir);
        List<String> students = new ArrayList<>();
        for (Student s : service.getStudents()) students.add(line(s.getId(), s.getName()));
        Files.write(dir.resolve("students.csv"), students, StandardCharsets.UTF_8);
        List<String> instructors = new ArrayList<>();
        for (Instructor i : service.getInstructors()) instructors.add(line(i.getId(), i.getName()));
        Files.write(dir.resolve("instructors.csv"), instructors, StandardCharsets.UTF_8);
        List<String> courses = new ArrayList<>();
        for (Course c : service.getCourses()) courses.add(line(c.getCode(), c.getTitle(), Integer.toString(c.getCapacity()), Integer.toString(c.getCredits()), c.getInstructor().getId()));
        Files.write(dir.resolve("courses.csv"), courses, StandardCharsets.UTF_8);
        List<String> enrollments = new ArrayList<>();
        for (Enrollment e : service.getEnrollments()) enrollments.add(line(e.getStudent().getId(), e.getCourse().getCode(), e.getGrade() == null ? "" : e.getGrade().toString()));
        Files.write(dir.resolve("enrollments.csv"), enrollments, StandardCharsets.UTF_8);
    }
    /** Loads a complete snapshot. Keep backups if the files are edited by hand. */
    public static AcademicService load(Path dir, GradePolicy policy) throws IOException {
        AcademicService service = new AcademicService(policy);
        try {
            for (String row : read(dir.resolve("students.csv"))) { var c = fields(row); service.addStudent(c.get(0), c.get(1)); }
            for (String row : read(dir.resolve("instructors.csv"))) { var c = fields(row); service.addInstructor(c.get(0), c.get(1)); }
            for (String row : read(dir.resolve("courses.csv"))) { var c = fields(row); service.addCourse(c.get(0), c.get(1), Integer.parseInt(c.get(2)), Integer.parseInt(c.get(3)), c.get(4)); }
            for (String row : read(dir.resolve("enrollments.csv"))) { var c = fields(row); service.enroll(c.get(0), c.get(1)); if (!c.get(2).isBlank()) service.recordGrade(c.get(0), c.get(1), Double.parseDouble(c.get(2))); }
        } catch (RuntimeException e) { throw new IOException("Invalid data in " + dir + ": " + e.getMessage(), e); }
        return service;
    }
}
