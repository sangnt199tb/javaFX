package com.example.demojavafx.model;

public class Book extends Item {
    private int amount;
    private int available;

    public Book() {}

    public Book(int id, String title, int amount) {
        super(id, title);
        setAmount(amount);
        this.available = amount;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        if (amount > 0) {
            this.amount = amount;
            if (available > amount || available == 0) {
                this.available = amount;
            }
        }
    }

    public int getAvailable() {
        return available;
    }

    // ✅ sửa return String
    @Override
    public String borrowItem() {
        if (available > 0) {
            available--;
            return "Mượn sách thành công";
        } else {
            return "Hết sách!";
        }
    }

    @Override
    public String returnItem() {
        if (available < amount) {
            available++;
            return "Trả sách thành công";
        } else {
            return "Sách đã đủ!";
        }
    }

    @Override
    public String showInfo() {
        return super.showInfo() +
                " | Tổng: " + amount +
                " | Còn: " + available;
    }

}