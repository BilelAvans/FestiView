package Functional;

import Agenda.AgendaManager;
import Objects.ObjectManager;
import People.Band.BandManager;
import People.Band.BandMember;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;

/**
 * Created by Bilel on 22-2-2016.
 */
public class Storage {


    public static File      agendasPath = new File("agenda.fst"),
                            bandsPath = new File("bands.fst"),
                            oManPath = new File("obman.fst");

    public static void saveStuff(Object ob, File file) throws Exception {
        try {

            ObjectOutputStream saveStream = new ObjectOutputStream(new FileOutputStream(file));
            saveStream.writeObject(ob);
            saveStream.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static Object loadStuff(File file) throws Exception {
        Object readObject;

        if (!file.exists())
            return false; // Cannot load, because there has not been a save yet

        ObjectInputStream saveStream = new ObjectInputStream(new FileInputStream(file));
        readObject = saveStream.readObject();
        saveStream.close();

        //System.out.println(readObject != null);

        return readObject;
    }

    public static void SaveAll(AgendaManager aMan, BandManager bm, ObjectManager obMan){
        System.out.println("Attempting to save everything for you");

        saveAgendaManager(aMan);
        saveBandManager(bm);
        saveObjectManager(obMan);
    }

    public static void LoadAll(AgendaManager aMan, BandManager bm, ObjectManager obMan){
        aMan    =   loadAgendaManager();
        bm      =   loadBands();
        obMan   =   loadObjectManager();
    }

    public static boolean saveBandManager(BandManager b) {
        try {
            saveStuff(b, bandsPath);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public static boolean saveAgendaManager(AgendaManager a) {
        try {
            saveStuff(a, agendasPath);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public static boolean saveObjectManager(ObjectManager obMan) {
        try {
            saveStuff(obMan, oManPath);
        } catch (Exception e) {

            return false;
        }

        return true;
    }


    public static BandManager loadBands() {
        try {
            return (BandManager) loadStuff(bandsPath);
        } catch (Exception e) {
            return null;
        }
    }

    public static AgendaManager loadAgendaManager() {
        try {
            return (AgendaManager) loadStuff(agendasPath);
        } catch (Exception e) {
            return null;
        }
    }

    public static ObjectManager loadObjectManager() {
        try {
            return (ObjectManager) loadStuff(oManPath);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static File getFile(File f) throws Exception {
        // Create file if it doesn't exist
        if (!f.exists())
            f.createNewFile();

        return f;
    }

    public static void selectFiles(Container cont) {
        // Ask for obMan file
        Storage.oManPath = Message.FileChooser(cont, "Select obMan", "Select");
        // Ask for agenda file
        Storage.agendasPath = Message.FileChooser(cont, "Select agendas", "Select");
        // Ask for bands file
        Storage.bandsPath = Message.FileChooser(cont, "Select bands", "Select");
    }
}
