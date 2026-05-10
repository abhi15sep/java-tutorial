package com.devopsmonk.java17.ch12_foreign_function;

/**
 * Java 17 — Foreign Function & Memory API (JEP 412, Incubator)
 * Matches blog article: 12-foreign-function-memory.md
 *
 * NOTE: JEP 412 is an INCUBATOR feature in Java 17. The API lives in the
 * jdk.incubator.foreign module and changed significantly through Java 18–22
 * before finalising as java.lang.foreign in Java 22.
 *
 * This file demonstrates the CONCEPTS and the FINAL API shape (Java 22+)
 * for educational purposes. The API shown below is the finalised version.
 *
 * To run with the Java 17 incubator API, add:
 *   --add-modules jdk.incubator.foreign
 *
 * Key concepts:
 *   - MemorySegment: a region of memory (on-heap or off-heap)
 *   - Arena: controls the lifetime of off-heap memory
 *   - Linker: connects Java to native functions
 *   - MemoryLayout: describes the memory shape of C structs
 */
public class ForeignFunctionExamples {

    // ── 1. Why JNI is being replaced ─────────────────────────────────────────
    static void whyJniIsProblematic() {
        System.out.println("=== Why JNI is Being Replaced ===");
        System.out.println("""
            JNI problems:
              1. Requires writing C header files and C wrapper code
              2. C code must be compiled per-platform (Windows/Linux/macOS × x86/ARM)
              3. Memory bugs in the C layer crash the JVM — no recovery
              4. Slow: crossing the JNI boundary has high overhead
              5. Complex build: native compilation steps in the Java build

            Foreign Function & Memory API goals:
              1. Call C functions directly from Java — no C code needed
              2. Manage off-heap memory safely with Arena (auto-cleanup)
              3. Describe C struct layouts with MemoryLayout
              4. Much faster than JNI for tight loops
              5. Pure Java build
            """);
    }

    // ── 2. Off-heap memory management (the safe way) ──────────────────────────
    static void offHeapMemoryDemo() {
        System.out.println("=== Off-Heap Memory with ByteBuffer (Available Now) ===");

        // Pre-FFM approach: java.nio.ByteBuffer for off-heap memory
        java.nio.ByteBuffer buffer = java.nio.ByteBuffer.allocateDirect(1024);
        buffer.order(java.nio.ByteOrder.nativeOrder());

        // Write structured data
        buffer.putInt(0, 42);                   // int at offset 0
        buffer.putDouble(4, Math.PI);            // double at offset 4
        buffer.putLong(12, System.nanoTime());   // long at offset 12

        System.out.printf("int    at offset 0:  %d%n", buffer.getInt(0));
        System.out.printf("double at offset 4:  %.6f%n", buffer.getDouble(4));
        System.out.printf("long   at offset 12: %d%n", buffer.getLong(12));

        System.out.println("\nLimitations of ByteBuffer:");
        System.out.println("  - Max size: Integer.MAX_VALUE (2GB)");
        System.out.println("  - No C struct layout description");
        System.out.println("  - Cannot call native functions directly");
        System.out.println("  → FFM API (final in Java 22) removes all these limits");
    }

    // ── 3. Evolution of the API ───────────────────────────────────────────────
    static void apiEvolution() {
        System.out.println("\n=== FFM API Evolution ===");
        System.out.println("""
            Java 14 (JEP 370):  Foreign-Memory Access API — 1st Incubator
            Java 15 (JEP 383):  Foreign-Memory Access API — 2nd Incubator
            Java 16 (JEP 393):  Foreign-Memory Access API — 3rd Incubator
            Java 16 (JEP 389):  Foreign Linker API        — 1st Incubator
            Java 17 (JEP 412):  Foreign Function & Memory API — 1st Incubator
                                (merged the two APIs, still jdk.incubator.foreign)
            Java 18 (JEP 419):  FFM API — 2nd Incubator
            Java 19 (JEP 424):  FFM API — 1st Preview     (moved to java.lang.foreign)
            Java 20 (JEP 434):  FFM API — 2nd Preview
            Java 21 (JEP 442):  FFM API — 3rd Preview
            Java 22 (JEP 454):  FFM API — FINAL

            Takeaway: In Java 17 it's incubator (unstable). Use Java 22+ for production FFM.
            """);
    }

    // ── 4. Concept: what calling strlen() looks like in the final API ─────────
    static void conceptualNativeCall() {
        System.out.println("=== Conceptual: Calling strlen() via FFM (final API shape) ===");
        System.out.println("""
            // With the final FFM API (Java 22+):

            try (Arena arena = Arena.ofConfined()) {
                // Allocate off-heap memory and write a C string
                MemorySegment str = arena.allocateFrom("Hello, World!");

                // Look up strlen in the C standard library
                Linker linker = Linker.nativeLinker();
                SymbolLookup stdlib = linker.defaultLookup();
                MemorySegment strlenAddr = stdlib.find("strlen").orElseThrow();

                // Describe the function signature: (pointer) -> size_t
                FunctionDescriptor descriptor = FunctionDescriptor.of(
                    ValueLayout.JAVA_LONG,      // return type
                    ValueLayout.ADDRESS          // char* parameter
                );

                // Create a callable MethodHandle
                MethodHandle strlen = linker.downcallHandle(strlenAddr, descriptor);

                // Call it — purely from Java, no JNI, no C wrapper
                long length = (long) strlen.invoke(str);
                System.out.println("strlen = " + length);   // 13
            }
            // arena.close() → off-heap memory freed automatically

            To try this: use Java 22+ and import java.lang.foreign.*;
            """);
    }

    // ── 5. Memory layout concepts ─────────────────────────────────────────────
    static void memoryLayoutConcepts() {
        System.out.println("=== Memory Layout Concepts ===");
        System.out.println("""
            C struct:                          Java MemoryLayout (final API):
            ─────────────────────────────      ──────────────────────────────────────────
            struct Point {                     StructLayout pointLayout = MemoryLayout.structLayout(
                int   x;    // 4 bytes             ValueLayout.JAVA_INT.withName("x"),
                int   y;    // 4 bytes             ValueLayout.JAVA_INT.withName("y")
            };                                 );

            struct Particle {                  StructLayout particleLayout = MemoryLayout.structLayout(
                double mass;  // 8 bytes           ValueLayout.JAVA_DOUBLE.withName("mass"),
                double x;     // 8 bytes           ValueLayout.JAVA_DOUBLE.withName("x"),
                double y;     // 8 bytes           ValueLayout.JAVA_DOUBLE.withName("y"),
                double z;     // 8 bytes           ValueLayout.JAVA_DOUBLE.withName("z")
            };                                 );

            Benefits:
              - Layout knows exact byte offsets and sizes
              - VarHandle provides type-safe, bounds-checked field access
              - Works for arrays of structs (SequenceLayout)
            """);
    }

    public static void main(String[] args) {
        whyJniIsProblematic();
        offHeapMemoryDemo();
        apiEvolution();
        conceptualNativeCall();
        memoryLayoutConcepts();

        System.out.println("=== Summary ===");
        System.out.println("Java 17 ships FFM as an incubator (jdk.incubator.foreign).");
        System.out.println("The API finalised in Java 22 (java.lang.foreign).");
        System.out.println("For production native interop today, use Java 22+.");
    }
}
