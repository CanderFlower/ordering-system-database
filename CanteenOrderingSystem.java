import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CanteenOrderingSystem extends Application {


    private Stage primaryStage;
    private Scene mainScene, userLoginScene, merchantLoginScene, adminScene;
    private TextArea outputArea;
    private String currentUserId, currentMerchantId;
    Connection connection;

    public static void main(String[] args) {
        launch(args);
    }

    static final String DB_URL = "jdbc:mysql://localhost:3306/?user=root";
    static final String user = "root";
    static final String password = "123456";

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("食堂点餐系统");

        try{
            connection = DriverManager.getConnection(DB_URL, user, password);
        }catch(Exception e){
            System.out.println("!!!");
            System.out.println(e);
            return;
        }

        initDatabase();

        initData();

        //主界面
        VBox mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(20));

        Button userButton = new Button("用户");
        userButton.setOnAction(e -> primaryStage.setScene(userLoginScene));

        Button merchantButton = new Button("商户");
        merchantButton.setOnAction(e -> primaryStage.setScene(merchantLoginScene));

        Button adminButton = new Button("管理员");
        adminButton.setOnAction(e -> primaryStage.setScene(adminScene));

        mainLayout.getChildren().addAll(new Label("选择角色"), userButton, merchantButton, adminButton);
        mainScene = new Scene(mainLayout, 300, 200);

        userLoginScene = createUserLoginScene();

        merchantLoginScene = createMerchantLoginScene();

        adminScene = createAdminScene();

        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    
    static final String initDatabasePath = "init_database.sql";
    private void initDatabase(){
        try{    
            BufferedReader reader = new BufferedReader(new FileReader(initDatabasePath));
            String line;
            StringBuilder sql = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sql.append(line);
                sql.append("\n");
            }

            Statement statement = connection.createStatement();
            System.out.println(sql.toString());
            statement.execute(sql.toString());

            System.out.println("数据库初始化完成...");
            reader.close();
        }catch(Exception e){
            System.out.println(e);
        }
    }

    static final String initUserPath= "initDataUser.txt";
    static final String initMerchantPath= "initDataMerchant.txt";
    static final String initMealPath= "initDataMeal.txt";
    private void initData(){
        initUser();
        initMerchant();
        initMeal();
    }

    private void initUser(){
        try{
            BufferedReader br = new BufferedReader(new FileReader(initUserPath));
            String buf;
            while((buf = br.readLine())!=null){
                String[] args = buf.split(" ");
                addUser(args[0], args[1], args[2], args[3]);
            }
            br.close();
        }catch(Exception e){
            System.out.println(e);
        }
    }

    private void initMerchant(){
        try{
            BufferedReader br = new BufferedReader(new FileReader(initMerchantPath));
            String buf;
            while((buf = br.readLine())!=null){
                String[] args = buf.split(" ");
                addMerchant(args[0], args[1]);
            }
            br.close();
        }catch(Exception e){
            System.out.println(e);
        }
    }

    private void initMeal(){
        try{
            BufferedReader br = new BufferedReader(new FileReader(initMealPath));
            String buf;
            while((buf = br.readLine())!=null){
                String[] args = buf.split(" ");
                String merchantID = String.valueOf(getMerchantID(args[1]));
                createDish(args[0], merchantID, args[2].equals("1"), args[3], args[4], 
                args[5], args[6]);
            }
            br.close();
        }catch(Exception e){
            System.out.println(e);
        }
    }

    private Scene createUserLoginScene() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        Button backButton = new Button("返回");
        backButton.setOnAction(e -> primaryStage.setScene(mainScene));

        Label userIdLabel = new Label("输入ID:");
        TextField userIdField = new TextField();
        Button loginButton = new Button("登录");
        loginButton.setOnAction(e -> {
            currentUserId = userIdField.getText();
            primaryStage.setScene(createUserScene());
        });

        layout.getChildren().addAll(backButton, userIdLabel, userIdField, loginButton);
        return new Scene(layout, 300, 200);
    }

    private Scene createMerchantLoginScene() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        Button backButton = new Button("返回");
        backButton.setOnAction(e -> primaryStage.setScene(mainScene));

        Label merchantIdLabel = new Label("输入商户ID:");
        TextField merchantIdField = new TextField();
        Button loginButton = new Button("登录");
        loginButton.setOnAction(e -> {
            currentMerchantId = merchantIdField.getText();
            primaryStage.setScene(createMerchantScene());
        });

        layout.getChildren().addAll(backButton, merchantIdLabel, merchantIdField, loginButton);
        return new Scene(layout, 300, 200);
    }

    private Scene createUserScene() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        Button backButton = new Button("返回");
        backButton.setOnAction(e -> primaryStage.setScene(mainScene));

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setVgap(10);
        grid.setHgap(10);

        Label accountInfoLabel = new Label("用户信息:");
        Button viewAccountInfoButton = new Button("查看");
        viewAccountInfoButton.setOnAction(e -> viewAccountInfo());

        Label searchMerchantLabel = new Label("搜索商户:");
        TextField searchMerchantField = new TextField();
        searchMerchantField.setPromptText("商户名");
        Button searchMerchantButton = new Button("搜索");
        searchMerchantButton.setOnAction(e -> searchMerchants(searchMerchantField.getText()));

        Label merchantDetailLabel = new Label("查看商户详情:");
        TextField merchantIdField = new TextField();
        merchantIdField.setPromptText("商户ID");
        Button merchantDetailButton = new Button("查看");
        merchantDetailButton.setOnAction(e -> viewMerchantDetail(merchantIdField.getText()));

        Label searchDishLabel = new Label("搜索菜品:");
        TextField searchDishField = new TextField();
        searchDishField.setPromptText("菜品名");
        TextField allergenField = new TextField();
        allergenField.setPromptText("过敏原");
        Button searchDishButton = new Button("搜索");
        searchDishButton.setOnAction(e -> searchDishes(searchDishField.getText(), allergenField.getText()));

        Label dishDetailLabel = new Label("查看菜品详情:");
        TextField dishIdField = new TextField();
        dishIdField.setPromptText("菜品ID");
        Button dishDetailButton = new Button("查看");
        dishDetailButton.setOnAction(e -> viewDishDetail(dishIdField.getText()));

        Label orderDishLabel = new Label("点餐:");
        TextField orderDishField = new TextField();
        orderDishField.setPromptText("菜品ID (用空格隔开)");
        Button orderDishButton = new Button("下单");
        orderDishButton.setOnAction(e -> orderDish(orderDishField.getText()));

        Button viewOrdersButton = new Button("查看订单");
        viewOrdersButton.setOnAction(e -> viewOrders());

        Label favoriteLabel = new Label("收藏菜品:");
        TextField favoriteDishField = new TextField();
        favoriteDishField.setPromptText("菜品ID");
        Button favoriteButton = new Button("收藏");
        favoriteButton.setOnAction(e -> favoriteDish(favoriteDishField.getText()));

        Button receiveMessagesButton = new Button("查看信息");
        receiveMessagesButton.setOnAction(e -> receiveMessages());

        Label reviewLabel = new Label("评价商户:");
        TextField reviewMerchantField = new TextField();
        TextField reviewContentField = new TextField();
        TextField reviewRatingField = new TextField();
        reviewMerchantField.setPromptText("商户ID");
        reviewContentField.setPromptText("评价内容");
        reviewRatingField.setPromptText("评分");
        Button reviewButton = new Button("提交");
        reviewButton.setOnAction(e -> submitReview(reviewMerchantField.getText(), reviewContentField.getText(), reviewRatingField.getText()));

        Label priceChangeLabel = new Label("查看价格变化:");
        TextField priceChangeDishField = new TextField();
        priceChangeDishField.setPromptText("菜品ID");
        Button priceChangeButton = new Button("查看");
        priceChangeButton.setOnAction(e -> viewPriceChange(priceChangeDishField.getText()));

        Label reservationLabel = new Label("预约商户:");
        TextField reservationMerchantField = new TextField();
        TextField reservationTimeField = new TextField();
        reservationMerchantField.setPromptText("商户ID");
        reservationTimeField.setPromptText("时间(YYYY-MM-DD)");
        Button reservationButton = new Button("预约");
        reservationButton.setOnAction(e -> reserve(reservationMerchantField.getText(), reservationTimeField.getText()));

        outputArea = new TextArea();
        outputArea.setPrefHeight(200);

        grid.add(accountInfoLabel, 0, 0);
        grid.add(viewAccountInfoButton, 1, 0);
        grid.add(searchMerchantLabel, 0, 1);
        grid.add(searchMerchantField, 1, 1);
        grid.add(searchMerchantButton, 2, 1);
        grid.add(merchantDetailLabel, 0, 2);
        grid.add(merchantIdField, 1, 2);
        grid.add(merchantDetailButton, 2, 2);
        grid.add(searchDishLabel, 0, 3);
        grid.add(searchDishField, 1, 3);
        grid.add(allergenField, 2, 3);
        grid.add(searchDishButton, 3, 3);
        grid.add(dishDetailLabel, 0, 4);
        grid.add(dishIdField, 1, 4);
        grid.add(dishDetailButton, 2, 4);
        grid.add(orderDishLabel, 0, 5);
        grid.add(orderDishField, 1, 5);
        grid.add(orderDishButton, 2, 5);
        grid.add(favoriteLabel, 0, 6);
        grid.add(favoriteDishField, 1, 6);
        grid.add(favoriteButton, 2, 6);
        grid.add(reviewLabel, 0, 7);
        grid.add(reviewMerchantField, 1, 7);
        grid.add(reviewContentField, 2, 7);
        grid.add(reviewRatingField, 3, 7);
        grid.add(reviewButton, 4, 7);
        grid.add(priceChangeLabel, 0, 8);
        grid.add(priceChangeDishField, 1, 8);
        grid.add(priceChangeButton, 2, 8);
        grid.add(reservationLabel, 0, 9);
        grid.add(reservationMerchantField, 1, 9);
        grid.add(reservationTimeField, 2, 9);
        grid.add(reservationButton, 3, 9);

        layout.getChildren().addAll(backButton, grid, viewOrdersButton, receiveMessagesButton, outputArea);
        return new Scene(layout, 800, 600);
    }

    private Scene createMerchantScene() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
    
        Button backButton = new Button("返回");
        backButton.setOnAction(e -> primaryStage.setScene(mainScene));
    
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setVgap(10);
        grid.setHgap(10);
    
        Label accountInfoLabel = new Label("账号信息:");
        Button viewAccountInfoButton = new Button("查看");
        viewAccountInfoButton.setOnAction(e -> viewMerchantAccountInfo());
    
        Label sendMessageLabel = new Label("发送消息:");
        TextField sendMessageField = new TextField();
        Button sendMessageButton = new Button("发送");
        sendMessageButton.setOnAction(e -> sendMessage(sendMessageField.getText()));
    
        Label manageMenuLabel = new Label("【↓管理菜单↓】");
    
        // 菜品名
        Label dishNameLabel = new Label("菜品名:");
        TextField dishNameField = new TextField();
    
        // 是否主打菜
        CheckBox isSpecialDishCheckBox = new CheckBox("主打菜");
    
        // 描述
        Label descriptionLabel = new Label("描述:");
        TextField descriptionField = new TextField();
    
        // 价格
        Label priceLabel = new Label("价格:");
        TextField priceField = new TextField();
    
        // 图片ID
        Label imageIdLabel = new Label("图片ID:");
        TextField imageIdField = new TextField();
    
        // 类别
        Label categoryLabel = new Label("类别:");
        ComboBox<String> categoryComboBox = new ComboBox<>();
        categoryComboBox.getItems().addAll(getCategoryList());
    
        // 创建新菜品按钮
        Button createDishButton = new Button("创建新菜品");
        createDishButton.setOnAction(e -> createDish(
            dishNameField.getText(),
            currentMerchantId,
            isSpecialDishCheckBox.isSelected(),
            descriptionField.getText(),
            priceField.getText(),
            imageIdField.getText(),
            categoryComboBox.getValue()
        ));
    
        // 修改菜品按钮
        Button updateDishButton = new Button("修改当前菜品");
        updateDishButton.setOnAction(e -> updateDish(
            dishNameField.getText(),
            currentMerchantId,
            isSpecialDishCheckBox.isSelected(),
            descriptionField.getText(),
            priceField.getText(),
            imageIdField.getText(),
            categoryComboBox.getValue()
        ));
    
        outputArea = new TextArea();
        outputArea.setPrefHeight(200);
    
        grid.add(accountInfoLabel, 0, 0);
        grid.add(viewAccountInfoButton, 1, 0);
        grid.add(sendMessageLabel, 0, 1);
        grid.add(sendMessageField, 1, 1);
        grid.add(sendMessageButton, 2, 1);
    
        grid.add(manageMenuLabel, 0, 2);
        grid.add(dishNameLabel, 0, 3);
        grid.add(dishNameField, 1, 3);
        grid.add(isSpecialDishCheckBox, 1, 4);
        grid.add(descriptionLabel, 0, 5);
        grid.add(descriptionField, 1, 5);
        grid.add(priceLabel, 0, 6);
        grid.add(priceField, 1, 6);
        grid.add(imageIdLabel, 0, 7);
        grid.add(imageIdField, 1, 7);
        grid.add(categoryLabel, 0, 8);
        grid.add(categoryComboBox, 1, 8);
        grid.add(createDishButton, 0, 9);
        grid.add(updateDishButton, 1, 9);
    
        layout.getChildren().addAll(backButton, grid, outputArea);
        return new Scene(layout, 800, 600);
    }
    
    private Scene createAdminScene() {
    VBox layout = new VBox(10);
    layout.setPadding(new Insets(10));

    Button backButton = new Button("返回");
    backButton.setOnAction(e -> primaryStage.setScene(mainScene));

    GridPane grid = new GridPane();
    grid.setPadding(new Insets(10));
    grid.setVgap(10);
    grid.setHgap(10);

    // 新增商户
    Label addMerchantLabel = new Label("新增商户:");
    TextField merchantNameField = new TextField();
    merchantNameField.setPromptText("名称");
    TextField merchantAddressField = new TextField();
    merchantAddressField.setPromptText("地址");
    Button addMerchantButton = new Button("添加");
    addMerchantButton.setOnAction(e -> addMerchant(
        merchantNameField.getText(), 
        merchantAddressField.getText()
    ));

    // 删除商户
    Label deleteMerchantLabel = new Label("删除商户:");
    TextField deleteMerchantField = new TextField();
    deleteMerchantField.setPromptText("商户ID");
    Button deleteMerchantButton = new Button("删除");
    deleteMerchantButton.setOnAction(e -> deleteMerchant(deleteMerchantField.getText()));

    // 新增用户
    Label addUserLabel = new Label("新增用户:");
    TextField userNameField = new TextField();
    userNameField.setPromptText("名称");
    ComboBox<String> userGenderComboBox = new ComboBox<>();
    userGenderComboBox.getItems().addAll("男", "女");
    DatePicker userBirthDatePicker = new DatePicker();
    TextField userIDField = new TextField();
    userIDField.setPromptText("学工号");
    Button addUserButton = new Button("添加");
    addUserButton.setOnAction(e -> {
        try{
            addUser(
            userNameField.getText(), 
            userGenderComboBox.getValue(),
            userBirthDatePicker.getValue().toString(), 
            userIDField.getText()
            );
        }catch(SQLException exception){
            System.out.println(exception);
        }
    });

    // 删除用户
    Label deleteUserLabel = new Label("删除用户:");
    TextField deleteUserField = new TextField();
    deleteUserField.setPromptText("用户ID");
    Button deleteUserButton = new Button("删除");
    deleteUserButton.setOnAction(e -> deleteUser(deleteUserField.getText()));

    // 添加其余标签和字段
    Label analyzeDishLabel = new Label("分析菜品数据:");
    TextField analyzeDishField = new TextField();
    analyzeDishField.setPromptText("菜品ID");
    Button analyzeDishButton = new Button("分析");
    analyzeDishButton.setOnAction(e -> analyzeDishData(analyzeDishField.getText()));

    Label filterFavoritesLabel = new Label("过滤最少收藏菜品:");
    TextField filterFavoritesField = new TextField();
    filterFavoritesField.setPromptText("最少收藏数");
    Button filterFavoritesButton = new Button("过滤");
    filterFavoritesButton.setOnAction(e -> filterFavorites(filterFavoritesField.getText()));

    Label loyalCustomerLabel = new Label("忠实用户分析:");
    TextField loyalCustomerField = new TextField();
    loyalCustomerField.setPromptText("商户ID");
    Button loyalCustomerButton = new Button("分析");
    loyalCustomerButton.setOnAction(e -> analyzeLoyalCustomers(loyalCustomerField.getText()));

    Label userActivityLabel = new Label("用户行为分析:");
    TextField userActivityField = new TextField();
    userActivityField.setPromptText("时间段(YYYY-MM)");
    Button userActivityButton = new Button("分析");
    userActivityButton.setOnAction(e -> analyzeUserActivity(userActivityField.getText()));

    Label userGroupLabel = new Label("用户群体特征:");
    Label roleLabel = new Label("角色:");
    ComboBox<String> roleComboBox = new ComboBox<>();
    roleComboBox.getItems().addAll("老师", "学生", "职工");

    Label ageRangeLabel = new Label("年龄范围:");
    TextField ageRangeField = new TextField();
    ageRangeField.setPromptText("minAge-maxAge");

    Label genderLabel = new Label("性别:");
    ComboBox<String> genderComboBox = new ComboBox<>();
    genderComboBox.getItems().addAll("男", "女");

    Button userGroupButton = new Button("分析");
    userGroupButton.setOnAction(e -> analyzeUserGroup(
        roleComboBox.getValue(),
        ageRangeField.getText(),
        genderComboBox.getValue()
    ));

    Label hotSellingDishLabel = new Label("热销菜品分析:");
    TextField hotSellingDishField = new TextField();
    hotSellingDishField.setPromptText("商户ID");
    Button hotSellingDishButton = new Button("分析");
    hotSellingDishButton.setOnAction(e -> analyzeHotSellingDishes(hotSellingDishField.getText()));

    outputArea = new TextArea();
    outputArea.setPrefHeight(200);

    // 将所有元素添加到网格中
    grid.add(addMerchantLabel, 0, 0);
    grid.add(merchantNameField, 1, 0);
    grid.add(merchantAddressField, 2, 0);
    grid.add(addMerchantButton, 3, 0);

    grid.add(deleteMerchantLabel, 0, 1);
    grid.add(deleteMerchantField, 1, 1);
    grid.add(deleteMerchantButton, 2, 1);

    grid.add(addUserLabel, 0, 2);
    grid.add(userNameField, 1, 2);
    grid.add(userGenderComboBox, 2, 2);
    grid.add(userBirthDatePicker, 3, 2);
    grid.add(userIDField, 4, 2);
    grid.add(addUserButton, 5, 2);

    grid.add(deleteUserLabel, 0, 3);
    grid.add(deleteUserField, 1, 3);
    grid.add(deleteUserButton, 2, 3);

    grid.add(analyzeDishLabel, 0, 4);
    grid.add(analyzeDishField, 1, 4);
    grid.add(analyzeDishButton, 2, 4);
    grid.add(filterFavoritesLabel, 0, 5);
    grid.add(filterFavoritesField, 1, 5);
    grid.add(filterFavoritesButton, 2, 5);
    grid.add(loyalCustomerLabel, 0, 6);
    grid.add(loyalCustomerField, 1, 6);
    grid.add(loyalCustomerButton, 2, 6);
    grid.add(userActivityLabel, 0, 7);
    grid.add(userActivityField, 1, 7);
    grid.add(userActivityButton, 2, 7);
    grid.add(userGroupLabel, 0, 8);
    grid.add(roleLabel, 0, 9);
    grid.add(roleComboBox, 1, 9);
    grid.add(ageRangeLabel, 0, 10);
    grid.add(ageRangeField, 1, 10);
    grid.add(genderLabel, 0, 11);
    grid.add(genderComboBox, 1, 11);
    grid.add(userGroupButton, 2, 12);
    grid.add(hotSellingDishLabel, 0, 13);
    grid.add(hotSellingDishField, 1, 13);
    grid.add(hotSellingDishButton, 2, 13);

    layout.getChildren().addAll(backButton, grid, outputArea);
    return new Scene(layout, 800, 600);
}

    private void viewAccountInfo() {
        outputArea.setText("TODO: Display user account information for user ID: " + currentUserId);
    }

    private void searchMerchants(String merchantName) {
        outputArea.setText("TODO: Search merchants by name: " + merchantName);
    }

    private void viewMerchantDetail(String merchantId) {
        outputArea.setText("TODO: Display merchant details for merchant ID: " + merchantId);
    }

    private void searchDishes(String dishName, String allergen) {
        outputArea.setText("TODO: Search dishes by name: " + dishName + " avoiding allergen: " + allergen);
    }

    private void viewDishDetail(String dishId) {
        outputArea.setText("TODO: Display dish details for dish ID: " + dishId);
    }

    private void orderDish(String dishIds) {
        outputArea.setText("TODO: Order dishes with IDs: " + dishIds);
    }

    private void viewOrders() {
        outputArea.setText("TODO: Display all orders for user ID: " + currentUserId);
    }

    private void favoriteDish(String dishId) {
        outputArea.setText("Favorite dish ID: " + dishId + " added successfully.");
    }

    private void receiveMessages() {
        outputArea.setText("TODO: Display all messages for user ID: " + currentUserId);
    }

    private void submitReview(String merchantId, String reviewContent, String rating) {
        outputArea.setText("Review for merchant ID: " + merchantId + " submitted successfully.\nRating: " + rating + "\nContent: " + reviewContent);
    }

    private void viewPriceChange(String dishId) {
        outputArea.setText("TODO: Display price change history for dish ID: " + dishId);
    }

    private void reserve(String merchantId, String time) {
        outputArea.setText("Reservation for merchant ID: " + merchantId + " at time: " + time + " made successfully.");
    }

    private void viewMerchantAccountInfo() {
        outputArea.setText("TODO: Display merchant account information for merchant ID: " + currentMerchantId);
    }

    private void sendMessage(String message) {
        outputArea.setText("Message sent successfully: " + message);
    }

    private List<String> getCategoryList(){
        List<String> list = new ArrayList<>();
        return list;
    }

    private int getMerchantID(String merchantName){
        //todo
        return 0;
    }

    private void createDish(String name, String merchantID, boolean isSpecial, String description, String price, String imageId, String category) {
        outputArea.appendText("创建新菜品: 名称=" + name + ", 主打菜=" + isSpecial + ", 描述=" + description + ", 价格=" + price + ", 图片ID=" + imageId + ", 类别=" + category + "\n");
        // 此处添加创建新菜品的逻辑
    }
    
    private void updateDish(String name, String merchantID, boolean isSpecial, String description, String price, String imageId, String category) {
        outputArea.appendText("修改当前菜品: 名称=" + name + ", 主打菜=" + isSpecial + ", 描述=" + description + ", 价格=" + price + ", 图片ID=" + imageId + ", 类别=" + category + "\n");
        // 此处添加修改菜品的逻辑
    }

    private boolean hasOutputArea(){
        TextArea outputArea = (TextArea) primaryStage.getScene().lookup("#outputArea");

        if (outputArea != null && primaryStage.getScene().getRoot().equals(outputArea.getParent())) {
            return true;
        } else {
            return false;
        }

    }
    
    static final String addUserString = "INSERT INTO 用户 VALUES(?,?,?,?);";
    private void addUser(String userName, String userGender, String birthDate, String userPersonID) throws SQLException {
        PreparedStatement addUserStatement  = connection.prepareStatement(addUserString);
        addUserStatement.setString(1, userName);
        addUserStatement.setString(2, userGender);
        addUserStatement.setString(3, birthDate);
        addUserStatement.setString(4, userPersonID);
        addUserStatement.executeUpdate();
        if(hasOutputArea())
            outputArea.setText("User added successfully: " + userName);
    }

    private void deleteUser(String userId) {
        outputArea.setText("User deleted successfully: " + userId);
    }

    private void addMerchant(String merchantName, String merchantAddress){

    }

    private void deleteMerchant(String merchantID){

    }

    private void analyzeDishData(String dishId) {
        outputArea.setText("TODO: Analyze dish data for dish ID: " + dishId);
    }

    private void filterFavorites(String minFavorites) {
        outputArea.setText("TODO: Filter dishes with at least " + minFavorites + " favorites.");
    }

    private void analyzeLoyalCustomers(String merchantId) {
        outputArea.setText("TODO: Analyze loyal customers for merchant ID: " + merchantId);
    }

    private void analyzeUserActivity(String timeRange) {
        outputArea.setText("TODO: Analyze user activity for time range: " + timeRange);
    }


    private void analyzeUserGroup(String role, String ageRange, String gender) {
        outputArea.appendText("用户群体特征分析: 角色=" + role + ", 年龄范围=" + ageRange + ", 性别=" + gender + "\n");
        // 此处添加用户群体特征分析的逻辑
    }

    private void analyzeHotSellingDishes(String merchantId) {
        outputArea.setText("TODO: Analyze hot selling dishes for merchant ID: " + merchantId);
    }
}
