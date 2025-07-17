package org.reactome.web.diagram.util;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.i18n.client.DateTimeFormat;
import org.reactome.web.diagram.client.DiagramFactory;

import java.util.Date;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class DiagramEventBus extends SimpleEventBus {

    private DateTimeFormat fmt = DateTimeFormat.getFormat("HH:mm:ss");

    @Override
    public void fireEvent(GwtEvent<?> event) {
        String msg = "Please do not use fireEvent. Use fireEventFromSource instead.";
        throw new RuntimeException(msg);
    }

    @Override
    public void fireEventFromSource(GwtEvent<?> event, Object source) {
        if(DiagramFactory.EVENT_BUS_VERBOSE) {
            Console.info(
                    this.fmt.format(new Date()) + " " +
                            source.getClass().getSimpleName() + " >> " +
                            event
            );
        }
        super.fireEventFromSource(event, source);
    }
}