package com.example.demojavafx.model;

public class Library {
    private String libraryName;
    private Item[] itemList;

    private static int numberOfItems = 0; // ❗ bỏ static
    public static final int MAX_NUMBER_OF_ITEM = 100;

    public Library(String name) {
        this.libraryName = name;
        itemList = new Item[MAX_NUMBER_OF_ITEM];
    }

    // ✅ thêm item
    public String addNewItem(Item item) {
        if (numberOfItems >= MAX_NUMBER_OF_ITEM) {
            return "Thư viện đã đầy!";
        }

        for (int i = 0; i < numberOfItems; i++) {
            if (itemList[i].getItemID() == item.getItemID()) {
                return "Trùng ID!";
            }
        }

        itemList[numberOfItems++] = item;
        return "Thêm thành công!";
    }

    // tìm
    public Item findItem(int itemID) {
        for (int i = 0; i < numberOfItems; i++) {
            if (itemList[i].getItemID() == itemID) {
                return itemList[i];
            }
        }
        return null;
    }

    // mượn
    public String borrowItem(int itemID) {
        Item item = findItem(itemID);
        if (item != null) {
            return item.borrowItem();
        }
        return "Không tìm thấy!";
    }

    // trả
    public String returnItem(int itemID) {
        Item item = findItem(itemID);
        if (item != null) {
            return item.returnItem();
        }
        return "Không tìm thấy!";
    }

    // ✅ lấy danh sách (dùng cho UI)
    public Item[] getItemList() {
        return itemList;
    }

    public int getNumberOfItems() {
        return numberOfItems;
    }

    // ✅ hiển thị dạng string
    public String showLibraryInfo() {
        String result = "Tên thư viện: " + libraryName + "\n";

        for (int i = 0; i < numberOfItems; i++) {
            result += itemList[i].showInfo() + "\n";
        }

        return result;
    }
}