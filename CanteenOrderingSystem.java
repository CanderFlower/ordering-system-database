import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CanteenOrderingSystem extends Application {

    private Stage primaryStage;
    private Scene mainScene, userScene, merchantScene, adminScene;
    private TextArea outputArea;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        primaryStage.setTitle("Canteen Ordering System");

        // Main scene with role selection
        VBox mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(20));

        Button userButton = new Button("User");
        userButton.setOnAction(e -> primaryStage.setScene(userScene));

        Button merchantButton = new Button("Merchant");
        merchantButton.setOnAction(e -> primaryStage.setScene(merchantScene));

        Button adminButton = new Button("Admin");
        adminButton.setOnAction(e -> primaryStage.setScene(adminScene));

        mainLayout.getChildren().addAll(new Label("Select Role:"), userButton, merchantButton, adminButton);
        mainScene = new Scene(mainLayout, 300, 200);

        // User scene
        userScene = createUserScene();

        // Merchant scene
        merchantScene = createMerchantScene();

        // Admin scene
        adminScene = createAdminScene();

        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    private Scene createUserScene() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> primaryStage.setScene(mainScene));

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setVgap(10);
        grid.setHgap(10);

        Label searchMerchantLabel = new Label("Search Merchant:");
        TextField searchMerchantField = new TextField();
        Button searchMerchantButton = new Button("Search");
        searchMerchantButton.setOnAction(e -> searchMerchants(searchMerchantField.getText()));

        Label searchDishLabel = new Label("Search Dish:");
        TextField searchDishField = new TextField();
        Button searchDishButton = new Button("Search");
        searchDishButton.setOnAction(e -> searchDishes(searchDishField.getText()));

        Label orderDishLabel = new Label("Order Dish:");
        TextField orderDishField = new TextField();
        Button orderDishButton = new Button("Order");
        orderDishButton.setOnAction(e -> orderDish(orderDishField.getText()));

        outputArea = new TextArea();
        outputArea.setPrefHeight(200);

        grid.add(searchMerchantLabel, 0, 0);
        grid.add(searchMerchantField, 1, 0);
        grid.add(searchMerchantButton, 2, 0);
        grid.add(searchDishLabel, 0, 1);
        grid.add(searchDishField, 1, 1);
        grid.add(searchDishButton, 2, 1);
        grid.add(orderDishLabel, 0, 2);
        grid.add(orderDishField, 1, 2);
        grid.add(orderDishButton, 2, 2);

        layout.getChildren().addAll(backButton, grid, outputArea);
        return new Scene(layout, 600, 400);
    }

    private Scene createMerchantScene() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> primaryStage.setScene(mainScene));

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setVgap(10);
        grid.setHgap(10);

        Label manageMenuLabel = new Label("Manage Menu:");
        TextField dishNameField = new TextField();
        dishNameField.setPromptText("Dish Name");
        TextField dishPriceField = new TextField();
        dishPriceField.setPromptText("Dish Price");
        Button addButton = new Button("Add Dish");
        addButton.setOnAction(e -> manageMenu(dishNameField.getText(), dishPriceField.getText()));

        Label sendMessageLabel = new Label("Send Message:");
        TextField messageField = new TextField();
        Button sendButton = new Button("Send");
        sendButton.setOnAction(e -> sendMessage(messageField.getText()));

        outputArea = new TextArea();
        outputArea.setPrefHeight(200);

        grid.add(manageMenuLabel, 0, 0);
        grid.add(dishNameField, 1, 0);
        grid.add(dishPriceField, 2, 0);
        grid.add(addButton, 3, 0);
        grid.add(sendMessageLabel, 0, 1);
        grid.add(messageField, 1, 1);
        grid.add(sendButton, 2, 1);

        layout.getChildren().addAll(backButton, grid, outputArea);
        return new Scene(layout, 600, 400);
    }

    private Scene createAdminScene() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> primaryStage.setScene(mainScene));

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setVgap(10);
        grid.setHgap(10);

        Label manageAccountsLabel = new Label("Manage Accounts:");
        TextField accountField = new TextField();
        accountField.setPromptText("Account ID");
        Button addButton = new Button("Add Account");
        addButton.setOnAction(e -> manageAccounts(accountField.getText(), "add"));
        Button deleteButton = new Button("Delete Account");
        deleteButton.setOnAction(e -> manageAccounts(accountField.getText(), "delete"));

        Label analyzeDataLabel = new Label("Analyze Data:");
        Button analyzeButton = new Button("Analyze");
        analyzeButton.setOnAction(e -> analyzeData());

        outputArea = new TextArea();
        outputArea.setPrefHeight(200);

        grid.add(manageAccountsLabel, 0, 0);
        grid.add(accountField, 1, 0);
        grid.add(addButton, 2, 0);
        grid.add(deleteButton, 3, 0);
        grid.add(analyzeDataLabel, 0, 1);
        grid.add(analyzeButton, 1, 1);

        layout.getChildren().addAll(backButton, grid, outputArea);
        return new Scene(layout, 600, 400);
    }

    private void searchMerchants(String query) {
        // Mockup for merchant search results
        outputArea.setText("Searching for merchants with name: " + query);
        // Add actual search logic here
    }

    private void searchDishes(String query) {
        // Mockup for dish search results
        outputArea.setText("Searching for dishes with name: " + query);
        // Add actual search logic here
    }

    private void orderDish(String dishId) {
        // Mockup for ordering dish
        outputArea.setText("Ordering dish with ID: " + dishId);
        // Add actual ordering logic here
    }

    private void manageMenu(String name, String price) {
        // Mockup for managing menu
        outputArea.setText("Adding dish " + name + " with price " + price);
        // Add actual menu management logic here
    }

    private void sendMessage(String message) {
        // Mockup for sending message
        outputArea.setText("Sending message: " + message);
        // Add actual message sending logic here
    }

    private void manageAccounts(String accountId, String action) {
        // Mockup for managing accounts
        outputArea.setText(action.substring(0, 1).toUpperCase() + action.substring(1) + " account with ID: " + accountId);
        // Add actual account management logic here
    }

    private void analyzeData() {
        // Mockup for data analysis
        outputArea.setText("Analyzing data...");
        // Add actual data analysis logic here
    }
}
