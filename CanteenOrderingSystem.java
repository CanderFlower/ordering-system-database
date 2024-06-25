import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
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
            Statement statement = connection.createStatement();
            while ((line = reader.readLine()) != null) {
                sql.append(line);
                if(line.endsWith(";")){
                    statement.execute(sql.toString());
                    sql = new StringBuilder();
                }
                //sql.append("\n");
            }

            //statement.execute(sql.toString());

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
            System.out.println("用户初始数据加载完成...");
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
            System.out.println("商户初始数据加载完成...");
        }catch(Exception e){
            System.out.println(e);
        }
    }

    private void initMeal(){
        try{
            try (BufferedReader br = new BufferedReader(new FileReader(initMealPath))) {
                String buf;
                while((buf = br.readLine())!=null){
                    String[] args = buf.split(" ");
                    int merchantID = getMerchantID(args[1]);
                    if(merchantID==-1){
                        System.out.println("没找到商家!");
                        return;
                    }
                    createDish(args[0], String.valueOf(merchantID), args[2].equals("1"), args[3], args[4], 
                    args[5], args[6], args.length>7?args[7]:null);
                }
                br.close();
            }
        }catch(Exception e){
            System.out.println(e);
        }
        System.out.println("菜品初始数据加载完成...");
    }

    private Scene createUserLoginScene() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        Button backButton = new Button("返回");
        backButton.setOnAction(e -> primaryStage.setScene(mainScene));

        Label userIdLabel = new Label("输入ID:");
        TextField userIdField = new TextField();
        Button loginButton = new Button("登录");
        Label userLoginMessage = new Label();
        loginButton.setOnAction(e -> {
            currentUserId = userIdField.getText();
            try{
                if(currentUserId.equals("")||!userIDExists(currentUserId)){
                    userLoginMessage.setText("没有该用户!");
                }else{
                    primaryStage.setScene(createUserScene());
                }
            }catch(Exception err){
                System.out.println(err);
            }
        });

        layout.getChildren().addAll(backButton, userIdLabel, userIdField, loginButton, userLoginMessage);
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
        Label merchantLoginMessage = new Label();
        loginButton.setOnAction(e -> {
            currentMerchantId = merchantIdField.getText();
            try{
                if(currentMerchantId.equals("")||!merchantIDExists(currentMerchantId)){
                    merchantLoginMessage.setText("没有该商家!");
                }else{
                    primaryStage.setScene(createMerchantScene());
                }
            }catch(Exception err){
                System.out.println(err);
            }
        });

        layout.getChildren().addAll(backButton, merchantIdLabel, merchantIdField, loginButton,merchantLoginMessage);
        return new Scene(layout, 300, 200);
    }
    
    private Scene createUserScene() throws SQLException {
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
        viewAccountInfoButton.setOnAction(e -> {
            try {
                viewAccountInfo();
            } catch (SQLException e1) {
                System.out.println(e1);
            }
        });
    
        Label searchMerchantLabel = new Label("搜索商户:");
        TextField searchMerchantField = new TextField();
        searchMerchantField.setPromptText("商户名");
        Button searchMerchantButton = new Button("搜索");
        searchMerchantButton.setOnAction(e -> {
            try {
                searchMerchants(searchMerchantField.getText());
            } catch (SQLException e1) {
                System.out.println(e1);
            }
        });
    
        Label merchantDetailLabel = new Label("查看商户详情:");
        TextField merchantIdField = new TextField();
        merchantIdField.setPromptText("商户名称");
        Button merchantDetailButton = new Button("查看");
        merchantDetailButton.setOnAction(e -> {
            try {
                viewMerchantDetail(merchantIdField.getText());
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
    
        Label searchDishLabel = new Label("搜索菜品:");
        TextField searchDishField = new TextField();
        searchDishField.setPromptText("菜品名");
        TextField allergenField = new TextField();
        allergenField.setPromptText("过敏原(空格隔开)");
        Button searchDishButton = new Button("搜索");
        searchDishButton.setOnAction(e -> {
            try {
                searchDishes(searchDishField.getText(), allergenField.getText());
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
    
        Label dishDetailLabel = new Label("查看菜品详情:");
        TextField dishIdField = new TextField();
        dishIdField.setPromptText("菜品ID");
        Button dishDetailButton = new Button("查看");
        dishDetailButton.setOnAction(e -> {
            try {
                viewDishDetail(dishIdField.getText());
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
    
        Label orderDishLabelOnline = new Label("在线点餐:");
        TextField orderDishFieldOnline = new TextField();
        orderDishFieldOnline.setPromptText("菜品ID (用-隔开)");
        Button orderDishButtonOnline = new Button("下单");
        orderDishButtonOnline.setOnAction(e -> {
            try {
                if(orderDishButtonOnline.getText().equals(""))
                    return;
                orderDishOnline(orderDishFieldOnline.getText());
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
    
        Label orderDishLabelQueue = new Label("排队点餐:");
        TextField orderDishFieldQueue = new TextField();
        orderDishFieldQueue.setPromptText("菜品ID (用-隔开)");
        Button orderDishButtonQueue = new Button("下单");
        orderDishButtonQueue.setOnAction(e -> {
            try {
                if(orderDishFieldQueue.getText().equals(""))
                    return;
                orderDishQueue(orderDishFieldQueue.getText());
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
    
        Button viewOrdersButton = new Button("查看订单");
        viewOrdersButton.setOnAction(e -> {
            try {
                viewOrders();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
    
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
        grid.add(orderDishLabelOnline, 0, 5);
        grid.add(orderDishFieldOnline, 1, 5);
        grid.add(orderDishButtonOnline, 2, 5);
        grid.add(orderDishLabelQueue, 0, 6);
        grid.add(orderDishFieldQueue, 1, 6);
        grid.add(orderDishButtonQueue, 2, 6);
        grid.add(favoriteLabel, 0, 7);
        grid.add(favoriteDishField, 1, 7);
        grid.add(favoriteButton, 2, 7);
        grid.add(reviewLabel, 0, 8);
        grid.add(reviewMerchantField, 1, 8);
        grid.add(reviewContentField, 2, 8);
        grid.add(reviewRatingField, 3, 8);
        grid.add(reviewButton, 4, 8);
        grid.add(priceChangeLabel, 0, 9);
        grid.add(priceChangeDishField, 1, 9);
        grid.add(priceChangeButton, 2, 9);
        grid.add(reservationLabel, 0, 10);
        grid.add(reservationMerchantField, 1, 10);
        grid.add(reservationTimeField, 2, 10);
        grid.add(reservationButton, 3, 10);
    
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
        grid.setVgap(13);
        grid.setHgap(13);
    
        Label accountInfoLabel = new Label("账号信息:");
        Button viewAccountInfoButton = new Button("查看");
        viewAccountInfoButton.setOnAction(e -> {
            try {
                viewMerchantAccountInfo();
            } catch (SQLException e1) {
                System.out.println(e1);
            }
        });
    
        Label sendMessageLabel = new Label("发送消息:");
        TextField sendMessageField = new TextField();
        sendMessageField.setPromptText("消息内容");
        TextField sendToField = new TextField();
        sendToField.setPromptText("用户id");
        Button sendMessageButton = new Button("发送");
        sendMessageButton.setOnAction(e -> {
                try{
                    sendMessage(sendMessageField.getText(), sendToField.getText());
                }catch(SQLException e1){
                    System.out.println(e1);
                }
            });
    
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

        Label allergenLabel = new Label("过敏原:");
        TextField allergenField = new TextField();
        allergenField.setPromptText("用-隔开");
    
        // 创建新菜品按钮
        Button createDishButton = new Button("创建新菜品");
        createDishButton.setOnAction(e -> {
            try{createDish(
            dishNameField.getText(),
            currentMerchantId,
            isSpecialDishCheckBox.isSelected(),
            descriptionField.getText(),
            priceField.getText(),
            imageIdField.getText(),
            categoryComboBox.getValue(),
            allergenField.getText()
            );}catch(SQLException e1){
                System.out.println(e1);
            }
        });
    
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
        grid.add(sendToField, 2, 1);
        grid.add(sendMessageButton, 3, 1);
    
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
        grid.add(allergenLabel, 0, 9);
        grid.add(allergenField, 1, 9);
        grid.add(createDishButton, 0, 10);
        grid.add(updateDishButton, 1, 10);
    
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

        outputArea = new TextArea();
        outputArea.setPrefHeight(200);

        // 新增商户
        Label addMerchantLabel = new Label("新增商户:");
        TextField merchantNameField = new TextField();
        merchantNameField.setPromptText("名称");
        TextField merchantAddressField = new TextField();
        merchantAddressField.setPromptText("地址");
        Button addMerchantButton = new Button("添加");
        addMerchantButton.setOnAction(e -> {
            try{
                addMerchant(
                    merchantNameField.getText(), 
                    merchantAddressField.getText()
                    );
                outputArea.setText("商户ID: "+getMerchantID(merchantNameField.getText())+" 添加成功!");
                //System.out.println("1111");
            }catch(Exception exc){
                System.out.println(exc);
            }
        });

        // 删除商户
        Label deleteMerchantLabel = new Label("删除商户:");
        TextField deleteMerchantField = new TextField();
        deleteMerchantField.setPromptText("商户名称");
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
            }catch(Exception exception){
                System.out.println(exception);
            }
        });

        // 删除用户
        Label deleteUserLabel = new Label("删除用户:");
        TextField deleteUserField = new TextField();
        deleteUserField.setPromptText("用户名称");
        Button deleteUserButton = new Button("删除");
        deleteUserButton.setOnAction(e -> deleteUser(deleteUserField.getText()));

        // 添加其余标签和字段
        Label analyzeDishLabel = new Label("分析菜品数据:");
        TextField analyzeDishField = new TextField();
        analyzeDishField.setPromptText("菜品名称");
        Button analyzeDishButton = new Button("分析");
        analyzeDishButton.setOnAction(e -> analyzeDishData(analyzeDishField.getText()));

        Label filterFavoritesLabel = new Label("过滤最少收藏菜品:");
        TextField filterFavoritesField = new TextField();
        filterFavoritesField.setPromptText("最少收藏数");
        Button filterFavoritesButton = new Button("过滤");
        filterFavoritesButton.setOnAction(e -> filterFavorites(filterFavoritesField.getText()));

        Label loyalCustomerLabel = new Label("忠实用户分析:");
        TextField loyalCustomerField = new TextField();
        loyalCustomerField.setPromptText("商户名称");
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
        return new Scene(layout, 950, 700);
}

    private boolean userIDExists(String userID)throws Exception{
        PreparedStatement userIDExistsStatement = connection.prepareStatement(viewAccountInfoString);
        userIDExistsStatement.setInt(1, Integer.parseInt(userID));
        ResultSet res = userIDExistsStatement.executeQuery();
        if(res.isBeforeFirst())
            return true;
        else
            return false;
    }

    private boolean merchantIDExists(String merchantID)throws Exception{
        PreparedStatement merchantIDExistsStatement = connection.prepareStatement(viewMerchantAccountInfoString);
        merchantIDExistsStatement.setInt(1, Integer.parseInt(merchantID));
        ResultSet res = merchantIDExistsStatement.executeQuery();
        if(res.isBeforeFirst())
            return true;
        else
            return false;
    }

    static final String viewAccountInfoString = "SELECT * FROM 用户 WHERE id=?;";
    private void viewAccountInfo() throws SQLException{
        StringBuilder sb = new StringBuilder();
        PreparedStatement viewAccountInfoStatement = connection.prepareStatement(viewAccountInfoString);
        viewAccountInfoStatement.setInt(1, Integer.parseInt(currentUserId));
        ResultSet res = viewAccountInfoStatement.executeQuery();
        res.next();
        sb.append("UserID: "+res.getString("id")+"\n");
        sb.append("名称: "+res.getString("名称")+"\n");
        sb.append("出生日期: "+res.getString("出生日期")+"\n");
        sb.append("学工号: "+res.getString("学工号")+"\n");
        outputArea.setText(sb.toString());
        res.close();
        viewAccountInfoStatement.close();
    }

    static final String searchMerchantsString = "SELECT * FROM 商户 WHERE 名称 LIKE ?;";
    private void searchMerchants(String merchantName) throws SQLException{
        StringBuilder sb = new StringBuilder();
        PreparedStatement searchMerchantsStatement = connection.prepareStatement(searchMerchantsString);
        searchMerchantsStatement.setString(1, "%"+merchantName+"%");
        ResultSet res = searchMerchantsStatement.executeQuery();
        while(res.next()){
            sb.append("商户ID: "+res.getString("id")+"\t");
            sb.append("名称: "+res.getString("名称")+"\t");
            sb.append("地址: "+res.getString("地址")+"\n");
        }
        outputArea.setText(sb.toString());
        res.close();
        searchMerchantsStatement.close();
    }

    private Scene createDishScene(ResultSet res)throws Exception{
        
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        Button backButton = new Button("返回");
        backButton.setOnAction(e -> {
            try {
                primaryStage.setScene(createUserScene());
            } catch (SQLException e1) {
                System.out.println(e1);
            }
        });

        layout.getChildren().add(backButton);

        while(res.next()) {
            HBox dishBox = new HBox(10);
            dishBox.setPadding(new Insets(5));

            String imagePath = "/pic/"+res.getInt("图片编号")+".png";
            ImageView dishImageView = new ImageView(new Image(imagePath));
            dishImageView.setFitWidth(100);
            dishImageView.setFitHeight(100);

            VBox dishDetails = new VBox(5);
            Label idLabel = new Label("ID: " + res.getInt("id"));
            Label nameLabel = new Label("名称: " + res.getString("名称"));
            Label mainDishLabel = new Label("是否主打菜: " + (res.getBoolean("是否主打菜") ? "是" : "否"));
            Label descriptionLabel = new Label("描述: " + res.getString("描述"));
            Label priceLabel = new Label("价格: " + res.getDouble("价格"));
            Label favoritesLabel = new Label("收藏量: " + res.getInt("收藏量"));
            Label onlineSalesLabel = new Label("在线销量: " + res.getInt("在线销量"));
            Label queueSalesLabel = new Label("排队销量: " + res.getInt("排队销量"));

            String categoryName = getCategoryName(res.getInt("类别_id"));
            Label categoryLabel = new Label("类别: " + categoryName);
            Label ratingLabel = new Label("评分: " + res.getDouble("评分"));

            List<String> allergens = getAllergens(res.getString("名称"));
            Label allergenLabel = new Label("过敏原: "+((allergens.isEmpty())?"无":allergens));

            dishDetails.getChildren().addAll(
                idLabel, nameLabel, mainDishLabel, descriptionLabel, priceLabel, 
                favoritesLabel, onlineSalesLabel, queueSalesLabel, categoryLabel, ratingLabel, allergenLabel
            );

            dishBox.getChildren().addAll(dishImageView, dishDetails);
            layout.getChildren().add(dishBox);
        }

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(layout);
        scrollPane.setFitToWidth(true);
        return new Scene(scrollPane, 800, 600);
    }

    static final String viewMerchantDetailString = "SELECT * FROM 菜品 WHERE id IN (SELECT 菜品_id FROM 菜品属于商户 WHERE 商户_id=?);";
    private void viewMerchantDetail(String merchantName) throws Exception{
        PreparedStatement viewMerchantDetailStatement = connection.prepareStatement(viewMerchantDetailString);
        int merchantID = getMerchantID(merchantName);
        if(merchantID==-1){
            outputArea.setText("没有该商家\n");
            return;
        }
        viewMerchantDetailStatement.setInt(1, merchantID);
        ResultSet res = viewMerchantDetailStatement.executeQuery();

        primaryStage.setScene(createDishScene(res));
    }

    static final String getAllergenIDString = "SELECT id FROM 过敏原 WHERE 名称=?;";
    private int getAllergenID(String allergen)throws SQLException{
        PreparedStatement getAllergenIPreparedStatement = connection.prepareStatement(getAllergenIDString);
        getAllergenIPreparedStatement.setString(1, allergen);
        ResultSet res = getAllergenIPreparedStatement.executeQuery();
        if(!res.isBeforeFirst())
            return -1;
        res.next();
        return res.getInt("id");
    }

    static final String createAllergenString = "INSERT INTO 过敏原(名称) VALUES(?);";
    private void createAllergen(String allergen)throws SQLException{
        PreparedStatement createAllergStatement = connection.prepareStatement(createAllergenString);
        createAllergStatement.setString(1, allergen);
        createAllergStatement.executeUpdate();
        createAllergStatement.close();
    }

    static final String createDishAllergenString = "INSERT INTO 菜品包含过敏原(菜品_id,过敏原_id) VALUES(?,?) ;";
    private void createDishAllergen(String dishName, String allergen) throws SQLException{
        int allergenID;
        if((allergenID = getAllergenID(allergen))==-1){
            createAllergen(allergen);
            allergenID = getAllergenID(allergen);
        }
        PreparedStatement createDishAllergenStatement = connection.prepareStatement(createDishAllergenString);
        createDishAllergenStatement.setInt(1, getDishID(dishName));
        createDishAllergenStatement.setInt(2, allergenID);
        createDishAllergenStatement.executeUpdate();
        createDishAllergenStatement.close();
    }

    static final String getAllergensString = "SELECT 名称 FROM 过敏原 WHERE id IN (SELECT 过敏原_id FROM 菜品包含过敏原 WHERE 菜品_id=?);";
    private List<String> getAllergens(String dishName)throws SQLException{
        List<String> allergens = new ArrayList<>();
        int dishID = getDishID(dishName);
        PreparedStatement getAllergensStatement = connection.prepareStatement(getAllergensString);
        getAllergensStatement.setInt(1, dishID);
        ResultSet res = getAllergensStatement.executeQuery();
        while(res.next())
            allergens.add(res.getString("名称"));
        return allergens;
    }

    private void createDishAllergens(String dishName, String allergens)throws SQLException{
        String[] allergenList = allergens.split("-");
        for(String allergen:allergenList)
            createDishAllergen(dishName, allergen);
    }
    
    static final String searchDishesPre = "SELECT * FROM 菜品 WHERE 名称 LIKE ? AND id NOT IN(SELECT DISTINCT 菜品.id FROM 菜品 LEFT JOIN 菜品包含过敏原 ON 菜品.id = 菜品包含过敏原.菜品_id LEFT JOIN 过敏原 ON 菜品包含过敏原.过敏原_id = 过敏原.id WHERE 过敏原.名称 IS NOT NULL AND 过敏原.名称 IN (\'占位\'";
    private void searchDishes(String dishName, String allergenList) throws Exception{
        StringBuilder sb = new StringBuilder(searchDishesPre);
        if(!allergenList.equals("")){
            String[] allergens = allergenList.split(" ");
            for(String allergen : allergens)
                sb.append(",\'"+allergen+"\'");
        }
        sb.append("));");
        String searchDishesString = sb.toString();
        //System.out.println(searchDishesString);
        PreparedStatement searchDishesStatement = connection.prepareStatement(searchDishesString);
        searchDishesStatement.setString(1, "%"+dishName+"%");
        ResultSet res = searchDishesStatement.executeQuery();
        if(!res.isBeforeFirst()){
            outputArea.setText("没有结果!");
            return;
        }
        primaryStage.setScene(createDishScene(res));
    }

    static final String viewDishDetailString = "SELECT * FROM 菜品 WHERE id=?;";
    private void viewDishDetail(String dishId) throws Exception{
        PreparedStatement viewDishDetailStatement = connection.prepareStatement(viewDishDetailString);
        viewDishDetailStatement.setInt(1, Integer.parseInt(dishId));
        ResultSet res = viewDishDetailStatement.executeQuery();
        if(!res.isBeforeFirst()){
            outputArea.setText("没有这一菜品!");
            return;
        }
        primaryStage.setScene(createDishScene(res));
    }

    static final String createOnlineOrderString = "INSERT INTO 在线订单(创建时间,是否完成) VALUES(CURRENT_TIMESTAMP,false);";
    static final String createOnlineOrderUserString = "INSERT INTO 用户创建在线订单(用户_id,在线订单_id) VALUES(?,?);";
    static final String createOnlineOrderDishString = "INSERT INTO 订单包含菜品(在线订单_id,菜品_id) VALUES(?,?);";
    private void orderDishOnline(String dishIdList)throws Exception{
        PreparedStatement orderDishOnlineStatement = connection.prepareStatement(createOnlineOrderString, Statement.RETURN_GENERATED_KEYS);
        orderDishOnlineStatement.executeUpdate();
        ResultSet generatedKey = orderDishOnlineStatement.getGeneratedKeys();
        generatedKey.next();
        int key = generatedKey.getInt(1);
        orderDishOnlineStatement.close();

        PreparedStatement createOnlineOrderUserStatement = connection.prepareStatement(createOnlineOrderUserString);
        createOnlineOrderUserStatement.setInt(1, Integer.parseInt(currentUserId));
        createOnlineOrderUserStatement.setInt(2, key);
        createOnlineOrderUserStatement.executeUpdate();
        createOnlineOrderUserStatement.close();

        String[] dishIds = dishIdList.split(" ");
        for(String dishId : dishIds){
            int dish = Integer.parseInt(dishId);
            PreparedStatement createOnlineOrderDishStatement = connection.prepareStatement(createOnlineOrderDishString);
            createOnlineOrderDishStatement.setInt(1, key);
            createOnlineOrderDishStatement.setInt(2, dish);
            createOnlineOrderDishStatement.executeUpdate();
            createOnlineOrderDishStatement.close();
        }
    }

    static final String createQueueOrderString = "INSERT INTO 排队订单(创建时间,是否完成) VALUES(CURRENT_TIMESTAMP,false);";
    static final String createQueueOrderUserString = "INSERT INTO 用户创建排队订单(用户_id,排队订单_id) VALUES(?,?);";
    static final String createQueueOrderDishString = "INSERT INTO 订单包含菜品(排队订单_id,菜品_id) VALUES(?,?);";
    private void orderDishQueue(String dishIdList)throws Exception{
        PreparedStatement orderDishQueueStatement = connection.prepareStatement(createQueueOrderString, Statement.RETURN_GENERATED_KEYS);
        orderDishQueueStatement.executeUpdate();
        ResultSet generatedKey = orderDishQueueStatement.getGeneratedKeys();
        generatedKey.next();
        int key = generatedKey.getInt(1);
        orderDishQueueStatement.close();

        PreparedStatement createQueueOrderUserStatement = connection.prepareStatement(createQueueOrderUserString);
        createQueueOrderUserStatement.setInt(1, Integer.parseInt(currentUserId));
        createQueueOrderUserStatement.setInt(2, key);
        createQueueOrderUserStatement.executeUpdate();
        createQueueOrderUserStatement.close();

        String[] dishIds = dishIdList.split(" ");
        for(String dishId : dishIds){
            int dish = Integer.parseInt(dishId);
            PreparedStatement createQueueOrderDishStatement = connection.prepareStatement(createQueueOrderDishString);
            createQueueOrderDishStatement.setInt(1, key);
            createQueueOrderDishStatement.setInt(2, dish);
            createQueueOrderDishStatement.executeUpdate();
            createQueueOrderDishStatement.close();
        }
    }

    static final String viewOnlineOrdersString = "SELECT 在线订单.* FROM 用户创建在线订单 LEFT JOIN 在线订单 ON 用户创建在线订单.订单_id=在线订单.id WHERE 用户创建在线订单.用户id=?;";
    static final String viewQueueOrdersString = "SELECT 排队订单.* FROM 用户创建排队订单 LEFT JOIN 排队订单 ON 用户创建排队订单.订单_id=排队订单.id WHERE 用户创建排队订单.用户id=?;";
    private void viewOrders() throws Exception{
        StringBuilder sb = new StringBuilder();
        PreparedStatement viewOnlineOrderStatement = connection.prepareStatement(viewOnlineOrdersString);
        viewOnlineOrderStatement.setInt(1, Integer.parseInt(currentUserId));
        ResultSet res = viewOnlineOrderStatement.executeQuery();
        while(res.next()){
            sb.append("在线订单["+res.getInt("id")+"],");
            sb.append("创建时间: "+res.getTime("创建时间")+", ");
            sb.append(res.getBoolean("是否完成")?"已完成":"未完成"+"\n");
        }
        res.close();
        PreparedStatement viewQueueOrderStatement = connection.prepareStatement(viewQueueOrdersString);
        viewQueueOrderStatement.setInt(1, Integer.parseInt(currentUserId));
        res = viewQueueOrderStatement.executeQuery();
        while(res.next()){
            sb.append("排队订单["+res.getInt("id")+"],");
            sb.append("创建时间: "+res.getTime("创建时间")+", ");
            sb.append(res.getBoolean("是否完成")?"已完成":"未完成"+"\n");
        }
        res.close();
        outputArea.setText(sb.toString());
    }

    static final String insertFavoriteDishString = "INSERT INTO `用户收藏菜品` (`用户_id`, `菜品_id`) VALUES (?, ?);";
    private void favoriteDish(String dishId) {
        try {
            PreparedStatement insertFavoriteDishStatement = connection.prepareStatement(insertFavoriteDishString);

            insertFavoriteDishStatement.setInt(1, Integer.parseInt(currentUserId));
            insertFavoriteDishStatement.setInt(2, Integer.parseInt(dishId));
            insertFavoriteDishStatement.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
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

    static final String viewMerchantAccountInfoString = "SELECT * FROM 商户 WHERE id=?";
    private void viewMerchantAccountInfo() throws SQLException{
        StringBuilder sb = new StringBuilder();
        PreparedStatement viewMerchantAccountInfoStatement = connection.prepareStatement(viewMerchantAccountInfoString);
        viewMerchantAccountInfoStatement.setInt(1, Integer.parseInt(currentMerchantId));
        ResultSet res = viewMerchantAccountInfoStatement.executeQuery();
        res.next();
        sb.append("商户ID: "+res.getString("id")+"\n");
        sb.append("名称: "+res.getString("名称")+"\n");
        sb.append("地址: "+res.getString("地址")+"\n");
        outputArea.setText(sb.toString());
        res.close();
        viewMerchantAccountInfoStatement.close();
    }

    static private final String sendMessageString = "INSERT INTO 消息(内容,用户_id) VALUES(?,?);";
    private void sendMessage(String message, String userID) throws SQLException{
        PreparedStatement sendMessageStatement = connection.prepareStatement(sendMessageString);
        sendMessageStatement.setString(1, message); 
        sendMessageStatement.setInt(2, Integer.parseInt(userID));
        sendMessageStatement.executeUpdate();
        outputArea.setText("Message sent successfully: " + message);
    }

    static private final String getCategoryListString = "SELECT 名称 FROM 类别;";
    private List<String> getCategoryList(){
        try{
            List<String> list = new ArrayList<>();
            Statement getCategoryListStatement = connection.createStatement();
            ResultSet res = getCategoryListStatement.executeQuery(getCategoryListString);
            while(res.next()){
                list.add(res.getString("名称"));
            }
            return list;
        }catch(SQLException e1){
            System.out.println(e1);
            return null;
        }
    }
    
    static private final String getCategoryIdString = "SELECT id FROM 类别 WHERE 名称=?;";
    private int getCategoryId(String categoryName)throws SQLException{
        PreparedStatement getCategoryStatement = connection.prepareStatement(getCategoryIdString);
        getCategoryStatement.setString(1, categoryName);
        ResultSet res = getCategoryStatement.executeQuery();
        if(!res.isBeforeFirst())
            return -1;
        res.next();
        return res.getInt("id");
    }

    static private final String getCategoryNameString = "SELECT 名称 FROM 类别 WHERE id=?;";
    private String getCategoryName(int categoryID)throws SQLException{
        PreparedStatement getCategoryNameStatement = connection.prepareStatement(getCategoryNameString);
        getCategoryNameStatement.setInt(1, categoryID);
        ResultSet res = getCategoryNameStatement.executeQuery();
        if(!res.isBeforeFirst())
            return null;
        res.next();
        return res.getString("名称");
    }
    
    static private String getMerchantIDString = "SELECT id FROM 商户 WHERE 名称=?;";
    private int getMerchantID(String merchantName)throws Exception{
        PreparedStatement getMerchantIDStatement = connection.prepareStatement(getMerchantIDString);
        getMerchantIDStatement.setString(1, merchantName);
        ResultSet res = getMerchantIDStatement.executeQuery();
        if(!res.isBeforeFirst())
            return -1;
        res.next();
        return res.getInt("id");
    }

    static private String getUserIDString = "SELECT id FROM 用户 WHERE 名称=?;";
    private int getuserID(String userName)throws Exception{
        PreparedStatement getUserIDStatement = connection.prepareStatement(getUserIDString);
        getUserIDStatement.setString(1, userName);
        ResultSet res = getUserIDStatement.executeQuery();
        if(!res.isBeforeFirst())
            return -1;
        res.next();
        return res.getInt("id");
    }

    static private String createCategoryString = "INSERT INTO 类别(名称) VALUES(?);";
    private void createCategory(String categoryName)throws SQLException{
        PreparedStatement createCategoryStatement = connection.prepareStatement(createCategoryString);
        createCategoryStatement.setString(1, categoryName);
        createCategoryStatement.executeUpdate();
        createCategoryStatement.close();
    }
    
    static private String getDishIDString = "SELECT id FROM 菜品 WHERE 名称=?;";
    private int getDishID(String dishName)throws SQLException{
        PreparedStatement getDishIDStatement = connection.prepareStatement(getDishIDString);
        getDishIDStatement.setString(1, dishName);
        ResultSet res = getDishIDStatement.executeQuery();
        if(!res.isBeforeFirst())
            return -1;
        res.next();
        return res.getInt("id");
    }
    
    static private String createDishString = "INSERT INTO 菜品(名称,是否主打菜,描述,价格,图片编号,类别_id) VALUES(?,?,?,?,?,?);";
    static private String createDishMerchantString = "INSERT INTO 菜品属于商户(菜品_id,商户_id) VALUES(?,?);";
    private void createDish(String name, String merchantID, boolean isSpecial, String description, String price, String imageId, String category, String allergens) throws SQLException{
        if(getCategoryId(category)==-1)
            createCategory(category);
        int categoryID = getCategoryId(category);
        PreparedStatement createDishStatement = connection.prepareStatement(createDishString);
        createDishStatement.setString(1, name);
        createDishStatement.setBoolean(2, isSpecial);
        createDishStatement.setString(3, description);
        createDishStatement.setDouble(4, Double.parseDouble(price));
        createDishStatement.setDouble(5, Integer.parseInt(imageId));
        createDishStatement.setInt(6, categoryID);
        createDishStatement.executeUpdate();
        createDishStatement.close();

        PreparedStatement createDishMerchantStatement = connection.prepareStatement(createDishMerchantString);
        createDishMerchantStatement.setInt(1, getDishID(name));
        createDishMerchantStatement.setInt(2, Integer.parseInt(merchantID));
        createDishMerchantStatement.executeUpdate();
        createDishMerchantStatement.close();

        if(allergens!=null)
            createDishAllergens(name, allergens);
    }
    
    private void updateDish(String name, String merchantID, boolean isSpecial, String description, String price, String imageId, String category) {
        
    }

    
    static final String addUserString = "INSERT INTO 用户 (名称,性别,出生日期,学工号) VALUES(?,?,?,?);";
    private void addUser(String userName, String userGender, String birthDate, String userPersonID) throws Exception {
        PreparedStatement addUserStatement  = connection.prepareStatement(addUserString);
        addUserStatement.setString(1, userName);
        addUserStatement.setString(2, userGender);
        addUserStatement.setString(3, birthDate);
        addUserStatement.setString(4, userPersonID);
        addUserStatement.executeUpdate();
        addUserStatement.close();
        if(outputArea!=null)
            outputArea.setText("用户ID为 "+getuserID(userName)+" 添加成功!");
    }

    static final String deleteUserString = "DELETE FROM 用户 WHERE 名称=?";
    private void deleteUser(String userName) {
        try{
            PreparedStatement deleteUserStatement = connection.prepareStatement(deleteUserString);
            deleteUserStatement.setString(1, userName);
            deleteUserStatement.executeUpdate();
            deleteUserStatement.close();
        }catch(SQLException e){
            outputArea.setText("Error!");
        }
        if(outputArea!=null)
            outputArea.setText("用户已删除: " + userName);
    }

    static final String addMerchantString = "INSERT INTO 商户 (名称,地址) VALUES(?,?);";
    private void addMerchant(String merchantName, String merchantAddress)throws SQLException{
        PreparedStatement addMerchantStatement = connection.prepareStatement(addMerchantString);
        addMerchantStatement.setString(1, merchantName);
        addMerchantStatement.setString(2, merchantAddress);
        addMerchantStatement.executeUpdate();
        addMerchantStatement.close();
    }

    static final String deleteMerchantString = "DELETE FROM 商户 WHERE 名称=?";
    private void deleteMerchant(String merchantName){
        try{
            PreparedStatement deleteMerchantStatement = connection.prepareStatement(deleteMerchantString);
            deleteMerchantStatement.setString(1, merchantName);
            deleteMerchantStatement.executeUpdate();
            deleteMerchantStatement.close();
        }catch(SQLException e){
            outputArea.setText("Error!");
        }
        if(outputArea!=null)
            outputArea.setText("商户已删除: " + merchantName);
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
