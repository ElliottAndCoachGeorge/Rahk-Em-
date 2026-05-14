package com.elliottandcoachgeorge.javafxtest.Controllers;

// all these imports are stuff Java needs so the game can use buttons, labels, animations, files, dates, etc.
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

// these are for reading the word file and storing words and stuff
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

// this is the controller for the hard mode screen
public class HardController {

    // =========================================================
    // GRID LABELS — 6 columns, 6 rows
    // =========================================================
    // these labels are the little boxes on the game board from the FXML file
    @FXML private Label r0c0; @FXML private Label r0c1; @FXML private Label r0c2; @FXML private Label r0c3; @FXML private Label r0c4; @FXML private Label r0c5;
    @FXML private Label r1c0; @FXML private Label r1c1; @FXML private Label r1c2; @FXML private Label r1c3; @FXML private Label r1c4; @FXML private Label r1c5;
    @FXML private Label r2c0; @FXML private Label r2c1; @FXML private Label r2c2; @FXML private Label r2c3; @FXML private Label r2c4; @FXML private Label r2c5;
    @FXML private Label r3c0; @FXML private Label r3c1; @FXML private Label r3c2; @FXML private Label r3c3; @FXML private Label r3c4; @FXML private Label r3c5;
    @FXML private Label r4c0; @FXML private Label r4c1; @FXML private Label r4c2; @FXML private Label r4c3; @FXML private Label r4c4; @FXML private Label r4c5;
    @FXML private Label r5c0; @FXML private Label r5c1; @FXML private Label r5c2; @FXML private Label r5c3; @FXML private Label r5c4; @FXML private Label r5c5;

    // =========================================================
    // KEYBOARD BUTTONS
    // =========================================================
    // these are the buttons for the on-screen keyboard
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
    // these are extra buttons and images from the FXML screen
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
    // hard mode uses 6 letter words and the player gets 6 guesses
    private static final int WORD_LENGTH = 6;
    private static final int MAX_ROWS = 6;

    // this 2D array makes the board easier to loop through instead of using r0c0 every time
    private Label[][] board;

    // this connects letters like "A" to the actual keyboard button so we can change button colors later
    private final HashMap<String, Button> keyboardMap = new HashMap<>();

    // these track where the next letter should go
    private int currentRow = 0;
    private int currentCol = 0;

    // this stores the word list from the text file
    private String[] WORDS;

    // HashSet is used so checking if a word is real is faster
    private final HashSet<String> dictionary = new HashSet<>();

    // this is the word the player is trying to guess
    private String targetWord;

    // starts in free play unless another mode gets picked
    private GameMode gameMode = GameMode.FREE_PLAY;

    // this lets the back button run code from another screen
    private Runnable backCallback;

    // this keeps track of the theme being used
    private String currentTheme = "Dark";

    // =========================================================
    // SETTERS
    // =========================================================
    // sets the game mode and picks the correct kind of word
    public void setGameMode(GameMode mode) {
        this.gameMode = mode;
        if (mode == GameMode.FREE_PLAY) {
            targetWord = getRandomWord();
        } else {
            targetWord = getDailyWord();
        }
        setupBoardStyle();
    }

    // saves what should happen when the back button is clicked
    public void setBackCallback(Runnable callback) {
        this.backCallback = callback;
    }

    // lets another class change the theme from outside this controller
    public void applyThemeExternal(String theme) {
        currentTheme = theme;
        applyTheme(theme);
        setupBoardStyle();
    }

    // =========================================================
    // INITIALIZE
    // =========================================================
    // this runs automatically when the FXML screen loads
    @FXML
    public void initialize() {
        // putting all the board labels into a 2D array makes the grid way easier to use
        board = new Label[][]{
                {r0c0, r0c1, r0c2, r0c3, r0c4, r0c5},
                {r1c0, r1c1, r1c2, r1c3, r1c4, r1c5},
                {r2c0, r2c1, r2c2, r2c3, r2c4, r2c5},
                {r3c0, r3c1, r3c2, r3c3, r3c4, r3c5},
                {r4c0, r4c1, r4c2, r4c3, r4c4, r4c5},
                {r5c0, r5c1, r5c2, r5c3, r5c4, r5c5}
        };

        // setting up the game before the user starts playing
        setupKeyboardMap();
        loadWords();
        targetWord = getRandomWord();

        // these tell the buttons what to do when clicked
        submitButton.setOnAction(e -> submitGuess());
        clearButton.setOnAction(e -> clearRow());
        restartButton.setOnAction(e -> restartGame());
        exitButton.setOnAction(e -> System.exit(0));
        backButton.setOnAction(e -> handleBack());

        // this listens for regular typed letters on the real keyboard
        rootPane.addEventFilter(KeyEvent.KEY_TYPED, this::handleKeyTyped);

        // this handles special keys like enter and backspace
        rootPane.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) submitGuess();
            if (e.getCode() == KeyCode.BACK_SPACE) deleteLetter();
        });

        // this makes sure the game screen is actually listening for keyboard typing
        rootPane.setFocusTraversable(true);
        Platform.runLater(() -> rootPane.requestFocus());
    }

    // =========================================================
    // KEYBOARD MAP
    // =========================================================
    // puts every letter button into a map so we can find it by letter later
    private void setupKeyboardMap() {
        keyboardMap.put("Q", qButton); keyboardMap.put("W", wButton); keyboardMap.put("E", eButton);
        keyboardMap.put("R", rButton); keyboardMap.put("T", tButton); keyboardMap.put("Y", yButton);
        keyboardMap.put("U", uButton); keyboardMap.put("I", iButton); keyboardMap.put("O", oButton);
        keyboardMap.put("P", pButton); keyboardMap.put("A", aButton); keyboardMap.put("S", sButton);
        keyboardMap.put("D", dButton); keyboardMap.put("F", fButton); keyboardMap.put("G", gButton);
        keyboardMap.put("H", hButton); keyboardMap.put("J", jButton); keyboardMap.put("K", kButton);
        keyboardMap.put("L", lButton); keyboardMap.put("Z", zButton); keyboardMap.put("X", xButton);
        keyboardMap.put("C", cButton); keyboardMap.put("V", vButton); keyboardMap.put("B", bButton);
        keyboardMap.put("N", nButton); keyboardMap.put("M", mButton);
    }

    // =========================================================
    // BOARD STYLE
    // =========================================================
    // styles all the tiles on the board
    private void setupBoardStyle() {
        // if the board has not loaded yet, don't try to style it
        if (board == null) return;

        // this picks the color based on the theme
        String backgroundColor = getThemeTileColor();

        // this loops through every row and column and styles each tile
        for (int r = 0; r < MAX_ROWS; r++) {
            for (int c = 0; c < WORD_LENGTH; c++) {
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

    // picks the tile color for whatever theme is being used
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
    // runs the back button code if there is any
    @FXML
    private void handleBack() {
        if (backCallback != null) backCallback.run();
    }

    // =========================================================
    // LOAD WORDS
    // =========================================================
    // loads words from hard.txt and puts them in the word list and dictionary
    private void loadWords() {
        ArrayList<String> wordList = new ArrayList<>();
        try {
            // this looks for the hard.txt file in the resources folder
            InputStream inputStream = getClass().getResourceAsStream("/hard.txt");

            // if the file is missing, use PLANET so the game doesn't totally explode
            if (inputStream == null) {
                wordList.add("PLANET");
                dictionary.add("PLANET");
            } else {
                // this reads the file one line at a time
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    // clean up the word and make it uppercase
                    line = line.trim().toUpperCase();

                    // only use words that are the right length for hard mode
                    if (line.length() == WORD_LENGTH) {
                        wordList.add(line);
                        dictionary.add(line);
                    }
                }
                reader.close();
            }
        } catch (Exception e) {
            // if something goes wrong, print the error and still give the game a backup word
            e.printStackTrace();
            wordList.add("PLANET");
            dictionary.add("PLANET");
        }

        // changes the ArrayList into a regular array
        WORDS = wordList.toArray(new String[0]);
    }

    // =========================================================
    // WORD SELECTION
    // =========================================================
    // picks a random word from the list
    private String getRandomWord() {
        if (WORDS == null || WORDS.length == 0) return "PLANET";
        return WORDS[new Random().nextInt(WORDS.length)];
    }

    // picks a daily word based on how many days have passed since Jan 1, 2024
    private String getDailyWord() {
        if (WORDS == null || WORDS.length == 0) return "PLANET";
        LocalDate start = LocalDate.of(2024, 1, 1);
        long days = ChronoUnit.DAYS.between(start, LocalDate.now());
        return WORDS[(int) (Math.abs(days) % WORDS.length)];
    }

    // =========================================================
    // INPUT
    // =========================================================
    // handles real keyboard typing
    private void handleKeyTyped(KeyEvent event) {
        String character = event.getCharacter().toUpperCase();

        // if it is not a letter, ignore it
        if (!character.matches("[A-Z]")) { event.consume(); return; }

        addLetter(character);
    }

    // adds a letter to the current tile
    private void addLetter(String letter) {
        // only add the letter if the row is not already full
        if (currentCol < WORD_LENGTH) {
            Label tile = board[currentRow][currentCol];
            tile.setText(letter);
            playPopAnimation(tile);
            currentCol++;
        }
    }

    // deletes the last letter typed
    private void deleteLetter() {
        // only delete if there is actually something to delete
        if (currentCol > 0) {
            currentCol--;
            board[currentRow][currentCol].setText("");
        }
    }

    // clears the whole current row
    private void clearRow() {
        for (int i = 0; i < WORD_LENGTH; i++) board[currentRow][i].setText("");
        currentCol = 0;
    }

    // =========================================================
    // POP ANIMATION
    // =========================================================
    // makes a tile pop bigger for a second when a letter is typed
    private void playPopAnimation(Label tile) {
        ScaleTransition pop = new ScaleTransition(Duration.millis(100), tile);
        pop.setFromX(1.0); pop.setFromY(1.0);
        pop.setToX(1.15);  pop.setToY(1.15);
        pop.setAutoReverse(true);
        pop.setCycleCount(2);
        pop.play();
    }

    // =========================================================
    // BOUNCE ROW
    // =========================================================
    // bounces the row when the guess is not in the dictionary
    private void shakeRow(int row) {
        SequentialTransition sequence = new SequentialTransition();

        // each tile moves up and down to show the guess was bad
        for (int c = 0; c < WORD_LENGTH; c++) {
            Label tile = board[row][c];

            TranslateTransition bounceUp   = new TranslateTransition(Duration.millis(80), tile);
            bounceUp.setByY(-18);

            TranslateTransition bounceDown = new TranslateTransition(Duration.millis(80), tile);
            bounceDown.setByY(18);

            TranslateTransition returnHome = new TranslateTransition(Duration.millis(80), tile);
            returnHome.setByY(0); returnHome.setToY(0);

            sequence.getChildren().add(new SequentialTransition(bounceUp, bounceDown, returnHome));
        }
        sequence.play();
    }

    // =========================================================
    // SUBMIT GUESS
    // =========================================================
    // checks the guess and colors the tiles like Wordle
    private void submitGuess() {
        // do nothing if the player has not typed enough letters
        if (currentCol < WORD_LENGTH) return;

        // builds the guess from the letters on the current row
        StringBuilder guessBuilder = new StringBuilder();
        for (int i = 0; i < WORD_LENGTH; i++) guessBuilder.append(board[currentRow][i].getText());
        String guess = guessBuilder.toString();

        // if the word is not in the list, bounce the row and stop
        if (!dictionary.contains(guess)) {
            shakeRow(currentRow);
            return;
        }

        // this keeps track of target letters that already got matched
        boolean[] used = new boolean[WORD_LENGTH];

        // checks each letter in the guess
        for (int i = 0; i < WORD_LENGTH; i++) {
            Label tile = board[currentRow][i];
            String guessLetter  = guess.substring(i, i + 1);
            String targetLetter = targetWord.substring(i, i + 1);
            final int index = i;

            // delays each tile so they flip one at a time instead of all at once
            PauseTransition delay = new PauseTransition(Duration.millis(i * 250));
            delay.setOnFinished(e -> {
                // first half of the flip animation
                RotateTransition flip = new RotateTransition(Duration.millis(300), tile);
                flip.setAxis(Rotate.X_AXIS);
                flip.setFromAngle(0);
                flip.setToAngle(90);

                flip.setOnFinished(ev -> {
                    // green means correct letter in the correct place
                    if (guessLetter.equals(targetLetter)) {
                        used[index] = true;
                        setTileGreen(tile);
                        updateKeyboardColor(guessLetter, "green");
                    } else {
                        // checks if the letter exists somewhere else in the target word
                        boolean found = false;
                        for (int j = 0; j < WORD_LENGTH; j++) {
                            if (!used[j] && guessLetter.equals(targetWord.substring(j, j + 1))) {
                                used[j] = true; found = true; break;
                            }
                        }

                        // yellow means right letter but wrong spot, gray means not in the word
                        if (found) { setTileYellow(tile); updateKeyboardColor(guessLetter, "yellow"); }
                        else       { setTileGray(tile);   updateKeyboardColor(guessLetter, "gray"); }
                    }

                    // second half of the flip animation so the tile turns back around
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

        // waits until the flips finish before moving on
        PauseTransition end = new PauseTransition(Duration.millis(WORD_LENGTH * 250 + 350));
        end.setOnFinished(e -> {
            // win if the guess is exactly the target word
            if (guess.equals(targetWord)) {
                showWinWindow();
            } else {
                // otherwise move to the next row
                currentRow++;
                currentCol = 0;

                // if there are no rows left, the player loses
                if (currentRow >= MAX_ROWS) showLoseWindow();
            }
        });
        end.play();
    }

    // =========================================================
    // TILE COLORS
    // =========================================================
    // these three methods change a tile's color after a guess
    private void setTileGreen(Label tile) {
        tile.setStyle("-fx-background-color:#6aaa64;-fx-background-radius:6;-fx-text-fill:white;-fx-font-size:34;-fx-font-family:'Arial';-fx-font-weight:bold;");
    }
    private void setTileYellow(Label tile) {
        tile.setStyle("-fx-background-color:#c9b458;-fx-background-radius:6;-fx-text-fill:white;-fx-font-size:34;-fx-font-family:'Arial';-fx-font-weight:bold;");
    }
    private void setTileGray(Label tile) {
        tile.setStyle("-fx-background-color:#787c7e;-fx-background-radius:6;-fx-text-fill:white;-fx-font-size:34;-fx-font-family:'Arial';-fx-font-weight:bold;");
    }

    // =========================================================
    // KEYBOARD COLORS
    // =========================================================
    // changes the on-screen keyboard button colors too
    private void updateKeyboardColor(String letter, String color) {
        Button key = keyboardMap.get(letter);

        // if the key was not found, just stop
        if (key == null) return;

        String current = key.getStyle();

        // don't make a green key worse because green is the best clue
        if (current.contains("#6aaa64")) return;

        // don't turn a yellow key gray because yellow is more useful than gray
        if (current.contains("#c9b458") && color.equals("gray")) return;

        // pick the right button color
        switch (color) {
            case "green":  key.setStyle("-fx-background-color:#6aaa64;-fx-text-fill:white;-fx-font-weight:bold;"); break;
            case "yellow": key.setStyle("-fx-background-color:#c9b458;-fx-text-fill:white;-fx-font-weight:bold;"); break;
            case "gray":   key.setStyle("-fx-background-color:#787c7e;-fx-text-fill:white;-fx-font-weight:bold;"); break;
        }
    }

    // =========================================================
    // RESTART
    // =========================================================
    // resets the game back to the beginning
    private void restartGame() {
        currentRow = 0;
        currentCol = 0;

        // choose a new random word or the daily word depending on the mode
        targetWord = (gameMode == GameMode.FREE_PLAY) ? getRandomWord() : getDailyWord();

        // reset board colors and keyboard colors
        setupBoardStyle();
        for (Button button : keyboardMap.values()) button.setStyle("");

        // clear all the letters from the board
        for (int r = 0; r < MAX_ROWS; r++)
            for (int c = 0; c < WORD_LENGTH; c++)
                board[r][c].setText("");

        // give focus back to the game so typing works again
        rootPane.requestFocus();
    }

    // =========================================================
    // THEME
    // =========================================================
    // applies the theme and clears old stylesheets
    private void applyTheme(String theme) {
        currentTheme = theme;
        Scene scene = rootPane.getScene();

        // if the scene does not exist yet, stop
        if (scene == null) return;

        scene.getStylesheets().clear();
        setupBoardStyle();
    }

    // =========================================================
    // SUBJECT / IMAGE
    // =========================================================
    // changes the logo image based on the subject name
    public void setSubject(String subject) {
        if (logoImage == null) return;

        // makes the file name from the subject, like math.png or science.png
        String imageName = subject.toLowerCase() + ".png";
        try {
            javafx.scene.image.Image img = new javafx.scene.image.Image(
                    getClass().getResourceAsStream("/Images/" + imageName)
            );
            logoImage.setImage(img);
        } catch (Exception e) {
            // image not found so the logo just stays whatever it already was
            System.out.println("Image not found: " + imageName);
        }
    }

    // =========================================================
    // WIN / LOSE
    // =========================================================
    // shows the little pop-up window when the player wins
    private void showWinWindow() {
        Stage winStage = new Stage();

        // big win text
        Label winLabel = new Label("YOU WIN!");
        winLabel.setStyle("-fx-font-size:48;-fx-font-weight:bold;-fx-text-fill:white;");

        // shows the answer word
        Label wordLabel = new Label(targetWord);
        wordLabel.setStyle("-fx-font-size:22;-fx-font-weight:bold;-fx-text-fill:#6aaa64;-fx-background-color:#2f2f2f;-fx-padding:14 28 14 28;-fx-background-radius:8;");

        // restart button starts a new game and closes the pop-up
        Button restartBtn = new Button("RESTART");
        restartBtn.setStyle("-fx-font-size:16;-fx-font-weight:bold;-fx-background-color:#444444;-fx-text-fill:white;-fx-padding:10 24 10 24;-fx-background-radius:6;");
        restartBtn.setOnAction(e -> { restartGame(); winStage.close(); });

        // exit button quits the program
        Button exitBtn = new Button("EXIT");
        exitBtn.setStyle("-fx-font-size:16;-fx-font-weight:bold;-fx-background-color:#444444;-fx-text-fill:white;-fx-padding:10 24 10 24;-fx-background-radius:6;");
        exitBtn.setOnAction(e -> System.exit(0));

        // layouts put the buttons and labels in the pop-up window
        HBox buttonBox = new HBox(16, restartBtn, exitBtn);
        buttonBox.setAlignment(Pos.CENTER);
        VBox layout = new VBox(24, winLabel, wordLabel, buttonBox);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color:black;-fx-padding:40;");

        // creates and shows the window
        winStage.setScene(new Scene(layout, 340, 260));
        winStage.setTitle("You Win!");
        winStage.show();
    }

    // shows the little pop-up window when the player loses
    private void showLoseWindow() {
        Stage loseStage = new Stage();

        // big lose text
        Label loseLabel = new Label("YOU LOSE!");
        loseLabel.setStyle("-fx-font-size:48;-fx-font-weight:bold;-fx-text-fill:white;");

        // shows the correct answer
        Label wordLabel = new Label(targetWord);
        wordLabel.setStyle("-fx-font-size:22;-fx-font-weight:bold;-fx-text-fill:#cc3333;-fx-background-color:#2f2f2f;-fx-padding:14 28 14 28;-fx-background-radius:8;");

        // restart button starts over and closes this window
        Button restartBtn = new Button("RESTART");
        restartBtn.setStyle("-fx-font-size:16;-fx-font-weight:bold;-fx-background-color:#444444;-fx-text-fill:white;-fx-padding:10 24 10 24;-fx-background-radius:6;");
        restartBtn.setOnAction(e -> { restartGame(); loseStage.close(); });

        // exit button closes the program
        Button exitBtn = new Button("EXIT");
        exitBtn.setStyle("-fx-font-size:16;-fx-font-weight:bold;-fx-background-color:#444444;-fx-text-fill:white;-fx-padding:10 24 10 24;-fx-background-radius:6;");
        exitBtn.setOnAction(e -> System.exit(0));

        // layouts organize the pop-up window stuff
        HBox buttonBox = new HBox(16, restartBtn, exitBtn);
        buttonBox.setAlignment(Pos.CENTER);
        VBox layout = new VBox(24, loseLabel, wordLabel, buttonBox);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color:black;-fx-padding:40;");

        // creates and shows the lose window
        loseStage.setScene(new Scene(layout, 340, 260));
        loseStage.setTitle("You Lose!");
        loseStage.show();
    }

    // =========================================================
    // KEYBOARD BUTTONS
    // =========================================================
    // these methods run when each on-screen keyboard button gets clicked
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
