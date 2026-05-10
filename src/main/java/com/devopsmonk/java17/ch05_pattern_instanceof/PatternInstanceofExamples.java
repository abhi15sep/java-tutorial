package com.devopsmonk.java17.ch05_pattern_instanceof;

import java.util.List;

/**
 * Java 17 — Pattern Matching for instanceof (JEP 394)
 * Matches blog article: 05-pattern-matching-instanceof.md
 */
public class PatternInstanceofExamples {

    // ── 1. Old style vs. new style ────────────────────────────────────────────
    static void oldVsNew(Object obj) {
        // Old Java — redundant cast
        if (obj instanceof String) {
            String s = (String) obj;  // cast is redundant
            System.out.println("Old: length = " + s.length());
        }

        // Java 17 — pattern variable, no cast
        if (obj instanceof String s) {
            System.out.println("New: length = " + s.length());
        }
    }

    // ── 2. Pattern variable scope ────────────────────────────────────────────
    static void patternScope(Object obj) {
        // 's' is in scope for the entire if block
        if (obj instanceof String s && s.length() > 5) {
            System.out.println("Long string: " + s.toUpperCase());
        }

        // 's' is NOT in scope in the else branch — this would be a compile error
        // if (obj instanceof String s || s.isEmpty()) { ... }  ← would not compile
    }

    // ── 3. Real-world: rendering a shape hierarchy ────────────────────────────
    interface Shape {}
    record Circle(double radius) implements Shape {}
    record Rectangle(double width, double height) implements Shape {}
    record Triangle(double base, double height) implements Shape {}

    static double area(Shape shape) {
        if (shape instanceof Circle c) {
            return Math.PI * c.radius() * c.radius();
        } else if (shape instanceof Rectangle r) {
            return r.width() * r.height();
        } else if (shape instanceof Triangle t) {
            return 0.5 * t.base() * t.height();
        }
        throw new IllegalArgumentException("Unknown shape: " + shape);
    }

    // ── 4. Combining instanceof with logical operators ────────────────────────
    static void combinedPatterns(Object obj) {
        // AND — pattern variable AND additional condition
        if (obj instanceof String s && !s.isBlank()) {
            System.out.println("Non-blank string: [" + s.trim() + "]");
        }

        // Negation — test before binding
        if (!(obj instanceof Integer i)) {
            System.out.println("Not an integer: " + obj.getClass().getSimpleName());
            return;
        }
        // i is in scope here (after the guard)
        System.out.println("Integer value: " + i);
    }

    // ── 5. Pattern matching in a polymorphic rendering system ────────────────
    sealed interface JsonValue permits JsonString, JsonNumber, JsonBoolean, JsonArray, JsonNull {}
    record JsonString(String value) implements JsonValue {}
    record JsonNumber(double value) implements JsonValue {}
    record JsonBoolean(boolean value) implements JsonValue {}
    record JsonArray(List<JsonValue> elements) implements JsonValue {}
    record JsonNull() implements JsonValue {}

    static String renderJson(JsonValue value) {
        if (value instanceof JsonString s)  return "\"" + s.value() + "\"";
        if (value instanceof JsonNumber n)  return n.value() % 1 == 0
                                                ? String.valueOf((long) n.value())
                                                : String.valueOf(n.value());
        if (value instanceof JsonBoolean b) return String.valueOf(b.value());
        if (value instanceof JsonNull)      return "null";
        if (value instanceof JsonArray a) {
            var sb = new StringBuilder("[");
            for (int i = 0; i < a.elements().size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(renderJson(a.elements().get(i)));
            }
            return sb.append("]").toString();
        }
        throw new AssertionError("Unknown JsonValue: " + value);
    }

    // ── 6. equals() simplified with instanceof pattern ───────────────────────
    record Coordinate(double lat, double lon) {
        @Override
        public boolean equals(Object obj) {
            // Clean equals — no double cast needed
            return obj instanceof Coordinate other
                && Double.compare(lat, other.lat) == 0
                && Double.compare(lon, other.lon) == 0;
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Old vs New instanceof ===");
        oldVsNew("Hello, World!");
        oldVsNew(42);

        System.out.println("\n=== Pattern Scope ===");
        patternScope("JavaDev");
        patternScope("Hi");

        System.out.println("\n=== Shape Areas ===");
        List<Shape> shapes = List.of(
            new Circle(5.0),
            new Rectangle(4.0, 6.0),
            new Triangle(3.0, 8.0)
        );
        shapes.forEach(s -> System.out.printf("  %s → area = %.2f%n", s, area(s)));

        System.out.println("\n=== Combined Patterns ===");
        combinedPatterns("  Hello  ");
        combinedPatterns("   ");
        combinedPatterns(99);
        combinedPatterns(List.of());

        System.out.println("\n=== JSON Renderer ===");
        var json = new JsonArray(List.of(
            new JsonString("Alice"),
            new JsonNumber(30),
            new JsonBoolean(true),
            new JsonNull()
        ));
        System.out.println(renderJson(json));

        System.out.println("\n=== Equals with instanceof ===");
        var c1 = new Coordinate(51.5074, -0.1278);
        var c2 = new Coordinate(51.5074, -0.1278);
        var c3 = new Coordinate(48.8566,  2.3522);
        System.out.println("c1.equals(c2): " + c1.equals(c2));
        System.out.println("c1.equals(c3): " + c1.equals(c3));
    }
}
