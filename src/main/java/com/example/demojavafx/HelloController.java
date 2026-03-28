package com.example.demojavafx;

import com.example.demojavafx.model.Book;
import com.example.demojavafx.model.Item;
import com.example.demojavafx.model.Library;
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
    private TableView<Book> tableView;

    @FXML
    private TableColumn<Book, Integer> colId;

    @FXML
    private TableColumn<Book, String> colTitle;

    @FXML
    private TableColumn<Book, Integer> colAmount;

    @FXML
    private TableColumn<Book, Integer> colAvailable;

    private Library library = new Library("Thư viện của tôi");

    // ✅ Khởi tạo TableView
    @FXML
    public void initialize() {
        colId.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getItemID()).asObject());

        colTitle.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getTitle()));

        colAmount.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getAmount()).asObject());

        colAvailable.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getAvailable()).asObject());

        // 👉 Căn giữa cho các cột số
        colId.setStyle("-fx-alignment: CENTER;");
        colAmount.setStyle("-fx-alignment: CENTER;");
        colAvailable.setStyle("-fx-alignment: CENTER;");

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

            if (item instanceof Book) {
                tableView.getItems().add((Book) item);
            }
        }
    }

    //Thêm sách
    @FXML
    public void addBook() {

        Dialog<Book> dialog = new Dialog<>();
        dialog.setTitle("Thêm sách");

        ButtonType addButtonType = new ButtonType("Thêm", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        // 👉 Form nhập
        TextField txtId = new TextField();
        txtId.setPromptText("ID");

        TextField txtTitle = new TextField();
        txtTitle.setPromptText("Tên sách");

        TextField txtQty = new TextField();
        txtQty.setPromptText("Số lượng");

        VBox content = new VBox(10, txtId, txtTitle, txtQty);
        dialog.getDialogPane().setContent(content);

        // 👉 Convert result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    int id = Integer.parseInt(txtId.getText());
                    String title = txtTitle.getText();
                    int qty = Integer.parseInt(txtQty.getText());

                    return new Book(id, title, qty);
                } catch (Exception e) {
                    return null;
                }
            }
            return null;
        });

        // 👉 Xử lý sau khi OK
        dialog.showAndWait().ifPresent(book -> {
            String result = library.addNewItem(book);
            output.setText(result);
            loadTable();
        });
    }

    //Tìm sách
    @FXML
    public void findBook() {

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Tìm sách");

        ButtonType searchBtn = new ButtonType("Tìm", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(searchBtn, ButtonType.CANCEL);

        // 👉 chọn kiểu tìm
        RadioButton rbId = new RadioButton("Theo ID");
        RadioButton rbName = new RadioButton("Theo tên");
        ToggleGroup group = new ToggleGroup();
        rbId.setToggleGroup(group);
        rbName.setToggleGroup(group);
        rbName.setSelected(true); // mặc định

        TextField txtInput = new TextField();
        txtInput.setPromptText("Nhập dữ liệu");

        VBox content = new VBox(10, rbId, rbName, txtInput);
        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(btn -> btn == searchBtn ? null : null);

        dialog.showAndWait();

        if (dialog.getResult() != null || true) { // xử lý luôn
            String result = "";

            if (rbId.isSelected()) {
                try {
                    int id = Integer.parseInt(txtInput.getText());
                    Item item = library.findItem(id);

                    if (item instanceof Book) {
                        result = ((Book) item).showInfo();
                    }
                } catch (Exception e) {
                    result = "ID không hợp lệ!";
                }
            } else {
                String name = txtInput.getText();

                for (int i = 0; i < library.getNumberOfItems(); i++) {
                    Item item = library.getItemList()[i];

                    if (item instanceof Book) {
                        Book b = (Book) item;
                        if (b.getTitle().toLowerCase().contains(name.toLowerCase())) {
                            result += b.showInfo() + "\n";
                        }
                    }
                }
            }

            output.setText(result.isEmpty() ? "Không tìm thấy!" : result);
        }
    }

    //Mượn sách
    @FXML
    public void borrowBook() {

        Dialog<Book> dialog = new Dialog<>();
        dialog.setTitle("Mượn sách");

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

        // 👉 bảng kết quả
        TableView<Book> table = new TableView<>();

        TableColumn<Book, Integer> cId = new TableColumn<>("ID");
        cId.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(data.getValue().getItemID()).asObject());

        TableColumn<Book, String> cTitle = new TableColumn<>("Tên");
        cTitle.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getTitle()));

        TableColumn<Book, Integer> cAvailable = new TableColumn<>("Còn");
        cAvailable.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(data.getValue().getAvailable()).asObject());

        table.getColumns().addAll(cId, cTitle, cAvailable);
        table.setPrefHeight(200);

        // 👉 search action
        btnSearch.setOnAction(e -> {
            table.getItems().clear();

            if (rbId.isSelected()) {
                try {
                    int id = Integer.parseInt(txtInput.getText());
                    Item item = library.findItem(id);

                    if (item instanceof Book) {
                        table.getItems().add((Book) item);
                    }
                } catch (Exception ex) {
                    output.setText("ID không hợp lệ!");
                }
            } else {
                String name = txtInput.getText();

                for (int i = 0; i < library.getNumberOfItems(); i++) {
                    Item item = library.getItemList()[i];

                    if (item instanceof Book) {
                        Book b = (Book) item;

                        if (b.getTitle().toLowerCase().contains(name.toLowerCase())) {
                            table.getItems().add(b);
                        }
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

        // 👉 trả về sách được chọn
        dialog.setResultConverter(btn -> {
            if (btn == borrowBtn) {
                return table.getSelectionModel().getSelectedItem();
            }
            return null;
        });

        // 👉 xử lý sau khi chọn
        dialog.showAndWait().ifPresent(book -> {
            if (book != null) {
                String result = book.borrowItem();
                output.setText(result);
                loadTable();
            } else {
                output.setText("Bạn chưa chọn sách!");
            }
        });
    }

    //Trả sách
    @FXML
    public void returnBook() {

        Dialog<Book> dialog = new Dialog<>();
        dialog.setTitle("Trả sách");

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

        // 👉 bảng kết quả
        TableView<Book> table = new TableView<>();

        TableColumn<Book, Integer> cId = new TableColumn<>("ID");
        cId.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(data.getValue().getItemID()).asObject());

        TableColumn<Book, String> cTitle = new TableColumn<>("Tên");
        cTitle.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getTitle()));

        TableColumn<Book, Integer> cAvailable = new TableColumn<>("Còn");
        cAvailable.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(data.getValue().getAvailable()).asObject());

        table.getColumns().addAll(cId, cTitle, cAvailable);
        table.setPrefHeight(200);

        // 👉 search
        btnSearch.setOnAction(e -> {
            table.getItems().clear();

            if (rbId.isSelected()) {
                try {
                    int id = Integer.parseInt(txtInput.getText());
                    Item item = library.findItem(id);

                    if (item instanceof Book) {
                        table.getItems().add((Book) item);
                    }
                } catch (Exception ex) {
                    output.setText("ID không hợp lệ!");
                }
            } else {
                String name = txtInput.getText();

                for (int i = 0; i < library.getNumberOfItems(); i++) {
                    Item item = library.getItemList()[i];

                    if (item instanceof Book) {
                        Book b = (Book) item;

                        if (b.getTitle().toLowerCase().contains(name.toLowerCase())) {
                            table.getItems().add(b);
                        }
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

        // 👉 trả về sách được chọn
        dialog.setResultConverter(btn -> {
            if (btn == returnBtn) {
                return table.getSelectionModel().getSelectedItem();
            }
            return null;
        });

        // 👉 xử lý sau khi chọn
        dialog.showAndWait().ifPresent(book -> {
            if (book != null) {
                String result = book.returnItem();
                output.setText(result);
                loadTable(); // cập nhật bảng
            } else {
                output.setText("Bạn chưa chọn sách!");
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
        library.addNewItem(new Book(1, "Java Core", 10));
        library.addNewItem(new Book(2, "OOP", 8));
        library.addNewItem(new Book(3, "Data Structure", 5));
        library.addNewItem(new Book(4, "Lap trinh c", 7));
        library.addNewItem(new Book(5, "Lap trinh c#", 7));
        library.addNewItem(new Book(6, "Lap trinh c++", 8));
        library.addNewItem(new Book(7, "Lap trinh python", 9));
        library.addNewItem(new Book(8, "Lap trinh html", 10));
        library.addNewItem(new Book(9, "Lap trinh css", 11));
        library.addNewItem(new Book(10, "Lap trinh js", 12));
        library.addNewItem(new Book(11, "Lap trinh angular", 13));
        library.addNewItem(new Book(12, "Lap trinh react js", 14));
        library.addNewItem(new Book(13, "Lap trinh android", 15));
        library.addNewItem(new Book(14, "Lap trinh iOS", 16));
    }
}