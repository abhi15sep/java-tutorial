package com.devopsmonk.java17.ch13_migration;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Java 17 — Migration Guide (from Java 8 and Java 11)
 * Matches blog article: 13-migration-guide.md
 *
 * Shows code patterns that changed and how to fix them.
 */
public class MigrationGuideExamples {

    // ── 1. Deprecated / removed APIs ─────────────────────────────────────────
    static void removedApisAndReplacements() {
        System.out.println("=== Removed APIs and Replacements ===");

        // Thread.stop(), suspend(), resume() — removed
        // Old: thread.stop();
        // New: use a volatile flag or interruption
        boolean[] running = {true};  // array cell acts as a mutable flag
        Thread worker = new Thread(() -> {
            while (running[0]) {
                try { Thread.sleep(10); } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            System.out.println("  Worker stopped cleanly");
        });
        worker.start();
        running[0] = false;

        // Runtime.exec() with String — still works, but ProcessBuilder is better
        System.out.println("  ProcessBuilder preferred over Runtime.exec():");
        ProcessBuilder pb = new ProcessBuilder("java", "-version");
        System.out.println("  Command: " + pb.command());

        // Observer/Observable — deprecated since Java 9
        System.out.println("  Observer/Observable: use java.beans.PropertyChangeSupport instead");
    }

    // ── 2. String API modernisation (Java 11 → 17 recap) ─────────────────────
    static void stringApiChanges() {
        System.out.println("\n=== String API Modernisation ===");

        // Java 11+
        String s = "  hello world  ";
        System.out.println("strip():       [" + s.strip() + "]");          // Unicode-aware
        System.out.println("stripLeading():[" + s.stripLeading() + "]");
        System.out.println("stripTrailing():[" + s.stripTrailing() + "]");
        System.out.println("isBlank():      " + "   ".isBlank());
        System.out.println("lines():        " + "a\nb\nc".lines().collect(Collectors.toList()));
        System.out.println("repeat(3):      " + "ab".repeat(3));

        // Java 15+ (in Java 17)
        String block = """
                SELECT *
                FROM users
                WHERE active = true
                """;
        System.out.println("Text block (Java 15+):\n" + block);

        // Java 12+
        String result = "hello".indent(4);
        System.out.println("indent(4):\n" + result);
    }

    // ── 3. Collection factory methods modernisation ────────────────────────────
    static void collectionFactories() {
        System.out.println("=== Collection Factory Methods ===");

        // Java 9+: List.of, Set.of, Map.of — immutable
        List<String> names = List.of("Alice", "Bob", "Charlie");
        Map<String, Integer> scores = Map.of("Alice", 95, "Bob", 87, "Charlie", 92);

        System.out.println("List.of: " + names);
        System.out.println("Map.of:  " + scores);

        // copyOf — defensive copies
        List<String> copy = List.copyOf(names);
        System.out.println("List.copyOf: " + copy);

        // Java 16+: Stream.toList() — no Collectors.toList() needed
        List<String> upper = names.stream()
            .map(String::toUpperCase)
            .toList();  // Java 16+
        System.out.println("Stream.toList(): " + upper);
    }

    // ── 4. instanceof pattern migration ───────────────────────────────────────
    static void instanceofMigration() {
        System.out.println("\n=== instanceof Pattern Migration ===");

        Object obj = "Hello, Java 17!";

        // Java 8/11 style
        if (obj instanceof String) {
            String s = (String) obj;
            System.out.println("Old: " + s.toUpperCase());
        }

        // Java 17 style
        if (obj instanceof String s) {
            System.out.println("New: " + s.toUpperCase());
        }
    }

    // ── 5. Records instead of data classes ───────────────────────────────────
    static void recordsMigration() {
        System.out.println("\n=== Records Replace Data Classes ===");

        // Old: POJO with 50 lines of boilerplate
        // New: record in 1 line
        record Point(double x, double y) {
            double distance(Point other) {
                double dx = x - other.x, dy = y - other.y;
                return Math.sqrt(dx * dx + dy * dy);
            }
        }

        var p1 = new Point(0, 0);
        var p2 = new Point(3, 4);
        System.out.println("p1: " + p1);
        System.out.println("p2: " + p2);
        System.out.printf("distance: %.1f%n", p1.distance(p2));
        System.out.println("equal:    " + p1.equals(new Point(0, 0)));
    }

    // ── 6. Switch expression migration ────────────────────────────────────────
    static void switchMigration() {
        System.out.println("\n=== Switch Expression Migration ===");

        // Java 8 style — verbose, fall-through risk
        String day = "WEDNESDAY";
        String type;
        switch (day) {
            case "MONDAY": case "TUESDAY": case "WEDNESDAY":
            case "THURSDAY": case "FRIDAY":
                type = "Weekday"; break;
            default: type = "Weekend";
        }
        System.out.println("Old switch: " + type);

        // Java 17 style — expression, no fall-through
        type = switch (day) {
            case "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY" -> "Weekday";
            default -> "Weekend";
        };
        System.out.println("New switch: " + type);
    }

    // ── 7. Common dependency upgrade notes ────────────────────────────────────
    static void dependencyNotes() {
        System.out.println("\n=== Dependency Compatibility Notes ===");
        List.of(
            "Spring Boot → use 3.x (requires Java 17+)",
            "Hibernate   → use 6.x for Java 17 / Jakarta EE 10",
            "Lombok      → 1.18.24+ required for Java 17",
            "MapStruct   → 1.5.x+ required",
            "Mockito     → 4.x+ for Java 17 (adds --add-opens automatically)",
            "Log4j       → 2.17.1+ (critical security patches)",
            "Jackson     → 2.13+ for Java 17 support",
            "Byte Buddy  → 1.12+ (used by Mockito, Spring, Hibernate)",
            "CGLIB       → migrate to Byte Buddy or Spring's built-in proxies"
        ).forEach(note -> System.out.println("  • " + note));
    }

    // ── 8. JVM flags that changed ─────────────────────────────────────────────
    static void jvmFlagChanges() {
        System.out.println("\n=== JVM Flag Changes ===");
        List.of(
            "REMOVED: -XX:+UseBiasedLocking (biased locking removed in Java 15)",
            "REMOVED: -XX:+PrintGCDetails   → use -Xlog:gc* instead",
            "REMOVED: -XX:+PrintGCDateStamps → use -Xlog:gc*::time instead",
            "REMOVED: -Xloggc:<file>         → use -Xlog:gc*:file=<file> instead",
            "NEW:     -XX:+UseZGC            → production-ready in Java 15",
            "NEW:     -Xlog:gc*:file=gc.log:tags,time,uptime",
            "CHANGED: -XX:MaxPermSize        → use -XX:MaxMetaspaceSize instead"
        ).forEach(flag -> System.out.println("  " + flag));
    }

    public static void main(String[] args) {
        removedApisAndReplacements();
        stringApiChanges();
        collectionFactories();
        instanceofMigration();
        recordsMigration();
        switchMigration();
        dependencyNotes();
        jvmFlagChanges();
    }
}
