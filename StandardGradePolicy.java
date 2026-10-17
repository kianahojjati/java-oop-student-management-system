package edu.portfolio.grading;
/** Illustrative, not an official University of Guelph grade-conversion policy. */
public final class StandardGradePolicy implements GradePolicy {
    @Override public double gradePoints(double grade) {
        if (!Double.isFinite(grade) || grade < 0 || grade > 100) throw new IllegalArgumentException("Invalid grade");
        if (grade >= 90) return 4.0;
        if (grade >= 80) return 3.7;
        if (grade >= 70) return 3.0;
        if (grade >= 60) return 2.0;
        if (grade >= 50) return 1.0;
        return 0.0;
    }
}
