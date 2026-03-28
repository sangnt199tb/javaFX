package com.example.demojavafx.model;

public class Item {
    protected int itemID;
    protected String title;

    public Item() {}

    public Item(int itemID, String title) {
        this.itemID = itemID;
        this.title = title;
    }

    public int getItemID() {
        return itemID;
    }

    public void setItemID(int itemID) {
        if (itemID > 0) this.itemID = itemID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title != null && !title.trim().isEmpty()) {
            this.title = title;
        }
    }

    // ✅ đổi sang return String
    public String borrowItem() {
        return "Không thể mượn item này";
    }

    public String returnItem() {
        return "Không thể trả item này";
    }

    // ✅ trả về chuỗi thay vì println
    public String showInfo() {
        return "ID: " + itemID + " | Title: " + title;
    }
}