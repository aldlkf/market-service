package com.marketplace.model;

import java.math.BigDecimal;
import java.util.Objects;

public class Product {

    private Long id;
    private String title;
    private BigDecimal price;
    private int stockQuantity;


    public Product(){
    }

    public Product(Long id, String title, BigDecimal price, int stockQuantity){
        this.id = id;
        this.title = title;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public BigDecimal getPrice(){
        return price;
    }

    public String getFormattedPrice() {
        return String.format("%,.2f KZT", price);
    }

    public int getStockQuantity(){
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity){
        this.stockQuantity = stockQuantity;
    }

    @Override
    public String toString() {
        return "Product {" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", price=" + getFormattedPrice() +
                ", stockQuantity=" + stockQuantity +
                '}';
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id);
    }

    @Override
    public int hashCode(){
        return Objects.hash(id);
    }

}



















