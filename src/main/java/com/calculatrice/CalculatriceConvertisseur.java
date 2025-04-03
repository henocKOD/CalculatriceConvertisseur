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
    private Label operationDisplay;
    private String currentInput = "";
    private String operator = "";
    private double firstOperand = 0;
    private boolean isDarkMode = false;
    private List<String> history = new ArrayList<>();
    private ListView<String> historyView;
    private Stage historyStage;

    @Override
    public void start(Stage primaryStage) {
        // Create main display
        VBox displayBox = new VBox(5);
        displayBox.setStyle("-fx-background-color: black;");
        
        operationDisplay = new Label("");
        operationDisplay.setStyle("-fx-text-fill: #666666; -fx-font-size: 24px;");
        operationDisplay.setAlignment(Pos.CENTER_RIGHT);
        operationDisplay.setPrefWidth(300);
        
        display = new Label("0");
        display.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-padding: 20px;");
        display.setFont(Font.font("System", FontWeight.LIGHT, 48));
        display.setAlignment(Pos.CENTER_RIGHT);
        display.setPrefWidth(300);
        display.setMinHeight(100);
        
        displayBox.getChildren().addAll(operationDisplay, display);

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

        // Create History button
        Button historyButton = new Button("Historique");
        historyButton.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-size: 16px; " +
                             "-fx-background-radius: 5px; -fx-padding: 5px 10px;");
        historyButton.setOnAction(e -> showHistory());

        VBox root = new VBox(0, historyButton, displayBox, grid);
        root.setStyle("-fx-background-color: black;");
        Scene scene = new Scene(root, 320, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Calculatrice iPhone");
        primaryStage.show();
    }

    private void showHistory() {
        if (historyStage == null) {
            historyStage = new Stage();
            historyStage.setTitle("Historique des Calculs");
            
            VBox historyLayout = new VBox(10);
            historyLayout.setPadding(new Insets(20));
            historyLayout.setStyle("-fx-background-color: #1c1c1c;");
            
            Label historyLabel = new Label("Historique des Calculs");
            historyLabel.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");
            
            historyView = new ListView<>();
            historyView.setPrefHeight(400);
            historyView.setPrefWidth(300);
            historyView.setStyle("-fx-background-color: #1c1c1c; -fx-text-fill: white; -fx-font-size: 14px;");
            historyView.setItems(FXCollections.observableArrayList(history));
            
            Button clearHistoryButton = new Button("Effacer l'Historique");
            clearHistoryButton.setStyle("-fx-background-color: #FF3B30; -fx-text-fill: white; -fx-font-size: 14px; " +
                                      "-fx-padding: 8px; -fx-background-radius: 5px;");
            clearHistoryButton.setOnAction(e -> {
                history.clear();
                historyView.setItems(FXCollections.observableArrayList(history));
            });
            
            historyLayout.getChildren().addAll(historyLabel, historyView, clearHistoryButton);
            
            Scene scene = new Scene(historyLayout, 340, 500);
            historyStage.setScene(scene);
            historyStage.setResizable(false);
        }
        
        historyStage.show();
    }

    private void updateOperationDisplay() {
        if (!operator.isEmpty()) {
            operationDisplay.setText(String.format("%d %s %s", (int)firstOperand, operator, currentInput));
        } else {
            operationDisplay.setText(currentInput);
        }
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
                operationDisplay.setText("");
                break;
            case "⌫":
                if (!currentInput.isEmpty()) {
                    currentInput = currentInput.substring(0, currentInput.length() - 1);
                    display.setText(currentInput.isEmpty() ? "0" : currentInput);
                    updateOperationDisplay();
                }
                break;
            case "%":
                if (!currentInput.isEmpty()) {
                    int value = Integer.parseInt(currentInput);
                    currentInput = String.valueOf(value / 100);
                    display.setText(currentInput);
                    updateOperationDisplay();
                }
                break;
            case "÷":
            case "×":
            case "-":
            case "+":
                if (!currentInput.isEmpty()) {
                    if (!operator.isEmpty()) {
                        computeResult();
                    }
                    firstOperand = Integer.parseInt(currentInput);
                    operator = text;
                    currentInput = "";
                    updateOperationDisplay();
                }
                break;
            case "=":
                computeResult();
                break;
            case "Conv":
                openConversionWindow();
                break;
            case ".":
                // Ignore decimal point for integer-only calculator
                break;
            default:
                if (currentInput.equals("0")) {
                    currentInput = text;
                } else {
                    currentInput += text;
                }
                display.setText(currentInput);
                updateOperationDisplay();
        }
    }

    private void openConversionWindow() {
        Stage conversionStage = new Stage();
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #f0f0f0;");
        layout.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Convertisseur de Devises");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #333333;");
        
        Label subtitleLabel = new Label("XOF = Franc CFA (Communauté Financière Africaine)");
        subtitleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666666;");
        
        Label rateLabel = new Label("Taux actuel : 1 EUR = 655,96 XOF");
        rateLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #007AFF; -fx-font-weight: bold;");
        
        TextField amountField = new TextField();
        amountField.setPromptText("Entrez le montant");
        amountField.setStyle("-fx-font-size: 16px; -fx-padding: 10px; -fx-background-radius: 10px;");
        amountField.setMaxWidth(300);
        
        ComboBox<String> fromCurrency = new ComboBox<>();
        fromCurrency.getItems().addAll("EUR", "XOF");
        fromCurrency.setPromptText("Devise d'origine");
        fromCurrency.setStyle("-fx-font-size: 16px; -fx-background-radius: 10px;");
        fromCurrency.setMaxWidth(300);
        
        ComboBox<String> toCurrency = new ComboBox<>();
        toCurrency.getItems().addAll("EUR", "XOF");
        toCurrency.setPromptText("Devise de destination");
        toCurrency.setStyle("-fx-font-size: 16px; -fx-background-radius: 10px;");
        toCurrency.setMaxWidth(300);
        
        Button convertButton = new Button("Convertir");
        convertButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 16px; " +
                             "-fx-padding: 10px 20px; -fx-background-radius: 10px; " +
                             "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 5, 0, 0, 1);");
        convertButton.setMaxWidth(300);
        
        Label resultLabel = new Label("Résultat : ");
        resultLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #333333;");
        
        convertButton.setOnAction(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText().replace(",", "."));
                String from = fromCurrency.getValue();
                String to = toCurrency.getValue();
                
                if (from == null || to == null) {
                    resultLabel.setText("Veuillez sélectionner les devises !");
                    return;
                }
                
                double result = convertCurrency(amount, from, to);
                String resultText = String.format("%.2f %s = %.2f %s", amount, from, result, to);
                resultLabel.setText("Résultat : " + resultText.replace(".", ","));
                addToHistory(resultText.replace(".", ","));
            } catch (NumberFormatException ex) {
                resultLabel.setText("Entrée invalide !");
            }
        });
        
        layout.getChildren().addAll(
            titleLabel,
            subtitleLabel,
            rateLabel,
            amountField,
            fromCurrency,
            toCurrency,
            convertButton,
            resultLabel
        );
        
        Scene scene = new Scene(layout, 400, 450);
        conversionStage.setScene(scene);
        conversionStage.setTitle("Convertisseur de Devises");
        conversionStage.setResizable(false);
        conversionStage.show();
    }

    private double convertCurrency(double amount, String from, String to) {
        // Updated exchange rates (as of 2024)
        double[][] rates = {
            {1.0, 655.96},      // EUR rates (1 EUR = 655.96 XOF)
            {0.001524, 1.0}     // XOF rates (1 XOF = 0.001524 EUR)
        };
        
        int fromIndex = from.equals("EUR") ? 0 : 1;
        int toIndex = to.equals("EUR") ? 0 : 1;
        
        return amount * rates[fromIndex][toIndex];
    }

    private void addToHistory(String calculation) {
        history.add(0, calculation);
        if (history.size() > 5) {
            history.remove(history.size() - 1);
        }
        if (historyView != null) {
            historyView.setItems(FXCollections.observableArrayList(history));
        }
    }

    private void computeResult() {
        if (!currentInput.isEmpty() && !operator.isEmpty()) {
            int secondOperand = Integer.parseInt(currentInput);
            int result = 0;
            switch (operator) {
                case "+":
                    result = (int)firstOperand + secondOperand;
                    break;
                case "-":
                    result = (int)firstOperand - secondOperand;
                    break;
                case "×":
                    result = (int)firstOperand * secondOperand;
                    break;
                case "÷":
                    if (secondOperand == 0) {
                        display.setText("Error");
                        operationDisplay.setText("");
                        currentInput = "";
                        operator = "";
                        return;
                    }
                    result = (int)firstOperand / secondOperand;
                    break;
            }
            String resultText = String.format("%d %s %d = %d", (int)firstOperand, operator, secondOperand, result);
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
