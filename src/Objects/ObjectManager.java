package Objects;

import Objects.Buildings.FoodStand;
import Objects.Buildings.Paths.*;
import Objects.Buildings.Stage;
import Objects.Buildings.Toilet;
import Objects.Buildings.VisitableObject;
import Objects.People.Destination;
import Objects.People.PathDestination;
import Objects.People.VisitorObject;
import People.Visitor;
import Functional.Calculations;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Bilel on 24-2-2016.
 */
public class ObjectManager extends Observable implements Serializable {

    static final long serialVersionUID = 1111L;

    // All Stages
    private List<Stage> stages = Collections.synchronizedList(new ArrayList<>());
    // All Stages
    private List<FoodStand> foodstands = Collections.synchronizedList(new ArrayList<>());
    // All Stages
    private List<Toilet> toilets = Collections.synchronizedList(new ArrayList<>());
    // People
    private List<VisitorObject> people = Collections.synchronizedList(new ArrayList<>());
    // Paths
    private PathCreation pathsCreation = new PathCreation();

    // Create the boundries for our park (In which our park can exist)
    private Rectangle2D.Double  parkBoundries;

    // Constructor
    public ObjectManager() {

    }

    // Return objects
    public ArrayList<VisitableObject> getBuildings() {
        ArrayList<VisitableObject> objs = new ArrayList<>();
        objs.addAll(foodstands);
        objs.addAll(toilets);
        objs.addAll(stages);

        return objs;
    }

    public boolean visitableObjectExists(VisitableObject vObj){
        return getObjects(OBJECT_TYPE_RESTRICTIONS.VISITABLE_ONLY).stream().filter(v -> v.getID().equals(((FestiObject)vObj).getID())).findAny().isPresent();
    }

    // Add Object Building
    public boolean addBuilding(VisitableObject ob) {
        // Check if it's really a building object
        if (!isColliding(ob) && !visitableObjectExists(ob)) {
            if (ob instanceof FoodStand) {
                foodstands.add((FoodStand) ob);
                calculateParkBoundries();
                viewChanged();
                return true;
            }
            if (ob instanceof Stage) {
                stages.add((Stage) ob);
                calculateParkBoundries();
                viewChanged();
                return true;
            }
            if (ob instanceof Toilet) {
                toilets.add((Toilet) ob);
                calculateParkBoundries();
                viewChanged();
                return true;
            }
        }
        System.out.println("Not adding building");
        return false;
    }

    public boolean addVisitor(VisitorObject v) {
        if (!isColliding(v, OBJECT_TYPE_RESTRICTIONS.NO_PATHS)) {
            v.setID(generateUniqueID(v.getClass()));
            people.add(v);
            viewChanged();
            return true;
        }

        System.out.println("Visitor is colliding, cannot add");
        return false;
    }

    public boolean addPath(Path p) {
        p.setID(generateUniqueID(Path.class));
        boolean succeeded =  pathsCreation.addPath(p);

        calculateParkBoundries();
        return succeeded;
    }


    public enum OBJECT_TYPE_RESTRICTIONS {
        NONE,
        NO_PATHS, NO_VISITORS, NO_BUILDINGS, NO_STAGE_AREAS,
        VISITABLE_ONLY, BUILDINGS_ONLY, VISITORS_ONLY, STAGE_AREAS_ONLY,
    }

    public ArrayList<FestiObject> getObjects(OBJECT_TYPE_RESTRICTIONS type) {
        /*if (type != OBJECT_TYPE_RESTRICTIONS.NO_STAGE_AREAS)
            stages.forEach(s -> objs.add(new CustomFestiObject(s.getStageArea())));
*/
        ArrayList<FestiObject> objs = new ArrayList<>();

        if (type != OBJECT_TYPE_RESTRICTIONS.NO_BUILDINGS)
            objs.addAll(getBuildings().stream().map(obj -> (FestiObject)obj).collect(Collectors.toCollection(ArrayList::new)));
        if (type != OBJECT_TYPE_RESTRICTIONS.NO_PATHS)
            objs.addAll(pathsCreation.getAllPaths().stream().map(obj -> (FestiObject)obj).collect(Collectors.toCollection(ArrayList::new)));
        if (type != OBJECT_TYPE_RESTRICTIONS.NO_VISITORS)
            objs.addAll(people.stream().map(obj -> (FestiObject)obj).collect(Collectors.toCollection(ArrayList::new)));
        if (type != OBJECT_TYPE_RESTRICTIONS.NO_STAGE_AREAS)
            stages.forEach(s -> objs.add(new CustomFestiObject(s.getStageArea())));

        return objs;
    }


    public boolean goToBuilding(VisitorObject visitor, FestiObject fObj) {
        System.out.println("Going to "+ fObj.getID());
        System.out.println("stage in obman at "+ fObj.hashCode());
        if (fObj instanceof Stage) {
            return goToConcert(visitor, (Stage) fObj);
        }
        else if (fObj instanceof FoodStand) {
            return goEat(visitor, (FoodStand) fObj);
        }
        else if (fObj instanceof Toilet) {
            return goToToilet(visitor, (Toilet) fObj);
        }

        return true;
    }

    private boolean goToConcert(VisitorObject visitor, Stage s) {
        if (s.addVisitor(visitor)){
            visitor.setvState(VisitorObject.VisitorState.AT_CONCERT);
            people.remove(visitor);
            s.addVisitor(visitor);
            viewChanged();
            return true;
        }

        return false;
    }

    private boolean goEat(VisitorObject visitor, FoodStand food){
        if (food.addVisitor(visitor)){
            visitor.setvState(VisitorObject.VisitorState.AT_FOODSTAND);
            people.remove(visitor);
            food.addVisitor(visitor);
            viewChanged();
            return true;
        }

        return false;
    }

    private boolean goToToilet(VisitorObject visitor, Toilet toilet){
        if (toilet.addVisitor(visitor)){
            visitor.setvState(VisitorObject.VisitorState.AT_TOILET);
            people.remove(visitor);
            toilet.addVisitor(visitor);
            viewChanged();
            return true;
        }

        return false;
    }

    public boolean isColliding(VisitableObject v){
        return isColliding((FestiObject)v, OBJECT_TYPE_RESTRICTIONS.NO_PATHS);
    }

    public boolean isColliding(FestiObject f2, OBJECT_TYPE_RESTRICTIONS obj_types) {
        // Compare with present collision
        for (FestiObject obj : getObjects(obj_types)) {
            if (obj.isColliding(f2) && !f2.equals(obj)) {

                System.out.println("Colliding with " + obj.getClass().toString());
                return true;
            }
        }

        return false;
    }

    private boolean IDAlreadyExists(String ID) {
        try {
            return getObjects(OBJECT_TYPE_RESTRICTIONS.NONE).stream().filter(obj -> obj.getID().equals(ID)).findAny().isPresent();
        } catch (NullPointerException e) {
            return false;
        }
    }

    public String generateUniqueID(Class<?> type) {
        int counter = 1;
        while (true) {
            if (!IDAlreadyExists(type.getTypeName() + counter))
                return type.getTypeName() + Integer.toString(counter);        // Return the unique ID we found

            counter++;
        }
    }

    // fObj is the object we are currently at, type is the required type we are looking for
    public Optional<FestiObject> findNearestFestiObject(FestiObject fObj, Class<?> type) {
        ArrayList<FestiObject> list = getObjects(OBJECT_TYPE_RESTRICTIONS.NONE).stream().filter(o -> type.isInstance(o)).collect(Collectors.toCollection(ArrayList::new));

        FestiObject obj = null;
        double minDistanceBetween = 0;

        for (FestiObject fob: list){
            if (Calculations.DistanceBetweenObjects(fob, fObj) < minDistanceBetween || minDistanceBetween == 0) {
                if (!fob.equals(fObj)) {
                    obj = fob;
                    minDistanceBetween = Calculations.DistanceBetweenObjects(fob, fObj);
                }
            }
        }
        //System.out.println("Retuning "+obj.getID());
        return Optional.ofNullable(obj);
    }

    // Same as above, but with custom boundries
    public Optional<FestiObject> findNearestFestiObjectWithBoundries(FestiObject fObj, Class<?> type, ArrayList<FestiObject> boundries) {
        System.out.println("amount of objects :"+ boundries.size());
        Optional<FestiObject> obj = getObjects(OBJECT_TYPE_RESTRICTIONS.NONE).stream().filter(o -> type.isInstance(o) && boundries.contains(fObj)).min((fo, fx) -> (int)Calculations.DistanceBetweenObjects(fo, fObj));

        return obj;
    }

    // Based off X, Y coordinates
    public Optional<FestiObject> findNearestFestiObject(double x, double y, double maxDistance) {
        return getObjects(OBJECT_TYPE_RESTRICTIONS.NONE).stream().filter(o -> (int) Calculations.DistanceBetweenPoints(
                    new double[]{o.getShape().getBounds2D().getCenterX(),
                            o.getShape().getBounds2D().getCenterY()},
                    new double[]{x,
                            y}) <= maxDistance)
                    .min((obj, u) -> (int) Calculations.DistanceBetweenPoints(
                            new double[]{obj.getShape().getBounds2D().getCenterX(),
                                    obj.getShape().getBounds2D().getCenterY()},
                            new double[]{x,
                                    y}));
    }

    public void removeObject(FestiObject f1) {
        // Remove the
        if (f1 instanceof FoodStand) {
            foodstands.remove(f1);
        }
        else if (f1 instanceof Stage) {
            stages.remove(f1);
        }
        else if (f1 instanceof Toilet) {
            toilets.remove(f1);
        }
        else if (f1 instanceof Path) {
            pathsCreation.removePath((Path) f1);
        }

        viewChanged();
    }

    public List<Stage> getStages() throws NullPointerException {
        return stages;
    }

    public Optional<Stage> getStage(String name) throws NullPointerException {
        return stages.stream().filter(b -> b.getID().equals(name)).findFirst();
    }
/*
    public ArrayList<Path> pathsToWalkFromFestiToFesti(FestiObject f1, FestiObject f2) {
        try {
            // Find nearest path where they can allign with the path
            System.out.println("F1 = "+ f1.getID());
            System.out.println("F2 = "+ f2.getID());
            Path pathStart = (Path) findNearestFestiObject(f1, Path.class);
            Path pathEnd = (Path) findNearestFestiObject(f2, Path.class);
            System.out.println("Found pathstart: " + pathStart.getID());
            System.out.println("Found pathend: "   + pathEnd.getID());
            // Start our search.
            // PathStart is our begin path and Pathend is destination path
            ArrayList<Path> walkingPaths = pathStart.findPath(new ArrayList<>(), pathEnd, null);
            System.out.println("From start to end length: "+walkingPaths.size());
            // Did we find the endpath?
            if (walkingPaths != null) {
                for (Path walkingPath : walkingPaths) {
                    System.out.println("Found path: " + walkingPath.getID());
                }

                return walkingPaths;
            } else {

            }
        } catch (NullPointerException e) {
        }
        // Return empty list to avoid NPException
        return new ArrayList<>();
    }
*/
    public List<VisitorObject> getPeople() {
        return people;
    }

    public List<FoodStand> getFoodstands() {
        return foodstands;
    }

    public List<Toilet> getToilets() {
        return toilets;
    }

    private void calculateParkBoundries(){
        System.out.println("Object count "+getObjects(OBJECT_TYPE_RESTRICTIONS.NONE).size());
        FestiObject leftX = getObjects(OBJECT_TYPE_RESTRICTIONS.NO_VISITORS).stream().min((f, u) -> (int)f.getShape().getBounds2D().getX()).get(),
                    topY = getObjects(OBJECT_TYPE_RESTRICTIONS.NO_VISITORS).stream().min((f, u) -> (int)f.getShape().getBounds2D().getX()).get(),
                    rightX = getObjects(OBJECT_TYPE_RESTRICTIONS.NO_VISITORS).stream().max((f, u) -> (int)f.getShape().getBounds2D().getX()).get(),
                    bottomY = getObjects(OBJECT_TYPE_RESTRICTIONS.NO_VISITORS).stream().max((f, u) -> (int)f.getShape().getBounds2D().getY()).get();

        double width  = rightX.getShape().getBounds2D().getX() - leftX.getShape().getBounds2D().getX();
        double height = bottomY.getShape().getBounds2D().getY() - topY.getShape().getBounds2D().getY();

        //System.out.println(leftX+" "+topY+" "+width+" "+height);

        parkBoundries = new Rectangle2D.Double( leftX.getShape().getBounds2D().getX() - width / 6 - 250,
                                                topY.getShape().getBounds2D().getY() - height / 6 - 250,
                                                width + width / 3 + 500,
                                                height + height / 3 + 500);
    }

    public void paintParkBoundries(Graphics2D g){
        if (parkBoundries != null) {
            g.setColor(Color.cyan);
            g.draw(parkBoundries);
        }
    }

    public void spawnRandomVisitor(){
        // Only add new one if we haven't reached max already
        double[] spawnCoords = spawnOutSideStageArea();

        addVisitor(new VisitorObject(spawnCoords[0], spawnCoords[1], new Visitor(), this));
    }

    public void viewChanged(){
        setChanged();
        notifyObservers();
        clearChanged();
    }

    public void clearVisitors(){
        people.clear();
        getBuildings().clear();
    }

    public double[] spawnOutSideStageArea(){
        if (parkBoundries == null)
            calculateParkBoundries();

        double  x = new Random().nextInt((int)parkBoundries.getWidth() + 2 * 20),
                y;

        x += parkBoundries.getX() - 20;
        y = parkBoundries.getY();

        if (x < parkBoundries.getX() || x > parkBoundries.getX() + parkBoundries.getWidth()){
            // Dan is tie goed
            y += new Random().nextInt((int)parkBoundries.getHeight() + 20);
        } else {
            y += new Random().nextInt(20);
            y += (int)parkBoundries.getHeight() + 20;
        }

        return new double[] { x, y };

    }

    public ArrayList<FestiObject> getObjectsBetween(double[] ends, Class<?> type){

        return  getObjects(OBJECT_TYPE_RESTRICTIONS.NO_VISITORS).stream()
                .filter(o -> type.isInstance(o))
                .filter(o -> o.getShape().getBounds2D().getX() > ends[0] &&
                        o.getShape().getBounds2D().getX() < ends[1] &&
                        o.getShape().getBounds2D().getY() > ends[2] &&
                        o.getShape().getBounds2D().getY() > ends[3])
                .collect(Collectors.toCollection(ArrayList::new));

    }
    // int[]: x < left, right, bottom, top >
    public double[] outsidesOfTwoObjects(FestiObject fob1, FestiObject fob2){
        double[] ends = new double[4];
        //
        if (fob1.getShape().getBounds2D().getX() < fob2.getShape().getBounds2D().getX())
            ends[0] = fob1.getShape().getBounds2D().getX();
        else
            ends[0] = fob2.getShape().getBounds2D().getX();

        if (fob1.getShape().getBounds2D().getX() + fob1.getShape().getBounds2D().getWidth() <
                fob2.getShape().getBounds2D().getX() + fob2.getShape().getBounds2D().getWidth())
            ends[1] = fob2.getShape().getBounds2D().getX() + fob2.getShape().getBounds2D().getWidth();
        else
            ends[1] = fob1.getShape().getBounds2D().getX() + fob1.getShape().getBounds2D().getWidth();

        if (fob1.getShape().getBounds2D().getY() < fob2.getShape().getBounds2D().getY())
            ends[2] = fob1.getShape().getBounds2D().getY();
        else
            ends[2] = fob2.getShape().getBounds2D().getY();

        if (fob1.getShape().getBounds2D().getY() + fob1.getShape().getBounds2D().getHeight() <
                fob2.getShape().getBounds2D().getY() + fob2.getShape().getBounds2D().getHeight())
            ends[3] = fob2.getShape().getBounds2D().getY() + fob2.getShape().getBounds2D().getHeight();
        else
            ends[3] = fob1.getShape().getBounds2D().getY() + fob1.getShape().getBounds2D().getHeight();

        System.out.println("Returning 4 ends leftX "+ ends[0] + ", rightX "+ ends[1] +", topY "+ ends[2] +", bottomY "+ ends[3]);

        return ends;

    }

    public ArrayList<PathDestination> getFullPathBetweenObjects(FestiObject start, FestiObject end, ArrayList<Destination> dests){
        // The list we will be returning
        ArrayList<PathDestination> destinations = new ArrayList<PathDestination>();
        // Create collections to hold our findings
        ArrayList<Path> pathsFromStart = new ArrayList<>();
        ArrayDeque<Path> pathsFromEnd   = new ArrayDeque<>();

        // Temp containers
        Path startPath, endPath;
        // Our null holder
        Optional<FestiObject> fs;

        // Find nearest paths between visitor and object
            // First path
            fs = findNearestFestiObject(start, Path.class);
            //System.out.println("Finding object");
            if (fs.isPresent()) {
                System.out.println("Found object");
                startPath = (Path) fs.get();
                System.out.println(startPath.getID());
                Optional<FestiObject> fs2        = findNearestFestiObject(end, Path.class);
                endPath   = (Path) fs2.get();
                System.out.println(endPath.getID());

                //System.out.println("Paths between them "+ startPath.findPath(new ArrayList<>(), endPath, null).size());
                /*
                System.out.println("Items between "+ getObjectsBetween(new double[] {
                        startPath.getShape().getBounds2D().getX(),
                        startPath.getShape().getBounds2D().getY(),
                        endPath.getShape().getBounds2D().getX(),
                        endPath.getShape().getBounds2D().getY()
                },
                        Stage.class).size());
                */
                if (startPath.equals(endPath)) {
                    pathsFromStart.add(startPath);
                }

            }
            else // return arraylist with no destinations (just walk straight towards it)
                return new ArrayList<>();


        // Objects to hold the beginning of the paths
        Path pathHolderStart = startPath;
        System.out.println("Start path is "+ startPath.getID());
        Path pathHolderEnd   = endPath;
        System.out.println("End path is "+ endPath.getID());
        // Objects to hold the ending of the paths
        Path pathHolderStartEnd, pathHolderEndEnd;
        // True while objects needs to be found
        boolean objectsBetween = true;
        while (objectsBetween) {
            System.out.println("Objects between is le true");

            // See if paths align with eachother
            Optional<ArrayList<Path>> paths = Optional.ofNullable(startPath.findPath(new ArrayList<>(), endPath, null));

            if (paths.isPresent()) {
                // They align, so we are done
                System.out.println("and it's present!");
                paths.get().forEach(p -> pathsFromStart.add(p));
                // Stop the loop, we already solved the puzzle
                objectsBetween = false;
            } else {

                Path[] p = getNearestPathWhenNoAllignmentHasBeenFound(startPath, endPath);
                // Get path start to path
                pathHolderStartEnd = p[0];
                pathHolderEndEnd = p[1];

                System.out.println("the path we found was "+pathHolderStart.getID());
                System.out.println("and trying to match to "+p[0].getID());
/*
                System.out.println("Last path "+ p[0].getID());
                System.out.println("Last path "+ p[1].getID());
*/
                // Gen path 1 (startStart -> startEnd)
                pathHolderStart.findPath(new ArrayList<>(), pathHolderStartEnd, null).forEach(pa ->
                {
                    pathsFromStart.add(pa);
                    System.out.println("1. Adding "+ pa.getID());
                });
/*
                // Gen path 2 (startStart -> startEnd)
                pathHolderEndEnd.findPath(new ArrayList<>(), pathHolderEnd, null).forEach(pa ->
                {
                    pathsFromEnd.add(pa);
                });
*/
                objectsBetween = false;
                /*
                // We need objects between the paths
                fs = Optional.ofNullable(getNearestPathWhenNoAllignmentHasBeenFound(pathHolderStart, pathHolderEnd)[0]);
                // Find path where the visitor needs to go off the path
                if (fs.isPresent()) {
                    pathHolderStartEnd = (Path) fs.get();
                    // Find path where the visitor needs to go onto the path
                    fs = Optional.ofNullable(getNearestPathWhenNoAllignmentHasBeenFound(pathHolderStart, pathHolderEnd)[1]);

                    pathHolderEndEnd = (Path) fs.get();

                    // Find the path we will need to walk as well
                    paths = Optional.ofNullable(pathHolderStart.findPath(new ArrayList<>(), pathHolderStartEnd, null));

                    if (paths.isPresent())
                        pathsFromStart.addAll(paths.get());

                    paths = Optional.ofNullable(pathHolderEnd.findPath(new ArrayList<>(), pathHolderEndEnd, null));

                    if (paths.isPresent())
                        pathsFromEnd.addAll(paths.get());

                    objectsBetween = false;
                    //System.out.println("Found objects: "+ pathHolderStartEnd.getID() +" and "+ pathHolderEndEnd.getID());
                } else {
                    objectsBetween = false;
                }
                */
            }
        }
            /*
            // Paths align, we are done.
            objectsBetween = pathsFound == null;
            if (objectsBetween){

                System.out.println("New pathHolderStartEnd "+ pathHolderStartEnd.getID());
                System.out.println("New pathHolderEndEnd "+ pathHolderEndEnd.getID());

                pathsFromStart.addAll(pathHolderStart.findPath(new ArrayList<>(), pathHolderStartEnd ,null));

                pathHolderEnd.findPath(new ArrayList<>(), pathHolderEndEnd, null).forEach(p -> {
                    System.out.println("Adding "+ p.getID() +" to queue");
                    pathsFromEnd.addLast(p);
                });

                ArrayList<FestiObject> searchArea = getObjectsBetween(outsidesOfTwoObjects(pathHolderStartEnd, pathHolderEndEnd), Path.class);

                pathHolderStart = (Path)findNearestFestiObjectWithBoundries(pathHolderStartEnd, Path.class, searchArea);
                pathHolderEnd  = (Path)findNearestFestiObjectWithBoundries(pathHolderEndEnd, Path.class, searchArea);

                System.out.println("New pathHolderStart "+ pathHolderStart.getID());
                System.out.println("New pathHolderEnd "+ pathHolderEnd.getID());


            } else {
                // Just add all and we're done here
                pathsFromStart.addAll(pathsFound);
            }
            */

        // Add endpaths to startpaths to create one list from start to end
        pathsFromEnd.forEach(p -> {
            pathsFromStart.add(p);
        });
        // Form all paths to destinations and then return
        ArrayList<PathDestination> pathDests = new ArrayList<>();
        Path path = null;
        System.out.println("Paths from start adding: ");
        for (Path p: pathsFromStart){
            Optional<Path.Direction> dir = p.getAlignmentDirection(path);
            if (dir.isPresent())
                pathDests.add(new PathDestination(p, dir.get()));
            else
                pathDests.add(new PathDestination(p, Path.Direction.NORTH));

            path = p;
        }

        //return pathsFromStart.stream().map(p -> new PathDestination(p)).collect(Collectors.toCollection(ArrayList::new));
        return pathDests;


        //return pathsFromStart.stream().map(p -> new PathDestination(p)).collect(Collectors.toCollection(ArrayList::new));
    }

    public Path[] getNearestPathWhenNoAllignmentHasBeenFound(Path p1, Path p2){
        ArrayList<Path> paths1 = p1.getAllPaths(new ArrayList<>(), new ArrayList<>());
        //System.out.println("paths 1 has "+ paths1.size() +" alligned paths");
        ArrayList<Path> paths2 = p2.getAllPaths(new ArrayList<>(), new ArrayList<>());
        //System.out.println("paths 2 has "+ paths2.size() +" alligned paths");

        Path foundPath1 = null, foundPath2 = null;
        double smallestDistance = -10;
        // Iterate all paths, while doing so compare the iteration of every path and look for smallest distance
        for (Path path : paths1) {
            for (Path peth: paths2){
                if (smallestDistance == -10) {
                    foundPath1 = path;
                    foundPath2 = peth;
                    smallestDistance = Calculations.DistanceBetweenObjects(foundPath1, foundPath2);
                }
                if (Calculations.DistanceBetweenObjects(path, peth) < smallestDistance){
                    foundPath1 = path;
                    foundPath2 = peth;
                    smallestDistance = Calculations.DistanceBetweenObjects(path, peth);
                }
            }
        }

        System.out.println("Paths closest to eachother: "+ p1.getID()+ " and "+foundPath1.getID());
        System.out.println("Paths closest to eachother: "+ p2.getID()+ " and "+foundPath2.getID());
        return new Path[] { foundPath1, foundPath2 };
    }

    // Get objects then find leftX, rightX, topY, bottomY
    public FestiObject[] getXYMaximums(ArrayList<FestiObject> selection) {
        FestiObject leftX = selection.stream().min((f, u) -> (int)f.getShape().getBounds2D().getX()).get(),
                    topY = selection.stream().min((f, u) -> (int)f.getShape().getBounds2D().getX()).get(),
                    rightX = selection.stream().max((f, u) -> (int)f.getShape().getBounds2D().getX()).get(),
                    bottomY = selection.stream().max((f, u) -> (int)f.getShape().getBounds2D().getY()).get();


        return new FestiObject[] { leftX, rightX, topY, bottomY };
    }

    public void rearrangePath(Path p){
        p.removeAllAlignments();
    }

    public void clear(){
        stages.clear();
        foodstands.clear();
        toilets.clear();
        clearVisitors();
        pathsCreation.clear();
    }


    public FestiObject getCollisionObject(FestiObject fObj, ObjectManager.OBJECT_TYPE_RESTRICTIONS type){
        return getObjects(type).stream().filter(o -> o.isColliding(fObj)).findFirst().get();
    }

    private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
        in.defaultReadObject();
        calculateParkBoundries();
    }
}
