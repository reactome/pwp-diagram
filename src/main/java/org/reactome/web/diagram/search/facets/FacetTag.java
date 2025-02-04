package org.reactome.web.diagram.search.facets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import org.reactome.web.diagram.util.SearchResultImageMapper;
import org.reactome.web.diagram.util.SearchResultImageMapper.ImageContainer;


/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public class FacetTag extends FocusPanel {

    private FlowPanel tagPanel;
    private String name;
    private Integer count;
    private boolean selected;

    public FacetTag(String name, Integer count) {
        this.name = name;
        this.count = count;

        init();
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        if(selected) {
            tagPanel.addStyleName(RESOURCES.getCSS().tagActive());
        } else {
            tagPanel.removeStyleName(RESOURCES.getCSS().tagActive());
        }
    }

    public String getName() {
        return name;
    }

    public Integer getCount() {
        return count;
    }

    public boolean isSelected() {
        return selected;
    }

    private void init() {
        setStyleName(RESOURCES.getCSS().base());
        NumberFormat nf = NumberFormat.getDecimalFormat();

        ImageContainer imgContainer = SearchResultImageMapper.getImage(name);
        Image image = new Image(imgContainer.getImageResource());
        image.setTitle(imgContainer.getTooltip());
        Label title = new Label(name + " (" + nf.format(count) + ")");
        title.setStyleName(RESOURCES.getCSS().tagText());

        tagPanel = new FlowPanel();
        tagPanel.setStyleName(RESOURCES.getCSS().tag());
        tagPanel.add(image);
        tagPanel.add(title);

        add(tagPanel);
    }


    public static Resources RESOURCES;
    static {
        RESOURCES = GWT.create(Resources.class);
        RESOURCES.getCSS().ensureInjected();
    }

    /**
     * A ClientBundle of resources used by this widget.
     */
    public interface Resources extends ClientBundle {
        /**
         * The styles used in this widget.
         */
        @Source(FacetTagCSS.CSS)
        FacetTagCSS getCSS();
    }

    /**
     * Styles used by this widget.
     */
    @CssResource.ImportedWithPrefix("diagram-FacetTagCSS")
    public interface FacetTagCSS extends CssResource {
        /**
         * The path to the default CSS styles used by this resource.
         */
        String CSS = "org/reactome/web/diagram/search/facets/FacetTag.css";

        String base();

        String tag();

        String tagText();

        String tagActive();
    }
}
