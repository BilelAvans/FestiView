package Objects.Buildings;

import Objects.Buildings.Paths.Path;
import Objects.FestiObject;
import Objects.ImageFactory;
import Objects.ObjectManager;
import Objects.People.VisitorObject;
import People.Visitor;

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
public class FoodStand implements FestiObject, VisitableObject, Cloneable, Serializable {

    static final long serialVersionUID = 1113L;

    private String ID;

    private Rectangle2D.Double shape;
    private transient BufferedImage image = ImageFactory.getImage(ImageFactory.foodStand);

    private List<VisitorObject> visitors = Collections.synchronizedList(new ArrayList<>());
    private ArrayDeque<VisitorObject> finishedVisitors = new ArrayDeque<>();

    public FoodStand(double x, double y) {
        shape = new Rectangle2D.Double(x, y, 70, 70);
    }

    @Override
    public void draw(Graphics2D g) {
        g.setPaint(getPaint());
        g.draw(getShape());
        g.fill(getShape());
    }

    @Override
    public void move(double x, double y) {
        shape.setRect(shape.getX() + x, shape.getY() + y, shape.getWidth(), shape.getHeight());
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

    public void releaseVisitor(ObjectManager obMan) {
        VisitorObject vis = finishedVisitors.pollLast();

        if (vis != null) {
            double[] spawnCoords = spawnOutSideFoodStandArea();
            vis.setXY(spawnCoords[0], spawnCoords[1]);

            if (!obMan.isColliding(vis, ObjectManager.OBJECT_TYPE_RESTRICTIONS.NO_PATHS)) {
                vis.getDestMan().clear();
                vis.setvState(VisitorObject.VisitorState.FINDING);

                System.out.println("Toilet Spawn outside stage coords: " + spawnCoords);
                visitors.remove(vis);
                obMan.addVisitor(vis);

                return;
            } else {
                System.out.println("Cannot place due to collision");
            }
        }
    }

    public double[] spawnOutSideFoodStandArea(){
        double  x = new Random().nextInt((int)shape.getWidth() + 2 * 20),
                y;

        x += shape.getX() - 20;
        y = shape.getY();

        if (x < shape.getX() || x > shape.getX() + shape.getWidth()){
            // Dan is tie goed
            y += new Random().nextInt((int)shape.getHeight() + 20);
        } else {
            y += new Random().nextInt(20);
            y += (int)shape.getHeight() + 20;
        }

        return new double[] { x, y };

    }

    public boolean run() {
        // Do something if we have visitors
        if (visitors.size() > 0) {
            // Decrement hunger by 1 on every run
            synchronized (visitors) {
                Iterator iter = visitors.iterator();
                VisitorObject v;

                while (iter.hasNext()) {
                    v = (VisitorObject)iter.next();
                        v.getVisitorData().incrementHungerNeeds(-50);
                        if (v.getVisitorData().getHunger() == 0) {
                            iter.remove();
                            System.out.println("okfdokfd");

                            double[] outsideArea = spawnOutSideFoodStandArea();
                            v.setXY(outsideArea[0], outsideArea[1]);
                            v.setvState(VisitorObject.VisitorState.FINDING);
                            // Add to removal queue (back to the park)
                            finishedVisitors.addFirst(v);
                            setCanRelease();
                        }
                }
            }
        }

        return true;
    }

    @Override
    public void clear() {
        visitors.clear();
        finishedVisitors.clear();
    }

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public void setID(String ID) {
        this.ID = ID;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return new FoodStand(getShape().getBounds2D().getX(),
                getShape().getBounds2D().getY()
        );
    }

    @Override
    public boolean canRelease() {
        return !finishedVisitors.isEmpty();
    }

    @Override
    public boolean addVisitor(VisitorObject vObj) {
        visitors.add(vObj);
        return true;
    }

    @Override
    public void setCanRelease() {
        //
    }



    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        image = ImageFactory.getImage(ImageFactory.foodStand);
        in.defaultReadObject();
    }
}
