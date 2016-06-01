package Objects;

import java.awt.*;

/**
 * Created by Bilel on 20-2-2016.
 */
public interface FestiObject {

    // Identifier
    String getID();

    void setID(String ID);

    // Move the object
    void move(double x, double y);

    // Draw the object
    void draw(Graphics2D g);

    // Run the object logic
    boolean run();

    // return the shape (Rectangle/Ellipse, etc...)
    Shape getShape();

    // return a paint object
    Paint getPaint();

    // Collision detection per object
    boolean isColliding(FestiObject f2);

    // Cloneable
    Object clone() throws CloneNotSupportedException; // Cloneable interface method (Shallow copy)
}
