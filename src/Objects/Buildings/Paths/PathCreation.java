package Objects.Buildings.Paths;

import Objects.CustomFestiObject;
import Objects.FestiObject;
import Objects.ImageFactory;
import Objects.ObjectManager;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Bilel on 18-3-2016.
 */
public class PathCreation implements FestiObject, Cloneable, Serializable {

    // All paths must have same width and height
    private int pathWidth = 30;
    private int pathHeight = 30;

    private Rectangle2D.Double shape;

    private ArrayList<Path> paths = new ArrayList<>();

    public PathCreation() {

    }

    // For Cloning purpouses
    private PathCreation(ArrayList<Path> paths) {
        this.paths = paths;
    }

    // Add path to our list
    public boolean addPath(final Path path) {

        // Adding first path
        if (paths.size() == 0) {
            paths.add(path);
            return true;
        }
        else {
            // Can we find a path to fit this in?
            try {
                // Is it 80 px away from another walking area?
                ArrayList<Path> matchedPaths = new ArrayList<>();
                Optional<Path> matchingPath = paths.stream().filter(p -> p.getPlacementDirection(path) != null).findFirst();
                if (matchingPath.isPresent()) {
                    // Path ophalen waar het aan kan allignen
                    Path p = matchingPath.get();
                    // Richting waarin het alligned
                    Path.Direction direction = p.getPlacementDirection(path);
                    Path newPath = p.allignDirectionNullIfSet(direction, path);
                    // Attach the tiles to eachother
                    // oldPath is null if already set
                    if (newPath != null)
                        paths.add(newPath);

                    Optional<Path.Direction> dir = p.getAlignmentDirection(newPath);

                    Optional<ArrayList<Path>> otherPaths = Optional.ofNullable(paths.stream()
                            .filter(pl -> pl.getPlacementDirection(path) != null && !pl.equals(p))
                            .collect(Collectors.toCollection(ArrayList::new)));

                    //Can we allign with other paths, already existing?
                    if (otherPaths.isPresent()){
                        otherPaths.get().forEach(p2 -> {
                            p2.allignDirection(p2.getPlacementDirection(newPath), newPath);
                            //System.out.println("Extra alignment with "+ p2.getID());
                        });
                    }

                    //paths.forEach(p -> p.getAlignmentDirection());

                    //System.out.println("Current amm of p: "+ paths.size());
                } else {
                    paths.add(path);
                    //System.out.println("Current amm of p: "+ paths.size());
                }
                // Add it to all our paths

            } catch (NoSuchElementException e){
                e.printStackTrace();
                /*
                // Found no paths to allign our path with, so just add it
                if (!paths.contains(path))
                    paths.add(path);
                    */
            }
            return true;
        }

    }

    public void removePath(Path p) {
        // Remove path from selections
        p.getAvailablePaths().forEach(avP -> {
            Path tPath = p.getPath(avP);                    // Get the path
            tPath.unalignPath(Path.OppositeDirection(avP)); // Remove our path from obtained path
        });

        paths.remove(p);
    }

    public ArrayList<Path> getAllPaths() {
        return paths;
    }
    /*
    public ArrayList<Path> getAllPathsBetween(double x, double y, double width, double height) {
        return getAllPathsBetween(new CustomFestiObject(x, y, width, height));
    }

    public ArrayList<Path> getAllPathsBetween(FestiObject fest) {
        return paths.stream().filter(p -> p.isColliding(fest)).collect(Collectors.toCollection(ArrayList::new));
    }
*/

    // Remove all paths
    public void clear(){
        paths.clear();
    }

    @Override
    public void move(double x, double y) {
        // Move everything
        paths.forEach(p -> move(x, y));
    }

    @Override
    public void draw(Graphics2D g) {
        // Draw Paths for each, not as a whole
        paths.forEach(p -> p.draw(g));
    }

    @Override
    public Shape getShape() {
        Area area = new Area();

        paths.forEach(path -> area.add(new Area(path.getShape())));

        return area;
    }

    @Override
    public Paint getPaint() {
        return null;
    }

    @Override
    public boolean isColliding(FestiObject f2) {


        return false;
    }

    @Override
    public String getID() {
        return null;
    }

    @Override
    public void setID(String ID) {

    }

    public boolean run(){
        return true;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return new PathCreation(paths);
    }
}
