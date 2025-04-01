package com.calculatrice;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.ArrayList;
import java.util.List;

public class CalculatriceConvertisseur extends Application {
    private Label display;
    private String currentInput = "";
    private String operator = "";
    private double firstOperand = 0;
    private boolean isDarkMode = false;
    private List<String> history = new ArrayList<>();
    private ListView<String> historyView;

    @Override
    public void start(Stage primaryStage) {
        // Create display
        display = new Label("0");
        display.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-padding: 20px;");
        display.setFont(Font.font("System", FontWeight.LIGHT, 48));
        display.setAlignment(Pos.CENTER_RIGHT);
        display.setPrefWidth(300);
        display.setMinHeight(100);

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setStyle("-fx-background-color: black;");

        String[][] buttons = {
                {"AC", "⌫", "%", "÷"},
                {"7", "8", "9", "×"},
                {"4", "5", "6", "-"},
                {"1", "2", "3", "+"},
                {"0", ".", "=", "Conv"}
        };

        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[i].length; j++) {
                Button button = new Button(buttons[i][j]);
                button.setFont(Font.font("System", FontWeight.LIGHT, 24));
                button.setPrefSize(70, 70);
                button.setStyle(getButtonStyle(buttons[i][j]));
                
                // Add click animation
                button.setOnMousePressed(e -> {
                    ScaleTransition st = new ScaleTransition(Duration.millis(100), button);
                    st.setToX(0.95);
                    st.setToY(0.95);
                    st.playFromStart();
                });
                
                button.setOnMouseReleased(e -> {
                    ScaleTransition st = new ScaleTransition(Duration.millis(100), button);
                    st.setToX(1.0);
                    st.setToY(1.0);
                    st.playFromStart();
                });
                
                button.setOnAction(e -> handleButtonPress(button.getText()));
                grid.add(button, j, i);
            }
        }

        // Make the zero button span two columns
        Button zeroButton = (Button) grid.getChildren().stream()
                .filter(node -> node instanceof Button && ((Button) node).getText().equals("0"))
                .findFirst()
                .orElse(null);
        if (zeroButton != null) {
            GridPane.setColumnSpan(zeroButton, 2);
            zeroButton.setPrefWidth(150);
        }

        // Create history view
        historyView = new ListView<>();
        historyView.setPrefHeight(100);
        historyView.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-font-size: 14px;");
        historyView.setItems(FXCollections.observableArrayList(history));

        VBox root = new VBox(0, display, historyView, grid);
        root.setStyle("-fx-background-color: black;");
        Scene scene = new Scene(root, 320, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("iPhone Calculator");
        primaryStage.show();
    }

    private String getButtonStyle(String text) {
        String baseStyle = "-fx-background-radius: 35; -fx-border-radius: 35; " +
                          "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 5, 0, 0, 1);";
        switch (text) {
            case "AC":
            case "⌫":
            case "%":
                return baseStyle + "-fx-background-color: #A5A5A5; -fx-text-fill: black;";
            case "÷":
            case "×":
            case "-":
            case "+":
            case "=":
                return baseStyle + "-fx-background-color: #FF9F0A; -fx-text-fill: white;";
            case "Conv":
                return baseStyle + "-fx-background-color: #4CAF50; -fx-text-fill: white;";
            default:
                return baseStyle + "-fx-background-color: #333333; -fx-text-fill: white;";
        }
    }

    private void handleButtonPress(String text) {
        switch (text) {
            case "AC":
                currentInput = "";
                firstOperand = 0;
                operator = "";
                display.setText("0");
                break;
            case "⌫":
                if (!currentInput.isEmpty()) {
                    currentInput = currentInput.substring(0, currentInput.length() - 1);
                    display.setText(currentInput.isEmpty() ? "0" : currentInput);
                }
                break;
            case "%":
                if (!currentInput.isEmpty()) {
                    double value = Double.parseDouble(currentInput);
                    currentInput = String.valueOf(value / 100);
                    display.setText(currentInput);
                }
                break;
            case "=":
                computeResult();
                break;
            case "Conv":
                openConversionWindow();
                break;
            case "÷":
            case "×":
            case "-":
            case "+":
                if (!currentInput.isEmpty()) {
                    firstOperand = Double.parseDouble(currentInput);
                    operator = text.equals("÷") ? "/" : 
                              text.equals("×") ? "*" : text;
                    currentInput = "";
                }
                break;
            default:
                if (currentInput.equals("0") && !text.equals(".")) {
                    currentInput = text;
                } else {
                    currentInput += text;
                }
                display.setText(currentInput);
        }
    }

    private void openConversionWindow() {
        Stage conversionStage = new Stage();
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #f0f0f0;");

        // Create tabs for different conversion types
        TabPane tabPane = new TabPane();
        tabPane.setStyle("-fx-background-color: #f0f0f0;");
        
        // Currency Conversion Tab
        Tab currencyTab = new Tab("Currency");
        VBox currencyLayout = new VBox(15);
        currencyLayout.setPadding(new Insets(20));
        currencyLayout.setStyle("-fx-background-color: #f0f0f0;");
        
        Label titleLabel = new Label("Currency Converter");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #333333;");
        
        TextField amountField = new TextField();
        amountField.setPromptText("Enter amount");
        amountField.setStyle("-fx-font-size: 16px; -fx-padding: 10px; -fx-background-radius: 10px;");
        
        ComboBox<String> fromCurrency = new ComboBox<>();
        fromCurrency.getItems().addAll("USD", "EUR", "XOF");
        fromCurrency.setPromptText("From Currency");
        fromCurrency.setStyle("-fx-font-size: 16px; -fx-background-radius: 10px;");
        
        ComboBox<String> toCurrency = new ComboBox<>();
        toCurrency.getItems().addAll("USD", "EUR", "XOF");
        toCurrency.setPromptText("To Currency");
        toCurrency.setStyle("-fx-font-size: 16px; -fx-background-radius: 10px;");
        
        Button convertButton = new Button("Convert");
        convertButton.setStyle("-fx-background-color: #007AFF; -fx-text-fill: white; -fx-font-size: 16px; " +
                             "-fx-padding: 10px; -fx-background-radius: 10px;");
        
        Label resultLabel = new Label("Result: ");
        resultLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #333333;");
        
        convertButton.setOnAction(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText());
                String from = fromCurrency.getValue();
                String to = toCurrency.getValue();
                
                if (from == null || to == null) {
                    resultLabel.setText("Please select currencies!");
                    return;
                }
                
                double result = convertCurrency(amount, from, to);
                String resultText = String.format("%.2f %s = %.2f %s", amount, from, result, to);
                resultLabel.setText(resultText);
                addToHistory(resultText);
            } catch (NumberFormatException ex) {
                resultLabel.setText("Invalid input!");
            }
        });
        
        currencyLayout.getChildren().addAll(
            titleLabel,
            amountField,
            fromCurrency,
            toCurrency,
            convertButton,
            resultLabel
        );
        currencyTab.setContent(currencyLayout);
        
        // Length Conversion Tab
        Tab lengthTab = new Tab("Length");
        VBox lengthLayout = new VBox(15);
        lengthLayout.setPadding(new Insets(20));
        lengthLayout.setStyle("-fx-background-color: #f0f0f0;");
        
        Label lengthTitleLabel = new Label("Length Converter");
        lengthTitleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #333333;");
        
        TextField lengthField = new TextField();
        lengthField.setPromptText("Enter length");
        lengthField.setStyle("-fx-font-size: 16px; -fx-padding: 10px; -fx-background-radius: 10px;");
        
        ComboBox<String> fromLength = new ComboBox<>();
        fromLength.getItems().addAll("Meters", "Kilometers", "Miles", "Feet");
        fromLength.setPromptText("From Unit");
        fromLength.setStyle("-fx-font-size: 16px; -fx-background-radius: 10px;");
        
        ComboBox<String> toLength = new ComboBox<>();
        toLength.getItems().addAll("Meters", "Kilometers", "Miles", "Feet");
        toLength.setPromptText("To Unit");
        toLength.setStyle("-fx-font-size: 16px; -fx-background-radius: 10px;");
        
        Button convertLengthButton = new Button("Convert");
        convertLengthButton.setStyle("-fx-background-color: #007AFF; -fx-text-fill: white; -fx-font-size: 16px; " +
                                   "-fx-padding: 10px; -fx-background-radius: 10px;");
        
        Label lengthResultLabel = new Label("Result: ");
        lengthResultLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #333333;");
        
        convertLengthButton.setOnAction(e -> {
            try {
                double length = Double.parseDouble(lengthField.getText());
                String from = fromLength.getValue();
                String to = toLength.getValue();
                
                if (from == null || to == null) {
                    lengthResultLabel.setText("Please select units!");
                    return;
                }
                
                double result = convertLength(length, from, to);
                String resultText = String.format("%.2f %s = %.2f %s", length, from, result, to);
                lengthResultLabel.setText(resultText);
                addToHistory(resultText);
            } catch (NumberFormatException ex) {
                lengthResultLabel.setText("Invalid input!");
            }
        });
        
        lengthLayout.getChildren().addAll(
            lengthTitleLabel,
            lengthField,
            fromLength,
            toLength,
            convertLengthButton,
            lengthResultLabel
        );
        lengthTab.setContent(lengthLayout);
        
        tabPane.getTabs().addAll(currencyTab, lengthTab);
        layout.getChildren().add(tabPane);
        
        Scene scene = new Scene(layout, 400, 400);
        conversionStage.setScene(scene);
        conversionStage.setTitle("Converter");
        conversionStage.show();
    }

    private double convertCurrency(double amount, String from, String to) {
        // Base rates (as of 2024)
        double usdToEur = 0.92;
        double usdToXof = 655.957;
        double eurToXof = 656.0;
        
        // Convert to USD first (as base currency)
        double amountInUSD;
        switch (from) {
            case "EUR":
                amountInUSD = amount / usdToEur;
                break;
            case "XOF":
                amountInUSD = amount / usdToXof;
                break;
            default: // USD
                amountInUSD = amount;
                break;
        }
        
        // Convert from USD to target currency
        switch (to) {
            case "EUR":
                return amountInUSD * usdToEur;
            case "XOF":
                return amountInUSD * usdToXof;
            default: // USD
                return amountInUSD;
        }
    }

    private double convertLength(double length, String from, String to) {
        // Convert to meters first (as base unit)
        double lengthInMeters;
        switch (from) {
            case "Kilometers":
                lengthInMeters = length * 1000;
                break;
            case "Miles":
                lengthInMeters = length * 1609.34;
                break;
            case "Feet":
                lengthInMeters = length * 0.3048;
                break;
            default: // Meters
                lengthInMeters = length;
                break;
        }
        
        // Convert from meters to target unit
        switch (to) {
            case "Kilometers":
                return lengthInMeters / 1000;
            case "Miles":
                return lengthInMeters / 1609.34;
            case "Feet":
                return lengthInMeters / 0.3048;
            default: // Meters
                return lengthInMeters;
        }
    }

    private void addToHistory(String calculation) {
        history.add(0, calculation);
        if (history.size() > 5) {
            history.remove(history.size() - 1);
        }
        historyView.setItems(FXCollections.observableArrayList(history));
    }

    private void computeResult() {
        if (!currentInput.isEmpty() && !operator.isEmpty()) {
            double secondOperand = Double.parseDouble(currentInput);
            double result = 0;
            switch (operator) {
                case "+": result = firstOperand + secondOperand; break;
                case "-": result = firstOperand - secondOperand; break;
                case "*": result = firstOperand * secondOperand; break;
                case "/": result = (secondOperand == 0) ? Double.NaN : firstOperand / secondOperand; break;
            }
            String resultText = String.format("%.2f %s %.2f = %.2f", firstOperand, operator, secondOperand, result);
            display.setText(String.valueOf(result));
            addToHistory(resultText);
            currentInput = "";
            operator = "";
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
