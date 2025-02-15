package net.gazeplay.games.trainSwitches;



public enum TrainColors {
    BLACK("black"),
    BROWN("brown"),
    DARKBLUE("darkBlue"),
    DARKGREEN("darkGreen"),
    GREEN("green"),
    GREY("grey"),
    LIGHTBLUE("lightBlue"),
    ORANGE("orange"),
    PINK("pink"),
    PURPLE("purple"),
    RED("red"),
    WHITE("white"),
    YELLOW("yellow");

    private final String color;

    TrainColors(final String color){
        this.color = color;
    }

    public String getColor() {
        return color;
    }
}
