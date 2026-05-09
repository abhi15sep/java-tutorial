package com.devopsmonk.java8.model;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class Order {

    public enum Status { PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED }

    private final long id;
    private final String customerId;
    private final List<OrderItem> items;
    private final Status status;
    private final LocalDate orderDate;

    public Order(long id, String customerId, List<OrderItem> items,
                 Status status, LocalDate orderDate) {
        this.id = id;
        this.customerId = customerId;
        this.items = items;
        this.status = status;
        this.orderDate = orderDate;
    }

    public long getId()             { return id; }
    public String getCustomerId()   { return customerId; }
    public List<OrderItem> getItems(){ return items; }
    public Status getStatus()       { return status; }
    public LocalDate getOrderDate() { return orderDate; }

    public double getTotal() {
        return items.stream().mapToDouble(OrderItem::getLineTotal).sum();
    }

    @Override
    public String toString() {
        return String.format("Order{id=%d, customer='%s', status=%s, total=%.2f, date=%s}",
                id, customerId, status, getTotal(), orderDate);
    }

    // -------------------------------------------------------------------------
    // Sample data
    // -------------------------------------------------------------------------

    public static List<Order> sampleOrders() {
        Product laptop  = new Product(101, "Laptop Pro",      Product.Category.ELECTRONICS, 1299.99, 50);
        Product phone   = new Product(102, "Smartphone X",    Product.Category.ELECTRONICS,  799.00, 120);
        Product jacket  = new Product(103, "Winter Jacket",   Product.Category.CLOTHING,      149.99, 75);
        Product book    = new Product(104, "Clean Code",      Product.Category.BOOKS,          39.99, 200);
        Product headset = new Product(105, "Noise Cancelling Headset", Product.Category.ELECTRONICS, 249.99, 60);
        Product trainers= new Product(106, "Running Trainers",Product.Category.SPORTS,          89.99, 90);

        return Arrays.asList(
            new Order(1001, "customer-A", Arrays.asList(
                new OrderItem(laptop, 1), new OrderItem(headset, 1)), Status.DELIVERED, LocalDate.of(2026, 1, 10)),
            new Order(1002, "customer-B", Arrays.asList(
                new OrderItem(phone, 2)), Status.SHIPPED, LocalDate.of(2026, 2, 14)),
            new Order(1003, "customer-A", Arrays.asList(
                new OrderItem(book, 3), new OrderItem(jacket, 1)), Status.DELIVERED, LocalDate.of(2026, 2, 20)),
            new Order(1004, "customer-C", Arrays.asList(
                new OrderItem(trainers, 1)), Status.PROCESSING, LocalDate.of(2026, 3, 5)),
            new Order(1005, "customer-B", Arrays.asList(
                new OrderItem(laptop, 1)), Status.CANCELLED, LocalDate.of(2026, 3, 8)),
            new Order(1006, "customer-D", Arrays.asList(
                new OrderItem(headset, 2), new OrderItem(trainers, 1)), Status.PENDING, LocalDate.of(2026, 4, 1)),
            new Order(1007, "customer-A", Arrays.asList(
                new OrderItem(phone, 1), new OrderItem(book, 2)), Status.SHIPPED, LocalDate.of(2026, 4, 15))
        );
    }
}
