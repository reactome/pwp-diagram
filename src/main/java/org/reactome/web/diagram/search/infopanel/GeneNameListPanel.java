package org.reactome.web.diagram.search.infopanel;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasVerticalAlignment;

import java.util.Collection;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class GeneNameListPanel extends Composite {

    public GeneNameListPanel(Collection<String> geneNames, final EventBus eventBus) {
        super();
        StringBuilder sb = new StringBuilder("");
        for (final String geneName : geneNames) {
            sb.append(" ").append(geneName);
        }

        FlexTable table = new FlexTable();
        table.setText(0, 0, "Gene Names:");
        table.setText(0, 1, sb.toString());

        table.getElement().getStyle().setProperty("margin", "-3px 0 -8px -4px");
        table.getElement().getStyle().setMarginLeft(-4, Style.Unit.PX);
        table.getColumnFormatter().getElement(0).setAttribute("width", "85px");
        table.getCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);

        initWidget(table);
    }
}
