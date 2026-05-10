package com.devopsmonk.java17.ch06_switch_expressions;

import java.time.DayOfWeek;
import java.util.List;

/**
 * Java 17 — Switch Expressions (JEP 361)
 * Matches blog article: 06-switch-expressions.md
 */
public class SwitchExpressionExamples {

    // ── 1. Old switch statement vs. new switch expression ─────────────────────
    static void oldVsNewSwitch(DayOfWeek day) {
        // Old — fall-through, no return value
        String type;
        switch (day) {
            case MONDAY: case TUESDAY: case WEDNESDAY:
            case THURSDAY: case FRIDAY:
                type = "Weekday";
                break;
            case SATURDAY: case SUNDAY:
                type = "Weekend";
                break;
            default:
                throw new IllegalArgumentException("Unknown day: " + day);
        }
        System.out.println("Old: " + day + " is a " + type);

        // New — arrow syntax, no fall-through, returns a value
        String typeNew = switch (day) {
            case MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY -> "Weekday";
            case SATURDAY, SUNDAY -> "Weekend";
        };
        System.out.println("New: " + day + " is a " + typeNew);
    }

    // ── 2. Switch expression returning a value ────────────────────────────────
    static int daysInMonth(int month, int year) {
        return switch (month) {
            case 1, 3, 5, 7, 8, 10, 12 -> 31;
            case 4, 6, 9, 11            -> 30;
            case 2 -> (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) ? 29 : 28;
            default -> throw new IllegalArgumentException("Invalid month: " + month);
        };
    }

    // ── 3. yield — multi-statement switch arm ─────────────────────────────────
    static String classify(int httpStatus) {
        return switch (httpStatus) {
            case 200, 201, 202, 204 -> "Success";
            case 301, 302, 304      -> "Redirect";
            case 400                -> "Bad Request";
            case 401                -> "Unauthorized";
            case 403                -> "Forbidden";
            case 404                -> "Not Found";
            case 500, 502, 503      -> "Server Error";
            default -> {
                String category = httpStatus >= 100 && httpStatus < 200 ? "Informational"
                                : httpStatus >= 400 && httpStatus < 500 ? "Client Error"
                                : "Unknown";
                yield category + " (" + httpStatus + ")";  // yield in block arm
            }
        };
    }

    // ── 4. Switch expression on String ────────────────────────────────────────
    static double convert(double value, String fromUnit, String toUnit) {
        // Normalise to metres first
        double inMetres = switch (fromUnit.toLowerCase()) {
            case "km"    -> value * 1000;
            case "m"     -> value;
            case "cm"    -> value / 100;
            case "mm"    -> value / 1000;
            case "miles" -> value * 1609.344;
            case "feet"  -> value * 0.3048;
            case "inch"  -> value * 0.0254;
            default -> throw new IllegalArgumentException("Unknown unit: " + fromUnit);
        };

        return switch (toUnit.toLowerCase()) {
            case "km"    -> inMetres / 1000;
            case "m"     -> inMetres;
            case "cm"    -> inMetres * 100;
            case "mm"    -> inMetres * 1000;
            case "miles" -> inMetres / 1609.344;
            case "feet"  -> inMetres / 0.3048;
            case "inch"  -> inMetres / 0.0254;
            default -> throw new IllegalArgumentException("Unknown unit: " + toUnit);
        };
    }

    // ── 5. Switch expression on enum — exhaustive checking ────────────────────
    enum Priority { LOW, MEDIUM, HIGH, CRITICAL }

    static int priorityScore(Priority p) {
        // Compiler enforces all enum values are covered — no default needed
        return switch (p) {
            case LOW      -> 1;
            case MEDIUM   -> 5;
            case HIGH     -> 10;
            case CRITICAL -> 100;
        };
    }

    // ── 6. Switch expression inside a stream ──────────────────────────────────
    record Task(String name, Priority priority) {}

    static void processTasks() {
        var tasks = List.of(
            new Task("Fix login bug",        Priority.CRITICAL),
            new Task("Update docs",          Priority.LOW),
            new Task("Refactor auth module", Priority.MEDIUM),
            new Task("Deploy to staging",    Priority.HIGH),
            new Task("Write tests",          Priority.MEDIUM)
        );

        System.out.println("=== Tasks by Priority ===");
        tasks.stream()
             .sorted((a, b) -> Integer.compare(
                 priorityScore(b.priority()), priorityScore(a.priority())))
             .forEach(t -> System.out.printf("  [%8s | score=%3d] %s%n",
                 t.priority(), priorityScore(t.priority()), t.name()));
    }

    public static void main(String[] args) {
        System.out.println("=== Old vs New Switch ===");
        oldVsNewSwitch(DayOfWeek.WEDNESDAY);
        oldVsNewSwitch(DayOfWeek.SATURDAY);

        System.out.println("\n=== Days in Month ===");
        System.out.println("Feb 2024 (leap): " + daysInMonth(2, 2024));
        System.out.println("Feb 2023:        " + daysInMonth(2, 2023));
        System.out.println("March 2024:      " + daysInMonth(3, 2024));

        System.out.println("\n=== HTTP Status Classification ===");
        List.of(200, 404, 500, 301, 418).forEach(
            code -> System.out.println("  " + code + " → " + classify(code)));

        System.out.println("\n=== Unit Conversion ===");
        System.out.printf("  5 km = %.2f miles%n", convert(5, "km", "miles"));
        System.out.printf("  100 feet = %.2f m%n", convert(100, "feet", "m"));
        System.out.printf("  1 mile = %.0f mm%n",  convert(1, "miles", "mm"));

        System.out.println("\n=== Priority Scores ===");
        for (Priority p : Priority.values()) {
            System.out.printf("  %-8s = %d%n", p, priorityScore(p));
        }

        System.out.println();
        processTasks();
    }
}
