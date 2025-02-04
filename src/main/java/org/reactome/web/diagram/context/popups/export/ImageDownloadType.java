package org.reactome.web.diagram.context.popups.export;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import org.reactome.web.diagram.client.DiagramFactory;

import java.util.Arrays;
import java.util.List;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public enum ImageDownloadType implements DownloadType {
    SVG     ("SVG",     "/diagram/__STID__.svg__PARAMS__",       "SVG",     ImageFormatIcons.INSTANCE.SVGIcon()),
    PNG     ("PNG",     "/diagram/__STID__.png__PARAMS__",       "PNG",     ImageFormatIcons.INSTANCE.PNGIcon(),    true),
    JPG     ("JPG",     "/diagram/__STID__.jpg__PARAMS__",       "JPG",     ImageFormatIcons.INSTANCE.JPGIcon(),    true),
    GIF     ("GIF",     "/diagram/__STID__.gif__PARAMS__",       "GIF",     ImageFormatIcons.INSTANCE.GIFIcon(),    true),
    PDF     ("PDF ",    "/document/event/__STID__.pdf__PARAMS__","PDF",     ImageFormatIcons.INSTANCE.PDFIcon()),
    PPT     ("PPTX",    "/diagram/__STID__.pptx__PARAMS__",      "PPTX",    ImageFormatIcons.INSTANCE.PPTXIcon()),
    SBGN    ("SBGN",    "/event/__STID__.sbgn__PARAMS__",        "SBGN",    ImageFormatIcons.INSTANCE.SBGNIcon());

    //NOTE: please put the quality values below in ascending order
    public static final List<Integer> QUALITIES = Arrays.asList(2, 5, 7);

    private String name;
    private String templateURL;
    private String tooltip;
    private transient ImageResource icon;
    private boolean hasQualityOptions;

    ImageDownloadType(String name, String templateURL, String tooltip, ImageResource icon) {
        this(name, templateURL, tooltip, icon, false);
    }

    ImageDownloadType(String name, String templateURL, String tooltip, ImageResource icon, boolean hasQualityOptions) {
        this.name = name;
        this.templateURL =  DiagramFactory.SERVER + "/ContentService/exporter" + templateURL;
        this.tooltip = tooltip;
        this.icon = icon;
        this.hasQualityOptions = hasQualityOptions;
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
    public boolean hasQualityOptions() {
        return hasQualityOptions;
    }

    public interface ImageFormatIcons extends ClientBundle {

        ImageFormatIcons INSTANCE = GWT.create(ImageFormatIcons.class);

        @Source("../images/export2png.png")
        ImageResource PNGIcon();

        @Source("../images/export2gif.png")
        ImageResource GIFIcon();

        @Source("../images/export2jpg.png")
        ImageResource JPGIcon();

        @Source("../images/export2svg.png")
        ImageResource SVGIcon();

        @Source("../images/export2pptx.png")
        ImageResource PPTXIcon();

        @Source("../images/export2sbgn.png")
        ImageResource SBGNIcon();

        @Source("../images/export2pdf.png")
        ImageResource PDFIcon();

    }
}
