package com.example.demojavafx.model;

public class Magazine extends Item {

    private int issueNumber; // số phát hành
    private int amount;      // tổng số
    private int available;   // còn lại

    public Magazine() {}

    public Magazine(int id, String title, int issueNumber, int amount) {
        super(id, title);
        setIssueNumber(issueNumber);
        setAmount(amount);
        this.available = amount;
    }

    // ===== Getter & Setter =====
    public int getIssueNumber() {
        return issueNumber;
    }

    public void setIssueNumber(int issueNumber) {
        if (issueNumber > 0) {
            this.issueNumber = issueNumber;
        }
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

    // ===== Override hành vi =====
    @Override
    public String borrowItem() {
        if (available > 0) {
            available--;
            return "Mượn tạp chí thành công!";
        }
        return "Tạp chí đã hết!";
    }

    @Override
    public String returnItem() {
        if (available < amount) {
            available++;
            return "Trả tạp chí thành công!";
        }
        return "Tạp chí đã đủ!";
    }

    @Override
    public String showInfo() {
        return "ID: " + itemID +
                " | Tạp chí: " + title +
                " | Số phát hành: " + issueNumber +
                " | Tổng: " + amount +
                " | Còn: " + available;
    }
}