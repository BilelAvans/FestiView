package Objects.Buildings;

import Objects.ObjectManager;
import Objects.People.VisitorObject;

import java.util.ArrayList;

/**
 * Created by Bilel on 26-3-2016.
 */
public interface VisitableObject {

    // Does it have visitors inside ?
    boolean canRelease();
    // Can we add a visitor?
    boolean addVisitor(VisitorObject vObj);
    // Get visitors one at a time (to avoid a lot of collisions on exit)
    void releaseVisitor(ObjectManager obMan);

    void setCanRelease();
    // Remove all visitors
    void clear();


}
