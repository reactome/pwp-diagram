package org.reactome.web.diagram.util.slider;

/**
* @author Antonio Fabregat (fabregat@ebi.ac.uk)
*/
enum PinStatus {
    STD ("#58ACFA"),
    HOVERED ("#2E9AFE"),
    CLICKED ("#0000FF");

    String colour;

    PinStatus(String colour) {
        this.colour = colour;
    }
}
