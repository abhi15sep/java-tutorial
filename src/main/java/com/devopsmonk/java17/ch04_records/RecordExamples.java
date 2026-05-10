package com.devopsmonk.java17.ch04_records;

import java.util.List;
import java.util.Objects;

/**
 * Java 17 — Records (JEP 395)
 * Matches blog article: 04-records.md
 */
public class RecordExamples {

    // ── 1. Basic record ───────────────────────────────────────────────────────
    record Point(int x, int y) {}

    // ── 2. Record with compact canonical constructor (validation) ─────────────
    record Range(int min, int max) {
        Range {  // compact constructor — no parameter list, no this.field = field
            if (min > max) throw new IllegalArgumentException(
                "min " + min + " must be <= max " + max);
        }
    }

    // ── 3. Record with custom accessor and additional method ──────────────────
    record Money(long cents, String currency) {
        // Custom accessor — normalise currency to uppercase
        @Override
        public String currency() {
            return currency.toUpperCase();
        }

        // Additional instance method
        public double amount() {
            return cents / 100.0;
        }

        @Override
        public String toString() {
            return String.format("%.2f %s", amount(), currency());
        }
    }

    // ── 4. Record implementing an interface ───────────────────────────────────
    interface Describable {
        String describe();
    }

    record Product(String name, double price, int stock) implements Describable {
        @Override
        public String describe() {
            return String.format("%s — £%.2f (%d in stock)", name, price, stock);
        }
    }

    // ── 5. Generic record ─────────────────────────────────────────────────────
    record Pair<A, B>(A first, B second) {
        public Pair<B, A> swap() {
            return new Pair<>(second, first);
        }
    }

    // ── 6. Nested records — a real-world Address / Customer example ───────────
    record Address(String street, String city, String postcode) {}

    record Customer(String id, String name, Address address, List<String> tags) {
        // Compact constructor: defensive copy of mutable list
        Customer {
            Objects.requireNonNull(id,      "id cannot be null");
            Objects.requireNonNull(name,    "name cannot be null");
            Objects.requireNonNull(address, "address cannot be null");
            tags = List.copyOf(tags);  // make immutable copy
        }
    }

    // ── 7. Record used as a DTO / value in a stream pipeline ─────────────────
    record OrderSummary(String orderId, String customer, double total) {}

    static void streamWithRecords() {
        var orders = List.of(
            new OrderSummary("ORD-001", "Alice",  129.99),
            new OrderSummary("ORD-002", "Bob",    249.50),
            new OrderSummary("ORD-003", "Alice",   79.00),
            new OrderSummary("ORD-004", "Charlie", 399.99)
        );

        System.out.println("=== Orders over £100 ===");
        orders.stream()
              .filter(o -> o.total() > 100)
              .sorted((a, b) -> Double.compare(b.total(), a.total()))
              .forEach(o -> System.out.printf("  %s: %s — £%.2f%n",
                  o.orderId(), o.customer(), o.total()));

        double aliceTotal = orders.stream()
              .filter(o -> o.customer().equals("Alice"))
              .mapToDouble(OrderSummary::total)
              .sum();
        System.out.printf("Alice's total: £%.2f%n", aliceTotal);
    }

    public static void main(String[] args) {
        // Basic record
        Point p = new Point(3, 4);
        System.out.println("=== Basic Record ===");
        System.out.println("Point: " + p);
        System.out.println("x=" + p.x() + ", y=" + p.y());
        System.out.println("equals: " + p.equals(new Point(3, 4)));

        // Compact constructor validation
        System.out.println("\n=== Range Validation ===");
        Range r = new Range(1, 10);
        System.out.println("Range: " + r);
        try {
            new Range(10, 1);
        } catch (IllegalArgumentException e) {
            System.out.println("Caught: " + e.getMessage());
        }

        // Money record
        System.out.println("\n=== Money Record ===");
        Money m = new Money(1999, "gbp");
        System.out.println(m);
        System.out.println("Currency: " + m.currency());

        // Product implementing interface
        System.out.println("\n=== Product ===");
        Product prod = new Product("Mechanical Keyboard", 89.99, 42);
        System.out.println(prod.describe());

        // Generic Pair
        System.out.println("\n=== Generic Pair ===");
        Pair<String, Integer> pair = new Pair<>("hello", 42);
        System.out.println("Pair: " + pair);
        System.out.println("Swapped: " + pair.swap());

        // Nested records
        System.out.println("\n=== Customer ===");
        var customer = new Customer("C001", "Alice",
            new Address("10 Downing St", "London", "SW1A 2AA"),
            List.of("vip", "newsletter"));
        System.out.println(customer);
        System.out.println("City: " + customer.address().city());

        // Stream pipeline
        System.out.println();
        streamWithRecords();
    }
}
