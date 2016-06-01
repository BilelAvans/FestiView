package GUIcomponents;

import Agenda.*;
import Functional.Message;
import Functional.Storage;
import Objects.ObjectManager;
import Objects.ParkCreatorGUI;
import People.Band.BandManager;
import People.Band.BandMember;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by Bilel on 20-2-2016.
 */
public class MainNavBar extends JMenuBar {

    // Our center frame
    private MainPage    mPage;

    private JMenu       mainNav,
                        appNav,
                        simNav,
                        fileNav;

    private JMenuItem   fileNavChooseFiles,
                        fileNavLoadAgenda,
                        fileNavSaveAgenda,
                        appNavGoToFestiView,
                        appNavGoToSimulation,
                        appNavExit,
                        simNavShowVisitors,
                        appAgendaCreateNewAgenda,
                        appAgendaCreateNewAItem,
                        appAgendaSaveAgendas,
                        appAgendaLoadAgendas,
                        mainNavAddBand,
                        mainNavShowAgenda,
                        mainNavAddBandMember;

    private JMenu       buildMenu;

    private JMenuItem   buildMenuNew;

    private Container cont = this;

    public MainNavBar(MainPage mPage) {
        this.mPage = mPage;

        initComponents();
        initListeners();
    }

    private void initComponents() {
        appNav = new JMenu("FestiView");
        appNavGoToFestiView = new JMenuItem("Build");
        appNavGoToSimulation = new JMenuItem("Simulation");
        appNavExit = new JMenuItem("Exit");

        appNav.add(appNavGoToFestiView);
        appNav.add(appNavGoToSimulation);
        appNav.add(appNavExit);

        this.add(appNav);

        mainNav                     = new JMenu("Agenda");
        mainNavShowAgenda           = new JMenu("Show Agenda");
        appAgendaCreateNewAgenda    = new JMenuItem("Add Agenda");
        appAgendaCreateNewAItem     = new JMenuItem("Add Event");
        mainNavAddBand              = new JMenuItem("Add Band");
        mainNavAddBandMember        = new JMenuItem("Add Band Member");

        mainNav.add(mainNavShowAgenda);
        mainNav.add(appAgendaCreateNewAgenda);
        mainNav.add(appAgendaCreateNewAItem);
        mainNav.add(mainNavAddBand);
        mainNav.add(mainNavAddBandMember);

        simNav              = new JMenu("Simulation");
        simNavShowVisitors  = new JMenuItem("Visitors");

        simNav.add(simNavShowVisitors);

        this.add(mainNav);
        // Build menu adding
        buildMenu = new JMenu("Build");
        buildMenuNew = new JMenuItem("New");

        buildMenu.add(buildMenuNew);

        buildMenu.setVisible(false);
        this.add(buildMenu);

        fileNav = new JMenu("Save Options");
        // Choose our files
        fileNavSaveAgenda = new JMenuItem("Save data");
        fileNavLoadAgenda = new JMenuItem("Load data");
        fileNavChooseFiles = new JMenuItem("Choose File Locations");

        fileNav.add(fileNavLoadAgenda);
        fileNav.add(fileNavSaveAgenda);
        fileNav.add(fileNavChooseFiles);

        this.add(fileNav);

        setAgendasAtSelectionMenu();
    }

    private void initListeners() {
        this.appNavExit.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                System.exit(0);
            }

        });

        this.appNavGoToFestiView.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mainNav.setVisible(true);
                revalidate();
                mPage.setCenterFrame("festipanel", null);
                buildMenu.setVisible(true);
            }
        });

        this.appAgendaCreateNewAgenda.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mPage.setCenterFrame("addAgendaGUI", null);
            }
        });

        this.mainNavShowAgenda.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mPage.setCenterFrame("showAgendaGUI", null);
            }
        });

        this.appNavGoToSimulation.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mainNav.setVisible(false);
                buildMenu.setVisible(false);
                mPage.setCenterFrame("simulation", null);
            }
        });

        this.mainNavAddBand.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mPage.setCenterFrame("bandsGUI", null);
            }
        });

        this.mainNavAddBandMember.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mPage.setCenterFrame("bandMembersGUI", null);
            }
        });

        this.appAgendaCreateNewAItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mPage.setCenterFrame("addAgendaItemGUI", null);
            }
        });
        this.fileNavSaveAgenda.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                //System.out.println("Save clicked");
                try {
                    if (mPage.getAMan() != null && mPage.getPMan().getbMan() != null && mPage.getPMan() != null && mPage.getPMan().getObMan() != null) {
                        Storage.SaveAll(mPage.getAMan(), mPage.getPMan().getbMan(), mPage.getPMan().getObMan());
                    }
                } catch (NullPointerException ex){
                    ex.printStackTrace();
                }
            }
        });
        this.fileNavLoadAgenda.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {

                try {
                    AgendaManager tempAMan          =   Storage.loadAgendaManager();
                    BandManager tempBMan            =   Storage.loadBands();
                    ObjectManager tempObMan         =   Storage.loadObjectManager();

                    mPage.loadStorage(tempAMan, tempBMan, tempObMan);


                } catch (NullPointerException ex){
                    ex.printStackTrace();
                }
            }
        });

        this.fileNavChooseFiles.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Storage.selectFiles(cont);
            }
        });

        this.buildMenuNew.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mPage.clearObman();
            }
        });

    }

    public void setAgendasAtSelectionMenu() {
        mainNavShowAgenda.removeAll(); // Clear items
        for (Agenda ag : mPage.getAgendas()) {
            JMenuItem item = new JMenuItem(ag.getName());
            mainNavShowAgenda.add(item);

            item.addActionListener(it -> {
                mPage.setAgenda(ag);
            });
        }
    }

}
