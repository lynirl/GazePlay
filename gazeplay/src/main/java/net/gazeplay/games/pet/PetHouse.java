package net.gazeplay.games.pet;

import java.awt.MouseInfo;

import com.sun.glass.ui.Cursor;

import javafx.animation.Animation.Status;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Dimension2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.gazeplay.GameContext;
import net.gazeplay.GameLifeCycle;
import net.gazeplay.commons.configuration.Configuration;
import net.gazeplay.commons.utils.ProgressButton;
import net.gazeplay.commons.utils.stats.Stats;
import net.gazeplay.games.cakes.CakeFactory;

@Slf4j
public class PetHouse extends Parent implements GameLifeCycle {

    private final static int INIT_MODE = 1;
    private final static int BATH_MODE = 3;
    private final static int EAT_MODE = 2;
    private final static int SPORT_MODE = 0;

    private final GameContext gameContext;
    private final Stats stats;
    private final static int LIFE_SIZE = 18;

    private final double handSize;

    private Mypet pet;
    private HBox Bars;

    private Integer waterNeeded = 100;

    public int[] it = { LIFE_SIZE, LIFE_SIZE, LIFE_SIZE };
    public Timeline[] timelines = { new Timeline(), new Timeline(), new Timeline() };
    private final Color[] color = { Color.DARKSEAGREEN, Color.ALICEBLUE, Color.DARKSALMON, Color.LAVENDER };
    private final String[] screen = { "park.jpg", "room.jpg", "kitchen.jpg", "shower.jpg" };
    private final String[] cursor = { "hand.png", "hand.png", "hand.png", "pommeau.png" };
    private final Color[] colorBar = { Color.BLUE, Color.RED, Color.GREEN };
    private final int[] regressionTime = { 2, 1, 3 };

    @Getter
    @Setter
    private int mode;

    private boolean inside = false;

    @Getter
    private Rectangle background;

    @Getter
    private Rectangle zone;

    public Rectangle hand;

    public PetHouse(GameContext gameContext, Stats stats) {
        this.gameContext = gameContext;
        this.stats = stats;

        setMode(INIT_MODE);

        Dimension2D dimension2D = gameContext.getGamePanelDimensionProvider().getDimension2D();
        this.background = new Rectangle(0, 0, dimension2D.getWidth(), dimension2D.getHeight());
        this.background.setFill(Color.BEIGE /* new ImagePattern(new Image("background.jpg")) */);
        gameContext.getChildren().add(this.background);

        double facteur = (2 / 2.5) + (1 - 2 / 2.5) / 3;

        zone = new Rectangle(0, 0, dimension2D.getWidth() / 1.7, facteur * dimension2D.getHeight());

        zone.setFill(Color.WHITE);
        zone.setX(dimension2D.getWidth() / 2 - dimension2D.getWidth() / (1.7 * 2));
        zone.setY(dimension2D.getHeight() / 2 - dimension2D.getHeight() / 2.5);

        handSize = (zone.getWidth() > zone.getHeight()) ? zone.getHeight() / 10 : zone.getWidth() / 10;

        createHand();

        createZoneEvents();

        gameContext.getChildren().add(zone);
        zone.toFront();

        gameContext.getChildren().add(this);
    }

    @Override
    public void launch() {

        createButtons();

        Bars = createBars();
        gameContext.getChildren().add(Bars);

        activateBars();

        pet = new Mypet(zone.getHeight(), zone.getWidth());

        pet.setLayoutX(zone.getX() + zone.getWidth() / 2 - pet.getBiboulew() / 2);
        pet.setLayoutY(zone.getY() + zone.getHeight() / 2 - pet.getBibouleh() / 2);

        EventHandler<MouseEvent> handevent = new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                Cursor.setVisible(false);
                inside = true;
                hand.setX(MouseInfo.getPointerInfo().getLocation().getX()
                        - gameContext.getGazePlay().getPrimaryStage().getX() - hand.getWidth() / 2);
                hand.setY(MouseInfo.getPointerInfo().getLocation().getY()
                        - gameContext.getGazePlay().getPrimaryStage().getY() - hand.getHeight() / 2);
                hand.toFront();
                hand.setVisible(true);
            }

        };

        pet.addEventFilter(MouseEvent.MOUSE_MOVED, handevent);

        gameContext.getChildren().add(pet);

    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub

    }

    public void createZoneEvents() {
        EventHandler<MouseEvent> handevent = new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                Cursor.setVisible(false);
                hand.setX(MouseInfo.getPointerInfo().getLocation().getX()
                        - gameContext.getGazePlay().getPrimaryStage().getX() - hand.getWidth() / 2);
                hand.setY(MouseInfo.getPointerInfo().getLocation().getY()
                        - gameContext.getGazePlay().getPrimaryStage().getY() - hand.getHeight() / 2);
            }

        };

        zone.addEventFilter(MouseEvent.MOUSE_MOVED, handevent);

        EventHandler<MouseEvent> enterevent = new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                Cursor.setVisible(false);
                inside = true;
                hand.toFront();
                hand.setVisible(true);
                hand.setX(MouseInfo.getPointerInfo().getLocation().getX()
                        - gameContext.getGazePlay().getPrimaryStage().getX() - hand.getWidth() / 2);
                hand.setY(MouseInfo.getPointerInfo().getLocation().getY()
                        - gameContext.getGazePlay().getPrimaryStage().getY() - hand.getHeight() / 2);
            }

        };

        zone.addEventFilter(MouseEvent.MOUSE_ENTERED, enterevent);

        EventHandler<MouseEvent> outhandevent = new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                inside = false;
                Cursor.setVisible(true);
                hand.toBack();
                hand.setVisible(false);
            }

        };

        zone.addEventFilter(MouseEvent.MOUSE_EXITED, outhandevent);
    }

    public HBox createBars() {
        Dimension2D dimension2D = gameContext.getGamePanelDimensionProvider().getDimension2D();

        HBox Bars = new HBox();

        Bars.widthProperty().addListener((observable, oldValue, newValue) -> {
            Bars.setLayoutX(dimension2D.getWidth() / 2 - newValue.doubleValue() / 2);
        });

        Bars.heightProperty().addListener((observable, oldValue, newValue) -> {
            Bars.setLayoutY(newValue.doubleValue() / 2);
        });

        double offset = dimension2D.getHeight() / LIFE_SIZE;

        Bars.setSpacing(offset);

        Bars.getChildren().addAll(createColoredProgressBar(0), createColoredProgressBar(1),
                createColoredProgressBar(2));

        return Bars;
    }

    public HBox createColoredProgressBar(int numero) {
        HBox Bar = new HBox();
        Dimension2D dimension2D = gameContext.getGamePanelDimensionProvider().getDimension2D();
        double size = dimension2D.getHeight() / 20;

        for (int i = 0; i < LIFE_SIZE; i++) {
            Rectangle r = new Rectangle(0, 0, (6 * size) / LIFE_SIZE, size);
            r.setFill(colorBar[numero]);
            Bar.getChildren().add(r);
        }

        return Bar;
    }

    public void activateBars() {
        for (int i = 0; i < 3; i++) {
            int index = getIt(i);
            HBox Bar = (HBox) Bars.getChildren().get(i);

            timelines[i] = new Timeline();
            timelines[i].setDelay(Duration.seconds(regressionTime[i]));
            timelines[i].getKeyFrames().add(new KeyFrame(Duration.millis(500),
                    new KeyValue(((Rectangle) Bar.getChildren().get(index)).fillProperty(), Color.WHITE)));

            final int number = i;
            timelines[i].setOnFinished(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    int index = getIt(number);
                    if (index >= 0) {
                        timelines[number].getKeyFrames().clear();
                        timelines[number].getKeyFrames().add(new KeyFrame(Duration.millis(500),
                                new KeyValue(((Rectangle) Bar.getChildren().get(index)).fillProperty(), Color.WHITE)));
                        timelines[number].play();
                    } else {
                        timelines[number].stop();
                    }
                }
            });

            timelines[i].play();
        }
    }

    public void createHand() {

        hand = new Rectangle(0, 0, handSize, handSize);
        hand.setMouseTransparent(true);
        hand.toBack();
        hand.setVisible(false);

        gameContext.getChildren().add(hand);
    }

    public int getIt(int i) {
        it[i]--;
        return it[i];
    }

    public void createButtons() {
        Dimension2D dimension2D = gameContext.getGamePanelDimensionProvider().getDimension2D();
        double buttonSize = dimension2D.getHeight() / 4;
        for (int i = 0; i < 4; i++) {
            ProgressButton bt = new ProgressButton();
            bt.button.setStyle("-fx-background-radius: " + buttonSize + "em; " + "-fx-min-width: " + buttonSize + "px; "
                    + "-fx-min-height: " + buttonSize + "px; " + "-fx-max-width: " + buttonSize + "px; "
                    + "-fx-max-height: " + buttonSize + "px;");
            ImageView iv = new ImageView(new Image("data/pet/images/menu" + i + ".png"));

            iv.setFitWidth(2 * buttonSize / 3);
            iv.setPreserveRatio(true);
            bt.setImage(iv);
            bt.button.setRadius(buttonSize / 2);
            bt.setLayoutY((((i % 2) + 1) * (dimension2D.getHeight() / 2.5)) - (buttonSize * 1.5));

            EventHandler<Event> buttonHandler = createprogessButtonHandler(i);

            if (i < 2) {
                bt.setLayoutX(dimension2D.getWidth() - buttonSize * 1.2);
            } else {
                bt.setLayoutX(buttonSize * 0.2);
            }

            bt.assignIndicator(buttonHandler, Configuration.getInstance().getFixationLength());
            bt.active();
            this.getChildren().add(bt);
            gameContext.getGazeDeviceManager().addEventFilter(bt.button);
        }
    }

    public EventHandler<Event> createprogessButtonHandler(int number) {
        EventHandler<Event> buttonHandler;
        buttonHandler = new EventHandler<Event>() {
            @Override
            public void handle(Event e) {

                setMode(number);

                background.setFill(color[number % 4]);
                int j = 1;
                switch (number) {
                case INIT_MODE:
                    j = 2;
                    break;
                case BATH_MODE:
                    j = 1;
                    hand.setWidth(3 * handSize);
                    hand.setHeight(2 * handSize);
                    turnOnShower();

                    break;
                case EAT_MODE:
                    j = 2;
                    break;
                case SPORT_MODE:
                    j = 1;
                    break;
                default:
                    j = 1;
                }

                Timeline t = new Timeline();
                t.getKeyFrames().add(new KeyFrame(Duration.millis(200), new KeyValue(pet.scaleXProperty(), j)));
                t.getKeyFrames().add(new KeyFrame(Duration.millis(200), new KeyValue(pet.scaleYProperty(), j)));
                t.play();

                zone.setFill(new ImagePattern(new Image("data/pet/images/" + screen[number % 4]), 0, 0, 1, 1, true));
                hand.setFill(new ImagePattern(new Image("data/pet/images/" + cursor[number % 4])));

            }
        };
        return buttonHandler;
    }

    public void refill(int i) {
        if (it[i] < LIFE_SIZE) {
            if (it[i] < LIFE_SIZE - 1) {
                it[i] = it[i] + 2;
            } else {
                it[i] = it[i] + 1;
            }
            timelines[i].stop();
            timelines[i].getKeyFrames().clear();
            timelines[i].getKeyFrames()
                    .add(new KeyFrame(Duration.millis(500), new KeyValue(
                            ((Rectangle) ((HBox) Bars.getChildren().get(i)).getChildren().get(getIt(i))).fillProperty(),
                            Color.WHITE)));
            ((Rectangle) ((HBox) Bars.getChildren().get(i)).getChildren().get(it[i])).setFill(Color.BLUE);
            ((Rectangle) ((HBox) Bars.getChildren().get(i)).getChildren().get(it[i] - 1)).setFill(Color.BLUE);
            timelines[i].play();
        }
    }

    public void turnOnShower() {

        for (int i = 0; i < 50; i++) {
            Circle c = new Circle();
            c.toFront();
            c.setMouseTransparent(true);
            c.setOpacity(0);
            c.setRadius(hand.getHeight() / 20);
            c.setFill(Color.AQUA);

            gameContext.getChildren().add(c);
            TranslateTransition t = new TranslateTransition(Duration.seconds(1 + Math.random() * 1), c);

            c.translateYProperty().addListener((observable, oldValue, newValue) -> {
                if (pet.localToParent(pet.getChildren().get(2).getBoundsInParent()).contains(c.getBoundsInParent())) {
                    t.stop();
                    t.setFromX(hand.getX() + Math.random() * (hand.getWidth() / 2));
                    t.setFromY(hand.getY() + hand.getHeight() / 2);
                    t.setToY(zone.getHeight());
                    t.play();

                    pet.setBlinkingEnabled(false);
                    if (pet.isEyesAreOpen()) {
                        pet.setHappy();
                    }

                    synchronized (waterNeeded) {

                        waterNeeded--;

                        if (waterNeeded <= 0) {
                            refill(0);
                            waterNeeded = 100;
                        }

                    }
                    ;

                } else {
                    pet.setBlinkingEnabled(true);
                }

            });

            t.setFromX(hand.getX() + Math.random() * (hand.getWidth() / 2));
            t.setFromY(hand.getY() + hand.getHeight() / 2);
            t.setToY(zone.getHeight());
            t.setOnFinished(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    if (inside && mode == BATH_MODE) {
                        t.setFromX(hand.getX() + Math.random() * (hand.getWidth() / 2));
                        t.setFromY(hand.getY() + hand.getHeight() / 2);
                        t.setToY(zone.getHeight());
                        c.setOpacity(1);
                        t.play();
                    } else if (mode == BATH_MODE) {
                        c.setOpacity(0);
                        t.play();
                    } else {
                        t.stop();
                    }

                }
            });
            t.play();
        }

    }

    public void setBath() {

    }

    public void setLunch() {

    }

    public void setSports() {

    }

}
