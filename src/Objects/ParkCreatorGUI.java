package Objects;

import Agenda.Agenda;
import Agenda.AgendaManager;
import GUIcomponents.MainPage;
import GUIcomponents.SimulationPanel;
import People.PeopleManager;
import Simulation.TimeLine;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Bilel on 27-2-2016.
 */
public class ParkCreatorGUI extends JPanel {

    private ParkCreation parkData;

    private FestiFrame          festiFrame;
    private ObjectSelectorPanel buildPanel;
    private AgendaManager   a;
    private MainPage        mPage;
    private TimeLine        tLine;

    private CardLayout          eastPanelcLayout = new CardLayout();
    private SimulationPanel     simPanel;
    private JPanel              eastPanelFrame, simPanelFrame;

    public ParkCreatorGUI(PeopleManager pMan, AgendaManager a, MainPage mPage) {
        this.a     = a;

        parkData   = new ParkCreation("Test Park", pMan.getObMan());
        buildPanel = new ObjectSelectorPanel(festiFrame);
        festiFrame = new FestiFrame(buildPanel, pMan, a.getCurrentAgenda(), mPage);


        this.mPage = mPage;

        setName("Test Park");
        initCardComponents();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(festiFrame);
        add(eastPanelFrame);
        setEastFrame("build", null);
    }

    private void initCardComponents(){
        eastPanelFrame = new JPanel();
        simPanel      = new SimulationPanel(getObjectManager());
        simPanelFrame = simPanel.getPane();

        eastPanelcLayout = new CardLayout();
        eastPanelFrame.setLayout(eastPanelcLayout);
        eastPanelFrame.add(buildPanel,    "build");
        eastPanelFrame.add(simPanelFrame, "simPanelFrame");
    }

    public void setEastFrame(String name, Object params){
        switch (name){
            case "build":
                simPanelFrame.setPreferredSize(new Dimension(100, 500));
                eastPanelcLayout.show(eastPanelFrame, "build");
                break;
            case "simPanelFrame":
                this.tLine = (TimeLine)params;
                simPanel.setTimeLine(tLine);
                simPanel.resetAll();
                simPanelFrame.setPreferredSize(new Dimension(300, 500));
                eastPanelcLayout.show(eastPanelFrame, "simPanelFrame");
                break;
        }
    }

    public ParkCreation getParkCreator() {
        return parkData;
    }

    public ObjectManager getObjectManager() {
        return festiFrame.getObMan();
    }

    public FestiFrame getFestiFrame() {
        return festiFrame;
    }

    public void setObMan(ObjectManager obMan){
        this.festiFrame.setObMan(obMan);
        this.simPanel.setObMan(obMan);
        this.parkData.setObMan(obMan);

    }

    public void setAMan(AgendaManager aMan){
        this.a = aMan;
    }
}
