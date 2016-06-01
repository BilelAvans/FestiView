package Objects.People;

import Functional.Calculations;
import Objects.*;
import Objects.Buildings.FoodStand;
import Objects.Buildings.Paths.Path;
import Objects.Buildings.Stage;
import Objects.Buildings.Toilet;
import Objects.Buildings.VisitableObject;
import People.Visitor;
import Agenda.*;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Bilel on 29-2-2016.
 */
public class VisitorObject extends Observable implements FestiObject, Cloneable, Serializable {

    // Texture stuff
    protected Rectangle2D.Double shape = new Rectangle2D.Double();
    protected transient TexturePaint paint;
    // All textures
    protected transient BufferedImage[] images;
    protected transient BufferedImage image;
    // Object manager for updates
    private ObjectManager obMan;
    // Visitor data (Name, etc..)
    private Visitor visitor;
    // Destinations
    private DestinationManager destMan = new DestinationManager();
    // State of the visitor
    private VisitorState vState = VisitorState.FINDING; // What is it doing

    private int collisionsInARow = 0;

    private double lastAngle;

    private short isStuck = 0;
    private boolean stillColliding = false;


    public VisitorObject(double x, double y, Visitor v, ObjectManager obMan) {
        shape.setRect(x, y, 20, 20);
        this.visitor = v;

        this.obMan = obMan;
        // Load a profile of skins (For movement angle specific sprites)
        images = ImageFactory.createSkinProfileSet();
        image  = images[0];
    }


    public void init() {
        shape = new Rectangle2D.Double(shape.x, shape.y,
                image.getWidth(), image.getHeight());
        paint = new TexturePaint(image, shape);
    }

    public void draw(Graphics2D g2) {
        init();
        g2.setPaint(paint);
        g2.draw(shape);
        g2.fill(shape);
    }

    public boolean run(){
        // Increment the needs
        visitor.runOnce();

        return true;
    }


    @Override
    public void move(double x, double y) { // Move without collision detection
        shape.setFrame(shape.x + x, shape.y + y, shape.getWidth(), shape.getHeight());
        //visitor.runOnce();
    }

    private void alterTextureByMovementDirection(double angle){
        if (angle > -180 && angle <= -90)
            image = images[0];
        else if (angle > -90 && angle <= 0)
            image = images[1];
        else if (angle > 0 && angle <= 90)
            image = images[2];
        else if (angle > 90 && angle <= 180)
            image = images[3];
    }
    // Move in angle with collision and destination handler active
    public void move(double angle) {
        System.out.println("Init pos, X: "+getShape().getBounds2D().getX()+" Y:"+ getShape().getBounds2D().getY());
        setXY(  shape.x + 1 * Math.cos(angle / 180 * Math.PI),
                shape.y + 1 * Math.sin(angle / 180 * Math.PI));
        System.out.println("Post pos, X: "+getShape().getBounds2D().getX()+" Y:"+ getShape().getBounds2D().getY());
        alterTextureByMovementDirection(angle);
    }
    // Will return the distance between the destination and our object
    public MOVEMENT_STATE moveTowardsDestination() {
        // Store old position
        double oldX = shape.getX();
        double oldY = shape.getY();
        // Get angle
        double angle = Math.toDegrees(Math.atan2(   destMan.Current().getY() - getShape().getBounds2D().getY(),
                                                    destMan.Current().getX() - getShape().getBounds2D().getX()
                                                    ));
        lastAngle = angle; // Register last moved angle
        move(angle); // Move
        // Colliding = go back
        if (destMan.Current().returnFObject().get() instanceof VisitableObject) {
            if (this.isColliding(destMan.Current().returnFObject().get()))
                return MOVEMENT_STATE.ARRIVED;
        }

        if (Calculations.DistanceBetweenObjects(this, destMan.Current().returnFObject().get()) < 5) {
            return MOVEMENT_STATE.ARRIVED;
        }

        // Check collision with end dest
        if (Calculations.DistanceBetweenObjects(this, destMan.currentEndLocation()) < 5 ||
                this.isColliding(destMan.currentEndLocation())) {

            //destMan.clear();
            return MOVEMENT_STATE.ARRIVED;
        }

        if (destMan.Current().returnFObject().get() instanceof Path && destMan.peekNext().get().returnFObject().get() instanceof Path){
            //System.out.println(visitor.getfName() + " "+ visitor.getlName()+ " is in between 2 paths");
            System.out.println("Destination is "+ destMan.Current().getX() +":"+ destMan.Current().getY());
            /*
            if (Calculations.DistanceBetweenObjects(destMan.Current().returnFObject().get(), destMan.peekNext().get().returnFObject().get()) < 30){
                return MOVEMENT_STATE.ARRIVED;
            }
            */
        }

        if (obMan.isColliding(this, ObjectManager.OBJECT_TYPE_RESTRICTIONS.NO_PATHS)) {
            //System.out.println("Dist betw "+Calculations.DistanceBetweenObjects(this, destMan.Current().returnFObject().get()));

            //System.out.println("Collision found during movement towards destination with "+ visitor.getlName() +" "+ visitor.getfName());

            collisionsInARow++;
            if (collisionsInARow < 3) {
                // Do nothing for 3 frames
                setXY(oldX, oldY);
                return MOVEMENT_STATE.COLLISION;
            }
            else {
                // Find object we're colliding with
                FestiObject collisionObject = obMan.getCollisionObject(this, ObjectManager.OBJECT_TYPE_RESTRICTIONS.NO_PATHS);
                // Calc angle with the found object
                double deltaY = collisionObject.getShape().getBounds2D().getCenterY() - this.getShape().getBounds2D().getCenterY();
                double deltaX = collisionObject.getShape().getBounds2D().getCenterX() - this.getShape().getBounds2D().getCenterX();
                // Back to old position
                setXY(oldX, oldY);

                double angleBetweenVisitorAndCollisionObject = Math.atan2(deltaY, deltaX) / Math.PI * 180;

                // Alter the angle to a new direction, in which we hope not to collide any longer
                if (angleBetweenVisitorAndCollisionObject < 0)
                    angleBetweenVisitorAndCollisionObject = Calculations.alterAngle(angleBetweenVisitorAndCollisionObject, -90 * isStuck);
                else
                    angleBetweenVisitorAndCollisionObject = Calculations.alterAngle(angleBetweenVisitorAndCollisionObject, 90 * isStuck);


                double currentX = getShape().getBounds2D().getX();
                double currentY = getShape().getBounds2D().getY();

                move(angleBetweenVisitorAndCollisionObject);
                // Did the movement cause a new collision? If so, go back
                if (obMan.isColliding(this, ObjectManager.OBJECT_TYPE_RESTRICTIONS.NO_PATHS) && !stillColliding){
                    this.setXY(currentX, currentY); // Revert movement
                    isStuck++;
                } else {
                    // Check if our collision problem is solved
                    //move(angle);
                    if (obMan.isColliding(this, ObjectManager.OBJECT_TYPE_RESTRICTIONS.NO_PATHS)){
                        stillColliding = true;
                        setXY(oldX, oldY);
                        //isStuck++;
                    }
                    else {
                        // No more problems
                    }
                    return MOVEMENT_STATE.CORRECTED;
                }
            }
        } else {
            collisionsInARow = 0;
        }

       return MOVEMENT_STATE.MOVING;
    }

    // Teleport instead of move.
    public void setXY(double x, double y) {
        shape.setRect(x, y, shape.getWidth(), shape.getHeight());
    }

    public enum MOVEMENT_STATE {
        PAUSE,
        CORRECTED,
        MOVING,
        COLLISION,
        ARRIVED,
    }

    // Move to a Destination
    public MOVEMENT_STATE moveTowardsObject() {
        // Do we have a destination?
        if (destMan.Current() != null) {
            // Go to event when available
            System.out.println("Moving towards "+ destMan.Current().returnFObject().get().getID());
            return moveTowardsDestination();
            // Check if we arrived at a visitable object (Toilet, Stage, etc...)
        }

        return MOVEMENT_STATE.MOVING;
    }

    public void findEvent(ArrayList<AgendaItem> currentlyAvailable, VisitorObject v) {
        // Don't we have some events in our queue we still need to go to?
        if (!destMan.hasCurrent() && destMan.hasAny()) {
            destMan.next();

            return;
        }
        // Find event
        try {
            VisitableObject destinationObject = null;
            if (visitor.isHungry()) {
                //System.out.println(visitor.isHungry());
                Optional<FestiObject> fs = obMan.findNearestFestiObject(this, FoodStand.class);
                if (fs.isPresent()) {
                    destinationObject = (VisitableObject) fs.get();
                    System.out.println("Found Food stand");
                }

            } else if (visitor.fullBladder() && destinationObject == null) {
                System.out.println(visitor.fullBladder());
                Optional<FestiObject> fs = obMan.findNearestFestiObject(this, Toilet.class);
                if (fs.isPresent())
                    destinationObject = (VisitableObject)fs.get();
            } else {
                if (destinationObject == null) {
                    System.out.println("Find concert to visit");

                    // No events available
                    if (currentlyAvailable.size() > 0) {
                        //ArrayList<AgendaItem> possibleEvents = currentlyAvailable.stream().filter(s -> s.getEventLocation() != null).collect(Collectors.toCollection(ArrayList::new));
                        int genNum = (new Random()).nextInt(currentlyAvailable.size());
                        System.out.println("Gen'd number "+ genNum);
                        destinationObject = currentlyAvailable.get(genNum).getEventLocation();
                        // Select random event
                        System.out.println("Events possible total: "+ currentlyAvailable.size());
                        System.out.println("Going to "+ ((FestiObject)destinationObject).getID());
                    }
                }

            }
            //System.out.println("Not hungry or toilety");
            if (destinationObject != null){
                System.out.println("we'd like to go to "+ ((FestiObject)destinationObject).getID());
                //destMan.add(obMan.getFullPathBetweenObjects(v, (FestiObject)destinationObject, new ArrayList<>()));
                obMan.getFullPathBetweenObjects(v, (FestiObject)destinationObject, new ArrayList<>()).forEach(d -> {
                    System.out.println("Adding dest "+ d.returnFObject().get().getID());
                    destMan.add(d);
                });

                System.out.println("Adding dest "+ ((FestiObject) destinationObject).getID());
                // Push the queue (Start walking towards first destination)
                destMan.addAndPush(new Destination((FestiObject)destinationObject));
                vState = VisitorState.MOVING;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Shape getShape() {
        return shape;
    }

    @Override
    public TexturePaint getPaint() {
        return new TexturePaint(image, shape);
    }

    @Override
    public boolean isColliding(FestiObject f2) {
        if (f2 instanceof Stage || f2 instanceof Toilet || f2 instanceof FoodStand || f2 instanceof VisitorObject) {
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
    public String getID() {
        if (visitor != null)
            return visitor.getfName()+ " " + visitor.getlName();
        else
            return "Visitor";
    }

    @Override
    public void setID(String ID) { // Not allowed
    }



    @Override
    public Object clone() throws CloneNotSupportedException {
        return new VisitorObject(getShape().getBounds2D().getX(),
                getShape().getBounds2D().getY(),
                visitor,
                obMan
        );
    }

    public enum VisitorState {
        FINDING,
        MOVING,
        AT_CONCERT,
        AT_TOILET,
        AT_FOODSTAND,
        I_WANT_TO_BREAK_FREE
    }

    public VisitorState getvState() {
        return vState;
    }

    public void setvState(VisitorState vState) {
        this.vState = vState;
    }

    public Visitor getVisitorData() {
        return visitor;
    }

    public DestinationManager getDestMan() {
        return destMan;
    }

    public void setDestMan(DestinationManager destMan) {
        this.destMan = destMan;
    }

    public void setDirection(String direction){
        switch (direction){
            case "north":   image = images[3]; break;
            case "east":    image = images[1]; break;
            case "south":   image = images[2]; break;
            case "west":    image = images[0]; break;
        }
    }


    public double getLastAngle() {
        return lastAngle;
    }

    public void setLastAngle(double lastAngle) {
        this.lastAngle = lastAngle;
    }

    public boolean removeIfDestinationInQueue(AgendaItem event){
        return destMan.removeIfDestinationInQueue(event);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        images = ImageFactory.createSkinProfileSet();
        image  = images[0];
        paint = new TexturePaint(image, shape);
    }
}
