package org.reactome.web.diagram.context.dialogs.interactors;

import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class InteractorClickableCell extends ClickableTextCell {

    @Override
    protected void render(Context context, SafeHtml value, SafeHtmlBuilder sb) {
        if(value != null) {
            // The width of the span has to be explicitly set to 100% for the 3 dots to appear.
            sb.appendHtmlConstant("<span style='display: inline-block; overflow:hidden; white-space:nowrap; text-overflow:ellipsis; width:100%;' title='" + value.asString() + "'>" + value.asString() + "</span>");
        }
    }

}
