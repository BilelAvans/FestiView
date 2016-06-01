import GUIcomponents.MainPage;

/**
 * Created by Bilel on 15-2-2016.
 */
public class NewFestiview {

    public NewFestiview() {
        doStuffs();
    }

    public static void main(String[] args) {
        NewFestiview.doStuffs();
    }

    private static void doStuffs() {
        new MainPage("showAgendaGUI");
    }
}

/*
            case "addAgendaGUI": cardLayout.show(centerPanel, "addAgendaGUI");
            break;
            case "showBands": cardLayout.show(centerPanel, "bandsGUI");
            break;
            case "showAgendaGUI": cardLayout.show(centerPanel, "showAgendaGUI");
            break;
            case "festipanel": cardLayout.show(centerPanel, "festipanel");
            break;
 */