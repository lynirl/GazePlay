package net.gazeplay.commons.utils;

import javafx.geometry.VPos;
import javafx.scene.SnapshotParameters;
import javafx.scene.text.TextAlignment;
import net.gazeplay.commons.utils.FixationPoint;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.lang.Math;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FixationSequence {

    /**
     * Writable image used to create the fixation Sequence image
     */
    private WritableImage image;

    private static Font sanSerifFont = new Font("SanSerif", 10);

    public FixationSequence(int width, int height, LinkedList<FixationPoint> fixSeq) {

        this.image = new WritableImage(width, height);

        Canvas canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // draw the line of the sequence
        gc.setStroke(Color.ORANGE);
        gc.setLineWidth(2);

        fixSeq = vertexReduction(fixSeq, 2.5);

        for (int i = 0; i < fixSeq.size() - 1; i++) {
            gc.strokeLine(fixSeq.get(i).getY(), fixSeq.get(i).getX(), fixSeq.get(i + 1).getY(),
                    fixSeq.get(i + 1).getX());
            log.info("Point nb :" + i + ", firstGaze = "+ fixSeq.get(i).getFirstGaze()+ ", gazeDuration = " + fixSeq.get(i).getGazeDuration());
        }
        gc.setFont(sanSerifFont);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);

        // draw the circles with the labels on top
        gc.setStroke(Color.RED);
        gc.setLineWidth(0.6);

        int label_count = 1;// for the labels of the fixation sequence

        gc.setStroke(Color.RED);
        int x = fixSeq.get(0).getY();
        int y = fixSeq.get(0).getX();

        int radius = 20; // central fixation bias . Read more about it at
                         // https://imotions.com/blog/7-terms-metrics-eye-tracking/

        gc.strokeOval(x - radius / 2, y - radius / 2, radius, radius);
        gc.setFill(Color.rgb(255, 255, 0, 0.5));// yellow 50% transparency
        gc.fillOval(x - radius / 2, y - radius / 2, radius, radius);
        gc.setFill(Color.BLACK);
        gc.fillText(Integer.toString(label_count), x, y, 40);

        double r1, r2, theta1, theta2, theta_tolerance, duration;

        for (int j = 1; j < fixSeq.size() - 1; j++) {

            gc.setStroke(Color.RED);
            x = fixSeq.get(j).getY();
            y = fixSeq.get(j).getX();
            duration = fixSeq.get(j).getGazeDuration();

//            r1 = Math.sqrt(Math.pow(fixSeq.get(j - 1).getY() - x, 2) + Math.pow(fixSeq.get(j - 1).getX() - y, 2));
//            // r1 = Math.sqrt(Math.pow(x - fixSeq.get(j-1).getY(),2) + Math.pow(y - fixSeq.get(j-1).getX(),2) );
//            if (r1 == 0)
//                continue;
//            else
//                // theta1 = Math.acos((x - fixSeq.get(j-1).getY())/r1) * Math.signum(y - fixSeq.get(j-1).getX());
//                theta1 = Math.acos((fixSeq.get(j - 1).getY() - x) / r1) * Math.signum(y - fixSeq.get(j - 1).getX());
//
//            r2 = Math.sqrt(Math.pow(x - fixSeq.get(j + 1).getY(), 2) + Math.pow(y - fixSeq.get(j + 1).getX(), 2));
//            // r2 = Math.sqrt(Math.pow(fixSeq.get(j+1).getY() - x ,2) + Math.pow(fixSeq.get(j+1).getX() - y,2) );
//            if (r2 == 0)
//                continue;
//            else
//                // theta2 = Math.acos((fixSeq.get(j+1).getY() - x)/r2) * Math.signum(fixSeq.get(j+1).getX() - y);
//                theta2 = Math.acos((x - fixSeq.get(j + 1).getY()) / r2) * Math.signum(fixSeq.get(j + 1).getX() - y);
//            theta_tolerance = Math.sqrt(Math.pow(theta2 - theta1, 2));
//
//            radius = Math.toIntExact(20 +
//            /* Math.abs(fixSeq.get(j + 1).getGazeDuration()/10)+ */
//                    Math.abs(fixSeq.get(j).getGazeDuration() / 10)
//                    + Math.abs(fixSeq.get(j - 1).getGazeDuration()) / 10); // radius depends on time spent on a position
//                                                                           // .

            if (duration > 20) {
                label_count++;
                radius = 20 + (int)duration/100;
                gc.strokeOval(x - radius / 2, y - radius / 2, radius, radius);
                gc.setFill(Color.rgb(255, 255, 0, 0.5));// yellow 50% transparency
                gc.fillOval(x - radius / 2, y - radius / 2, radius, radius);
                gc.setFill(Color.BLACK);
                gc.fillText(Integer.toString(label_count), x, y, 40);

            } else
                continue;
        }
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        canvas.snapshot(params, image);
    }

    /**
     * Saves the fixation Sequence to a PNG file
     *
     * @param outputFile
     *            The output file (Must be open and writable)
     */
    // creates a clear background image
    public void saveToFile(File outputFile) {
        BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
        try {
            ImageIO.write(bImage, "png", outputFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public LinkedList<FixationPoint> vertexReduction (LinkedList<FixationPoint> allPoints, double tolerance){

        int accepted = 0;

        FixationPoint pivotVertex = allPoints.get(accepted);

        LinkedList<FixationPoint> reducedPolyline = new LinkedList<FixationPoint>();
        reducedPolyline.add(pivotVertex);

        for(int i = 1 ; i < allPoints.size()-1; i ++){
            double distance = Math.sqrt(Math.pow(pivotVertex.getY() - allPoints.get(i).getY(), 2 ) + Math.pow(pivotVertex.getX() - allPoints.get(i).getX(),2));

            if(distance <= tolerance )
                continue;
            else{
                reducedPolyline.add(allPoints.get(i));

                accepted = i;

                pivotVertex = allPoints.get(accepted);
            }
        }
        return reducedPolyline;
    }
}
