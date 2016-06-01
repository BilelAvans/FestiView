package Objects;

import Objects.Buildings.FoodStand;
import Objects.Buildings.Paths.Path;
import Objects.Buildings.Stage;
import Objects.Buildings.Toilet;
import Objects.People.VisitorObject;
import People.Visitor;
import org.w3c.dom.css.Rect;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

/**
 * Created by Bilel on 4-4-2016.
 */
public class CustomFestiObject implements FestiObject, Cloneable, Serializable {

    private Rectangle2D.Double shape;

    public CustomFestiObject(int x, int y, int width, int height){
        shape = new Rectangle2D.Double(x, y, width, height);
    }

    public CustomFestiObject(Rectangle2D.Double shape){
        this.shape = shape;
    }



    @Override
    public String getID() {
        return "Placebo";
    }

    @Override
    public void setID(String ID) {
        // Idc, not gonna do it
    }

    @Override
    public void move(double x, double y) {

    }

    @Override
    public void draw(Graphics2D g) {

    }

    @Override
    public boolean run() {
        return true;
    }

    @Override
    public Shape getShape() {
        return shape;
    }

    @Override
    public Paint getPaint() {
        return null;
    }

    @Override
    public boolean isColliding(FestiObject f2) {
        if (f2 instanceof Stage || f2 instanceof Toilet || f2 instanceof FoodStand || f2 instanceof Path || f2 instanceof VisitorObject) {
            Area area = new Area(f2.getShape());

            if (f2 instanceof Stage)
                area.add(new Area(((Stage)f2).getStageArea()));

            // Check for collision
            if (area.getBounds2D().contains(shape.getBounds2D()) ||
                    area.getBounds2D().intersects(shape.getBounds2D()) ||
                    shape.getBounds2D().contains(area.getBounds2D()))
                return true;
        }
        return false;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return new CustomFestiObject(shape);
    }
}
