package com.devopsmonk.java17.ch08_pattern_switch_preview;

import java.util.List;

/**
 * Java 17 — Pattern Matching for switch (JEP 406, Preview)
 * Matches blog article: 08-pattern-matching-switch-preview.md
 *
 * NOTE: JEP 406 (type patterns in switch) is a PREVIEW feature exclusive to
 * Java 17. It does NOT compile on Java 16 even with --enable-preview.
 *
 * This file demonstrates the CONCEPT and the EVOLUTION from Java 16 → Java 17,
 * using Java 16-compatible code alongside comments showing the Java 17 syntax.
 *
 * To run the actual Java 17 preview examples, compile with:
 *   javac --enable-preview --release 17 PatternSwitchPreviewExamples.java
 *   java  --enable-preview PatternSwitchPreviewExamples
 */
public class PatternSwitchPreviewExamples {

    // ── 1. The problem: instanceof chains before Java 17 ─────────────────────
    static String describeJava16(Object obj) {
        // Java 16 — pattern instanceof, but no type patterns in switch
        if (obj == null)               return "null";
        if (obj instanceof Integer i)  return "Integer: " + i;
        if (obj instanceof Long l)     return "Long: " + l;
        if (obj instanceof Double d)   return "Double: " + d;
        if (obj instanceof String s)   return "String of length " + s.length() + ": \"" + s + "\"";
        return "Other: " + obj.getClass().getSimpleName();
    }

    /*
     * Java 17 PREVIEW equivalent of the above (cannot compile on Java 16):
     *
     * static String describeJava17(Object obj) {
     *     return switch (obj) {
     *         case Integer i  -> "Integer: " + i;
     *         case Long l     -> "Long: " + l;
     *         case Double d   -> "Double: " + d;
     *         case String s   -> "String of length " + s.length() + ": \"" + s + "\"";
     *         case null       -> "null";
     *         default         -> "Other: " + obj.getClass().getSimpleName();
     *     };
     * }
     */

    // ── 2. Guarded patterns — Java 16 if/else equivalent ─────────────────────
    static String categoriseJava16(Object obj) {
        if (obj == null)                              return "null";
        if (obj instanceof Integer i && i < 0)        return "Negative integer: " + i;
        if (obj instanceof Integer i && i == 0)       return "Zero";
        if (obj instanceof Integer i)                 return "Positive integer: " + i;
        if (obj instanceof String s && s.isBlank())   return "Blank string";
        if (obj instanceof String s)                  return "String: \"" + s + "\"";
        return "Unknown: " + obj;
    }

    /*
     * Java 17 PREVIEW equivalent with guarded patterns (when clause):
     *
     * static String categoriseJava17(Object obj) {
     *     return switch (obj) {
     *         case Integer i when i < 0    -> "Negative integer: " + i;
     *         case Integer i when i == 0   -> "Zero";
     *         case Integer i               -> "Positive integer: " + i;
     *         case String s when s.isBlank() -> "Blank string";
     *         case String s                -> "String: \"" + s + "\"";
     *         case null                    -> "null";
     *         default                      -> "Unknown: " + obj;
     *     };
     * }
     */

    // ── 3. Sealed types + pattern dispatch — Java 16 style ───────────────────
    sealed interface Notification permits EmailNotification, SmsNotification, PushNotification {}
    record EmailNotification(String to, String subject, String body) implements Notification {}
    record SmsNotification(String phone, String message) implements Notification {}
    record PushNotification(String deviceToken, String title, String payload) implements Notification {}

    // Java 16: instanceof chain
    static String sendJava16(Notification n) {
        if (n instanceof EmailNotification e)
            return "EMAIL → " + e.to() + " | Subject: " + e.subject();
        if (n instanceof SmsNotification s)
            return "SMS → " + s.phone() + " | " + s.message();
        if (n instanceof PushNotification p && p.payload().length() > 100)
            return "PUSH → " + p.deviceToken() + " | " + p.title() + " [payload truncated]";
        if (n instanceof PushNotification p)
            return "PUSH → " + p.deviceToken() + " | " + p.title();
        throw new AssertionError("Unknown notification type");
    }

    /*
     * Java 17 PREVIEW — exhaustive switch, no throw needed:
     *
     * static String sendJava17(Notification n) {
     *     return switch (n) {
     *         case EmailNotification e ->
     *             "EMAIL → " + e.to() + " | Subject: " + e.subject();
     *         case SmsNotification s ->
     *             "SMS → " + s.phone() + " | " + s.message();
     *         case PushNotification p when p.payload().length() > 100 ->
     *             "PUSH → " + p.deviceToken() + " | " + p.title() + " [payload truncated]";
     *         case PushNotification p ->
     *             "PUSH → " + p.deviceToken() + " | " + p.title();
     *     };  // no default needed — sealed type, compiler knows all cases
     * }
     */

    // ── 4. Null handling — Java 16 requires explicit null check ──────────────
    static void nullHandlingJava16(String value) {
        if (value == null) { System.out.println("  Got null");  return; }
        switch (value) {
            case "admin" -> System.out.println("  Admin user");
            case "guest" -> System.out.println("  Guest user");
            default      -> System.out.println("  Regular user: " + value);
        }
    }

    /*
     * Java 17 PREVIEW — null is a valid case label, no pre-check:
     *
     * static void nullHandlingJava17(String value) {
     *     switch (value) {
     *         case null    -> System.out.println("Got null");
     *         case "admin" -> System.out.println("Admin user");
     *         case "guest" -> System.out.println("Guest user");
     *         default      -> System.out.println("Regular user: " + value);
     *     }
     * }
     */

    // ── 5. The evolution timeline ─────────────────────────────────────────────
    static void evolutionTimeline() {
        System.out.println("\n=== Pattern Matching Evolution ===");
        List.of(
            "Java 14 (JEP 305):  instanceof pattern — 1st Preview",
            "Java 15 (JEP 375):  instanceof pattern — 2nd Preview",
            "Java 16 (JEP 394):  instanceof pattern — FINAL",
            "Java 17 (JEP 406):  type patterns in switch — 1st Preview",
            "Java 18 (JEP 420):  type patterns in switch — 2nd Preview",
            "Java 19 (JEP 427):  type patterns in switch — 3rd Preview",
            "Java 20 (JEP 433):  type patterns in switch — 4th Preview",
            "Java 21 (JEP 441):  pattern matching for switch — FINAL"
        ).forEach(line -> System.out.println("  " + line));
    }

    public static void main(String[] args) {
        System.out.println("=== Java 16: instanceof Chain (compiles here) ===");
        List<Object> values = List.of(42, 3L, 3.14, "hello", new int[]{1, 2, 3});
        values.forEach(v -> System.out.println("  " + describeJava16(v)));
        System.out.println("  " + describeJava16(null));

        System.out.println("\n=== Guarded Patterns (Java 16) ===");
        List.of(-5, 0, 42, "  ", "Java 17", (Object) null)
            .forEach(v -> System.out.println("  " + categoriseJava16(v)));

        System.out.println("\n=== Null Handling (Java 16) ===");
        List.of("admin", "alice", "guest", (String) null)
            .forEach(PatternSwitchPreviewExamples::nullHandlingJava16);

        System.out.println("\n=== Notifications (Java 16 instanceof chain) ===");
        List<Notification> notifications = List.of(
            new EmailNotification("alice@example.com", "Your order", "Order shipped"),
            new SmsNotification("+447700900123", "Your code: 482910"),
            new PushNotification("tok_abc123", "Flash Sale!", "50% off all items today!")
        );
        notifications.forEach(n -> System.out.println("  " + sendJava16(n)));

        evolutionTimeline();

        System.out.println("\nNote: Java 17 preview switch syntax is shown in comments.");
        System.out.println("      The final API landed in Java 21 (JEP 441).");
    }
}
