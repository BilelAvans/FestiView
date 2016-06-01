package Simulation;

import Agenda.Agenda;
import Agenda.AgendaItem;
import Objects.Buildings.Stage;
import Objects.FestiFrame;
import Objects.FestiObject;
import Objects.ObjectManager;
import Objects.People.Destination;
import Objects.People.VisitorObject;
import Objects.Buildings.VisitableObject;
import People.Visitor;

import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Created by Bilel on 17-3-2016.
 */
public class TimeLine extends Observable implements Serializable, ActionListener, Observer {

    private int runspeed = 1;
    private Timer timeLineThread = new Timer(1000/runspeed, this);

    private int visitorSpawnCounterLimit = 100;
    private int visitorSpawnCounter = 0;

    // Links agendaitems to the stages where they will take place
    private List<AgendaItem> events = Collections.synchronizedList(new ArrayList<>());

    private Agenda          agenda;
    private FestiFrame      simuFrame;
    private ObjectManager   obMan;

    private LocalDateTime   startTime;
    private LocalDateTime   currentTime;

    // Running, Paused, Stopped...
    private String          simulationStatus = "Stopped";

    public TimeLine(Agenda agenda, ObjectManager obMan, FestiFrame simuFrame) {
        this.startTime = agenda.getStarttime();
        this.agenda = agenda;
        this.simuFrame = simuFrame;

        this.obMan = obMan;
        currentTime = startTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getCurrentTime() {
        return currentTime;
    }

    public Duration elapsedTime() {
        return Duration.between(startTime, currentTime);
    }

    //public ArrayList<AgendaItem> currentlyActiveEvents(LocalDateTime time) {
    //    return agenda.itemsNow(time);
    //}

    //public Stage getStageFromAgendaItem(AgendaItem item) {
    //    return events.get(item);
    //}
    /*
    public ArrayList<AgendaItem> getEventsFromStage(Stage stage) {
        return events.stream().filter(kv -> kv.equals(stage)).map(kv -> kv.getKey()).collect(Collectors.toCollection(ArrayList::new));
    } */

    private void visitorSpawns(){
        visitorSpawnCounter++;
        if (visitorSpawnCounter % visitorSpawnCounterLimit == 0){
            visitorSpawnCounter = 0;
            // Gen new visitor
            obMan.spawnRandomVisitor();
        }
    }

    public void start(){
        timeLineThread.start();
        this.simulationStatus = "Running";
    }

    public void run() {

        runOnce();

        visitorSpawns();

        currentTime = currentTime.plus(Duration.ofSeconds(1));
        notifyObservers();

    }

    public void pause() {
        timeLineThread.stop();
        this.simulationStatus = "Paused";
    }

    public void stop() {
        timeLineThread.stop();
        currentTime = startTime;
        obMan.clearVisitors();
        this.simulationStatus = "Stopped";
    }

    public void runOnce() {
        // Get current events
        ArrayList<AgendaItem> currentEvents = agenda.itemsNow(currentTime);
        addToEndNotifier(currentEvents);

        // Run all characters when needed
        synchronized (obMan.getPeople()) {
            try {
                for (Iterator<VisitorObject> iter = obMan.getPeople().iterator(); iter.hasNext(); ) {
                    VisitorObject p = iter.next();
                    p.run();
                    switch (p.getvState()) {
                        case FINDING:
                            System.out.println(p.getID() + "is looking for an event");
                            p.findEvent(currentEvents, p);
                            break;
                        case MOVING:
                            // Move and get status
                            if (p.moveTowardsObject() == VisitorObject.MOVEMENT_STATE.ARRIVED) {
                                System.out.println("Arrived");
                                if (p.getDestMan().currentLocation() instanceof VisitableObject) {
                                    System.out.println("Arrived 2");
                                    // Where are we going? (Remove from park and go inside the destination)
                                    obMan.goToBuilding(p, p.getDestMan().currentLocation());
                                } else {
                                    if (p.getDestMan().hasCurrent())
                                        p.getDestMan().next();
                                }
                            }
                            break;
                        default:
                            break;

                    }
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        // Process all building logic (Visitors filling their stomach, emptying their bladder, ...)
        obMan.getObjects(ObjectManager.OBJECT_TYPE_RESTRICTIONS.NONE).forEach(f -> f.run());

        checkForEventEnd();
        synchronized (obMan.getBuildings()) {
            Iterator it = obMan.getBuildings().stream().filter(s -> s.canRelease()).iterator();

            while (it.hasNext()) {
                VisitableObject vOb = (VisitableObject)it.next();
                System.out.println(((FestiObject)vOb).getID()+ " can release");

                if (vOb.canRelease()){
                    System.out.println("releasing visitor from "+ ((FestiObject) vOb).getID());
                    vOb.releaseVisitor(obMan);
                }
                /*
                if (vOb instanceof Stage) {
                    Stage stage = (Stage)vOb;
                    if (stage.canRelease()) {
                        stage.releaseVisitor(obMan);
                        System.out.println("Releasing a visitor");
                    } else {
                        System.out.println("No more visitors left");
                        //events.remove(vOb);
                        // Reset all visitors going towards this event
                        /*
                        synchronized (obMan.getPeople()) {
                            obMan.getPeople().stream().filter(p -> p.getDestMan().currentEndLocation().equals(stage))
                                    .forEach(p -> {
                                        // Reset visitor state because the destination has
                                        p.getDestMan().clear();
                                        p.setvState(VisitorObject.VisitorState.FINDING);
                                    });
                        }
                        //vOb.setCanRelease(false);
                    }
                }
        */
            }
        }
    }

    private void addToEndNotifier(ArrayList<AgendaItem> currentEvents){
        synchronized (currentEvents) {
            for (AgendaItem currentEvent : currentEvents) {
                if (!events.contains(currentEvent) && currentEvent.getEventLocation() instanceof Stage) {
                    System.out.println("Added event to notifier");
                    events.add(currentEvent);
                    // Put current visitors into seperate queue
                    currentEvent.getEventLocation().release();

                }
            }
        }
    }

    private void removeFromEvent(AgendaItem event){
        synchronized (obMan.getPeople()){
            Iterator it = obMan.getPeople().iterator();
            VisitorObject v;

            while (it.hasNext()){
                v = (VisitorObject)it.next();
                if (v.getDestMan().Current() != null){
                    v.removeIfDestinationInQueue(event);
                    v.setvState(VisitorObject.VisitorState.FINDING);
                }
            }
        }
    }

    public Agenda getAgenda() {
        return agenda;
    }

    public Timer getTimeLineThread() {
        return timeLineThread;
    }

    public int getVisitorSpawnCounterLimit() {
        return visitorSpawnCounterLimit;
    }

    public int getVisitorSpawnCounter() {
        return visitorSpawnCounter;
    }

    public List<AgendaItem> getEvents() {
        return events;
    }
    /*  Check which events are done.
        Then set the stages to releasable, so they can release the visitors.
     */
    public void checkForEventEnd(){
        // Not when we have no active events
        synchronized (events) {
            if (events.size() > 0) {
                Iterator<AgendaItem> it = events.iterator();
                AgendaItem item;
                while (it.hasNext()) {
                    item = it.next();
                    if (item.getEventLocation().canRelease() != true && currentTime.isAfter(item.getStarttime().plus(item.getTimespan()))) {
                        System.out.println("Event is done");
                        item.getEventLocation().setCanRelease();
                        //item.getEventLocation().release();
                        // Stop all visitors currently walking towards the event
                        removeFromEvent(item);
                        it.remove();
                    }

                }
            }
        }
    }

    public FestiFrame getSimuFrame() {
        return simuFrame;
    }

    public ObjectManager getObMan() {
        return obMan;
    }
    // What are we doing?
    public String getStatus(){
        return simulationStatus;
    }

    public int getRunspeed() {
        return runspeed;
    }

    public void setRunspeed(int runspeed) {
        this.runspeed = runspeed;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        run();
    }

    @Override
    public void update(Observable o, Object arg) {

    }
}
