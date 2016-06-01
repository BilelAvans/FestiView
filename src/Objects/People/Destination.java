package Objects.People;

import Objects.FestiObject;

import java.io.Serializable;
import java.util.Optional;

/**
 * Created by Bilel on 9-3-2016.
 */
public class Destination implements Serializable {

    private Optional<FestiObject> fObj;

    private double x, y;

    public Destination(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Destination(FestiObject fObj) {
        this.fObj = Optional.of(fObj);
    }

    public double getX() {
        return (!fObj.isPresent()) ? x : fObj.get().getShape().getBounds2D().getCenterX();
    }

    public double getY() {
        return (!fObj.isPresent()) ? y : fObj.get().getShape().getBounds2D().getCenterY();
    }

    public Optional<FestiObject> returnFObject(){
        return fObj;
    }
}
