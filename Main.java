package edu.portfolio;
import edu.portfolio.grading.StandardGradePolicy;
import edu.portfolio.persistence.CsvStorage;
import edu.portfolio.service.AcademicService;
import java.nio.file.Path;
import java.util.Scanner;
/** Command-line demo; run with mvn exec:java. */
public final class Main {
    private static final Path DATA = Path.of("data");
    private Main() {}
    public static void main(String[] args) throws Exception {
        var policy = new StandardGradePolicy();
        AcademicService service = CsvStorage.load(DATA, policy);
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println("\n1 Add student | 2 Add instructor | 3 Add course | 4 Enroll | 5 Grade | 6 GPA | 7 List | 8 Save | 0 Quit");
                System.out.print("> ");
                if (!scanner.hasNextLine()) break;
                String selection = scanner.nextLine().trim();
                try {
                    switch (selection) {
                        case "1" -> service.addStudent(ask(scanner, "Student ID"), ask(scanner, "Name"));
                        case "2" -> service.addInstructor(ask(scanner, "Instructor ID"), ask(scanner, "Name"));
                        case "3" -> service.addCourse(ask(scanner, "Course code"), ask(scanner, "Title"), Integer.parseInt(ask(scanner, "Capacity")), Integer.parseInt(ask(scanner, "Credits")), ask(scanner, "Instructor ID"));
                        case "4" -> service.enroll(ask(scanner, "Student ID"), ask(scanner, "Course code"));
                        case "5" -> service.recordGrade(ask(scanner, "Student ID"), ask(scanner, "Course code"), Double.parseDouble(ask(scanner, "Grade (0-100)")));
                        case "6" -> {
                            var gpa = service.calculateGpa(ask(scanner, "Student ID"));
                            System.out.println(gpa.isPresent() ? "Illustrative GPA: " + String.format("%.2f", gpa.getAsDouble()) : "No graded courses yet");
                        }
                        case "7" -> {
                            System.out.println("Students:"); service.getStudents().forEach(System.out::println);
                            System.out.println("Instructors:"); service.getInstructors().forEach(System.out::println);
                            System.out.println("Courses:"); service.getCourses().forEach(System.out::println);
                            System.out.println("Enrollments:"); service.getEnrollments().forEach(e -> System.out.println(e.getStudent().getId() + " -> " + e.getCourse().getCode() + " grade=" + e.getGrade()));
                        }
                        case "8" -> { CsvStorage.save(service, DATA); System.out.println("Saved to data/"); }
                        case "0" -> { CsvStorage.save(service, DATA); return; }
                        default -> System.out.println("Unknown option");
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
        }
        CsvStorage.save(service, DATA);
    }
    private static String ask(Scanner scanner, String prompt) { System.out.print(prompt + ": "); return scanner.nextLine().trim(); }
}
