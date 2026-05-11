package com.elliottandcoachgeorge.javafxtest;

import com.elliottandcoachgeorge.javafxtest.Controllers.BabyController;
import com.elliottandcoachgeorge.javafxtest.Controllers.EasyController;
import com.elliottandcoachgeorge.javafxtest.Controllers.HardController;
import com.elliottandcoachgeorge.javafxtest.Controllers.RahkEmController;
import javafx.animation.AnimationTimer;
import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RahkEmStartScreen extends Application {

    // =========================================================
    // APPLICATION ENTRY POINT
    // =========================================================
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("StartScreen.fxml")
            );
            loader.setController(new RahkEmStartScreen(stage));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setTitle("Rahk Em'");
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // =========================================================
    // FXML FIELDS
    // =========================================================
    @FXML private Label titleLabel;
    @FXML private ComboBox<String> themeDropdown;
    @FXML private ComboBox<String> difficultyDropdown;
    @FXML private ComboBox<String> subjectDropdown;
    @FXML private Button freePlayButton;
    @FXML private Button dailyButton;
    @FXML private VBox rootVBox;
    @FXML private Canvas carouselCanvas;

    private final Stage stage;

    // =========================================================
    // CAROUSEL STATE
    // =========================================================
    private final String[] CAROUSEL_IMAGES = {
            "java", "geo", "lit", "science", "math", "compute", "latin"
    };

    private final List<Image> carouselImgs  = new ArrayList<>();
    private AnimationTimer     carouselTimer;
    private double             carouselOffset = 0;

    // height every image is scaled to — width scales proportionally
    private static final double SLOT_HEIGHT = 130;
    private static final double GAP         = 30;
    private static final double SPEED       = 0.7;

    public RahkEmStartScreen(Stage stage) {
        this.stage = stage;
    }

    public RahkEmStartScreen() {
        this.stage = null;
    }

    @FXML
    private void initialize() {
        themeDropdown.setValue("WSA");
        themeDropdown.setOnAction(e -> applyTheme(themeDropdown.getValue()));
        difficultyDropdown.setValue("Medium");
        subjectDropdown.setValue("Java");

        styleDropdown(themeDropdown);
        styleDropdown(difficultyDropdown);
        styleDropdown(subjectDropdown);

        startTitleAnimation();
        loadCarouselImages();
        startCarousel();
        applyTheme("WSA");
    }

    // =========================================================
    // CAROUSEL
    // =========================================================
    private void loadCarouselImages() {
        for (String name : CAROUSEL_IMAGES) {
            try {
                Image img = new Image(
                        getClass().getResourceAsStream("/Images/" + name + ".png")
                );
                carouselImgs.add(img);
            } catch (Exception e) {
                System.out.println("Could not load: " + name + ".png");
            }
        }
    }

    // Compute the display width of image i, preserving its aspect ratio
    private double slotWidth(int i) {
        if (i < 0 || i >= carouselImgs.size()) return SLOT_HEIGHT;
        Image img = carouselImgs.get(i);
        if (img.getHeight() == 0) return SLOT_HEIGHT;
        return (img.getWidth() / img.getHeight()) * SLOT_HEIGHT;
    }

    // Total belt width = sum of all natural widths + gaps
    private double totalBeltWidth() {
        double total = 0;
        for (int i = 0; i < carouselImgs.size(); i++) {
            total += slotWidth(i) + GAP;
        }
        return total;
    }

    private void startCarousel() {
        if (carouselImgs.isEmpty()) return;

        carouselTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                carouselOffset += SPEED;
                double belt = totalBeltWidth();
                if (carouselOffset >= belt) {
                    carouselOffset -= belt;
                }
                drawCarousel();
            }
        };
        carouselTimer.start();
    }

    private void drawCarousel() {
        if (carouselCanvas == null || carouselImgs.isEmpty()) return;

        GraphicsContext gc  = carouselCanvas.getGraphicsContext2D();
        double canvasW      = carouselCanvas.getWidth();
        double canvasH      = carouselCanvas.getHeight();
        double belt         = totalBeltWidth();

        gc.clearRect(0, 0, canvasW, canvasH);

        // draw three cycles so the canvas is always fully covered
        // regardless of how wide the canvas is vs the belt
        for (int cycle = 0; cycle < 3; cycle++) {
            double x = cycle * belt - carouselOffset;
            for (int i = 0; i < carouselImgs.size(); i++) {
                double w = slotWidth(i);
                double y = (canvasH - SLOT_HEIGHT) / 2.0;

                // clip check — skip if completely off screen
                if (x + w >= 0 && x <= canvasW) {
                    gc.drawImage(carouselImgs.get(i), x, y, w, SLOT_HEIGHT);
                }

                x += w + GAP;
            }
        }
    }

    // =========================================================
    // DROPDOWN WHITE TEXT FIX
    // =========================================================
    private void styleDropdown(ComboBox<String> dropdown) {
        dropdown.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); } else {
                    setText(item);
                    setStyle("-fx-text-fill: white; -fx-background-color: transparent;");
                }
            }
        });
        dropdown.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); } else {
                    setText(item);
                    setStyle("-fx-text-fill: white; -fx-background-color: #2f2f2f;");
                }
            }
        });
    }

    // =========================================================
    // TITLE ANIMATION
    // =========================================================
    private void startTitleAnimation() {
        ScaleTransition grow = new ScaleTransition(Duration.millis(800), titleLabel);
        grow.setFromX(1.0); grow.setFromY(1.0);
        grow.setToX(1.15);  grow.setToY(1.15);
        ScaleTransition shrink = new ScaleTransition(Duration.millis(800), titleLabel);
        shrink.setFromX(1.15); shrink.setFromY(1.15);
        shrink.setToX(1.0);    shrink.setToY(1.0);
        grow.setOnFinished(e -> shrink.play());
        shrink.setOnFinished(e -> grow.play());
        grow.play();
    }

    // =========================================================
    // THEME ENGINE
    // =========================================================
    private void applyTheme(String theme) {
        String bg        = getThemeBg(theme);
        String btnBg     = getThemeBtnBg(theme);
        String btnText   = getThemeBtnText(theme);
        String titleText = getThemeTitleText(theme);
        String dropBg    = getThemeDropBg(theme);

        rootVBox.setStyle("-fx-background-color:" + bg + ";");
        titleLabel.setStyle(
                "-fx-font-size:64;" +
                        "-fx-font-weight:bold;" +
                        "-fx-text-fill:" + titleText + ";"
        );

        String btnStyle =
                "-fx-font-size:18;" +
                        "-fx-font-weight:bold;" +
                        "-fx-background-color:" + btnBg + ";" +
                        "-fx-text-fill:" + btnText + ";" +
                        "-fx-background-radius:10;";
        freePlayButton.setStyle(btnStyle);
        dailyButton.setStyle(btnStyle);

        String dropStyle =
                "-fx-background-color:" + dropBg + ";" +
                        "-fx-background-radius:8;" +
                        "-fx-text-fill:white;";
        themeDropdown.setStyle(dropStyle);
        difficultyDropdown.setStyle(dropStyle);
        subjectDropdown.setStyle(dropStyle);

        final String finalDropBg = dropBg;
        for (ComboBox<String> dropdown : new ComboBox[]{themeDropdown, difficultyDropdown, subjectDropdown}) {
            dropdown.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) { setText(null); } else {
                        setText(item);
                        setStyle("-fx-text-fill: white; -fx-background-color: transparent;");
                    }
                }
            });
            dropdown.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) { setText(null); } else {
                        setText(item);
                        setStyle("-fx-text-fill: white; -fx-background-color:" + finalDropBg + ";");
                    }
                }
            });
        }
    }

    // =========================================================
    // THEME COLOR MAPS
    // =========================================================
    private String getThemeBg(String theme) {
        switch (theme) {
            case "WSA":      return "white";
            case "Dark":     return "#1a1a1a";
            case "Light":    return "#f5f5f5";
            case "Blue":     return "#1a2a4a";
            case "Ocean":    return "#003d56";
            case "Raven":    return "#1e1e30";
            case "Coach":    return "#3b0000";
            case "Willow":   return "#1e3318";
            case "Fuschia":  return "#3b0a3b";
            case "Bell":     return "#4a3800";
            case "The Four": return "#1e1a3a";
            case "Storm":    return "#2a2e33";
            case "Orange":   return "#3b2000";
            case "Math":
            case "Compute":
            case "Geo":
            case "Science":
            case "Lit":      return "white";
            default:         return "#1a1a1a";
        }
    }

    private String getThemeBtnBg(String theme) {
        switch (theme) {
            case "WSA":      return "#0a1a5c";
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
            case "Math":     return "#1a237e";
            case "Compute":  return "#0d1b6e";
            case "Geo":      return "#1a3a6e";
            case "Science":  return "#0d2b5e";
            case "Lit":      return "#1a1f6e";
            default:         return "#2f2f2f";
        }
    }

    private String getThemeBtnText(String theme) {
        switch (theme) {
            case "Light": return "#222222";
            case "Bell":  return "#222222";
            default:      return "white";
        }
    }

    private String getThemeTitleText(String theme) {
        switch (theme) {
            case "WSA":     return "#0a1a5c";
            case "Light":   return "#222222";
            case "Bell":    return "#d4af37";
            case "Math":    return "#1a237e";
            case "Compute": return "#0d1b6e";
            case "Geo":     return "#1a3a6e";
            case "Science": return "#0d2b5e";
            case "Lit":     return "#1a1f6e";
            default:        return "white";
        }
    }

    private String getThemeDropBg(String theme) {
        switch (theme) {
            case "WSA":      return "#0a1a5c";
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
            case "Math":     return "#1a237e";
            case "Compute":  return "#0d1b6e";
            case "Geo":      return "#1a3a6e";
            case "Science":  return "#0d2b5e";
            case "Lit":      return "#1a1f6e";
            default:         return "#2f2f2f";
        }
    }

    // =========================================================
    // BUTTON ACTIONS
    // =========================================================
    @FXML
    private void startFreePlay() {
        String difficulty = difficultyDropdown.getValue();
        String subject    = subjectDropdown.getValue();
        if (difficulty == null) difficulty = "Medium";
        if (subject == null)    subject    = "Java";
        loadGame(GameMode.FREE_PLAY, difficulty, subject);
    }

    @FXML
    private void startDailyChallenge() {
        String difficulty = difficultyDropdown.getValue();
        String subject    = subjectDropdown.getValue();
        if (difficulty == null) difficulty = "Medium";
        if (subject == null)    subject    = "Java";
        loadGame(GameMode.DAILY, difficulty, subject);
    }

    // =========================================================
    // GAME LOADER
    // =========================================================
    private void loadGame(GameMode mode, String difficulty, String subject) {
        try {
            if (carouselTimer != null) carouselTimer.stop();
            String theme = themeDropdown.getValue();
            switch (difficulty) {
                case "Baby":   loadBabyGame(mode, theme, subject);   break;
                case "Easy":   loadEasyGame(mode, theme, subject);   break;
                case "Hard":   loadHardGame(mode, theme, subject);   break;
                case "Medium":
                default:       loadMediumGame(mode, theme, subject); break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadMediumGame(GameMode mode, String theme, String subject) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
        Parent root = loader.load();
        RahkEmController controller = loader.getController();
        controller.setBackCallback(this::showStartScreen);
        controller.applyThemeExternal(theme);
        controller.setSubject(subject);
        controller.setGameMode(mode);
        Scene scene = new Scene(root);
        applyThemeToScene(scene, theme);
        stage.setScene(scene);
        stage.setTitle("Rahk Em' - Medium");
        stage.show();
    }

    private void loadBabyGame(GameMode mode, String theme, String subject) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("baby-view.fxml"));
        Parent root = loader.load();
        BabyController controller = loader.getController();
        controller.setBackCallback(this::showStartScreen);
        controller.applyThemeExternal(theme);
        controller.setSubject(subject);
        controller.setGameMode(mode);
        Scene scene = new Scene(root);
        applyThemeToScene(scene, theme);
        stage.setScene(scene);
        stage.setTitle("Rahk Em' - Baby");
        stage.show();
    }

    private void loadEasyGame(GameMode mode, String theme, String subject) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("easy-view.fxml"));
        Parent root = loader.load();
        EasyController controller = loader.getController();
        controller.setBackCallback(this::showStartScreen);
        controller.applyThemeExternal(theme);
        controller.setSubject(subject);
        controller.setGameMode(mode);
        Scene scene = new Scene(root);
        applyThemeToScene(scene, theme);
        stage.setScene(scene);
        stage.setTitle("Rahk Em' - Easy");
        stage.show();
    }

    private void loadHardGame(GameMode mode, String theme, String subject) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("hard-view.fxml"));
        Parent root = loader.load();
        HardController controller = loader.getController();
        controller.setBackCallback(this::showStartScreen);
        controller.applyThemeExternal(theme);
        controller.setSubject(subject);
        controller.setGameMode(mode);
        Scene scene = new Scene(root);
        applyThemeToScene(scene, theme);
        stage.setScene(scene);
        stage.setTitle("Rahk Em' - Hard");
        stage.show();
    }

    // =========================================================
    // THEME HELPERS
    // =========================================================
    private void applyThemeToScene(Scene scene, String theme) {
        scene.getStylesheets().clear();
        String cssPath = getCssPath(theme);
        if (cssPath != null) {
            try {
                scene.getStylesheets().add(
                        getClass().getResource(cssPath).toExternalForm()
                );
            } catch (Exception ignored) {}
        }
    }

    private String getCssPath(String theme) {
        switch (theme) {
            case "WSA":      return "/styles/wsa.css";
            case "Dark":     return "/styles/dark.css";
            case "Light":    return "/styles/light.css";
            case "Blue":     return "/styles/blue.css";
            case "Ocean":    return "/styles/Ocean.css";
            case "Raven":    return "/styles/Raven.css";
            case "Coach":    return "/styles/coach.css";
            case "Willow":   return "/styles/willow.css";
            case "Fuschia":  return "/styles/fuschia.css";
            case "Bell":     return "/styles/Bell.css";
            case "The Four": return "/styles/Four.css";
            case "Storm":    return "/styles/Storm.css";
            case "Orange":   return "/styles/Orange.css";
            case "Math":     return "/styles/math.css";
            case "Compute":  return "/styles/compute.css";
            case "Geo":      return "/styles/geo.css";
            case "Science":  return "/styles/science.css";
            case "Lit":      return "/styles/lit.css";
            default:         return "/styles/wsa.css";
        }
    }

    // =========================================================
    // RETURN FROM GAME
    // =========================================================
    public void showStartScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("StartScreen.fxml"));
            loader.setController(new RahkEmStartScreen(stage));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Rahk Em'");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}