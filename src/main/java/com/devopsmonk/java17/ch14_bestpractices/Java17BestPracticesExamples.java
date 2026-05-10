package com.devopsmonk.java17.ch14_bestpractices;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Supplier;
import java.util.stream.IntStream;

/**
 * Java 17 — Production Best Practices & Performance
 * Matches blog article: 14-production-best-practices.md
 */
public class Java17BestPracticesExamples {

    // ── 1. Use records for immutable data carriers ────────────────────────────
    static void recordBestPractices() {
        System.out.println("=== Records as Immutable Data ===");

        record ApiResponse<T>(int status, T body, String requestId) {
            ApiResponse {
                if (status < 100 || status > 599)
                    throw new IllegalArgumentException("Invalid HTTP status: " + status);
            }
            boolean isSuccess() { return status >= 200 && status < 300; }
        }

        var ok  = new ApiResponse<>(200, Map.of("user", "alice"), "req-001");
        var err = new ApiResponse<>(404, "Not found", "req-002");

        System.out.println("200 OK:  " + ok + " → success=" + ok.isSuccess());
        System.out.println("404 ERR: " + err + " → success=" + err.isSuccess());
    }

    // ── 2. Sealed classes for domain modelling ────────────────────────────────
    sealed interface OrderStatus
        permits Java17BestPracticesExamples.Pending, Java17BestPracticesExamples.Confirmed,
                Java17BestPracticesExamples.Shipped,  Java17BestPracticesExamples.Delivered,
                Java17BestPracticesExamples.Cancelled {}

    record Pending()                                         implements OrderStatus {}
    record Confirmed(Instant confirmedAt)                    implements OrderStatus {}
    record Shipped(String trackingNumber, Instant shippedAt) implements OrderStatus {}
    record Delivered(Instant deliveredAt)                    implements OrderStatus {}
    record Cancelled(String reason)                          implements OrderStatus {}

    static void sealedDomainModel() {
        System.out.println("\n=== Sealed Classes for Domain Modelling ===");

        List<OrderStatus> statuses = List.of(
            new Pending(),
            new Confirmed(Instant.now().minusSeconds(3600)),
            new Shipped("DHL-12345", Instant.now().minusSeconds(1800)),
            new Delivered(Instant.now()),
            new Cancelled("Customer requested cancellation")
        );

        statuses.forEach(status -> {
            String description = switch (status) {
                case Pending p    -> "Awaiting confirmation";
                case Confirmed c  -> "Confirmed at " + c.confirmedAt();
                case Shipped s    -> "Shipped, tracking: " + s.trackingNumber();
                case Delivered d  -> "Delivered at " + d.deliveredAt();
                case Cancelled c  -> "Cancelled: " + c.reason();
            };
            System.out.println("  " + status.getClass().getSimpleName() + ": " + description);
        });
    }

    // ── 3. Text blocks for config and query strings ───────────────────────────
    static void textBlocksInPractice() {
        System.out.println("\n=== Text Blocks in Practice ===");

        String user = "alice";
        int minOrders = 3;

        String query = """
                SELECT u.id, u.name, COUNT(o.id) AS order_count
                FROM   users u
                JOIN   orders o ON o.user_id = u.id
                WHERE  u.username = '%s'
                GROUP  BY u.id, u.name
                HAVING COUNT(o.id) >= %d
                ORDER  BY order_count DESC
                """.formatted(user, minOrders);

        System.out.println("Generated SQL:");
        System.out.print(query);

        String config = """
                {
                    "datasource": {
                        "url": "jdbc:postgresql://localhost/mydb",
                        "username": "app",
                        "pool": { "min": 5, "max": 20 }
                    }
                }
                """;
        System.out.println("Config JSON: " + config.lines().count() + " lines");
    }

    // ── 4. Enhanced PRNG for non-cryptographic use ────────────────────────────
    static void prngBestPractice() {
        System.out.println("\n=== PRNG Best Practices ===");

        // Prefer L64X128MixRandom for general-purpose — better statistical quality
        var rng = java.util.random.RandomGenerator.of("L64X128MixRandom");
        System.out.println("10 random [1-100]: " +
            rng.ints(10, 1, 101).boxed().toList());

        // Use ThreadLocalRandom for concurrent code (still fast and safe)
        System.out.println("ThreadLocalRandom: " +
            ThreadLocalRandom.current().nextInt(100));

        // Use SecureRandom for cryptographic purposes
        var secure = new java.security.SecureRandom();
        byte[] token = new byte[16];
        secure.nextBytes(token);
        System.out.println("SecureRandom token: " +
            java.util.HexFormat.of().formatHex(token));
    }

    // ── 5. Measure startup and throughput ─────────────────────────────────────
    static void performanceMeasurement() {
        System.out.println("\n=== Performance Measurement Pattern ===");

        Supplier<Long> benchmark = () -> {
            long sum = 0;
            for (int i = 0; i < 1_000_000; i++) sum += i;
            return sum;
        };

        // Warm up
        for (int i = 0; i < 5; i++) benchmark.get();

        // Measure
        Instant start = Instant.now();
        long result = benchmark.get();
        Duration elapsed = Duration.between(start, Instant.now());

        System.out.printf("Sum 1..1M = %d, elapsed = %d µs%n",
            result, elapsed.toNanos() / 1000);
    }

    // ── 6. Concurrency with virtual-thread-friendly patterns (Java 17 prep) ───
    static void modernConcurrency() throws InterruptedException {
        System.out.println("\n=== Modern Concurrency Patterns ===");

        // ExecutorService.close() / try-with-resources only available from Java 19+
        ExecutorService executor = Executors.newWorkStealingPool();
        try {
            List<Future<String>> futures = IntStream.range(1, 6)
                .mapToObj(i -> executor.submit(() -> {
                    Thread.sleep(ThreadLocalRandom.current().nextInt(50));
                    return "Task-" + i + " done on " + Thread.currentThread().getName();
                }))
                .toList();

            futures.forEach(f -> {
                try { System.out.println("  " + f.get()); }
                catch (Exception e) { System.out.println("  Error: " + e.getMessage()); }
            });
        } finally {
            executor.shutdown();
        }
    }

    // ── 7. JVM flags cheat-sheet ──────────────────────────────────────────────
    static void jvmFlagsCheatSheet() {
        System.out.println("\n=== Production JVM Flags Cheat Sheet ===");
        List.of(
            "# Heap",
            "-Xms512m -Xmx2g                    # initial / max heap",
            "-XX:MaxMetaspaceSize=256m           # metaspace cap",
            "-XX:+UseStringDeduplication        # deduplicate equal Strings in old gen",
            "",
            "# GC — G1 (default, good balance)",
            "-XX:+UseG1GC",
            "-XX:MaxGCPauseMillis=200            # target pause time",
            "-XX:G1HeapRegionSize=16m",
            "",
            "# GC — ZGC (low latency, Java 15+ production)",
            "-XX:+UseZGC",
            "-XX:+ZGenerational                 # generational ZGC (Java 21+)",
            "",
            "# Logging",
            "-Xlog:gc*:file=gc.log:tags,time,uptime,level",
            "",
            "# Startup / AppCDS (Class Data Sharing)",
            "-XX:SharedArchiveFile=app.jsa       # use a pre-built shared archive",
            "",
            "# Observability",
            "-XX:+FlightRecorder",
            "-XX:StartFlightRecording=duration=60s,filename=app.jfr",
            "",
            "# Container awareness",
            "-XX:+UseContainerSupport            # auto-detect container memory/CPU (default Java 17+)",
            "-XX:MaxRAMPercentage=75.0           # use 75% of container RAM as heap max"
        ).forEach(System.out::println);
    }

    public static void main(String[] args) throws InterruptedException {
        recordBestPractices();
        sealedDomainModel();
        textBlocksInPractice();
        prngBestPractice();
        performanceMeasurement();
        modernConcurrency();
        jvmFlagsCheatSheet();
    }
}
