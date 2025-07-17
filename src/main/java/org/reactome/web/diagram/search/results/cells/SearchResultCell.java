package org.reactome.web.diagram.search.results.cells;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Image;
import org.reactome.web.diagram.search.SearchLauncher;
import org.reactome.web.diagram.search.SearchResultObject;


/**
 * The Cell used to render a {@link SearchResultObject}.
 *
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public class SearchResultCell extends AbstractCell<SearchResultObject> {

    private static SafeHtml urHereIcon;

    interface Templates extends SafeHtmlTemplates {
        @SafeHtmlTemplates.Template("" +
                "<div style=\"overflow:hidden; white-space:nowrap; text-overflow:ellipsis; height:44px; border-bottom:#efefef solid 1px \">" +
                    "<div title=\"{1}\" style=\"float:left; margin:15px 0 0 5px;\">{0}</div>" +
                    "<div style=\"float:left; margin-left:10px; width:290px;\">" +
                        "<div title=\"{3}\" style=\"overflow:hidden; white-space:nowrap; text-overflow:ellipsis; font-size:small;\">" +
                            "{2}" +
                        "</div>" +
                        "<div style=\"float:left; width:270px;\">" +
                            "<div style=\"overflow:hidden; white-space:nowrap; text-overflow:ellipsis; margin-top:-2px; font-size:x-small; color:#89c053;\">" +
                                "{4}" +
                            "</div>" +
                            "<div style=\"overflow:hidden; white-space:nowrap; text-overflow:ellipsis; margin-top:-2px; font-size:x-small; color:#f5b945;\">" +
                                "{5}" +
                            "</div>" +
                        "</div>" +
                        "<div style=\"float:left; margin-left: 1px;\">{6}</div>" +
                    "</div>" +
                "</div>")
        SafeHtml cell(SafeHtml image, String imgTooltip, SafeHtml primary, String primaryTooltip, SafeHtml secondary, SafeHtml tertiary, SafeHtml uRHereImage);

        @SafeHtmlTemplates.Template("" +
                "<div style=\"overflow:hidden; white-space:nowrap; text-overflow:ellipsis; height:44px; border-bottom:#efefef solid 1px \">" +
                    "<div title=\"{1}\" style=\"float:left; margin:15px 0 0 5px;\">{0}</div>" +
                    "<div style=\"float:left; margin-left:10px; width:290px;\">" +
                        "<div title=\"{3}\" style=\"overflow:hidden; white-space:nowrap; text-overflow:ellipsis; font-size:small;\">" +
                            "{2}" +
                        "</div>" +
                        "<div style=\"overflow:hidden; white-space:nowrap; text-overflow:ellipsis; margin-top:-2px; font-size:x-small; color:#89c053;\">" +
                            "{4}" +
                        "</div>" +
                        "<div style=\"overflow:hidden; white-space:nowrap; text-overflow:ellipsis; margin-top:-2px; font-size:x-small; color:#f5b945;\">" +
                            "{5}" +
                        "</div>" +
                    "</div>" +
                "</div>")
        SafeHtml minCell(SafeHtml image, String imgTooltip, SafeHtml primary, String primaryTooltip, SafeHtml secondary, SafeHtml tertiary);
    }

    private static Templates templates = GWT.create(Templates.class);

    public SearchResultCell() {
        super();

        Image urHere = new Image(SearchLauncher.RESOURCES.youAreHere());
        urHere.setStyleName(SearchLauncher.RESOURCES.getCSS().youAreHereIcon());
        urHere.setTitle("You are here. This is the displayed pathway.");
        SearchResultCell.urHereIcon = SafeHtmlUtils.fromTrustedString(urHere.toString());
    }

    @Override
    public void render(Context context, SearchResultObject value, SafeHtmlBuilder sb) {
        /*
         * Always do a null check on the value. Cell widgets can pass null to
         * cells if the underlying data contains a null, or if the data arrives
         * out of order.
         */
        if (value == null) {
            return;
        }

//        Next two lines DO NOT work for Chrome
//        final ImagePrototypeElement imageElement = AbstractImagePrototype.create(value.getImageResource()).createElement();
//        final SafeHtml safeImage = new OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml(imageElement.getString());

        Image img = new Image(value.getImageResource());
        SafeHtml image = SafeHtmlUtils.fromTrustedString(img.toString());
        String imgTooltip = value.getSchemaClass().name;

        SafeHtml primary = SafeHtmlUtils.fromTrustedString(value.getPrimarySearchDisplay());
        String primaryTooltip = value.getPrimaryTooltip();

        SafeHtml secondary = SafeHtmlUtils.fromTrustedString(value.getSecondarySearchDisplay());
        SafeHtml tertiary = SafeHtmlUtils.fromTrustedString(value.getTertiarySearchDisplay());
        if(value.isDisplayed()) {
            sb.append(templates.cell(image, imgTooltip, primary, primaryTooltip, secondary, tertiary, SearchResultCell.urHereIcon));
        } else {
            sb.append(templates.minCell(image, imgTooltip, primary, primaryTooltip, secondary, tertiary));
        }
    }
}

