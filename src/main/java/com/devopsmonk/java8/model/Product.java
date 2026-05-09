package com.devopsmonk.java8.model;

public class Product {

    public enum Category { ELECTRONICS, CLOTHING, FOOD, BOOKS, SPORTS }

    private final long id;
    private final String name;
    private final Category category;
    private final double price;
    private final int stockQty;

    public Product(long id, String name, Category category, double price, int stockQty) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.stockQty = stockQty;
    }

    public long getId()          { return id; }
    public String getName()      { return name; }
    public Category getCategory(){ return category; }
    public double getPrice()     { return price; }
    public int getStockQty()     { return stockQty; }

    @Override
    public String toString() {
        return String.format("Product{id=%d, name='%s', category=%s, price=%.2f, stock=%d}",
                id, name, category, price, stockQty);
    }
}
