package Objects;

import Functional.Message;
import GUIcomponents.ImageBackground;
import GUIcomponents.MainPage;
import GUIcomponents.SimulationPanel;
import Objects.Buildings.FoodStand;
import Objects.Buildings.Paths.Path;
import Objects.Buildings.Stage;
import Objects.Buildings.Toilet;
import Objects.People.NameTable;
import Objects.People.VisitorObject;
import People.PeopleManager;
import People.Visitor;
import Simulation.TimeLine;
import Agenda.*;
import sun.awt.resources.awt;

import javax.lang.model.element.Name;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.Optional;
import java.util.TimerTask;

/**
 * Created by Bilel on 20-2-2016.
 */
public class FestiFrame extends JPanel implements ActionListener, MouseWheelListener {

    private int     offSetX = 0,
                    offSetY = 0,
                    lastXOnClick = 0,
                    lastYOnClick = 0,
                    lastX = 0,
                    lastY = 0;

    private boolean isDragging = false;

    private ObjectManager   obMan;
    private PeopleManager   pMan;
    private Timer           timer = new Timer(1000 / 30, this);

    // Wait 300 ms for next key (to detect double click)
    private short   waitForNextKeyTime = 300;
    // Will detect clicks
    private ClickListener clickListener;
    // Our builder panel (to select objects)
    private ObjectSelectorPanel buildPanel;

    private int     lastXDragObject = 0,
                    lastYDragObject = 0;

    private double  horScaling = 1,
                    vertScaling = 1;

    private double  horScalingAtLastClickHorizontal = 1,
                    horScalingAtLastClickVertical   = 1;

    private MainPage mPage;
    //private ImageBackground imgBk = new ImageBackground(ImageFactory.getImage(ImageFactory.backSand));

    private Optional<FestiObject> draggingObject = Optional.ofNullable(null);

    private TimeLine timeLine;

    public FestiFrame(ObjectSelectorPanel buildPanel, PeopleManager pMan, Agenda a, MainPage mPage) {
        this.buildPanel = buildPanel;
        this.pMan = pMan;
        this.obMan = pMan.getObMan();

        //obMan.addVisitor(new VisitorObject(0, 0, new Visitor(NameTable.randomFirst(), NameTable.randomLast()), obMan));
        this.mPage = mPage;

        setPreferredSize(new Dimension(900, 700));

        addListeners();

        timer.start();
        //timeLine = new TimeLine(a, obMan, this);
        setupListeners();
        this.setFocusable(true);
        this.requestFocus();
    }


    public PeopleManager getPMan() {
        return pMan;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        // Set to mid
        g2.translate(getWidth() / 2 - offSetX, getHeight() / 2 - offSetY);
        g2.scale(horScaling, vertScaling);
        // Background
        //paintBackGround(g2);
        obMan.paintParkBoundries(g2);
        // Paint all our objects
        obMan.getObjects(ObjectManager.OBJECT_TYPE_RESTRICTIONS.NONE).forEach(ob -> ob.draw(g2));

    }

    private void paintBackGround(Graphics2D g){
        BufferedImage img = ImageFactory.getImage(ImageFactory.backSand);
        g.drawImage(img, 0 - getWidth(), 0 - getHeight(), null);
        /*if (ObjectSelectorPanel.background.length() > 0){
            if (ObjectSelectorPanel.background.equals("Grass"))
                g.drawImage(ImageFactory.getImage(ImageFactory.backSand), 0, 0, null);
        } */
    }

    public void addListeners() {

        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                int x = (int)((e.getX() - (getWidth() / 2) + offSetX) / horScaling);
                int y = (int)((e.getY() - (getHeight() / 2) + offSetY) / vertScaling);
                /*
                int x = (int) ((e.getX() - getWidth() / 2  - (lastX - e.getX())) * horScaling);
                int y = (int) ((e.getY() - getHeight() / 2 - (lastY - e.getY())) * horScaling);
                */
                // Find nearest object to make dragging possible
                if (obMan.findNearestFestiObject(x, y, 40).isPresent()) {
                    // Find the shape we've (possibly) clicked.
                    draggingObject = obMan.findNearestFestiObject(x, y, 40);

                    lastXDragObject = e.getX();
                    lastYDragObject = e.getY();
                    isDragging = true;
                    return;
                }

                lastX = e.getX();
                lastY = e.getY();

                lastXOnClick = e.getX();
                lastYOnClick = e.getY();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                int x = (int)((e.getX() - (getWidth() / 2) + offSetX) / horScaling);
                int y = (int)((e.getY() - (getHeight() / 2) + offSetY) / vertScaling);

                try {
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        if (!isDragging) {
                            if (lastXOnClick == e.getX() && lastYOnClick == e.getY() && buildPanel.selected != null) {
                                switch (buildPanel.selected) {
                                    case "Stage":
                                        // Ask for init info
                                        int capacity = 0;
                                        while (capacity == 0)
                                            capacity = Integer.valueOf(Message.AskQuestionDialog("Capacity?", "How many people should the stage be able to take?"));
                                        String stageName = null;
                                        while (stageName == null)
                                            stageName = Message.AskQuestionDialog("Stage Name?", "What name will we give the stage?");

                                        obMan.addBuilding(new Stage(x, y, capacity, stageName));
                                        break;
                                    case "FoodStand":
                                        FoodStand fd = new FoodStand(x, y);
                                        fd.setID(obMan.generateUniqueID(FoodStand.class));
                                        obMan.addBuilding(fd);
                                        break;
                                    case "Toilet":
                                        Toilet tl = new Toilet(x, y);
                                        tl.setID(obMan.generateUniqueID(Toilet.class));
                                        obMan.addBuilding(tl);
                                        break;
                                    case "Path":
                                        Path p = new Path(x, y, buildPanel.theme);
                                        p.setID(obMan.generateUniqueID(Path.class));
                                        obMan.addPath(p);
                                        break;
                                }
                            }

                            lastX = e.getX();
                            lastY = e.getY();
                        }
                    }
                } catch (NullPointerException ex2) {

                }
                isDragging = false;
                draggingObject = null;
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                try {
                    if (draggingObject.isPresent() && isDragging) {
                        draggingObject.get().move(e.getX() - lastXDragObject, e.getY() - lastYDragObject);
                        lastXDragObject = e.getX();
                        lastYDragObject = e.getY();
                    }
                } catch (NullPointerException ex){

                    System.out.println("this");
                    offSetX += lastX - e.getX();
                    offSetY += lastY - e.getY();

                    lastX = e.getX();
                    lastY = e.getY();
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                /*
                lastX = e.getX();
                lastY = e.getY();
                */
            }
        });

        addMouseWheelListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        // 0 or 1
        if (e.getWheelRotation() == 1) {
            horScaling -= 0.01;
            vertScaling -= 0.01;
        } else {
            horScaling += 0.01;
            vertScaling += 0.01;
        }
    }

    public ObjectManager getObMan() {
        return obMan;
    }

    public void setObMan(ObjectManager obMan) {
        //this.obMan = obMan;
        this.obMan = obMan;
    }

    public void setupListeners(){
        addMouseListener(new ClickListener(){
            @Override
            public void singleClickLeft(MouseEvent e) {
                int x = (int)((e.getX() - (getWidth() / 2) + offSetX) / horScaling);
                int y = (int)((e.getY() - (getHeight() / 2) + offSetY) / vertScaling);

                Optional<FestiObject> fObj = obMan.findNearestFestiObject(x, y, 60);
                if (fObj.isPresent()) {
                    if (e.getClickCount() == 1 && fObj.get() instanceof Stage) {
                        // Set new stage name
                        String newName = Message.AskQuestionDialog("New Stage Name?", fObj.get().getID());
                        if (newName != null) {
                            fObj.get().setID(newName);
                        }
                    }
                }
            }

            @Override
            public void doubleClickLeft(MouseEvent e) {
                int x = (int)((e.getX() - (getWidth() / 2) + offSetX) / horScaling);
                int y = (int)((e.getY() - (getHeight() / 2) + offSetY) / vertScaling);

                Optional<FestiObject> obj = obMan.findNearestFestiObject(new Path(x, y), Stage.class);
                if (obj.isPresent()) {
                    mPage.setCenterFrame("addAgendaItemGUI", obj.get());
                }
            }

            @Override
            public void singleClickRight(MouseEvent e) {
                super.singleClickRight(e);
            }

            @Override
            public void doubleClickRight(MouseEvent e) {
                int x = (int)((e.getX() - (getWidth() / 2) + offSetX) / horScaling);
                int y = (int)((e.getY() - (getHeight() / 2) + offSetY) / vertScaling);

                Optional<FestiObject> fObj = obMan.findNearestFestiObject(x, y, 60);
                if (fObj.isPresent()) {
                    obMan.removeObject(fObj.get());
                }
            }
        });

        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == 127 && draggingObject.isPresent()){
                    obMan.removeObject(draggingObject.get());
                    draggingObject = null;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
    }
}
