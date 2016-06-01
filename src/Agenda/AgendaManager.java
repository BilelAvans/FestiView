package Agenda;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Bilel on 16-3-2016.
 */
public class AgendaManager implements Serializable {

    // Current Agenda
    private Agenda currentAgenda;
    // All stored agendas
    private ArrayList<Agenda> agendas = new ArrayList<>();

    public AgendaManager() {

    }

    public Agenda getCurrentAgenda() {
        return currentAgenda;
    }

    public Agenda getAgenda(String name) {
        for (Agenda a : agendas)
            if (a.getName().equals(name))
                return a;

        return null;
    }

    public void replaceAgenda(Agenda oldAgenda, Agenda newAgenda){
        agendas.set(agendas.indexOf(oldAgenda), newAgenda);
    }

    public ArrayList<Agenda> getAgendas() {
        return agendas;
    }

    public boolean addAgenda(Agenda agenda) {
        if (!agendaExists(agenda)) {
            agendas.add(agenda);
            return true;
        }

        return false;
    }

    public void setAgenda(Agenda agenda) {
        this.currentAgenda = agenda;
        addAgenda(agenda);
    }

    public boolean agendaExists(Agenda agenda) {
        if (agendas.stream().anyMatch(a -> a.getName().equals(agenda.getName())))
            return true;

        return false;
    }

    public boolean agendaExists(String name) {
        if (agendas.stream().anyMatch(a -> a.getName().equals(name)))
            return true;

        return false;
    }


}
