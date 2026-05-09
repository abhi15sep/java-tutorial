package com.devopsmonk.java8.model;

public class OrderItem {

    private final Product product;
    private final int quantity;

    public OrderItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct()  { return product; }
    public int getQuantity()     { return quantity; }
    public double getLineTotal() { return product.getPrice() * quantity; }

    @Override
    public String toString() {
        return String.format("OrderItem{product='%s', qty=%d, total=%.2f}",
                product.getName(), quantity, getLineTotal());
    }
}
