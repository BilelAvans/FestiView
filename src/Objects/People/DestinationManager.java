package Objects.People;

import Agenda.AgendaItem;
import Objects.Buildings.Paths.Path;
import Objects.FestiObject;
import Objects.Buildings.VisitableObject;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by Bilel on 28-3-2016.
 */
public class DestinationManager implements Serializable {

    private ArrayBlockingQueue<Destination> destinations = new ArrayBlockingQueue<Destination>(100);

    private Destination currentDestination, endDestination;
    // Filled when visitor is at an object
    private FestiObject currentLocation;

    public DestinationManager(){

    }

    public void add(Destination d){
        destinations.add(d);

        if (d.returnFObject().isPresent())
            if (d.returnFObject().get() instanceof VisitableObject)
                endDestination = d;
    }

    public void addAndPush(Destination d){
        add(d);
        // Automatically push if no current destination is set.
        next();
    }

    public void add(ArrayList<Destination> d){
        d.forEach(dest -> destinations.add(dest));
    }

    public Destination Current(){
        return currentDestination;
    }

    public boolean next(){
        if (destinations.isEmpty())
            return false;

        if (currentDestination != null && currentDestination.returnFObject() != null)
            currentLocation      = currentDestination.returnFObject().get();

        currentDestination   = destinations.poll();
        setCurrentLocation();
        return true;
    }

    public Optional<Destination> peekNext(){
        return Optional.ofNullable(destinations.peek());
    }

    public void clear(){
        destinations.clear();
        // Remove all destinations
        currentDestination = null;

        //currentLocation = null;
        endDestination = null;
    }

    public boolean hasCurrent(){
        return currentDestination != null;
    }

    public boolean hasAny(){
        return !destinations.isEmpty();
    }

    public void setCurrentLocation(){
        if (currentDestination != null)
            currentLocation = currentDestination.returnFObject().get(); // Set previous location as current
    }

    public boolean hasCurrentLocation(){
        if (currentDestination != null)
            if (currentDestination.returnFObject() != null) {
                currentDestination.returnFObject();
            }

        return false;
    }

    public FestiObject currentLocation(){
        return currentDestination.returnFObject().get();
    }


    public FestiObject currentEndLocation(){
        return endDestination.returnFObject().get();
    }

    public boolean removeIfDestinationInQueue(AgendaItem event){
        for (Destination dest: destinations){
            if (dest.returnFObject().get().equals(event.getEventLocation())) {
                System.out.println("Removing "+ event.getName() +" from queue");
                clear();
                return true;
            }
        }

        return false;
    }
}
