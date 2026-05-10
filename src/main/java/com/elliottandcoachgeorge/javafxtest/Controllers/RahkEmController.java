package com.elliottandcoachgeorge.javafxtest.Controllers;

import com.elliottandcoachgeorge.javafxtest.GameMode;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

public class RahkEmController {

    // =========================================================
    // GRID LABELS
    // =========================================================
    @FXML private Label r0c0; @FXML private Label r0c1; @FXML private Label r0c2; @FXML private Label r0c3; @FXML private Label r0c4;
    @FXML private Label r1c0; @FXML private Label r1c1; @FXML private Label r1c2; @FXML private Label r1c3; @FXML private Label r1c4;
    @FXML private Label r2c0; @FXML private Label r2c1; @FXML private Label r2c2; @FXML private Label r2c3; @FXML private Label r2c4;
    @FXML private Label r3c0; @FXML private Label r3c1; @FXML private Label r3c2; @FXML private Label r3c3; @FXML private Label r3c4;
    @FXML private Label r4c0; @FXML private Label r4c1; @FXML private Label r4c2; @FXML private Label r4c3; @FXML private Label r4c4;
    @FXML private Label r5c0; @FXML private Label r5c1; @FXML private Label r5c2; @FXML private Label r5c3; @FXML private Label r5c4;

    // =========================================================
    // KEYBOARD BUTTONS
    // =========================================================
    @FXML private Button qButton; @FXML private Button wButton; @FXML private Button eButton;
    @FXML private Button rButton; @FXML private Button tButton; @FXML private Button yButton;
    @FXML private Button uButton; @FXML private Button iButton; @FXML private Button oButton;
    @FXML private Button pButton;
    @FXML private Button aButton; @FXML private Button sButton; @FXML private Button dButton;
    @FXML private Button fButton; @FXML private Button gButton; @FXML private Button hButton;
    @FXML private Button jButton; @FXML private Button kButton; @FXML private Button lButton;
    @FXML private Button zButton; @FXML private Button xButton; @FXML private Button cButton;
    @FXML private Button vButton; @FXML private Button bButton; @FXML private Button nButton;
    @FXML private Button mButton;

    // =========================================================
    // SIDE BUTTONS
    // =========================================================
    @FXML private Button submitButton;
    @FXML private Button clearButton;
    @FXML private Button restartButton;
    @FXML private Button exitButton;
    @FXML private Button backButton;
    @FXML private ImageView logoImage;
    @FXML private BorderPane rootPane;

    // =========================================================
    // GAME VARIABLES
    // =========================================================
    private Label[][] board;
    private final HashMap<String, Button> keyboardMap = new HashMap<>();
    private int currentRow = 0;
    private int currentCol = 0;
    private String[] WORDS;
    private final HashSet<String> dictionary = new HashSet<>();
    private String targetWord;
    private boolean hasSubmitted = false;
    private GameMode gameMode = GameMode.FREE_PLAY;
    private Runnable backCallback;
    private String currentTheme = "Dark";

    // =========================================================
    // SETTERS
    // =========================================================
    public void setGameMode(GameMode mode) {
        this.gameMode = mode;
        if (mode == GameMode.FREE_PLAY) {
            targetWord = getRandomWord();
        } else {
            targetWord = getDailyWord();
        }
        setupBoardStyle();
    }

    public void setBackCallback(Runnable callback) {
        this.backCallback = callback;
    }

    public void applyThemeExternal(String theme) {
        currentTheme = theme;
        applyTheme(theme);
        setupBoardStyle();
    }

    // =========================================================
    // INITIALIZE
    // =========================================================
    @FXML
    public void initialize() {
        board = new Label[][]{
                {r0c0, r0c1, r0c2, r0c3, r0c4},
                {r1c0, r1c1, r1c2, r1c3, r1c4},
                {r2c0, r2c1, r2c2, r2c3, r2c4},
                {r3c0, r3c1, r3c2, r3c3, r3c4},
                {r4c0, r4c1, r4c2, r4c3, r4c4},
                {r5c0, r5c1, r5c2, r5c3, r5c4}
        };

        setupKeyboardMap();
        loadWords();
        targetWord = getRandomWord();

        submitButton.setOnAction(e -> submitGuess());
        clearButton.setOnAction(e -> clearRow());
        restartButton.setOnAction(e -> restartGame());
        exitButton.setOnAction(e -> System.exit(0));
        backButton.setOnAction(e -> handleBack());

        rootPane.addEventFilter(KeyEvent.KEY_TYPED, this::handleKeyTyped);
        rootPane.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                submitGuess();
            }
            if (e.getCode() == KeyCode.BACK_SPACE) {
                deleteLetter();
            }
        });

        rootPane.setFocusTraversable(true);
        Platform.runLater(() -> rootPane.requestFocus());
    }

    // =========================================================
    // KEYBOARD MAP
    // =========================================================
    private void setupKeyboardMap() {
        keyboardMap.put("Q", qButton);
        keyboardMap.put("W", wButton);
        keyboardMap.put("E", eButton);
        keyboardMap.put("R", rButton);
        keyboardMap.put("T", tButton);
        keyboardMap.put("Y", yButton);
        keyboardMap.put("U", uButton);
        keyboardMap.put("I", iButton);
        keyboardMap.put("O", oButton);
        keyboardMap.put("P", pButton);
        keyboardMap.put("A", aButton);
        keyboardMap.put("S", sButton);
        keyboardMap.put("D", dButton);
        keyboardMap.put("F", fButton);
        keyboardMap.put("G", gButton);
        keyboardMap.put("H", hButton);
        keyboardMap.put("J", jButton);
        keyboardMap.put("K", kButton);
        keyboardMap.put("L", lButton);
        keyboardMap.put("Z", zButton);
        keyboardMap.put("X", xButton);
        keyboardMap.put("C", cButton);
        keyboardMap.put("V", vButton);
        keyboardMap.put("B", bButton);
        keyboardMap.put("N", nButton);
        keyboardMap.put("M", mButton);
    }

    // =========================================================
    // BOARD STYLE — no borders
    // =========================================================
    private void setupBoardStyle() {
        if (board == null) return;
        String backgroundColor = getThemeTileColor();
        for (int r = 0; r < 6; r++) {
            for (int c = 0; c < 5; c++) {
                board[r][c].setStyle(
                        "-fx-background-color:" + backgroundColor + ";" +
                                "-fx-background-radius:6;" +
                                "-fx-font-size:34;" +
                                "-fx-font-family:'Arial';" +
                                "-fx-font-weight:bold;" +
                                "-fx-text-fill:white;" +
                                "-fx-alignment:center;"
                );
            }
        }
    }

    private String getThemeTileColor() {
        switch (currentTheme) {
            case "WSA": return "#0a1a5c";
            case "Dark":     return "#2f2f2f";
            case "Light":    return "#d3d6da";
            case "Blue":     return "#4d79ff";
            case "Ocean":    return "#006994";
            case "Raven":    return "#3b3b58";
            case "Coach":    return "#8B0000";
            case "Willow":   return "#4f7942";
            case "Fuschia":  return "#c154c1";
            case "Bell":     return "#d4af37";
            case "The Four": return "#6a5acd";
            case "Storm":    return "#708090";
            case "Orange":   return "#ff8c00";
            default:         return "#2f2f2f";
        }
    }

    // =========================================================
    // BACK BUTTON
    // =========================================================
    @FXML
    private void handleBack() {
        if (backCallback != null) {
            backCallback.run();
        }
    }

    // =========================================================
    // LOAD WORDS
    // =========================================================
    private void loadWords() {
        ArrayList<String> wordList = new ArrayList<>();
        try {
            InputStream inputStream = getClass().getResourceAsStream("/words.txt");
            if (inputStream == null) {
                wordList.add("LUCAS");
                dictionary.add("LUCAS");
            } else {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim().toUpperCase();
                    if (line.length() == 5) {
                        wordList.add(line);
                        dictionary.add(line);
                    }
                }
                reader.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            wordList.add("LUCAS");
            dictionary.add("LUCAS");
        }
        WORDS = wordList.toArray(new String[0]);
    }

    // =========================================================
    // WORD SELECTION
    // =========================================================
    private String getRandomWord() {
        if (WORDS == null || WORDS.length == 0) {
            return "LUCAS";
        }
        return WORDS[new Random().nextInt(WORDS.length)];
    }

    private String getDailyWord() {
        if (WORDS == null || WORDS.length == 0) {
            return "LUCAS";
        }
        LocalDate start = LocalDate.of(2024, 1, 1);
        long days = ChronoUnit.DAYS.between(start, LocalDate.now());
        int index = (int) (Math.abs(days) % WORDS.length);
        return WORDS[index];
    }

    // =========================================================
    // INPUT
    // =========================================================
    private void handleKeyTyped(KeyEvent event) {
        String character = event.getCharacter().toUpperCase();
        if (!character.matches("[A-Z]")) {
            event.consume();
            return;
        }
        addLetter(character);
    }

    private void addLetter(String letter) {
        if (currentCol < 5) {
            Label tile = board[currentRow][currentCol];
            tile.setText(letter);
            playPopAnimation(tile);
            currentCol++;
        }
    }

    private void deleteLetter() {
        if (currentCol > 0) {
            currentCol--;
            board[currentRow][currentCol].setText("");
        }
    }

    private void clearRow() {
        for (int i = 0; i < 5; i++) {
            board[currentRow][i].setText("");
        }
        currentCol = 0;
    }

    // =========================================================
    // POP ANIMATION
    // =========================================================
    private void playPopAnimation(Label tile) {
        ScaleTransition pop = new ScaleTransition(Duration.millis(100), tile);
        pop.setFromX(1.0);
        pop.setFromY(1.0);
        pop.setToX(1.15);
        pop.setToY(1.15);
        pop.setAutoReverse(true);
        pop.setCycleCount(2);
        pop.play();
    }

    // =========================================================
    // BOUNCE ROW
    // =========================================================
    private void shakeRow(int row) {
        SequentialTransition sequence = new SequentialTransition();

        for (int c = 0; c < 5; c++) {
            Label tile = board[row][c];

            TranslateTransition bounceUp = new TranslateTransition(Duration.millis(80), tile);
            bounceUp.setByY(-18);

            TranslateTransition bounceDown = new TranslateTransition(Duration.millis(80), tile);
            bounceDown.setByY(18);

            TranslateTransition returnHome = new TranslateTransition(Duration.millis(80), tile);
            returnHome.setByY(0);
            returnHome.setToY(0);

            SequentialTransition tileBounce = new SequentialTransition(bounceUp, bounceDown, returnHome);
            sequence.getChildren().add(tileBounce);
        }

        sequence.play();
    }

    // =========================================================
    // SUBMIT GUESS
    // =========================================================
    private void submitGuess() {
        if (currentCol < 5) {
            return;
        }

        StringBuilder guessBuilder = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            guessBuilder.append(board[currentRow][i].getText());
        }
        String guess = guessBuilder.toString();

        if (!dictionary.contains(guess)) {
            shakeRow(currentRow);
            return;
        }

        hasSubmitted = true;
        boolean[] used = new boolean[5];

        for (int i = 0; i < 5; i++) {
            Label tile = board[currentRow][i];
            String guessLetter = guess.substring(i, i + 1);
            String targetLetter = targetWord.substring(i, i + 1);
            final int index = i;

            PauseTransition delay = new PauseTransition(Duration.millis(i * 250));
            delay.setOnFinished(e -> {
                RotateTransition flip = new RotateTransition(Duration.millis(300), tile);
                flip.setAxis(Rotate.X_AXIS);
                flip.setFromAngle(0);
                flip.setToAngle(90);
                flip.setOnFinished(ev -> {
                    if (guessLetter.equals(targetLetter)) {
                        used[index] = true;
                        setTileGreen(tile);
                        updateKeyboardColor(guessLetter, "green");
                    } else {
                        boolean found = false;
                        for (int j = 0; j < 5; j++) {
                            if (!used[j] && guessLetter.equals(targetWord.substring(j, j + 1))) {
                                used[j] = true;
                                found = true;
                                break;
                            }
                        }
                        if (found) {
                            setTileYellow(tile);
                            updateKeyboardColor(guessLetter, "yellow");
                        } else {
                            setTileGray(tile);
                            updateKeyboardColor(guessLetter, "gray");
                        }
                    }

                    RotateTransition finishFlip = new RotateTransition(Duration.millis(300), tile);
                    finishFlip.setAxis(Rotate.X_AXIS);
                    finishFlip.setFromAngle(90);
                    finishFlip.setToAngle(0);
                    finishFlip.play();
                });
                flip.play();
            });
            delay.play();
        }

        PauseTransition end = new PauseTransition(Duration.millis(1600));
        end.setOnFinished(e -> {
            if (guess.equals(targetWord)) {
                showWinWindow();
            } else {
                currentRow++;
                currentCol = 0;
                if (currentRow >= 6) {
                    showLoseWindow();
                }
            }
        });
        end.play();
    }

    // =========================================================
    // TILE COLORS — no borders
    // =========================================================
    private void setTileGreen(Label tile) {
        tile.setStyle(
                "-fx-background-color:#6aaa64;" +
                        "-fx-background-radius:6;" +
                        "-fx-text-fill:white;" +
                        "-fx-font-size:34;" +
                        "-fx-font-family:'Arial';" +
                        "-fx-font-weight:bold;"
        );
    }

    private void setTileYellow(Label tile) {
        tile.setStyle(
                "-fx-background-color:#c9b458;" +
                        "-fx-background-radius:6;" +
                        "-fx-text-fill:white;" +
                        "-fx-font-size:34;" +
                        "-fx-font-family:'Arial';" +
                        "-fx-font-weight:bold;"
        );
    }

    private void setTileGray(Label tile) {
        tile.setStyle(
                "-fx-background-color:#787c7e;" +
                        "-fx-background-radius:6;" +
                        "-fx-text-fill:white;" +
                        "-fx-font-size:34;" +
                        "-fx-font-family:'Arial';" +
                        "-fx-font-weight:bold;"
        );
    }

    // =========================================================
    // KEYBOARD COLORS
    // =========================================================
    private void updateKeyboardColor(String letter, String color) {
        Button key = keyboardMap.get(letter);
        if (key == null) {
            return;
        }
        String current = key.getStyle();
        if (current.contains("#6aaa64")) {
            return;
        }
        if (current.contains("#c9b458") && color.equals("gray")) {
            return;
        }
        switch (color) {
            case "green":
                key.setStyle(
                        "-fx-background-color:#6aaa64;" +
                                "-fx-text-fill:white;" +
                                "-fx-font-weight:bold;"
                );
                break;
            case "yellow":
                key.setStyle(
                        "-fx-background-color:#c9b458;" +
                                "-fx-text-fill:white;" +
                                "-fx-font-weight:bold;"
                );
                break;
            case "gray":
                key.setStyle(
                        "-fx-background-color:#787c7e;" +
                                "-fx-text-fill:white;" +
                                "-fx-font-weight:bold;"
                );
                break;
        }
    }

    // =========================================================
    // RESTART
    // =========================================================
    private void restartGame() {
        currentRow = 0;
        currentCol = 0;
        if (gameMode == GameMode.FREE_PLAY) {
            targetWord = getRandomWord();
        } else {
            targetWord = getDailyWord();
        }
        setupBoardStyle();
        for (Button button : keyboardMap.values()) {
            button.setStyle("");
        }
        for (int r = 0; r < 6; r++) {
            for (int c = 0; c < 5; c++) {
                board[r][c].setText("");
            }
        }
        rootPane.requestFocus();
    }

    // =========================================================
    // THEME
    // =========================================================
    private void applyTheme(String theme) {
        currentTheme = theme;
        Scene scene = rootPane.getScene();
        if (scene == null) {
            return;
        }
        scene.getStylesheets().clear();
        setupBoardStyle();
    }
    // =========================================================
    // SUBJECT / IMAGE
    // =========================================================
    public void setSubject(String subject) {
        if (logoImage == null) return;
        String imageName = subject.toLowerCase() + ".png";
        try {
            javafx.scene.image.Image img = new javafx.scene.image.Image(
                    getClass().getResourceAsStream("/Images/" + imageName)
            );
            logoImage.setImage(img);
        } catch (Exception e) {
            // image not found — logo stays as default
            System.out.println("Image not found: " + imageName);
        }
    }
    // =========================================================
    // WIN / LOSE
    // =========================================================
    private void showWinWindow() {
        Stage winStage = new Stage();

        Label winLabel = new Label("YOU WIN!");
        winLabel.setStyle(
                "-fx-font-size:48;" +
                        "-fx-font-weight:bold;" +
                        "-fx-text-fill:white;"
        );

        Label wordLabel = new Label(targetWord);
        wordLabel.setStyle(
                "-fx-font-size:22;" +
                        "-fx-font-weight:bold;" +
                        "-fx-text-fill:#6aaa64;" +
                        "-fx-background-color:#2f2f2f;" +
                        "-fx-padding:14 28 14 28;" +
                        "-fx-background-radius:8;"
        );

        Button restartBtn = new Button("RESTART");
        restartBtn.setStyle(
                "-fx-font-size:16;" +
                        "-fx-font-weight:bold;" +
                        "-fx-background-color:#444444;" +
                        "-fx-text-fill:white;" +
                        "-fx-padding:10 24 10 24;" +
                        "-fx-background-radius:6;"
        );
        restartBtn.setOnAction(e -> {
            restartGame();
            winStage.close();
        });

        Button exitBtn = new Button("EXIT");
        exitBtn.setStyle(
                "-fx-font-size:16;" +
                        "-fx-font-weight:bold;" +
                        "-fx-background-color:#444444;" +
                        "-fx-text-fill:white;" +
                        "-fx-padding:10 24 10 24;" +
                        "-fx-background-radius:6;"
        );
        exitBtn.setOnAction(e -> System.exit(0));

        HBox buttonBox = new HBox(16, restartBtn, exitBtn);
        buttonBox.setAlignment(Pos.CENTER);

        VBox layout = new VBox(24, winLabel, wordLabel, buttonBox);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color:black; -fx-padding:40;");

        Scene scene = new Scene(layout, 340, 260);
        winStage.setScene(scene);
        winStage.setTitle("You Win!");
        winStage.show();
    }

    private void showLoseWindow() {
        Stage loseStage = new Stage();

        Label loseLabel = new Label("YOU LOSE!");
        loseLabel.setStyle(
                "-fx-font-size:48;" +
                        "-fx-font-weight:bold;" +
                        "-fx-text-fill:white;"
        );

        Label wordLabel = new Label("The word was: " + targetWord);
        wordLabel.setStyle(
                "-fx-font-size:22;" +
                        "-fx-font-weight:bold;" +
                        "-fx-text-fill:#cc3333;" +
                        "-fx-background-color:#2f2f2f;" +
                        "-fx-padding:14 28 14 28;" +
                        "-fx-background-radius:8;"
        );

        Button restartBtn = new Button("RESTART");
        restartBtn.setStyle(
                "-fx-font-size:16;" +
                        "-fx-font-weight:bold;" +
                        "-fx-background-color:#444444;" +
                        "-fx-text-fill:white;" +
                        "-fx-padding:10 24 10 24;" +
                        "-fx-background-radius:6;"
        );
        restartBtn.setOnAction(e -> {
            restartGame();
            loseStage.close();
        });

        Button exitBtn = new Button("EXIT");
        exitBtn.setStyle(
                "-fx-font-size:16;" +
                        "-fx-font-weight:bold;" +
                        "-fx-background-color:#444444;" +
                        "-fx-text-fill:white;" +
                        "-fx-padding:10 24 10 24;" +
                        "-fx-background-radius:6;"
        );
        exitBtn.setOnAction(e -> System.exit(0));

        HBox buttonBox = new HBox(16, restartBtn, exitBtn);
        buttonBox.setAlignment(Pos.CENTER);

        VBox layout = new VBox(24, loseLabel, wordLabel, buttonBox);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color:black; -fx-padding:40;");

        Scene scene = new Scene(layout, 340, 260);
        loseStage.setScene(scene);
        loseStage.setTitle("You Lose!");
        loseStage.show();
    }

    // =========================================================
    // KEYBOARD BUTTONS
    // =========================================================
    @FXML private void handleQ() { addLetter("Q"); }
    @FXML private void handleW() { addLetter("W"); }
    @FXML private void handleE() { addLetter("E"); }
    @FXML private void handleR() { addLetter("R"); }
    @FXML private void handleT() { addLetter("T"); }
    @FXML private void handleY() { addLetter("Y"); }
    @FXML private void handleU() { addLetter("U"); }
    @FXML private void handleI() { addLetter("I"); }
    @FXML private void handleO() { addLetter("O"); }
    @FXML private void handleP() { addLetter("P"); }
    @FXML private void handleA() { addLetter("A"); }
    @FXML private void handleS() { addLetter("S"); }
    @FXML private void handleD() { addLetter("D"); }
    @FXML private void handleF() { addLetter("F"); }
    @FXML private void handleG() { addLetter("G"); }
    @FXML private void handleH() { addLetter("H"); }
    @FXML private void handleJ() { addLetter("J"); }
    @FXML private void handleK() { addLetter("K"); }
    @FXML private void handleL() { addLetter("L"); }
    @FXML private void handleZ() { addLetter("Z"); }
    @FXML private void handleX() { addLetter("X"); }
    @FXML private void handleC() { addLetter("C"); }
    @FXML private void handleV() { addLetter("V"); }
    @FXML private void handleB() { addLetter("B"); }
    @FXML private void handleN() { addLetter("N"); }
    @FXML private void handleM() { addLetter("M"); }
}