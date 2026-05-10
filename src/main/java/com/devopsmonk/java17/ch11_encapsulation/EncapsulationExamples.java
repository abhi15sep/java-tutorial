package com.devopsmonk.java17.ch11_encapsulation;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Java 17 — Strong JDK Encapsulation & Removed APIs (JEP 403, 306, 407, 411)
 * Matches blog article: 11-jdk-encapsulation-removed-apis.md
 *
 * This file demonstrates:
 *   1. What Strong Encapsulation (JEP 403) means in practice
 *   2. How to detect and fix InaccessibleObjectException
 *   3. Strict floating-point semantics (JEP 306)
 *   4. Security Manager deprecation (JEP 411) migration
 */
public class EncapsulationExamples {

    // ── 1. Strong Encapsulation — what breaks and how to detect it ────────────
    static void strongEncapsulationDemo() {
        System.out.println("=== Strong JDK Encapsulation (JEP 403) ===");

        // Attempt to access an internal JDK class via reflection
        // In Java 17 this throws InaccessibleObjectException unless --add-opens is used
        try {
            Class<?> stringClass = String.class;
            Field valueField = stringClass.getDeclaredField("value");
            valueField.setAccessible(true);  // This WILL throw in Java 17 without --add-opens
            System.out.println("Accessed String.value field (unexpected in Java 17)");
        } catch (NoSuchFieldException e) {
            System.out.println("Field 'value' not found (renamed/removed): " + e.getMessage());
        } catch (Exception e) {
            // InaccessibleObjectException in Java 17
            System.out.println("Blocked by strong encapsulation: " + e.getClass().getSimpleName());
            System.out.println("  → Fix: use public API instead, or add --add-opens java.base/java.lang=ALL-UNNAMED");
        }

        // Public API — the correct approach
        String s = "Hello, Java 17!";
        System.out.println("String length via public API: " + s.length());
        System.out.println("Char at 0 via public API:     " + s.charAt(0));
    }

    // ── 2. Checking if a module is open ──────────────────────────────────────
    static void checkModuleAccess() {
        System.out.println("\n=== Module Accessibility Checks ===");

        Module javaBase = String.class.getModule();
        Module unnamed  = EncapsulationExamples.class.getModule();

        System.out.println("java.base module name:    " + javaBase.getName());
        System.out.println("Our module (unnamed):     " + (unnamed.isNamed() ? unnamed.getName() : "UNNAMED"));
        System.out.println("java.lang open to us:     " + javaBase.isOpen("java.lang", unnamed));
        System.out.println("java.lang exported to us: " + javaBase.isExported("java.lang", unnamed));
    }

    // ── 3. Strict Floating-Point Semantics (JEP 306) ─────────────────────────
    // strictfp is now redundant — all floating-point is IEEE 754 strict by default in Java 17
    static void strictFloatingPoint() {
        System.out.println("\n=== Strict Floating-Point (JEP 306) ===");

        // Before Java 17: strictfp keyword was needed to guarantee IEEE 754 strict mode
        // In Java 17: all FP operations are always IEEE 754 strict — strictfp is a no-op

        double a = 0.1;
        double b = 0.2;
        double sum = a + b;

        System.out.printf("0.1 + 0.2 = %.17f%n", sum);
        System.out.println("0.1 + 0.2 == 0.3: " + (sum == 0.3));

        // Use BigDecimal for precise financial arithmetic
        java.math.BigDecimal bdA = new java.math.BigDecimal("0.1");
        java.math.BigDecimal bdB = new java.math.BigDecimal("0.2");
        System.out.println("BigDecimal 0.1 + 0.2 = " + bdA.add(bdB));

        // Math operations guaranteed IEEE 754 in Java 17 without strictfp
        System.out.println("Math.sqrt(2.0)  = " + Math.sqrt(2.0));
        System.out.println("Math.PI         = " + Math.PI);
        System.out.println("Double.MAX_VALUE = " + Double.MAX_VALUE);
    }

    // ── 4. Security Manager deprecation (JEP 411) — migration patterns ───────
    static void securityManagerMigration() {
        System.out.println("\n=== Security Manager Deprecation (JEP 411) ===");

        // SecurityManager is deprecated and will be removed in a future release
        // Check if one is installed (should not be in Java 17+ apps)
        @SuppressWarnings("removal")
        SecurityManager sm = System.getSecurityManager();
        if (sm == null) {
            System.out.println("No SecurityManager installed (correct for Java 17+)");
        } else {
            System.out.println("SecurityManager active: " + sm.getClass().getName());
            System.out.println("  → Migrate to OS-level sandboxing, containers, or module system");
        }

        // Modern replacement patterns:
        System.out.println("\nModern security alternatives:");
        System.out.println("  1. Java Module System — control what code can access");
        System.out.println("  2. OS sandboxing (seccomp, AppArmor, SELinux)");
        System.out.println("  3. Container isolation (Docker, Podman)");
        System.out.println("  4. Application-level checks with proper authentication/authorisation");
    }

    // ── 5. Common --add-opens migration guide ────────────────────────────────
    static void addOpensGuide() {
        System.out.println("\n=== Common --add-opens Workarounds ===");
        List.of(
            "Hibernate/Spring accessing private fields via reflection:",
            "  --add-opens java.base/java.lang=ALL-UNNAMED",
            "  --add-opens java.base/java.util=ALL-UNNAMED",
            "",
            "Mockito/PowerMock internal mocking:",
            "  --add-opens java.base/java.lang.reflect=ALL-UNNAMED",
            "",
            "JavaFX / Swing internals:",
            "  --add-opens java.desktop/com.sun.java.swing=ALL-UNNAMED",
            "",
            "JAXB / XML binding:",
            "  --add-opens java.xml.bind/com.sun.xml.bind.v2=ALL-UNNAMED",
            "",
            "Long-term fix: migrate to APIs that don't require internal access."
        ).forEach(System.out::println);
    }

    public static void main(String[] args) {
        strongEncapsulationDemo();
        checkModuleAccess();
        strictFloatingPoint();
        securityManagerMigration();
        addOpensGuide();
    }
}
