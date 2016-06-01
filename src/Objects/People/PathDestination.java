package Objects.People;

import Objects.Buildings.Paths.Path;

/**
 * Created by Bilel on 18-4-2016.
 */
public class PathDestination extends Destination {

    private Path.Direction direction;

    public PathDestination(Path path, Path.Direction direction) {
        super(path);
        // Direction
        System.out.println("To direction "+ direction);
        this.direction = direction;
    }

    @Override
    public double getX() {
        if (direction != null) {
            switch (direction) {
                case NORTH:
                    return super.getX() + 21;
                case EAST:
                    return super.getX() + 21;
                case SOUTH:
                    return super.getX() - 1;
                case WEST:
                    return super.getX() - 1;
            }
        }
        return super.getX();
    }

    public double getY(){
        if (direction != null) {
            switch (direction) {
                case NORTH:
                    return super.getY() - 1;
                case EAST:
                    return super.getY() + 21;
                case SOUTH:
                    return super.getY() + 20;
                case WEST:
                    return super.getY() - 1;
            }
        }
        return super.getY();
    }
}
