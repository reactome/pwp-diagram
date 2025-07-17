package org.reactome.web.diagram.data.graph.model;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class Participant implements Comparable<Participant> {

    private String identifier;
    private Double expression;

    public Participant(String identifier, Double expression) {
        this.identifier = identifier;
        this.expression = expression;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Double getExpression() {
        return expression;
    }

    @Override
    public int compareTo(Participant o) {
        return identifier.compareTo(o.identifier);
    }

    public static List<Participant> asSortedList(Map<String, Double> participants){
        List<Participant> rtn = new LinkedList<>();
        for (String identifier : participants.keySet()) {
            rtn.add(new Participant(identifier, participants.get(identifier)));
        }
        Collections.sort(rtn);
        return rtn;
    }
}
