package Objects.Buildings.Paths;

import Functional.Calculations;
import Objects.Buildings.FoodStand;
import Objects.Buildings.Stage;
import Objects.Buildings.Toilet;
import Objects.FestiObject;
import Objects.ImageFactory;
import Objects.ObjectManager;
import Objects.ObjectSelectorPanel;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Created by Bilel on 18-3-2016.
 */
public class Path implements FestiObject, Serializable {

    private String id;
    private Path[] allignedPaths = {null, null, null, null};
    private Rectangle2D.Double shape;
    private transient BufferedImage image;
    private PATH_THEMES theme;

    public enum PATH_THEMES {
        BLUE,
        CHECKERS,
        SAND,
        STONES
    }

    public Path(double x, double y, PATH_THEMES theme) {
        shape = new Rectangle2D.Double(x, y, 40, 40);

        setImage(theme);
    }

    public Path(double x, double y) {
        shape = new Rectangle2D.Double(x, y, 40, 40);
        setImage(ObjectSelectorPanel.theme);
    }

    private void setImage(PATH_THEMES theme){
        this.theme = theme;
        switch (theme){
            case BLUE:      image    = ImageFactory.getImage(ImageFactory.pathBlue);        break;
            case CHECKERS:  image    = ImageFactory.getImage(ImageFactory.pathCheckers);    break;
            case SAND:      image    = ImageFactory.getImage(ImageFactory.pathSand);        break;
            case STONES:    image    = ImageFactory.getImage(ImageFactory.pathStones);      break;
        }
    }

    public static Direction OppositeDirection(Direction d) {
        switch (d) {
            case NORTH:
                return Direction.SOUTH;
            case EAST:
                return Direction.WEST;
            case SOUTH:
                return Direction.NORTH;
            case WEST:
                return Direction.EAST;
        }

        return null;
    }

    // Check if a size is alligned
    public boolean alligned(Direction d) {
        return (allignedPaths[d.ordinal()] != null);
    }

    public void setXY(double x, double y){
        shape.setRect(x, y, shape.getWidth(), shape.getHeight());
    }

    // Allign a direction
    public Path allignDirection(Direction d, Path p){
        if (alligned(d))
            return allignedPaths[d.ordinal()];

        // Set X and Y
        p.setXY(getPathFromDirection(d).getShape().getBounds2D().getX(),
                getPathFromDirection(d).getShape().getBounds2D().getY());

        switch (d) {
            case NORTH:
                p.setDirection(OppositeDirection(Direction.NORTH), this);
                allignedPaths[0] = p;
                return allignedPaths[0];
            case EAST:
                p.setDirection(OppositeDirection(Direction.EAST), this);
                allignedPaths[1] = p;
                return allignedPaths[1];
            case SOUTH:
                p.setDirection(OppositeDirection(Direction.SOUTH), this);
                allignedPaths[2] = p;
                return allignedPaths[2];
            case WEST:
                p.setDirection(OppositeDirection(Direction.WEST), this);
                allignedPaths[3] = p;
                return allignedPaths[3];
        }
        return null;
    }
    // Allign a direction
    public Path allignDirectionNullIfSet(Direction d, Path p){
        if (alligned(d))
            return null;

        System.out.println(d.name() + " was not alligned yet ");
        return allignDirection(d, p);
    }

    public void setDirection(Direction d, Path p){
        allignedPaths[d.ordinal()] = p;
    }


    // Get nearest placement direction from click/Path (if in mixDistance range)
    public Direction getPlacementDirection(Path p) {
        p.getShape().getBounds2D().setRect( p.getShape().getBounds2D().getX(),
                                            p.getShape().getBounds2D().getY(),
                                            1,
                                            1);

        for (Direction direction : Direction.values()) {
            //System.out.println("Trying "+ direction);
            if (getPathFromDirection(direction).isColliding(new Point2D.Double( p.getShape().getBounds2D().getX(),
                                                                                p.getShape().getBounds2D().getY())) &&
                                                                                !alligned(direction)) {
                //System.out.println("Collision "+ direction);
                return direction;
            }
        }
        /*
        if (maxDistance >= Calculations.DistanceBetweenObjects(this, p)) {
            System.out.println("Distance between "+this.getID()+" and "+p.getID()+ " is "+ Calculations.DistanceBetweenObjects(this, p));
            double angle = Math.atan2((shape.getCenterY() - ((Rectangle2D.Double) p.getShape()).getCenterY()),
                    (shape.getCenterX() - ((Rectangle2D.Double) p.getShape()).getCenterX()));
            angle += Math.PI;
            angle = angle / Math.PI * 180;

            if (angle >= 0 && angle < 45)
                return Direction.EAST;
            if (angle >= 45 && angle < 135)
                return Direction.SOUTH;
            if (angle >= 135 && angle < 225)
                return Direction.WEST;
            if (angle >= 225 && angle < 315)
                return Direction.NORTH;
            if (angle >= 315 && angle < 360)
                return Direction.EAST;
        }
        */

        return null;
    }

    private Path getPathFromDirection(Direction d) {
        if (alligned(d))
            return allignedPaths[d.ordinal()];

        switch (d) {
            case NORTH:
                return new Path(shape.getX(), shape.getY() - shape.getHeight());
            case EAST:
                return new Path(shape.getX() + shape.getWidth(), shape.getY());
            case SOUTH:
                return new Path(shape.getX(), shape.getY() + shape.getHeight());
            case WEST:
                return new Path(shape.getX() - shape.getWidth(), shape.getY());
            default:
                return null;
        }
    }

    public Path getPath(Direction d) {
        return allignedPaths[d.ordinal()];
    }

    public void unalignPath(Direction d) {
        allignedPaths[d.ordinal()] = null;
    }

    public ArrayList<Direction> getAvailablePaths() {
        ArrayList<Direction> availableDirections = new ArrayList<>();

        for (Direction direction : Direction.values()) {
            if (alligned(direction))
                availableDirections.add(direction);
        }

        return availableDirections;
    }

    public ArrayList<Path> findPath(ArrayList<Path> paths, Path destinationPath, Direction from) {
        // Add the path, we need it to discover our way back when we find the chosen path
        paths.add(this);
        if (this.equals(destinationPath)) { // Found path!
            return paths;
        } else if (paths.size() > 1 && this.equals(paths.get(0))) {
            return null;
        }

        // Get all set paths
        for (Direction p : getAvailablePaths()) {
            if (p != from) {
                return getPath(p).findPath(paths, destinationPath, OppositeDirection(p));
            }
        }

        // We're in a leaf
        return null;

    }

    public Optional<Direction> getAlignmentDirection(Path p){
        for (Direction direction : getAvailablePaths()) {
            if (allignedPaths[direction.ordinal()].equals(p))
                return Optional.of(direction);
        }
        return Optional.ofNullable(null);
    }

    @Override
    public String getID() {
        return id;
    }

    @Override
    public void setID(String ID) {
        id = ID;
    }

    @Override
    public void move(double x, double y) {
        shape.setRect(shape.getX() + x, shape.getY() + y, shape.getWidth(), shape.getHeight());
        getAllPaths(new ArrayList<>(), new ArrayList<>()).forEach(p -> {
            p.setXY(shape.getX() + x, shape.getY() + y);
        });
    }

    public boolean run(){
        return true;
    }

    @Override
    public void draw(Graphics2D g) {
        g.setPaint(getPaint());
        g.draw(getShape());
        g.fill(getShape());
    }

    @Override
    public Shape getShape() {
        return shape;
    }

    @Override
    public Paint getPaint() {
        return new TexturePaint(image, shape);
    }

    @Override
    public boolean isColliding(FestiObject f2) {
        if (f2 instanceof Stage || f2 instanceof Toilet || f2 instanceof FoodStand || f2 instanceof Path) {
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

    public boolean isColliding(Point2D.Double dub){
        return shape.contains(dub);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return new Path(getShape().getBounds2D().getX(),
                        getShape().getBounds2D().getY()
        );
    }

    public enum Direction {
        NORTH,
        EAST,
        SOUTH,
        WEST
    }


    // Get all paths (in)directly alligned with this one
    public ArrayList<Path> getAllPaths(ArrayList<Path> paths, ArrayList<Path> visitedPaths){
        if (!visitedPaths.contains(this)) {

            visitedPaths.add(this);

            getAvailablePaths().forEach(d ->
            {
                Path p = getPath(d);
                if (!paths.contains(p)) {
                    if (!paths.contains(p))
                        paths.add(p);

                    p.getAllPaths(paths, visitedPaths);
                }
            });
        }

        return paths;
    }

    public void removeAllAlignments(){
        for (Direction direction : getAvailablePaths()) {
            allignedPaths[direction.ordinal()] = null;
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        setImage(theme);
    }
}
