package GUIcomponents;

import Agenda.Agenda;
import Agenda.AgendaManager;
import Agenda.*;
import Objects.Buildings.Stage;
import Objects.FestiObject;
import Objects.ObjectManager;
import Objects.ObjectSelectorPanel;
import Objects.ParkCreatorGUI;
import People.Band.Band;
import People.Band.BandManager;
import People.Band.BandMember;
import People.PeopleManager;
import Simulation.TimeLine;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Created by Bilel on 16-2-2016.
 */
public class MainPage extends JPanel {

    // Main Frame
    private JFrame mainFrame = new JFrame("Welcome to Festiview");

    private JPanel centerPanel;
    // Our main layout
    private CardLayout cardLayout;

    private ObjectManager oMan = new ObjectManager();
    private PeopleManager pMan = new PeopleManager(oMan);

    private ParkCreatorGUI festiGUI;

    private AgendaManagerGUI    agendaGUI;
    private AddAgendaItemGUI    addAgendaItemGUI;
    private AddAgendaGUI        addAgendaGUI;
    private AddBandMemberGUI    addBandMemberGUI;
    private AddBandGUI          addBandGUI;

    private MainNavBar mainNavBar;
    private AgendaManager aMan = new AgendaManager();

    public MainPage(String panel) {
        // List for all agenda's
        //loadData();
        addTestAgenda();

        initComponents();
        setCenterFrame(panel, null);
    }

    private void addTestAgenda() {
        Agenda agenda = new Agenda("Check", LocalDateTime.now());
        aMan.addAgenda(agenda);
        aMan.setAgenda(agenda);
    }

    private void setFrame() {
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.add(this);
        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }

    private void initComponents() {
        // Main Layout
        //setLayout(new BorderLayout());
        // Layout for the center Frame
        cardLayout = new CardLayout();
        // Centerpanel itself
        centerPanel = new JPanel();
        centerPanel.setLayout(cardLayout);
        // Set our components
        mainNavBar = new MainNavBar(this);
        agendaGUI = new AgendaManagerGUI(this, aMan, pMan, mainNavBar);
        festiGUI = new ParkCreatorGUI(pMan, aMan, this);
        addAgendaItemGUI = new AddAgendaItemGUI(this, pMan, aMan, oMan);
        addAgendaGUI = new AddAgendaGUI(this);
        addBandGUI = new AddBandGUI(pMan, this);
        addBandMemberGUI = new AddBandMemberGUI(pMan, this);

        // Add our components to cardlayout
        centerPanel.add(agendaGUI, "showAgendaGUI");
        centerPanel.add(addAgendaItemGUI.getPane(), "addAgendaItemGUI");
        centerPanel.add(festiGUI, "festipanel");
        centerPanel.add(addAgendaGUI.getPane(), "addAgendaGUI");
        centerPanel.add(addBandGUI.getPane(), "bandsGUI");
        centerPanel.add(addBandMemberGUI.getPane(), "bandMembersGUI");

        // Add generated frames to the mainframe
        add(centerPanel);
        mainFrame.setJMenuBar(mainNavBar);

        cardLayout.show(centerPanel, "addAgendaGUI");

        setFrame();
        // Prepare and launch the full panel
        setVisible(true);
    }

    public void setCenterFrame(String panelname, Object args) {

        switch (panelname) {
            case "showAgendaGUI":
                showAgendaGUI(args);
                agendaGUI.refreshTable(AgendaManagerGUI.refreshArgs.ALL);
                cardLayout.show(centerPanel, "showAgendaGUI");
                break;
            case "addAgendaItemGUI":
                // Stage type means Add item at stage
                if (args instanceof Stage) {
                    addAgendaItemGUI.resetView();
                    addAgendaItemGUI.setDefaultStage(args);
                } // AgendaItem type means edit item
                else if (args instanceof AgendaItem) {
                    addAgendaItemGUI.setEditableEventInfo((AgendaItem)args);
                } // null for blanc item, so simply do nothing
                cardLayout.show(centerPanel, "addAgendaItemGUI");
                break;
            case "festipanel":
                festiGUI.setEastFrame("build", null);
                cardLayout.show(centerPanel, "festipanel");
                break;
            case "addAgendaGUI":
                if (args instanceof Agenda)
                    addAgendaGUI.setEditable((Agenda)args);
                cardLayout.show(centerPanel, "addAgendaGUI");
                break;
            case "bandsGUI":
                if (args instanceof Band)
                    addBandGUI.setEditableBand((Band)args);

                cardLayout.show(centerPanel, "bandsGUI");
                break;
            case "bandMembersGUI":
                addBandMemberGUI.resetBandList();
                if (args instanceof BandMember)
                    addBandMemberGUI.setEditableBandMemberInfo((BandMember)args);

                cardLayout.show(centerPanel, "bandMembersGUI");
                break;
            case "simulation":
                ObjectSelectorPanel.selected = ""; // Remove selection
                setCenterFrame("festipanel", null);
                festiGUI.setEastFrame("simPanelFrame", new TimeLine(aMan.getCurrentAgenda(), oMan, festiGUI.getFestiFrame() ));
                break;
        }

        centerPanel.revalidate();
        revalidate();
    }

    private void showAgendaGUI(Object args) {
        // Refresh table if args are given
        if (args instanceof AgendaManagerGUI.refreshArgs)
            agendaGUI.refreshTable((AgendaManagerGUI.refreshArgs) args);
    }

    public Agenda getAgenda() {
        return aMan.getCurrentAgenda();
    }

    public void setAgenda(Agenda agenda) {
        aMan.setAgenda(agenda);
        agendaGUI.setAgenda(agenda);
        mainNavBar.setAgendasAtSelectionMenu(); // Refresh the agenda's in the

    }

    public ArrayList<Agenda> getAgendas() {
        return aMan.getAgendas();
    }

    public AgendaManager getAMan(){
        return aMan;
    }

    public PeopleManager getPMan(){
        return pMan;
    }

    public MainNavBar getMainNavBar() {
        return mainNavBar;
    }

    public void clearObman(){
        oMan.clear();
    }


    public void onShutdown() {
    }

    public void loadStorage(AgendaManager tempAMan, BandManager tempBMan, ObjectManager tempObMan){
        System.out.println("Objectman object count: "+ tempObMan.getObjects(ObjectManager.OBJECT_TYPE_RESTRICTIONS.NONE));
        tempAMan.getCurrentAgenda().replaceEventsWithValidEvents(tempObMan);

        this.aMan = tempAMan;
        this.oMan = tempObMan;
        this.pMan.setBMan(tempBMan);
        this.pMan.setObMan(tempObMan);
        this.festiGUI.setObMan(tempObMan);
        this.festiGUI.setAMan(tempAMan);
        this.agendaGUI.setAgenda(aMan.getCurrentAgenda());
        this.addAgendaItemGUI.setaMan(tempAMan);

        addAgendaItemGUI.setObMan(tempObMan);

        System.out.println(tempObMan.getStages().size());

        agendaGUI.refreshTable(AgendaManagerGUI.refreshArgs.ALL);
        addAgendaItemGUI.resetView();
        addBandGUI.addBandMembers();

        System.out.println("Done loading data");
    }
}
