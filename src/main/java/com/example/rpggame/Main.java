package com.example.rpggame;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application {

    // ثابت‌ها برای ابعاد صحنه
    private final int WIDTH = 720; // عرض صحنه
    private final int HEIGHT = 1280; // ارتفاع صحنه
    private static final int SHOT_FRAME_COUNTER = 14; // تعداد فریم‌ها برای انیمیشن شلیک
    private static final int BOOM_FRAME_COUNTER = 10;// تعداد فریم‌ها برای انیمیشن BOOM
    private static final int SHOT_FRAME_SPEED = 30; // سرعت تغییر فریم‌های انیمیشن شلیک (میلی‌ثانیه)
    private static final int BOOM_FRAME_SPEED = 30; // سرعت تغییر فریم‌های انیمیشن BOOM (میلی‌ثانیه)
    private static final double ARROW_SPEED = 8; // سرعت حرکت تیر
    private static final double CACTUS_SPEED = 15; // سرعت حرکت کاکتوس (هماهنگ با کد قبلی)
    private static final double SNOW_BOLL_SPEED = 17; // سرعت حرکت کوله برف (هماهنگ با کد قبلی)
    private static final double SWORD_SPEED = 19; // سرعت حرکت شمشیر (هماهنگ با کد قبلی)

    // آرایه‌ای برای ذخیره فریم‌های انیمیشن شلیک بازیکن
    private Image[] shotFrames = new Image[SHOT_FRAME_COUNTER];
    private ImageView standSprite = new ImageView(); // تصویر بازیکن
    private int shotCurrentFrame = 0; // فریم فعلی انیمیشن شلیک
    private Timeline animation; // تایم‌لاین برای انیمیشن شلیک

    // آرایه‌ای برای ذخیره فریم‌های انیمیشن BOOM
    private Image[] boomFrames = new Image[BOOM_FRAME_COUNTER];
    private ImageView boomSprite = new ImageView(); // تصویر بازیکن
    private int boomCurrentFrame = 0; // فریم فعلی انیمیشن شلیک
    private Timeline boomAnimation; // تایم‌لاین برای انیمیشن شلیک

    // اشیاء و تایم‌لاین‌ها برای تیر و دشمن
    private ImageView arrowView; // تصویر تیر
    private ImageView nmeView; // تصویر دشمن
    private Timeline arrowMovement; // تایم‌لاین برای حرکت تیر

    // اشیاء و تایم‌لاین‌ها برای کاکتوس
    private ImageView cactusView; // تصویر کاکتوس
    private Timeline cactusMovement; // تایم‌لاین برای حرکت کاکتوس
    private int cactusCycleIndex = 0; // اندیس فعلی در الگوی کاکتوس
    private final int[] cactusPattern = {0, 0, 1, 0, 1, 1, 0, 0, 1}; // الگوی حرکت کاکتوس (پایین/بالا)
    private static final double CACTUS_Y_LOW = 450; // موقعیت پایین کاکتوس
    private static final double CACTUS_Y_HIGH = 300; // موقعیت بالای کاکتوس

    // اشیاء و تایم‌لاین‌ها برای کوله برف
    private ImageView snowBollView; // تصویر کوله برف
    private Timeline snowBollMovement; // تایم‌لاین برای حرکت کوله برف
    private int snowBollCycleIndex = 0; // اندیس فعلی در الگوی کوله برف
    private final int[] snowBollPattern = {0, 0, 1, 0, 1, 1, 0}; // الگوی حرکت کوله برف (پایین/بالا)
    private static final double SNOW_BOLL_Y_LOW = 450; // موقعیت پایین کوله برف
    private static final double SNOW_BOLL_Y_HIGH = 300; // موقعیت بالای کوله برف

    // اشیاء و تایم‌لاین‌ها برای شمشیر
    private ImageView swordView; // تصویر شمشیر
    private Timeline swordMovement; // تایم‌لاین برای حرکت شمشیر
    private int swordCycleIndex = 0; // اندیس فعلی در الگوی شمشیر
    private final int[] swordPattern = {0, 0, 1, 0, 1, 1, 0}; // الگوی حرکت شمشیر (پایین/بالا)
    private static final double SWORD_Y_LOW = 450; // موقعیت پایین شمشیر
    private static final double SWORD_Y_HIGH = 300; // موقعیت بالای شمشیر

    // متغیرهای سلامت بازیکن و دشمنان
    private static final int finalPlayerHealth = 5; // سلامت اولیه بازیکن
    private int playerHealth = finalPlayerHealth; // سلامت فعلی بازیکن
    private int nmeHealth1 = 20; // سلامت دشمن اول (NME_1)
    private int nmeHealth2 = 25; // سلامت دشمن دوم (NME_2)
    private int nmeHealth3 = 30; // سلامت دشمن سوم (NME_3)
    private Text playerHealthText; // متن برای نمایش سلامت بازیکن
    private Text nmeHealthText1; // متن برای نمایش سلامت دشمن اول
    private Text nmeHealthText2; // متن برای نمایش سلامت دشمن دوم
    private Text nmeHealthText3; // متن برای نمایش سلامت دشمن سوم

    private static Pane root; // پانل اصلی برای افزودن اشیاء گرافیکی
    private boolean spacePressed = false; // وضعیت کلید Space
    private boolean escapePressed = false; // وضعیت کلید F (برای شلیک)
    private boolean isJumping = false; // وضعیت پرش بازیکن
    private double jumpVelocity = 0; // سرعت عمودی پرش
    private static final double JUMP_HEIGHT = -15; // ارتفاع اولیه پرش
    private static final double GRAVITY = 0.5; // شتاب گرانش برای پرش
    private int currentLevel = 1; // سطح فعلی بازی

    // متغیرهای صدا
    private MediaPlayer startSoundPlayer; // برای StartSound.mp3 (صدای منوی شروع)
    private MediaPlayer level1SoundPlayer; // برای Level1Sound.mp3 (صدای سطح اول)
    private MediaPlayer level2SoundPlayer; // برای Level2Sound.mp3 (صدای سطح دوم)
    private MediaPlayer level3SoundPlayer; // برای Level3Sound.mp3 (صدای سطح سوم)
    private MediaPlayer jumpSoundPlayer; // برای JumpSound.mp3 (صدای پرش)
    private MediaPlayer arrowSoundPlayer; // برای ArrowSound.mp3 (صدای شلیک تیر)
    private MediaPlayer win1SoundPlayer; // برای Win1Sound.mp3 (صدای پیروزی در سطح)
    private MediaPlayer win2SoundPlayer; // برای Win2Sound.mp3 (صدای پایان بازی)
    private MediaPlayer gameOverSoundPlayer; // برای GameOverSound.mp3 (صدای باخت)
    private MediaPlayer hurtSound1Player; // برای HurtSound1.mp3 (صدای برخورد بازیکن با موانع)
    private MediaPlayer hurtSound2Player; // برای HurtSound2.mp3 (صدای برخورد تیر با NME_1 و NME_3)
    private MediaPlayer hurtSound3Player; // برای HurtSound3.mp3 (صدای برخورد تیر با NME_2)

    @Override
    public void start(Stage primaryStage) {
        root = new Pane(); // ایجاد پانل اصلی
        Scene scene = new Scene(root, HEIGHT, WIDTH); // ایجاد صحنه با ابعاد مشخص

        createStartMenu(); // نمایش منوی شروع

        // مدیریت رویدادهای کیبورد
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.SPACE && !isJumping) { // اگر کلید Space فشرده شد و بازیکن در حال پرش نیست
                spacePressed = true;
                startJump(); // شروع پرش
            }
        });

        scene.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.SPACE) { // اگر کلید Space رها شد
                spacePressed = false;
            }
        });

        // تایم‌لاین برای به‌روزرسانی پرش
        Timeline jumpTimeline = new Timeline(
                new KeyFrame(Duration.millis(16), e -> updateJump())
        );
        jumpTimeline.setCycleCount(Timeline.INDEFINITE); // اجرای نامحدود
        jumpTimeline.play(); // شروع تایم‌لاین

        primaryStage.setTitle("Oziro"); // تنظیم عنوان پنجره
        primaryStage.setScene(scene); // تنظیم صحنه
        primaryStage.show(); // نمایش پنجره
    }

    // ایجاد منوی شروع بازی
    private void createStartMenu() {
        root.getChildren().clear(); // پاک کردن پانل

        // بارگذاری و پخش صدای منوی شروع
        if (startSoundPlayer == null) {
            Media startSound = new Media(getClass().getResource("/SOUNDES/StartSound.mp3").toString());
            startSoundPlayer = new MediaPlayer(startSound);
            startSoundPlayer.setCycleCount(MediaPlayer.INDEFINITE); // پخش حلقه‌ای
            startSoundPlayer.play();
        } else {
            startSoundPlayer.stop();
            startSoundPlayer.play();
        }

        // تنظیم پس‌زمینه منو
        Image background = new Image(getClass().getResourceAsStream("/images/BACK_GARNDES/back_grand.png"));
        ImageView backgroundView = new ImageView(background);
        backgroundView.setPreserveRatio(true);
        root.getChildren().add(backgroundView);

        // ایجاد عنوان منو
        Text title = new Text("RPG Game");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 100));
        title.setFill(Color.rgb(45, 0, 0, 1));
        title.setLayoutX(((HEIGHT / 4) + 50));
        title.setLayoutY(100);
        root.getChildren().addAll(title);

        // دکمه شروع بازی
        Button startButton = new Button("Start Game");
        startButton.setFont(Font.font("Arial", FontWeight.BOLD, 40));
        startButton.setLayoutX((WIDTH / 2) + 100);
        startButton.setLayoutY(200);
        startButton.setPrefWidth(300);
        startButton.setPrefHeight(80);

        // دکمه انتخاب سطح
        Button levelButton = new Button("Level");
        levelButton.setFont(Font.font("Arial", FontWeight.BOLD, 40));
        levelButton.setLayoutX((WIDTH / 2) + 100);
        levelButton.setLayoutY(300);
        levelButton.setPrefWidth(300);
        levelButton.setPrefHeight(80);

        // دکمه خروج
        Button exitButton = new Button("Exit");
        exitButton.setFont(Font.font("Arial", FontWeight.BOLD, 40));
        exitButton.setLayoutX((WIDTH / 2) + 100);
        exitButton.setLayoutY(400);
        exitButton.setPrefWidth(300);
        exitButton.setPrefHeight(80);

        // استایل دکمه‌ها
        String buttonStyle = "-fx-background-color: #000050; -fx-text-fill: white; -fx-border-radius: 5; -fx-background-radius: 5;";

        startButton.setStyle(buttonStyle);
        levelButton.setStyle(buttonStyle);
        exitButton.setStyle(buttonStyle);

        root.getChildren().addAll(startButton, levelButton, exitButton);

        // عملکرد دکمه‌ها
        startButton.setOnAction(e -> {
            if (startSoundPlayer != null) {
                startSoundPlayer.stop();
            }
            startGame(); // شروع بازی
        });
        levelButton.setOnAction(e -> level()); // رفتن به منوی انتخاب سطح
        exitButton.setOnAction(e -> System.exit(0)); // خروج از بازی
    }

    // شروع بازی از سطح اول
    private void startGame() {
        Level1(); // رفتن به سطح اول
    }

    // نمایش منوی انتخاب سطح
    private void level() {
        root.getChildren().clear();

        // ادامه پخش صدای منوی شروع
        if (startSoundPlayer != null) {
            startSoundPlayer.stop();
            startSoundPlayer.play();
        } else {
            Media startSound = new Media(getClass().getResource("/SOUNDES/StartSound.mp3").toString());
            startSoundPlayer = new MediaPlayer(startSound);
            startSoundPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            startSoundPlayer.play();
        }

        // تنظیم پس‌زمینه
        Image background = new Image(getClass().getResourceAsStream("/images/BACK_GARNDES/back_grand.png"));
        ImageView backgroundView = new ImageView(background);
        backgroundView.setPreserveRatio(true);
        root.getChildren().add(backgroundView);

        // عنوان منوی انتخاب سطح
        Text title = new Text("Levels");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 100));
        title.setFill(Color.rgb(45, 0, 0, 1));
        title.setLayoutX(((HEIGHT / 4) + 145));
        title.setLayoutY(100);
        root.getChildren().addAll(title);

        // دکمه سطح ۱
        Button Level1 = new Button("Level 1");
        Level1.setFont(Font.font("Arial", FontWeight.BOLD, 40));
        Level1.setLayoutX((WIDTH / 2) + 100);
        Level1.setLayoutY(200);
        Level1.setPrefWidth(300);
        Level1.setPrefHeight(80);

        // دکمه سطح ۲
        Button Level2 = new Button("Level 2");
        Level2.setFont(Font.font("Arial", FontWeight.BOLD, 40));
        Level2.setLayoutX((WIDTH / 2) + 100);
        Level2.setLayoutY(300);
        Level2.setPrefWidth(300);
        Level2.setPrefHeight(80);

        // دکمه سطح ۳
        Button Level3 = new Button("Level 3");
        Level3.setFont(Font.font("Arial", FontWeight.BOLD, 40));
        Level3.setLayoutX((WIDTH / 2) + 100);
        Level3.setLayoutY(400);
        Level3.setPrefWidth(300);
        Level3.setPrefHeight(80);

        // استایل دکمه‌ها
        String buttonStyle = "-fx-background-color: #000050; -fx-text-fill: white; -fx-border-radius: 5; -fx-background-radius: 5;";

        Level1.setStyle(buttonStyle);
        Level2.setStyle(buttonStyle);
        Level3.setStyle(buttonStyle);

        root.getChildren().addAll(Level1, Level2, Level3);

        // عملکرد دکمه‌ها
        Level1.setOnAction(e -> {
            if (startSoundPlayer != null) {
                startSoundPlayer.stop();
            }
            Level1(); // رفتن به سطح ۱
        });
        Level2.setOnAction(e -> {
            if (startSoundPlayer != null) {
                startSoundPlayer.stop();
            }
            Level2(); // رفتن به سطح ۲
        });
        Level3.setOnAction(e -> {
            if (startSoundPlayer != null) {
                startSoundPlayer.stop();
            }
            Level3(); // رفتن به سطح ۳
        });
    }

    // تنظیمات سطح اول بازی
    private void Level1() {
        currentLevel = 1; // تنظیم سطح فعلی
        playerHealth = finalPlayerHealth; // ریست کردن سلامت بازیکن
        root.getChildren().clear(); // پاک کردن پانل

        // بارگذاری و پخش صدای سطح اول
        level1SoundPlayer = null;
        if (level1SoundPlayer == null) {
            Media level1Sound = new Media(getClass().getResource("/SOUNDES/Level1Sound.mp3").toString());
            level1SoundPlayer = new MediaPlayer(level1Sound);
            level1SoundPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            level1SoundPlayer.play();
        }

        // تنظیم پس‌زمینه سطح
        Image map = new Image(getClass().getResourceAsStream("/images/BACK_GARNDES/Map_1.png"));
        ImageView backgroundView = new ImageView(map);
        backgroundView.setPreserveRatio(true);
        root.getChildren().add(backgroundView);

        // تنظیم دشمن (NME_1)
        Image NME = new Image(getClass().getResourceAsStream("/images/NMES/NME1.png"));
        nmeView = new ImageView(NME);
        nmeView.setFitHeight(276);
        nmeView.setFitWidth(287);
        nmeView.setLayoutX((WIDTH / 2) + 630);
        nmeView.setLayoutY(300);
        root.getChildren().add(nmeView);

        // تنظیم انیمیشن شلیک بازیکن برای سطح ۱
        PlayerShot1();
        standSprite.setPreserveRatio(true);
        standSprite.setLayoutX((WIDTH / 2) - 350);
        standSprite.setLayoutY(430);
        root.getChildren().add(standSprite);

        // نمایش سلامت بازیکن
        playerHealthText = new Text("Player Health: " + playerHealth);
        playerHealthText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        playerHealthText.setFill(Color.WHITE);
        playerHealthText.setLayoutX(20);
        playerHealthText.setLayoutY(30);
        root.getChildren().add(playerHealthText);

        // نمایش سلامت دشمن
        nmeHealthText1 = new Text("Enemy Health: " + nmeHealth1);
        nmeHealthText1.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        nmeHealthText1.setFill(Color.WHITE);
        nmeHealthText1.setLayoutX(HEIGHT - 200);
        nmeHealthText1.setLayoutY(30);
        root.getChildren().add(nmeHealthText1);

        Cactus(); // شروع حرکت کاکتوس‌ها
    }

    // تنظیمات سطح دوم بازی
    private void Level2() {
        currentLevel = 2;
        playerHealth = finalPlayerHealth; // ریست کردن سلامت بازیکن
        root.getChildren().clear();

        // متوقف کردن صدای سطح ۱ و پخش صدای سطح ۲
        if (level1SoundPlayer != null) {
            level1SoundPlayer.stop();
        }
        level2SoundPlayer = null;
        if (level2SoundPlayer == null) {
            Media level2Sound = new Media(getClass().getResource("/SOUNDES/Level2Sound.mp3").toString());
            level2SoundPlayer = new MediaPlayer(level2Sound);
            level2SoundPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            level2SoundPlayer.play();
        }

        // تنظیم پس‌زمینه سطح
        Image map = new Image(getClass().getResourceAsStream("/images/BACK_GARNDES/Map_2.png"));
        ImageView backgroundView = new ImageView(map);
        backgroundView.setPreserveRatio(true);
        root.getChildren().add(backgroundView);

        // تنظیم دشمن (NME_2)
        Image NME = new Image(getClass().getResourceAsStream("/images/NMES/NME2.png"));
        nmeView = new ImageView(NME);
        nmeView.setFitHeight(296);
        nmeView.setFitWidth(307);
        nmeView.setLayoutX((WIDTH / 2) + 630);
        nmeView.setLayoutY(300);
        root.getChildren().add(nmeView);

        // تنظیم انیمیشن شلیک بازیکن برای سطح ۲
        PlayerShot2();
        standSprite.setPreserveRatio(true);
        standSprite.setLayoutX((WIDTH / 2) - 350);
        standSprite.setLayoutY(430);
        root.getChildren().add(standSprite);

        // نمایش سلامت بازیکن
        playerHealthText = new Text("Player Health: " + playerHealth);
        playerHealthText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        playerHealthText.setFill(Color.WHITE);
        playerHealthText.setLayoutX(20);
        playerHealthText.setLayoutY(30);
        root.getChildren().add(playerHealthText);

        // نمایش سلامت دشمن
        nmeHealthText2 = new Text("Enemy Health: " + nmeHealth2);
        nmeHealthText2.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        nmeHealthText2.setFill(Color.WHITE);
        nmeHealthText2.setLayoutX(HEIGHT - 200);
        nmeHealthText2.setLayoutY(30);
        root.getChildren().add(nmeHealthText2);

        SnowBoll(); // شروع حرکت کوله‌های برف
    }

    // تنظیمات سطح سوم بازی
    private void Level3() {
        currentLevel = 3;
        playerHealth = finalPlayerHealth; // ریست کردن سلامت بازیکن
        root.getChildren().clear();

        // متوقف کردن صدای سطح ۲ و پخش صدای سطح ۳
        if (level2SoundPlayer != null) {
            level2SoundPlayer.stop();
        }
        level3SoundPlayer = null;
        if (level3SoundPlayer == null) {
            Media level3Sound = new Media(getClass().getResource("/SOUNDES/Level3Sound.mp3").toString());
            level3SoundPlayer = new MediaPlayer(level3Sound);
            level3SoundPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            level3SoundPlayer.play();
        }

        // تنظیم پس‌زمینه سطح
        Image map = new Image(getClass().getResourceAsStream("/images/BACK_GARNDES/Map_3.png"));
        ImageView backgroundView = new ImageView(map);
        backgroundView.setPreserveRatio(true);
        root.getChildren().add(backgroundView);

        // تنظیم دشمن (NME_3)
        Image NME = new Image(getClass().getResourceAsStream("/images/NMES/NME3.png"));
        nmeView = new ImageView(NME);
        nmeView.setFitHeight(296);
        nmeView.setFitWidth(307);
        nmeView.setLayoutX((WIDTH / 2) + 600);
        nmeView.setLayoutY(285);
        root.getChildren().add(nmeView);

        // تنظیم انیمیشن شلیک بازیکن برای سطح ۳
        PlayerShot3();
        standSprite.setPreserveRatio(true);
        standSprite.setLayoutX((WIDTH / 2) - 350);
        standSprite.setLayoutY(430);
        root.getChildren().add(standSprite);

        // نمایش سلامت بازیکن
        playerHealthText = new Text("Player Health: " + playerHealth);
        playerHealthText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        playerHealthText.setFill(Color.WHITE);
        playerHealthText.setLayoutX(20);
        playerHealthText.setLayoutY(30);
        root.getChildren().add(playerHealthText);

        // نمایش سلامت دشمن
        nmeHealthText3 = new Text("Enemy Health: " + nmeHealth3);
        nmeHealthText3.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        nmeHealthText3.setFill(Color.WHITE);
        nmeHealthText3.setLayoutX(HEIGHT - 200);
        nmeHealthText3.setLayoutY(30);
        root.getChildren().add(nmeHealthText3);

        Sword(); // شروع حرکت شمشیرها
    }

    private void nmeBoom(){
        for (int i = 0; i < BOOM_FRAME_COUNTER; i++){
            boomFrames[i] = new Image(getClass().getResourceAsStream("/images/BOOM/boom" + i + ".png"));
        }
        boomSprite = new ImageView(boomFrames[0]);
        boomSprite.setFitHeight(296);
        boomSprite.setPreserveRatio(true);

        boomAnimation = new Timeline(
                new KeyFrame(Duration.millis(BOOM_FRAME_SPEED), e -> {
                    boomCurrentFrame = (boomCurrentFrame + 1) % BOOM_FRAME_COUNTER;
                    boomSprite.setImage(boomFrames[boomCurrentFrame]);
                })
        );
    }

    // تنظیم انیمیشن شلیک برای سطح ۱
    private void PlayerShot1() {
        for (int i = 0; i < SHOT_FRAME_COUNTER; i++) {
            shotFrames[i] = new Image(getClass().getResourceAsStream("/images/NMES/PLAYER/SHOT/Shot" + (i + 1) + ".png"));
        }
        standSprite = new ImageView(shotFrames[0]);
        standSprite.setFitHeight(150);
        standSprite.setPreserveRatio(true);

        // تایم‌لاین برای انیمیشن شلیک
        animation = new Timeline(
                new KeyFrame(Duration.millis(SHOT_FRAME_SPEED), e -> update1())
        );
        animation.setCycleCount(SHOT_FRAME_COUNTER - 1);

        // شلیک با کلیک چپ ماوس
        root.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                shotCurrentFrame = 0;
                animation.stop();
                animation.playFromStart();
                Arrow(); // شلیک تیر
            }
        });
    }

    // تنظیم انیمیشن شلیک برای سطح ۲
    private void PlayerShot2() {
        for (int i = 0; i < SHOT_FRAME_COUNTER; i++) {
            shotFrames[i] = new Image(getClass().getResourceAsStream("/images/NMES/PLAYER/SHOT/Shot" + (i + 1) + ".png"));
        }
        standSprite = new ImageView(shotFrames[0]);
        standSprite.setFitHeight(150);
        standSprite.setPreserveRatio(true);

        // تایم‌لاین برای انیمیشن شلیک
        animation = new Timeline(
                new KeyFrame(Duration.millis(SHOT_FRAME_SPEED), e -> update2())
        );
        animation.setCycleCount(SHOT_FRAME_COUNTER - 1);

        // شلیک با کلیک چپ ماوس
        root.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                shotCurrentFrame = 0;
                standSprite.setImage(shotFrames[shotCurrentFrame]);
                animation.stop();
                animation.playFromStart();
                Arrow();
            }
        });
    }

    // تنظیم انیمیشن شلیک برای سطح ۳
    private void PlayerShot3() {
        for (int i = 0; i < SHOT_FRAME_COUNTER; i++) {
            shotFrames[i] = new Image(getClass().getResourceAsStream("/images/NMES/PLAYER/SHOT/Shot" + (i + 1) + ".png"));
        }
        standSprite = new ImageView(shotFrames[0]);
        standSprite.setFitHeight(150);
        standSprite.setPreserveRatio(true);

        // تایم‌لاین برای انیمیشن شلیک
        animation = new Timeline(
                new KeyFrame(Duration.millis(SHOT_FRAME_SPEED), e -> update3())
        );
        animation.setCycleCount(SHOT_FRAME_COUNTER - 1);

        // شلیک با کلیک چپ ماوس
        root.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                shotCurrentFrame = 0;
                standSprite.setImage(shotFrames[shotCurrentFrame]);
                animation.stop();
                animation.playFromStart();
                Arrow();
            }
        });
    }

    // مدیریت شلیک تیر
    private void Arrow() {
        if (arrowView != null) {
            root.getChildren().remove(arrowView);
        }
        if (arrowMovement != null) {
            arrowMovement.stop();
        }

        // پخش صدای شلیک تیر
        arrowSoundPlayer = null;
        if (arrowSoundPlayer == null) {
            Media arrowSound = new Media(getClass().getResource("/SOUNDES/ArrowSound.mp3").toString());
            arrowSoundPlayer = new MediaPlayer(arrowSound);
            arrowSoundPlayer.setVolume(1.0); // تنظیم حجم (مقدار قبلی اصلاح شد)
        }
        if (arrowSoundPlayer.getStatus() != MediaPlayer.Status.PLAYING) {
            arrowSoundPlayer.play();
        }

        // تنظیم تصویر تیر
        Image Arrow = new Image(getClass().getResourceAsStream("/images/NMES/PLAYER/ARROW/Arrow.png"));
        arrowView = new ImageView(Arrow);
        arrowView.setPreserveRatio(true);
        arrowView.setFitHeight(50);
        arrowView.setLayoutX((WIDTH / 2) - 350);
        arrowView.setLayoutY(480);
        root.getChildren().add(arrowView);

        // تایم‌لاین برای حرکت تیر
        arrowMovement = new Timeline(
                new KeyFrame(Duration.millis(4), e -> {
                    arrowView.setLayoutX(arrowView.getLayoutX() + ARROW_SPEED);

                    // بررسی برخورد تیر با دشمن
                    if (nmeView != null && arrowView.getBoundsInParent().intersects(nmeView.getBoundsInParent())) {
                        if (currentLevel == 1) {
                            nmeHealth1--;
                            nmeHealthText1.setText("Enemy Health: " + nmeHealth1);
                            if (nmeHealth1 <= 0) {
                                root.getChildren().remove(nmeView);
                                nmeView = null;
                                WinOne(); // پیروزی در سطح ۱
                            }
                            // پخش صدای برخورد برای NME_1
                            hurtSound2Player = null;
                            if (hurtSound2Player == null) {
                                Media hurtSound2 = new Media(getClass().getResource("/SOUNDES/HurtSound2.mp3").toString());
                                hurtSound2Player = new MediaPlayer(hurtSound2);
                                hurtSound2Player.setVolume(1.0); // تنظیم حجم (مقدار قبلی اصلاح شد)
                                hurtSound2Player.play();
                            }
                        } else if (currentLevel == 2) {
                            nmeHealth2--;
                            nmeHealthText2.setText("Enemy Health: " + nmeHealth2);
                            if (nmeHealth2 <= 0) {
                                root.getChildren().remove(nmeView);
                                nmeView = null;
                                WinTwo(); // پیروزی در سطح ۲
                            }
                            // پخش صدای برخورد برای NME_2
                            hurtSound3Player = null;
                            if (hurtSound3Player == null) {
                                Media hurtSound3 = new Media(getClass().getResource("/SOUNDES/HurtSound3.mp3").toString());
                                hurtSound3Player = new MediaPlayer(hurtSound3);
                                hurtSound3Player.setVolume(1.0); // تنظیم حجم (مقدار قبلی اصلاح شد)
                                hurtSound3Player.play();
                            }
                        } else if (currentLevel == 3) {
                            nmeHealth3--;
                            nmeHealthText3.setText("Enemy Health: " + nmeHealth3);
                            if (nmeHealth3 <= 0) {
                                root.getChildren().remove(nmeView);
                                nmeView = null;
                                WinThree(); // پیروزی در سطح ۳
                            }
                            // پخش صدای برخورد برای NME_3
                            hurtSound2Player = null;
                            if (hurtSound2Player == null) {
                                Media hurtSound2 = new Media(getClass().getResource("/SOUNDES/HurtSound2.mp3").toString());
                                hurtSound2Player = new MediaPlayer(hurtSound2);
                                hurtSound2Player.setVolume(1.0);
                                hurtSound2Player.play();
                            }
                        }
                        root.getChildren().remove(arrowView);
                        arrowView = null;
                        arrowMovement.stop();
                    }

                    // حذف تیر اگر از صفحه خارج شد
                    if (arrowView != null && arrowView.getLayoutX() > HEIGHT) {
                        root.getChildren().remove(arrowView);
                        arrowView = null;
                        arrowMovement.stop();
                    }
                })
        );
        arrowMovement.setCycleCount(Timeline.INDEFINITE);
        arrowMovement.play();
    }

    // نمایش صفحه پیروزی در سطح ۱
    public void WinOne() {
        root.getChildren().clear();

        // متوقف کردن کاکتوس‌ها
        if (cactusView != null) {
            root.getChildren().remove(cactusView);
            cactusView = null;
        }
        if (cactusMovement != null) {
            cactusMovement.stop();
            cactusMovement = null;
        }

        // متوقف کردن صدای سطح ۱ و پخش صدای پیروزی
        if (level1SoundPlayer != null) {
            level1SoundPlayer.stop();
        }
        win1SoundPlayer = null;
        if (win1SoundPlayer == null) {
            Media win1Sound = new Media(getClass().getResource("/SOUNDES/Win1Sound.mp3").toString());
            win1SoundPlayer = new MediaPlayer(win1Sound);
            win1SoundPlayer.play();
        }

        // پس‌زمینه سبز برای صفحه پیروزی
        Rectangle rect = new Rectangle(HEIGHT, WIDTH);
        rect.setFill(Color.rgb(114, 255, 103, 1));
        root.getChildren().add(rect);

        // متن پیروزی
        Text Win = new Text("WIN");
        Win.setFill(Color.BLACK);
        Win.setFont(Font.font("Arial", FontWeight.BOLD, 200));
        Win.setLayoutX((WIDTH / 2) + 100);
        Win.setLayoutY((HEIGHT / 2) - 200);
        root.getChildren().add(Win);

        // انتقال به سطح بعدی بعد از ۴ ثانیه
        Timeline delay = new Timeline(new KeyFrame(Duration.seconds(4), e -> Level2()));
        delay.setCycleCount(1);
        delay.play();
    }

    // نمایش صفحه پیروزی در سطح ۲
    public void WinTwo() {
        root.getChildren().clear();

        // متوقف کردن کوله‌های برف
        if (snowBollView != null) {
            root.getChildren().remove(snowBollView);
            snowBollView = null;
        }
        if (snowBollMovement != null) {
            snowBollMovement.stop();
            snowBollMovement = null;
        }

        // متوقف کردن صدای سطح ۲ و پخش صدای پیروزی
        if (level2SoundPlayer != null) {
            level2SoundPlayer.stop();
        }
        win1SoundPlayer = null;
        if (win1SoundPlayer == null) {
            Media win1Sound = new Media(getClass().getResource("/SOUNDES/Win1Sound.mp3").toString());
            win1SoundPlayer = new MediaPlayer(win1Sound);
            win1SoundPlayer.play();
        }

        // پس‌زمینه سبز برای صفحه پیروزی
        Rectangle rect = new Rectangle(HEIGHT, WIDTH);
        rect.setFill(Color.rgb(114, 255, 103, 1));
        root.getChildren().add(rect);

        // متن پیروزی
        Text Win = new Text("WIN");
        Win.setFill(Color.BLACK);
        Win.setFont(Font.font("Arial", FontWeight.BOLD, 200));
        Win.setLayoutX((WIDTH / 2) + 100);
        Win.setLayoutY((HEIGHT / 2) - 200);
        root.getChildren().add(Win);

        // انتقال به سطح بعدی بعد از ۴ ثانیه
        Timeline delay = new Timeline(new KeyFrame(Duration.seconds(4), e -> Level3()));
        delay.setCycleCount(1);
        delay.play();
    }

    // نمایش صفحه پیروزی در سطح ۳
    public void WinThree() {
        root.getChildren().clear();

        // متوقف کردن شمشیرها
        if (swordView != null) {
            root.getChildren().remove(swordView);
            swordView = null;
        }
        if (swordMovement != null) {
            swordMovement.stop();
            swordMovement = null;
        }

        // متوقف کردن صدای سطح ۳ و پخش صدای پیروزی
        if (level3SoundPlayer != null) {
            level3SoundPlayer.stop();
        }
        win1SoundPlayer = null;
        if (win1SoundPlayer == null) {
            Media win1Sound = new Media(getClass().getResource("/SOUNDES/Win1Sound.mp3").toString());
            win1SoundPlayer = new MediaPlayer(win1Sound);
            win1SoundPlayer.play();
        }

        // پس‌زمینه سبز برای صفحه پیروزی
        Rectangle rect = new Rectangle(HEIGHT, WIDTH);
        rect.setFill(Color.rgb(114, 255, 103, 1));
        root.getChildren().add(rect);

        // متن پیروزی
        Text Win = new Text("WIN");
        Win.setFill(Color.BLACK);
        Win.setFont(Font.font("Arial", FontWeight.BOLD, 200));
        Win.setLayoutX((WIDTH / 2) + 100);
        Win.setLayoutY((HEIGHT / 2) - 200);
        root.getChildren().add(Win);

        // انتقال به صفحه پایان بازی بعد از ۴ ثانیه
        Timeline delay = new Timeline(new KeyFrame(Duration.seconds(4), e -> GameEnded()));
        delay.setCycleCount(1);
        delay.play();
    }

    // مدیریت حرکت کاکتوس‌ها در سطح ۱
    private void Cactus() {
        if (cactusView != null) {
            root.getChildren().remove(cactusView);
        }
        if (cactusMovement != null) {
            cactusMovement.stop();
        }

        // تنظیم تصویر کاکتوس
        Image Cactus = new Image(getClass().getResourceAsStream("/images/NMES/PLAYER/CACTUSC/Cactus.png"));
        cactusView = new ImageView(Cactus);
        cactusView.setPreserveRatio(true);
        cactusView.setFitHeight(100);

        // تنظیم موقعیت کاکتوس (بالا یا پایین)
        int patternIndex = cactusCycleIndex % cactusPattern.length;
        double yPosition = cactusPattern[patternIndex] == 0 ? CACTUS_Y_LOW : CACTUS_Y_HIGH;
        cactusView.setLayoutX((WIDTH / 2) + 630);
        cactusView.setLayoutY(yPosition);
        root.getChildren().add(cactusView);

        // تایم‌لاین برای حرکت کاکتوس
        cactusMovement = new Timeline(
                new KeyFrame(Duration.millis(15), e -> {
                    cactusView.setLayoutX(cactusView.getLayoutX() - CACTUS_SPEED);

                    // بررسی برخورد کاکتوس با بازیکن
                    if (cactusView.getBoundsInParent().intersects(standSprite.getBoundsInParent())) {
                        playerHealth--;
                        playerHealthText.setText("Player Health: " + playerHealth);
                        root.getChildren().remove(cactusView);
                        cactusView = null;
                        cactusMovement.stop();
                        cactusCycleIndex++;
                        Cactus();

                        // پخش صدای برخورد
                        hurtSound1Player = null;
                        if (hurtSound1Player == null) {
                            Media hurtSound1 = new Media(getClass().getResource("/SOUNDES/HurtSound1.mp3").toString());
                            hurtSound1Player = new MediaPlayer(hurtSound1);
                            hurtSound1Player.setVolume(1.0);
                            hurtSound1Player.play();
                        }

                        // بررسی باخت بازیکن
                        if (playerHealth <= 0) {
                            playerHealthText.setText("Game Over!");
                            root.getChildren().remove(standSprite);
                            GameOver();
                            cactusMovement.stop();
                            if (arrowMovement != null) {
                                arrowMovement.stop();
                            }
                        }
                    }

                    // حذف کاکتوس اگر از صفحه خارج شد
                    if (cactusView != null && cactusView.getLayoutX() < -50) {
                        root.getChildren().remove(cactusView);
                        cactusView = null;
                        cactusMovement.stop();
                        cactusCycleIndex++;
                        Cactus();
                    }
                })
        );
        cactusMovement.setCycleCount(Timeline.INDEFINITE);
        cactusMovement.play();
    }

    // مدیریت حرکت کوله‌های برف در سطح ۲
    private void SnowBoll() {
        if (snowBollView != null) {
            root.getChildren().remove(snowBollView);
        }
        if (snowBollMovement != null) {
            snowBollMovement.stop();
        }

        // تنظیم تصویر کوله برف
        Image snowBoll = new Image(getClass().getResourceAsStream("/images/NMES/PLAYER/SNOW_BOLL/SnowBoll.png"));
        snowBollView = new ImageView(snowBoll);
        snowBollView.setPreserveRatio(true);
        snowBollView.setFitHeight(100);

        // تنظیم موقعیت کوله برف (بالا یا پایین)
        int patternIndex = snowBollCycleIndex % snowBollPattern.length;
        double yPosition = snowBollPattern[patternIndex] == 0 ? SNOW_BOLL_Y_LOW : SNOW_BOLL_Y_HIGH;
        snowBollView.setLayoutX((WIDTH / 2) + 630);
        snowBollView.setLayoutY(yPosition);
        root.getChildren().add(snowBollView);

        // تایم‌لاین برای حرکت کوله برف
        snowBollMovement = new Timeline(
                new KeyFrame(Duration.millis(15), e -> {
                    snowBollView.setLayoutX(snowBollView.getLayoutX() - SNOW_BOLL_SPEED);

                    // بررسی برخورد کوله برف با بازیکن
                    if (snowBollView.getBoundsInParent().intersects(standSprite.getBoundsInParent())) {
                        playerHealth--;
                        playerHealthText.setText("Player Health: " + playerHealth);
                        root.getChildren().remove(snowBollView);
                        snowBollView = null;
                        snowBollMovement.stop();
                        snowBollCycleIndex++;
                        SnowBoll();

                        // پخش صدای برخورد
                        hurtSound1Player = null;
                        if (hurtSound1Player == null) {
                            Media hurtSound1 = new Media(getClass().getResource("/SOUNDES/HurtSound1.mp3").toString());
                            hurtSound1Player = new MediaPlayer(hurtSound1);
                            hurtSound1Player.setVolume(1.0);
                            hurtSound1Player.play();
                        }

                        // بررسی باخت بازیکن
                        if (playerHealth <= 0) {
                            playerHealthText.setText("Game Over!");
                            root.getChildren().remove(standSprite);
                            GameOver();
                            if (snowBollMovement != null) {
                                snowBollMovement.stop();
                            }
                            if (arrowMovement != null) {
                                arrowMovement.stop();
                            }
                        }
                    }
                    // حذف کوله برف اگر از صفحه خارج شد
                    if (snowBollView != null && snowBollView.getLayoutX() < -50) {
                        root.getChildren().remove(snowBollView);
                        snowBollView = null;
                        snowBollMovement.stop();
                        snowBollCycleIndex++;
                        SnowBoll();
                    }
                })
        );
        snowBollMovement.setCycleCount(Timeline.INDEFINITE);
        snowBollMovement.play();
    }

    // مدیریت حرکت شمشیرها در سطح ۳
    private void Sword() {
        if (swordView != null) {
            root.getChildren().remove(swordView);
        }
        if (swordMovement != null) {
            swordMovement.stop();
        }

        // تنظیم تصویر شمشیر
        Image Sword = new Image(getClass().getResourceAsStream("/images/NMES/PLAYER/SWORD/Sword.png"));
        swordView = new ImageView(Sword);
        swordView.setPreserveRatio(true);
        swordView.setFitHeight(100);

        // تنظیم موقعیت شمشیر (بالا یا پایین)
        int patternIndex = swordCycleIndex % swordPattern.length;
        double yPosition = swordPattern[patternIndex] == 0 ? SWORD_Y_LOW : SWORD_Y_HIGH;
        swordView.setLayoutX((WIDTH / 2) + 630);
        swordView.setLayoutY(yPosition);
        root.getChildren().add(swordView);

        // تایم‌لاین برای حرکت شمشیر
        swordMovement = new Timeline(
                new KeyFrame(Duration.millis(15), e -> {
                    swordView.setLayoutX(swordView.getLayoutX() - SWORD_SPEED);

                    // بررسی برخورد شمشیر با بازیکن
                    if (swordView.getBoundsInParent().intersects(standSprite.getBoundsInParent())) {
                        playerHealth--;
                        playerHealthText.setText("Player Health: " + playerHealth);
                        root.getChildren().remove(swordView);
                        swordView = null;
                        swordMovement.stop();
                        swordCycleIndex++;
                        Sword();

                        // پخش صدای برخورد
                        hurtSound1Player = null;
                        if (hurtSound1Player == null) {
                            Media hurtSound1 = new Media(getClass().getResource("/SOUNDES/HurtSound1.mp3").toString());
                            hurtSound1Player = new MediaPlayer(hurtSound1);
                            hurtSound1Player.setVolume(1.0);
                            hurtSound1Player.play();
                        }

                        // بررسی باخت بازیکن
                        if (playerHealth <= 0) {
                            playerHealthText.setText("Game Over!");
                            root.getChildren().remove(standSprite);
                            GameOver();
                            if (swordMovement != null) {
                                swordMovement.stop();
                            }
                            if (arrowMovement != null) {
                                arrowMovement.stop();
                            }
                        }
                    }
                    // حذف شمشیر اگر از صفحه خارج شد
                    if (swordView != null && swordView.getLayoutX() < -50) {
                        root.getChildren().remove(swordView);
                        swordView = null;
                        swordMovement.stop();
                        swordCycleIndex++;
                        Sword();
                    }
                })
        );
        swordMovement.setCycleCount(Timeline.INDEFINITE);
        swordMovement.play();
    }

    // نمایش صفحه پایان بازی (پیروزی کامل)
    private void GameEnded() {
        root.getChildren().clear();

        // متوقف کردن صداها و پخش صدای پایان بازی
        if (level3SoundPlayer != null) {
            level3SoundPlayer.stop();
        }
        if (win1SoundPlayer != null) {
            win1SoundPlayer.stop();
        }
        win2SoundPlayer = null;
        if (win2SoundPlayer == null) {
            Media win2Sound = new Media(getClass().getResource("/SOUNDES/Win2Sound.mp3").toString());
            win2SoundPlayer = new MediaPlayer(win2Sound);
            win2SoundPlayer.play();
        }

        // پس‌زمینه آبی تیره
        Rectangle rect = new Rectangle(HEIGHT, WIDTH);
        rect.setFill(Color.rgb(0, 0, 50, 1));
        root.getChildren().add(rect);

        // متن پایان بازی
        Text endText = new Text("Game Ended!");
        endText.setFill(Color.WHITE);
        endText.setFont(Font.font("Arial", FontWeight.BOLD, 100));
        endText.setLayoutX((HEIGHT / 2) - 350);
        endText.setLayoutY((HEIGHT / 2) - 200);
        root.getChildren().add(endText);

        // دکمه شروع مجدد
        Button restartButton = new Button("Restart");
        restartButton.setFont(Font.font("Arial", FontWeight.BOLD, 40));
        restartButton.setLayoutX((HEIGHT / 2) - 130);
        restartButton.setLayoutY((WIDTH / 2) - 100);
        restartButton.setPrefWidth(200);
        restartButton.setPrefHeight(80);
        restartButton.setStyle("-fx-background-color: #00FF00; -fx-text-fill: black; -fx-border-radius: 5; -fx-background-radius: 5;");
        restartButton.setOnAction(e -> startGame());
        root.getChildren().add(restartButton);

        // دکمه خروج
        Button exitButton = new Button("Exit");
        exitButton.setFont(Font.font("Arial", FontWeight.BOLD, 40));
        exitButton.setLayoutX((HEIGHT / 2) - 130);
        exitButton.setLayoutY((WIDTH / 2) - 200);
        exitButton.setPrefWidth(200);
        exitButton.setPrefHeight(80);
        exitButton.setStyle("-fx-background-color: #ff774d; -fx-text-fill: black; -fx-border-radius: 5; -fx-background-radius: 5;");
        exitButton.setOnAction(e -> createStartMenu());
        root.getChildren().add(exitButton);

        // ریست کردن سلامت دشمنان
        nmeHealth1 = 20;
        nmeHealth2 = 25;
        nmeHealth3 = 30;
    }

    // نمایش صفحه باخت
    private void GameOver() {
        root.getChildren().clear();

        // متوقف کردن صداها و پخش صدای باخت
        if (level1SoundPlayer != null) level1SoundPlayer.stop();
        if (level2SoundPlayer != null) level2SoundPlayer.stop();
        if (level3SoundPlayer != null) level3SoundPlayer.stop();
        if (win1SoundPlayer != null) win1SoundPlayer.stop();
        gameOverSoundPlayer = null;
        if (gameOverSoundPlayer == null) {
            Media gameOverSound = new Media(getClass().getResource("/SOUNDES/GameOverSound.mp3").toString());
            gameOverSoundPlayer = new MediaPlayer(gameOverSound);
            gameOverSoundPlayer.play();
        }

        // پس‌زمینه قرمز تیره
        Rectangle rect = new Rectangle(HEIGHT, WIDTH);
        rect.setFill(Color.rgb(50, 0, 0, 1));
        root.getChildren().add(rect);

        // متن باخت
        Text endText = new Text("Game Over!");
        endText.setFill(Color.WHITE);
        endText.setFont(Font.font("Arial", FontWeight.BOLD, 100));
        endText.setLayoutX((HEIGHT / 2) - 300);
        endText.setLayoutY((HEIGHT / 2) - 200);
        root.getChildren().add(endText);

        // دکمه تلاش مجدد
        Button retryButton = new Button("Retry");
        retryButton.setFont(Font.font("Arial", FontWeight.BOLD, 40));
        retryButton.setLayoutX((HEIGHT / 2) - 130);
        retryButton.setLayoutY((WIDTH / 2) - 100);
        retryButton.setPrefWidth(200);
        retryButton.setPrefHeight(80);
        retryButton.setStyle("-fx-background-color: #ffde4d; -fx-text-fill: black; -fx-border-radius: 5; -fx-background-radius: 5;");
        retryButton.setOnAction(e -> startGame());
        root.getChildren().add(retryButton);

        // دکمه خروج
        Button exitButton = new Button("Exit");
        exitButton.setFont(Font.font("Arial", FontWeight.BOLD, 40));
        exitButton.setLayoutX((HEIGHT / 2) - 130);
        exitButton.setLayoutY((WIDTH / 2) - 200);
        exitButton.setPrefWidth(200);
        exitButton.setPrefHeight(80);
        exitButton.setStyle("-fx-background-color: #ff774d; -fx-text-fill: black; -fx-border-radius: 5; -fx-background-radius: 5;");
        exitButton.setOnAction(e -> createStartMenu());
        root.getChildren().add(exitButton);

        // ریست کردن سلامت دشمنان
        nmeHealth1 = 20;
        nmeHealth2 = 25;
        nmeHealth3 = 30;
    }

    // شروع پرش بازیکن
    private void startJump() {
        if (!isJumping) {
            isJumping = true;
            jumpVelocity = JUMP_HEIGHT;

            // پخش صدای پرش
            jumpSoundPlayer = null;
            if (jumpSoundPlayer == null) {
                Media jumpSound = new Media(getClass().getResource("/SOUNDES/JumpSound.mp3").toString());
                jumpSoundPlayer = new MediaPlayer(jumpSound);
            }
            if (jumpSoundPlayer.getStatus() != MediaPlayer.Status.PLAYING) {
                jumpSoundPlayer.play();
            }
        }
    }

    // به‌روزرسانی موقعیت بازیکن در پرش
    private void updateJump() {
        if (isJumping) {
            double newY = standSprite.getLayoutY() + jumpVelocity;
            if (newY >= 430) { // اگر بازیکن به زمین رسید
                newY = 430;
                isJumping = false;
            } else {
                jumpVelocity += GRAVITY; // اعمال گرانش
            }
            standSprite.setLayoutY(newY);
        }
    }

    // به‌روزرسانی انیمیشن شلیک برای سطح ۱
    private void update1() {
        shotCurrentFrame = (shotCurrentFrame + 1) % SHOT_FRAME_COUNTER;
        standSprite.setImage(shotFrames[shotCurrentFrame]);
    }

    // به‌روزرسانی انیمیشن شلیک برای سطح ۲
    private void update2() {
        shotCurrentFrame = (shotCurrentFrame + 1) % SHOT_FRAME_COUNTER;
        standSprite.setImage(shotFrames[shotCurrentFrame]);
    }

    // به‌روزرسانی انیمیشن شلیک برای سطح ۳
    private void update3() {
        shotCurrentFrame = (shotCurrentFrame + 1) % SHOT_FRAME_COUNTER;
        standSprite.setImage(shotFrames[shotCurrentFrame]);
    }

    // متد اصلی برای اجرای برنامه
    public static void main(String[] args) {
        launch(args);
    }
}
