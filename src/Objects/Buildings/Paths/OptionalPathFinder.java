package Objects.Buildings.Paths;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Created by Bilel on 14-4-2016.
 */
public class OptionalPathFinder<T> {

    private ArrayList<Path> paths;
    private Path startPath, endPath;

    private boolean pathFound = false;

    public OptionalPathFinder(){
    }

    public boolean NotNull(){
        return paths != null;
    }

    public ArrayList<Path> Get(){
        return paths;
    }

    public void Insert(ArrayList<Path> paths){
        if (pathFound)
            if (this.paths.size() > paths.size())
                this.paths = paths;
    }
}
