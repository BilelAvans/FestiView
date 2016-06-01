package Objects.Buildings;

import Agenda.AgendaItem;
import Objects.Buildings.Paths.Path;
import Objects.FestiObject;
import Objects.ImageFactory;
import Objects.ObjectManager;
import Objects.People.VisitorObject;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.*;
import java.util.List;

/**
 * Created by Bilel on 27-2-2016.
 */
public class Stage implements FestiObject, VisitableObject, Cloneable, Serializable {

    static final long serialVersionUID = 1112L;

    // Stage Area (will contain visitors)
    public Rectangle2D.Double stageArea = new Rectangle2D.Double();
    public int capacity;
    // Name of the Stage
    private String ID;
    // Stage shape
    private Rectangle2D.Double shape;
    // Data for our stage area, we will draw visitors in this area.
    private transient BufferedImage image = ImageFactory.getImage(ImageFactory.stage1);
    private transient BufferedImage stageAreaImage = ImageFactory.getImage(ImageFactory.stage1);
    // Visitors (will be in the stage area)
    private java.util.List<VisitorObject> visitors = Collections.synchronizedList(new ArrayList<>());
    private ArrayDeque<VisitorObject> finishedVisitors = new ArrayDeque<>();
    // Jump animation boolean
    private boolean jumpAniUp = false;
    // Event currently playing at the stage
    private AgendaItem currentEvent;

    // Space provided in a visitor stage per visitor
    private int[] spacePerVisitor = new int[] { 20, 20 };

    public Stage(double x, double y, int capacity, String stagename) {
        this.capacity   = capacity;
        this.ID         = stagename;

        shape = new Rectangle2D.Double(x, y, 70, 70);
        initStageArea();
    }

    // Create the stage area
    public void initStageArea() {
        int[] area = getWidthHeightCapacity();

        double startPointX = shape.getCenterX() - area[0] * (spacePerVisitor[0] / 2);
        double startPointY = shape.getY() + shape.getHeight();

        stageArea.setRect(startPointX, startPointY, spacePerVisitor[0] * area[0], spacePerVisitor[1] * area[1]);
    }

    // Based on the capacity, crete a width:height representation for the visitor area.
    private int[] getWidthHeightCapacity() {
        int     stageAreaWidth = 2, // Width/Height scale x+1:x
                stageAreaHeight = 1;

        boolean width = false;
        while (capacity > (stageAreaHeight * stageAreaWidth)) {
            if (!width) {
                stageAreaHeight++;
                width = true;
            } else {
                stageAreaWidth++;
                width = false;
            }
        }

        return new int[]{ stageAreaWidth, stageAreaHeight };
    }

    public void setCurrentEvent(AgendaItem currentEvent) {
        this.currentEvent = currentEvent;
    }

    public AgendaItem getCurrentEvent() {
        return currentEvent;
    }

    @Override
    public void draw(Graphics2D g) {
        // Draw the stage
        g.setPaint(getPaint());
        g.draw(getShape());
        g.fill(getShape());

        // Draw the stage area visitors area
        g.setPaint(Color.LIGHT_GRAY);
        g.draw(stageArea);
        g.fill(stageArea);

        // Paint visitors inside paintarea
        paintStageArea(g);
    }

    @Override
    public void move(double x, double y) {
        shape.setRect(shape.getX() + x, shape.getY() + y, shape.getWidth(), shape.getHeight());
        initStageArea();
    }

    @Override
    public Shape getShape() {
        return shape;
    }

    @Override
    public TexturePaint getPaint() {
        return new TexturePaint(image, shape);
    }

    public TexturePaint getStageAreaPaint() {
        return new TexturePaint(stageAreaImage, stageArea);
    }

    @Override
    public boolean isColliding(FestiObject f2) {
        Area area = new Area(f2.getShape());

        if (f2 instanceof Stage)
            area.add(new Area(((Stage)f2).getStageArea()));

        // Check for collision
        if (area.contains(shape.getBounds2D()) || area.contains(stageArea) ||
                area.intersects(shape.getBounds2D()) || area.intersects(stageArea) ||
                shape.contains(area.getBounds2D()) || stageArea.contains(area.getBounds2D())) {
            return true;
        }
        return false;
    }

    public Area combineStageWithArea(){
        Area a = new Area();
        a.add(new Area(stageArea));
        a.add(new Area(shape));

        return a;
    }

    public boolean run(){
        // Animations?
        jumpAnimation();
        return true;
    }

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public void setID(String ID) {
        this.ID = ID;
    }

    public boolean addVisitor(VisitorObject v) {
        if (visitors.size() < capacity){
            visitors.add(v);
            v.setDirection("north");
            positionVisitors();
            System.out.println("Added visitor to "+getID());
            return true;
        }
        // Not enough capacity
        return false;
    }

    private void paintStageArea(Graphics2D g) {
        //positionVisitors();

        visitors.forEach(v -> v.draw(g));
    }

    private void positionVisitors() {

        try {
            int[] whratio = getWidthHeightCapacity();
            System.out.println(whratio[0]+ " and "+ whratio[1]);

            for (int height = 0; height < whratio[1]; height++){
                for (int width = 0; width < whratio[0]; width++){
                    if (jumpAniUp) {
                        visitors.get(height * whratio[1] + width)
                                .setXY(stageArea.getX() + spacePerVisitor[0]/2 * width,
                                        stageArea.getY() + height * spacePerVisitor[1] + 3);
                    } else {
                        visitors.get(height * whratio[1] + width)
                                .setXY(stageArea.getX() + spacePerVisitor[0]/2 * width,
                                        stageArea.getY() + height * spacePerVisitor[1] + 3);
                    }
                }
            }
        } catch (IndexOutOfBoundsException ex){
            // No1 cares
        }
    }

    public void jumpAnimation(){
        // Make em jump a bit
        if (jumpAniUp) {
            visitors.forEach(v -> v.move(0, 3));
        }
        else {
            visitors.forEach(v -> v.move(0, -3));
        }
        // Flip state
        jumpAniUp = !jumpAniUp;
    }

    public Rectangle2D.Double getStageArea() {
        return stageArea;
    }

    public List<VisitorObject> getVisitors() {
        return visitors;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return new Stage(getShape().getBounds2D().getX(),
                getShape().getBounds2D().getY(),
                this.capacity,
                this.ID
        );
    }


    @Override
    public void clear() {
        visitors.clear();
        finishedVisitors.clear();
    }

    @Override
    public boolean canRelease() {
        return finishedVisitors.size() > 0;
    }

    public boolean canReleaseVisitor(){
        System.out.println("Finished visitor size: "+ finishedVisitors.size());
        return finishedVisitors.size() > 0;
    }

    public void setCanRelease(){
        finishedVisitors.addAll(visitors);
    }

    public void releaseVisitor(ObjectManager obMan) {
        if (canRelease())
            setCanRelease();

        VisitorObject vis = finishedVisitors.pollLast();

        if (vis != null) {
            double[] spawnCoords = spawnOutSideStageArea();
            vis.setXY(spawnCoords[0], spawnCoords[1]);

            if (!obMan.isColliding(vis, ObjectManager.OBJECT_TYPE_RESTRICTIONS.NO_PATHS)) {
                vis.getDestMan().clear();
                vis.setvState(VisitorObject.VisitorState.FINDING);

                System.out.println("Stage Spawn outside stage coords: "+ spawnCoords);
                visitors.remove(vis);
                obMan.addVisitor(vis);
                positionVisitors();
            } else {
                finishedVisitors.addLast(vis);
                System.out.println("Cannot place due to collision");
            }

            return;
        }

        System.out.println("none found");
    }

    public double[] spawnOutSideStageArea(){
        double  x = new Random().nextInt((int)stageArea.getWidth() + 2 * spacePerVisitor[0]),
                y;

        x += stageArea.getX() - spacePerVisitor[0];
        y = stageArea.getY();

        if (x < stageArea.getX() || x > getStageArea().getX() + getStageArea().getWidth()){
            // Dan is tie goed
            y += new Random().nextInt((int)stageArea.getHeight() + spacePerVisitor[1]);
        } else {
            y += new Random().nextInt(spacePerVisitor[1]);
            y += (int)stageArea.getHeight() + spacePerVisitor[1];
        }

        return new double[] { x, y };
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        image = ImageFactory.getImage(ImageFactory.stage1);
        stageAreaImage = ImageFactory.getImage(ImageFactory.stage1);
        in.defaultReadObject();
    }

    public void release(){
        visitors.forEach(v -> {
            finishedVisitors.add(v);
        });
    }
}
