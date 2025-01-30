package org.reactome.web.diagram.context.dialogs.molecules;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safecss.shared.SafeStylesUtils;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.text.shared.SimpleSafeHtmlRenderer;
import org.reactome.web.diagram.profiles.analysis.AnalysisColours;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class ExpressionCell extends TextCell {

    public interface Template extends SafeHtmlTemplates {
        @SafeHtmlTemplates.Template("<div style=\"color:black\">{0}</div>")
        SafeHtml std(SafeHtml text);

        @SafeHtmlTemplates.Template("<div style=\"{0} color:black\">{1}</div>")
        SafeHtml exp(SafeStyles color, SafeHtml text);
    }

    private static Template template;

    private double min;
    private double max;

    public ExpressionCell (double min, double max) {
        super(SimpleSafeHtmlRenderer.getInstance());
        this.min = min;
        this.max = max;
        if (template == null) {
            template = GWT.create(Template.class);
        }
    }

    @Override
    public void render(Context context, String value, SafeHtmlBuilder sb) {
        if (value != null) {
            SafeHtml safeValue = SafeHtmlUtils.fromString(value);
            try {
                double exp = NumberFormat.getFormat("#.##E0").parse(value);
                String colour = AnalysisColours.get().expressionGradient.getColor(exp, min, max);
                sb.append(template.exp(SafeStylesUtils.forTrustedBackgroundColor(colour), safeValue));
            }catch (NumberFormatException ex){
                sb.append(template.std(safeValue));
            }
        }
    }


}
