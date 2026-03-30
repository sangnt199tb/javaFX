package com.example.demojavafx;

import com.example.demojavafx.model.Book;
import com.example.demojavafx.model.Item;
import com.example.demojavafx.model.Library;
import com.example.demojavafx.model.Magazine;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class HelloController {

    @FXML
    private TextField txtId, txtTitle, txtQty;

    @FXML
    private TextArea output;

    @FXML
    private TableView<Item> tableView;

    @FXML
    private TableColumn<Item, Integer> colId;

    @FXML
    private TableColumn<Item, String> colTitle;

    @FXML
    private TableColumn<Item, Integer> colAmount;

    @FXML
    private TableColumn<Item, Integer> colAvailable;

    @FXML
    private TableColumn<Item, String> colType;

    private Library library = new Library("Thư viện của tôi");

    // ✅ Khởi tạo TableView
    @FXML
    public void initialize() {
        colId.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getItemID()).asObject());

        colAmount.setCellValueFactory(data -> {
            Item item = data.getValue();

            if (item instanceof Book) {
                Book b = (Book) item;
                return new SimpleIntegerProperty(b.getAmount()).asObject();

            } else if (item instanceof Magazine) {
                Magazine m = (Magazine) item;
                return new SimpleIntegerProperty(m.getAmount()).asObject();
            }

            return new SimpleIntegerProperty(0).asObject();
        });

        colTitle.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getTitle()));

        colAmount.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getAmount()).asObject());

        colAvailable.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getAvailable()).asObject());

        colType.setCellValueFactory(data -> {
            if (data.getValue() instanceof Book) {
                return new SimpleStringProperty("Sách");
            } else {
                return new SimpleStringProperty("Tạp chí");
            }
        });

        // 👉 Căn giữa cho các cột số
        colId.setStyle("-fx-alignment: CENTER;");
        colAmount.setStyle("-fx-alignment: CENTER;");
        colAvailable.setStyle("-fx-alignment: CENTER;");
        colType.setStyle("-fx-alignment: CENTER;");

        //mock dữ liệu
        mockData();

        //load lên bảng luôn
        loadTable();
    }

    //Load danh sách lên bảng
    @FXML
    public void loadTable() {
        tableView.getItems().clear();

        for (int i = 0; i < library.getNumberOfItems(); i++) {
            Item item = library.getItemList()[i];
            tableView.getItems().add(item); // ✅ thêm tất cả
        }
    }

    //Thêm sách
    @FXML
    public void addItem() {
        ChoiceDialog<String> dialog = new ChoiceDialog<>("Sách", "Sách", "Tạp chí");
        dialog.setTitle("Chọn loại");
        dialog.setHeaderText("Bạn muốn thêm gì?");
        dialog.showAndWait().ifPresent(choice -> {
            if (choice.equals("Sách")) {
                showAddBookDialog();
            } else {
                showAddMagazineDialog();
            }
        });
    }

    //Tìm sách
    @FXML
    public void findItem() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Tìm tài liệu");
        ButtonType searchBtn = new ButtonType("Tìm", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(searchBtn, ButtonType.CANCEL);
        // chọn kiểu tìm
        RadioButton rbId = new RadioButton("Theo ID");
        RadioButton rbName = new RadioButton("Theo tên");
        ToggleGroup group = new ToggleGroup();
        rbId.setToggleGroup(group);
        rbName.setToggleGroup(group);
        rbName.setSelected(true);

        TextField txtInput = new TextField();
        txtInput.setPromptText("Nhập dữ liệu");

        VBox content = new VBox(10, rbId, rbName, txtInput);
        dialog.getDialogPane().setContent(content);
        dialog.showAndWait();
        String result = "";

        if (rbId.isSelected()) {
            try {
                int id = Integer.parseInt(txtInput.getText());
                Item item = library.findItem(id);

                if (item != null) {
                    result = item.showInfo(); // ✅ không cần instanceof
                }
            } catch (Exception e) {
                result = "ID không hợp lệ!";
            }
        } else {
            String name = txtInput.getText();
            for (int i = 0; i < library.getNumberOfItems(); i++) {
                Item item = library.getItemList()[i];
                if (item.getTitle().toLowerCase().contains(name.toLowerCase())) {
                    result += item.showInfo() + "\n"; // ✅ áp dụng cho cả Book & Magazine
                }
            }
        }
        output.setText(result.isEmpty() ? "Không tìm thấy!" : result);
    }

    //Mượn sách
    @FXML
    public void borrowBook() {

        Dialog<Item> dialog = new Dialog<>();
        dialog.setTitle("Mượn tài liệu");

        ButtonType borrowBtn = new ButtonType("Mượn", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(borrowBtn, ButtonType.CANCEL);

        // 👉 chọn kiểu tìm
        RadioButton rbId = new RadioButton("Theo ID");
        RadioButton rbName = new RadioButton("Theo tên");

        ToggleGroup group = new ToggleGroup();
        rbId.setToggleGroup(group);
        rbName.setToggleGroup(group);
        rbName.setSelected(true);

        TextField txtInput = new TextField();
        txtInput.setPromptText("Nhập dữ liệu");

        Button btnSearch = new Button("Tìm");

        // 👉 bảng kết quả (DÙNG ITEM)
        TableView<Item> table = new TableView<>();

        TableColumn<Item, Integer> cId = new TableColumn<>("ID");
        cId.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(data.getValue().getItemID()).asObject());

        TableColumn<Item, String> cTitle = new TableColumn<>("Tên");
        cTitle.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getTitle()));

        TableColumn<Item, Integer> cAvailable = new TableColumn<>("Còn");
        cAvailable.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(data.getValue().getAvailable()).asObject());

        table.getColumns().addAll(cId, cTitle, cAvailable);
        table.setPrefHeight(200);

        // 👉 search (KHÔNG dùng instanceof Book)
        btnSearch.setOnAction(e -> {
            table.getItems().clear();

            if (rbId.isSelected()) {
                try {
                    int id = Integer.parseInt(txtInput.getText());
                    Item item = library.findItem(id);

                    if (item != null) {
                        table.getItems().add(item); // ✅ add luôn
                    }
                } catch (Exception ex) {
                    output.setText("ID không hợp lệ!");
                }
            } else {
                String name = txtInput.getText();

                for (int i = 0; i < library.getNumberOfItems(); i++) {
                    Item item = library.getItemList()[i];

                    if (item.getTitle().toLowerCase().contains(name.toLowerCase())) {
                        table.getItems().add(item); // ✅ cả Book + Magazine
                    }
                }
            }
        });

        VBox content = new VBox(10,
                rbId, rbName,
                txtInput,
                btnSearch,
                table
        );

        dialog.getDialogPane().setContent(content);

        // 👉 disable nút mượn nếu chưa chọn
        Node borrowButton = dialog.getDialogPane().lookupButton(borrowBtn);
        borrowButton.setDisable(true);

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            borrowButton.setDisable(newVal == null);
        });

        // 👉 trả về Item
        dialog.setResultConverter(btn -> {
            if (btn == borrowBtn) {
                return table.getSelectionModel().getSelectedItem();
            }
            return null;
        });

        // 👉 xử lý mượn (đa hình)
        dialog.showAndWait().ifPresent(item -> {
            if (item != null) {
                String result = item.borrowItem(); // ✅ chạy đúng loại
                output.setText(result);
                loadTable();
            } else {
                output.setText("Bạn chưa chọn!");
            }
        });
    }

    //Trả sách
    @FXML
    public void returnBook() {

        Dialog<Item> dialog = new Dialog<>();
        dialog.setTitle("Trả tài liệu");

        ButtonType returnBtn = new ButtonType("Trả", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(returnBtn, ButtonType.CANCEL);

        // 👉 chọn kiểu tìm
        RadioButton rbId = new RadioButton("Theo ID");
        RadioButton rbName = new RadioButton("Theo tên");

        ToggleGroup group = new ToggleGroup();
        rbId.setToggleGroup(group);
        rbName.setToggleGroup(group);
        rbName.setSelected(true);

        TextField txtInput = new TextField();
        txtInput.setPromptText("Nhập dữ liệu");

        Button btnSearch = new Button("Tìm");

        // 👉 bảng kết quả (CHUYỂN SANG ITEM)
        TableView<Item> table = new TableView<>();

        TableColumn<Item, Integer> cId = new TableColumn<>("ID");
        cId.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(data.getValue().getItemID()).asObject());

        TableColumn<Item, String> cTitle = new TableColumn<>("Tên");
        cTitle.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getTitle()));

        TableColumn<Item, Integer> cAvailable = new TableColumn<>("Còn");
        cAvailable.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(data.getValue().getAvailable()).asObject());

        table.getColumns().addAll(cId, cTitle, cAvailable);
        table.setPrefHeight(200);

        // 👉 search (BỎ instanceof Book)
        btnSearch.setOnAction(e -> {
            table.getItems().clear();

            if (rbId.isSelected()) {
                try {
                    int id = Integer.parseInt(txtInput.getText());
                    Item item = library.findItem(id);

                    if (item != null) {
                        table.getItems().add(item); // ✅ add trực tiếp
                    }
                } catch (Exception ex) {
                    output.setText("ID không hợp lệ!");
                }
            } else {
                String name = txtInput.getText();

                for (int i = 0; i < library.getNumberOfItems(); i++) {
                    Item item = library.getItemList()[i];

                    if (item.getTitle().toLowerCase().contains(name.toLowerCase())) {
                        table.getItems().add(item); // ✅ add tất cả loại
                    }
                }
            }
        });

        VBox content = new VBox(10,
                rbId, rbName,
                txtInput,
                btnSearch,
                table
        );

        dialog.getDialogPane().setContent(content);

        // 👉 disable nút Trả nếu chưa chọn
        Node returnButton = dialog.getDialogPane().lookupButton(returnBtn);
        returnButton.setDisable(true);

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            returnButton.setDisable(newVal == null);
        });

        // 👉 trả về Item (KHÔNG phải Book)
        dialog.setResultConverter(btn -> {
            if (btn == returnBtn) {
                return table.getSelectionModel().getSelectedItem();
            }
            return null;
        });

        // 👉 xử lý trả (đa hình)
        dialog.showAndWait().ifPresent(item -> {
            if (item != null) {
                String result = item.returnItem(); // ✅ chạy đúng Book/Magazine
                output.setText(result);
                loadTable();
            } else {
                output.setText("Bạn chưa chọn!");
            }
        });
    }

    //Xem toàn bộ danh sách (hiện ra TextArea)
    @FXML
    public void showAllBooks() {
        String result = library.showLibraryInfo();
        output.setText(result);
    }

    private void mockData() {

        // ===== BOOK =====
        library.addNewItem(new Book(1, "Java Core", 10));
        library.addNewItem(new Book(2, "OOP", 8));
        library.addNewItem(new Book(3, "Data Structure", 5));
        library.addNewItem(new Book(4, "Lap trinh C", 7));
        library.addNewItem(new Book(5, "Lap trinh C#", 7));
        library.addNewItem(new Book(6, "Lap trinh C++", 8));
        library.addNewItem(new Book(7, "Lap trinh Python", 9));
        library.addNewItem(new Book(8, "Lap trinh HTML", 10));
        library.addNewItem(new Book(9, "Lap trinh CSS", 11));
        library.addNewItem(new Book(10, "Lap trinh JS", 12));

        // ===== MAGAZINE =====
        library.addNewItem(new Magazine(101, "IT Weekly", 1, 5));
        library.addNewItem(new Magazine(102, "Tech Today", 2, 6));
        library.addNewItem(new Magazine(103, "AI Magazine", 3, 4));
        library.addNewItem(new Magazine(104, "Dev Life", 4, 7));
        library.addNewItem(new Magazine(105, "Startup World", 5, 3));
    }

    private void showAddBookDialog() {
        Dialog<Book> dialog = new Dialog<>();
        dialog.setTitle("Thêm sách");

        ButtonType addBtn = new ButtonType("Thêm", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addBtn, ButtonType.CANCEL);

        TextField txtId = new TextField();
        TextField txtTitle = new TextField();
        TextField txtQty = new TextField();

        txtId.setPromptText("ID");
        txtTitle.setPromptText("Tên sách");
        txtQty.setPromptText("Số lượng");

        VBox content = new VBox(10, txtId, txtTitle, txtQty);
        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(btn -> {
            if (btn == addBtn) {
                try {
                    return new Book(
                            Integer.parseInt(txtId.getText()),
                            txtTitle.getText(),
                            Integer.parseInt(txtQty.getText())
                    );
                } catch (Exception e) {
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(book -> {
            output.setText(library.addNewItem(book));
            loadTable();
        });
    }

    private void showAddMagazineDialog() {

        Dialog<Magazine> dialog = new Dialog<>();
        dialog.setTitle("Thêm tạp chí");

        ButtonType addBtn = new ButtonType("Thêm", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addBtn, ButtonType.CANCEL);

        TextField txtId = new TextField();
        TextField txtTitle = new TextField();
        TextField txtIssue = new TextField();
        TextField txtQty = new TextField();

        txtId.setPromptText("ID");
        txtTitle.setPromptText("Tên tạp chí");
        txtIssue.setPromptText("Số phát hành");
        txtQty.setPromptText("Số lượng");

        VBox content = new VBox(10, txtId, txtTitle, txtIssue, txtQty);
        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(btn -> {
            if (btn == addBtn) {
                try {
                    return new Magazine(
                            Integer.parseInt(txtId.getText()),
                            txtTitle.getText(),
                            Integer.parseInt(txtIssue.getText()),
                            Integer.parseInt(txtQty.getText())
                    );
                } catch (Exception e) {
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(mag -> {
            output.setText(library.addNewItem(mag));
            loadTable();
        });
    }
}