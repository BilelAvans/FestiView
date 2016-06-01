package Functional;

import Objects.Buildings.Stage;
import Objects.FestiObject;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * Created by Bilel on 26-3-2016.
 */
public class Calculations {


    public static double DistanceBetweenObjects(FestiObject f1, FestiObject f2) {
        // Get Distance from all corners
        double smallestDistance = 0;

        ArrayList<Point2D.Double> f1points = new ArrayList<>();

        f1points.add(new Point2D.Double(((Rectangle2D.Double) f1.getShape()).getX(), ((Rectangle2D.Double) f1.getShape()).getY() ));
        f1points.add(new Point2D.Double(((Rectangle2D.Double) f1.getShape()).getX() + ((Rectangle2D.Double) f1.getShape()).getWidth(),
                                        ((Rectangle2D.Double) f1.getShape()).getY() ));
        f1points.add(new Point2D.Double(((Rectangle2D.Double) f1.getShape()).getX() + ((Rectangle2D.Double) f1.getShape()).getWidth(),
                                        ((Rectangle2D.Double) f1.getShape()).getY() + ((Rectangle2D.Double) f1.getShape()).getHeight() ));
        f1points.add(new Point2D.Double(((Rectangle2D.Double) f1.getShape()).getX(),
                                        ((Rectangle2D.Double) f1.getShape()).getY() + ((Rectangle2D.Double) f1.getShape()).getHeight()));

        if (f1 instanceof Stage)
            f1points.addAll(getStageAreaPoints((Stage)f1));

        ArrayList<Point2D.Double> f2points = new ArrayList<>();
        f2points.add(new Point2D.Double(((Rectangle2D.Double) f2.getShape()).getX(), ((Rectangle2D.Double) f2.getShape()).getY() ));
        f2points.add(new Point2D.Double(((Rectangle2D.Double) f2.getShape()).getX() + ((Rectangle2D.Double) f2.getShape()).getWidth(),
                ((Rectangle2D.Double) f2.getShape()).getY() ));
        f2points.add(new Point2D.Double(((Rectangle2D.Double) f2.getShape()).getX() + ((Rectangle2D.Double) f2.getShape()).getWidth(),
                ((Rectangle2D.Double) f2.getShape()).getY() + ((Rectangle2D.Double) f2.getShape()).getHeight() ));
        f2points.add(new Point2D.Double(((Rectangle2D.Double) f2.getShape()).getX(),
                ((Rectangle2D.Double) f2.getShape()).getY() + ((Rectangle2D.Double) f2.getShape()).getHeight()));

        if (f2 instanceof Stage)
            f2points.addAll(getStageAreaPoints((Stage)f2));

        for (int counter1 = 0; counter1 < f1points.size(); counter1++){
            for (int counter2 = 0; counter2 < f2points.size(); counter2++){
                double distance = DistanceBetweenPoints(f1points.get(counter1), f2points.get(counter2));
                if (smallestDistance == 0)
                    smallestDistance = distance;
                else {
                     if (smallestDistance > distance)
                         smallestDistance = distance;
                }
            }
        }
        return smallestDistance;
    }

    public static ArrayList<Point2D.Double> getStageAreaPoints(Stage stage){
        ArrayList<Point2D.Double> extraCords = new ArrayList<>();

        extraCords.add(new Point2D.Double(stage.getStageArea().getX(), stage.getStageArea().getY()));
        extraCords.add(new Point2D.Double(stage.getStageArea().getX() + stage.getStageArea().getWidth(),
                                        stage.getStageArea().getY() ));
        extraCords.add(new Point2D.Double(((Rectangle2D.Double) stage.getShape()).getX() + stage.getStageArea().getWidth(),
                                        stage.getStageArea().getY() + stage.getStageArea().getHeight() ));
        extraCords.add(new Point2D.Double(((Rectangle2D.Double) stage.getShape()).getX(),
                                        stage.getStageArea().getY() + stage.getStageArea().getHeight()));

        return extraCords;
    }

    public static double DistanceBetweenRects(Rectangle2D.Double r1, Rectangle2D.Double r2) {
        return DistanceBetweenPoints(
                new double[] { r1.getCenterX(), r1.getCenterY() },
                new double[] { r2.getCenterX(), r2.getCenterY() }
        );
    }

    public static double DistanceBetweenPoints(Point2D.Double i1, Point2D.Double i2) {

        return Math.abs(Math.sqrt(
                Math.pow(i2.getX() - i1.getX(), 2) +
                        Math.pow(i2.getY() - i1.getY(), 2)
        ));
    }

    public static double DistanceBetweenPoints(double[] i1, double[] i2) {

        return Math.sqrt(
                Math.pow(i2[0] - i1[0], 2) +
                        Math.pow(i2[1] - i1[1], 2)
        );
    }

    // Alter angle between -180 and 180 ranges
    public static double alterAngle(double angle, int inc){
        boolean positive = inc < 0 ? false: true;

        if (positive) {
            angle += 180;
            angle += inc;
            angle %= 360;
            angle -= 180;
        } else {
            angle -= 180;
            angle += inc;
            angle %= -360;
            angle += 180;
        }

        return angle;
    }
}
