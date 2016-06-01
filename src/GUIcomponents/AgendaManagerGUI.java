package GUIcomponents;

import Agenda.Agenda.*;
import Agenda.AgendaItem;
import Agenda.Agenda;
import Agenda.AgendaManager;
import People.Band.Band;
import People.PeopleManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Optional;

/**
 * Created by Bilel on 26-2-2016.
 */
public class AgendaManagerGUI extends JPanel {
    private JPanel mainPanel;
    private JTabbedPane tabbedPane1;
    private JTable table1;
    private JButton deleteButton;
    private JButton newButton;
    private JTable table2;
    private JButton newButton1;
    private JButton deleteButton1;
    private JTable table3;
    private JButton deleteButton2;
    private JButton newButton2;
    private JTextField textField1;
    private JButton editEventButton;
    private JButton bandEditButton;
    private JButton artistEditButton;
    private JButton editAgendaButton;
    private JLabel agendaNameLabel;

    private PeopleManager pMan;
    private AgendaManager aMan;
    private MainNavBar    mainNav;

    private MainPage mPage;

    public AgendaManagerGUI(MainPage mPage, AgendaManager aMan, PeopleManager pMan, MainNavBar mb) {
        this.aMan       = aMan;
        this.pMan       = pMan;
        this.mainNav    = mb;

        this.setLayout(new FlowLayout(FlowLayout.LEFT));

        this.add(mainPanel);

        setAgenda(aMan.getCurrentAgenda());
        newButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mPage.setCenterFrame("addAgendaItemGUI", null);
            }
        });
        deleteButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                aMan.getCurrentAgenda().removeAgendaItem(table1.getValueAt(table1.getSelectedRow(), 0).toString());
                refreshTable(refreshArgs.AGENDA);
            }
        });
        newButton1.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mPage.setCenterFrame("bandsGUI", null);
            }
        });
        deleteButton1.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                pMan.getbMan().removeBand(table2.getValueAt(table2.getSelectedRow(), 0).toString());
                refreshTable(refreshArgs.BANDS);
            }
        });
        newButton2.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mPage.setCenterFrame("bandMembersGUI", null);
            }
        });
        deleteButton2.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                System.out.println(table3.getValueAt(table3.getSelectedRow(), 0).toString()+" "+
                        table3.getValueAt(table3.getSelectedColumn(), 1).toString());

                pMan.getbMan().removeBandMember(table3.getValueAt(table3.getSelectedColumn(), 0).toString()+" "+
                                                table3.getValueAt(table3.getSelectedColumn(), 1).toString());
                refreshTable(refreshArgs.BANDMEMBERS);
            }
        });
        textField1.addActionListener((ActionEvent e) -> {
            if (!aMan.agendaExists(textField1.getText())) {
                aMan.getCurrentAgenda().setName(textField1.getText());
                mainNav.setAgendasAtSelectionMenu();
            }


        });
        editEventButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Optional<AgendaItem> item = aMan.getCurrentAgenda().findEvent(
                        table1.getValueAt(table1.getSelectedRow(), 0).toString()
                );

                if (item.isPresent())
                {
                    mPage.setCenterFrame("addAgendaItemGUI", item.get());
                }

                //mPage.setCenterFrame("addAgendaItem",  );
            }
        });
        editAgendaButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mPage.setCenterFrame("addAgendaGUI", aMan.getCurrentAgenda() );
            }
        });
        bandEditButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Optional<Band> item = Optional.ofNullable(pMan.getbMan().getBand(table2.getValueAt(table2.getSelectedRow(), 0).toString()));

                if (item.isPresent())
                    mPage.setCenterFrame("addBandGUI", item.get());

                mPage.setCenterFrame("bandsGUI", pMan.getbMan().getBand(table2.getValueAt(table2.getSelectedRow(), 0).toString() ));

            }
        });
        artistEditButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mPage.setCenterFrame("bandMembersGUI", pMan.getbMan().getBandMember(table3.getValueAt(table3.getSelectedRow(), 0).toString()) );
            }
        });
    }

    public void refreshTable(refreshArgs rArgs) {
        // Process args and refresh where needed
        if (rArgs == refreshArgs.ALL || rArgs == refreshArgs.AGENDA)
            table1.setModel(DataTable.getAgendaModel(aMan.getCurrentAgenda()));
        if (rArgs == refreshArgs.ALL || rArgs == refreshArgs.BANDS)
            table2.setModel(DataTable.getBandModel(pMan.getbMan().getBands()));
        if (rArgs == refreshArgs.ALL || rArgs == refreshArgs.BANDMEMBERS)
            table3.setModel(DataTable.getBandMemberModel(pMan.getbMan().getBandMembers()));
    }

    public void setAgenda(Agenda agenda) {
        aMan.setAgenda(agenda);
        textField1.setText(agenda.getName());
        textField1.revalidate();
        refreshTable(refreshArgs.ALL);
    }

    public enum refreshArgs {
        ALL,
        BANDS,
        AGENDA,
        BANDMEMBERS
    }

}
