package org.reactome.web.diagram.context.popups.export;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import org.reactome.web.diagram.client.DiagramFactory;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public enum ContentDownloadType implements DownloadType {
    SBML     ("SBML",        "/ContentService/exporter/event/__STID__.sbml",                 "SBML",     ImageFormatIcons.INSTANCE.SBMLIcon(),
            "Systems Biology Markup Language (SBML), is a free and open interchange format for computer models of biological processes."),
    BIOPAX_2 ("BIOPAX_2",    "/ReactomeRESTfulAPI/RESTfulWS/biopaxExporter/Level2/__ID__",   "BIOPAX 2", ImageFormatIcons.INSTANCE.BIOPAX2Icon(),
            "Biological Pathway Exchange (BioPAX) is a standard language aiming to enable integration, exchange, visualization and analysis of biological pathway data."),
    BIOPAX_3 ("BIOPAX_3",    "/ReactomeRESTfulAPI/RESTfulWS/biopaxExporter/Level3/__ID__",   "BIOPAX 3", ImageFormatIcons.INSTANCE.BIOPAX3Icon(),
            " BioPAX Level 3, expands the scope of BioPAX to include states of physical entities, generic physical entities, gene regulation and genetic interactions.");

    private String name;
    private String templateURL;
    private String tooltip;
    private transient ImageResource icon;
    private String info;

    ContentDownloadType(String name, String templateURL, String tooltip, ImageResource icon, String info) {
        this.name = name;
        this.templateURL =  DiagramFactory.SERVER + templateURL;
        this.tooltip = tooltip;
        this.icon = icon;
        this.info = info;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getTooltip() {
        return tooltip;
    }

    @Override
    public String getTemplateURL() {
        return templateURL;
    }

    @Override
    public ImageResource getIcon() {
        return icon;
    }

    @Override
    public String getInfo() {
        return info;
    }

    public interface ImageFormatIcons extends ClientBundle {

        ImageFormatIcons INSTANCE = GWT.create(ImageFormatIcons.class);

        @Source("../images/export2sbml.png")
        ImageResource SBMLIcon();

        @Source("../images/export2biopax2.png")
        ImageResource BIOPAX2Icon();

        @Source("../images/export2biopax3.png")
        ImageResource BIOPAX3Icon();

    }
}
