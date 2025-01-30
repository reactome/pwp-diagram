package org.reactome.web.diagram.client.visualisers.ehld;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;
import org.reactome.web.diagram.client.visualisers.ehld.filters.FilterColour;
import org.reactome.web.diagram.client.visualisers.ehld.filters.FilterFactory;
import org.reactome.web.diagram.context.popups.export.ExportDialog;
import org.reactome.web.diagram.data.Context;
import org.reactome.web.diagram.util.position.MousePosition;
import org.reactome.web.diagram.util.svg.SVGUtil;
import org.vectomatic.dom.svg.*;
import org.vectomatic.dom.svg.utils.SVGConstants;

import java.util.*;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public abstract class AbstractSVGPanel extends AbsolutePanel {
    protected EventBus eventBus;

    protected static final String REGION = "REGION-";
    protected static final String OVERLAY = "OVERLAY-";
    protected static final String ANALYSIS_INFO = "ANALINFO";

    protected static final String HOVERING_FILTER = "hoveringFilter";
    protected static final String SELECTION_FILTER = "selectionFilter";
    protected static final String FLAGGING_FILTER = "flaggingFilter";

    protected static final String FLAGGING_HOVERING_FILTER = "flaggingHoveringFilter";
    protected static final String SELECTION_HOVERING_FILTER = "selectionHoveredFilter";
    protected static final String SELECTION_FLAGGING_FILTER = "selectionFlaggingFilter";

    protected static final String SELECTION_FLAGGING_HOVERING_FILTER = "selectionFlaggingHoveringFilter";

    protected static final String HOVERING_OVERLAY_FILTER = "hoveringOverlayFilter";
    protected static final String SELECTION_OVERLAY_FILTER = "selectionOverlayFilter";

    protected static final String OVERLAY_TEXT_CLASS = "ST-OVERLAY-TEXT";
    protected static final String OVERLAY_TEXT_STYLE = "{ fill: #000000 !important; stroke:#000000; stroke-width:0.5px }";

    protected static final String ANALYSIS_INFO_CLASS = "ST-ANALYSIS-INFO";
    protected static final String ANALYSIS_INFO_STYLE = "{ opacity: 1 !important; -webkit-transition: all .9s ease-in-out;  -moz-transition: all .9s ease-in-out; transition: all .9s ease-in-out;}";

    protected static final String HIT_BASIS_COLOUR = "#FFFFFF";
    protected static final String HIT_BASIS_STROKE_COLOUR = "#000000";
    protected static final String HIT_BASIS_STROKE_WIDTH = "0.5";

    protected OMSVGSVGElement svg;
    protected List<OMSVGElement> svgLayers;
    protected Map<String, SVGEntity> entities;

    protected OMSVGDefsElement baseDefs;

    protected OMSVGMatrix ctm;
    protected OMSVGMatrix initialTM;
    protected OMSVGRect initialBB;
    protected float zFactor = 1;

    protected StringBuilder sb;

    protected boolean isSafari;

    protected Boolean includeInteractors;

    public AbstractSVGPanel(EventBus eventBus) {
        this.eventBus = eventBus;
        getElement().getStyle().setBackgroundColor("white");

        String userAgent = Window.Navigator.getUserAgent().toLowerCase();
        isSafari = userAgent.contains("safari") && !userAgent.contains("chrome");

        initFilters();
        sb = new StringBuilder();
    }

    public void showExportDialog(final Context context, final String selected, final String flagged) {
        if (svg != null) {
            OMSVGSVGElement auxSVG = (OMSVGSVGElement) svg.cloneNode(true);
            auxSVG.removeAttribute("viewBox");
            Image snapshot = new Image();
            snapshot.setUrl("data:image/svg+xml;base64," + btoa(auxSVG.getMarkup()));
            final ExportDialog dialog = new ExportDialog(context, selected, flagged, includeInteractors, snapshot);
            dialog.showCentered();
        }
    }

    native String btoa(String b64) /*-{
        return btoa(unescape(encodeURIComponent(b64)));
    }-*/;

    public void setSize(int width, int height) {
        //Set the size of the panel
        setWidth(width + "px");
        setHeight(height + "px");
        //Set the size of the SVG element itself
        if (svg != null) {
            svg.setWidth(Style.Unit.PX, width);
            svg.setHeight(Style.Unit.PX, height);
        }
    }

    protected SVGEntity addOrUpdateSVGEntity(OMElement element) {
        String elementId = element.getId();
        String stId = SVGUtil.keepStableId(elementId);
        SVGEntity entity = entities.get(stId);
        if (entity == null) {
            entity = new SVGEntity(stId);
            entities.put(stId, entity);
        }

        if (elementId.startsWith(REGION)) {
            entity.setRegion(element);
            //Check if there is an analysis info box and add it in the entity
            OMElement info = getAnalysisInfo(element);
            if (info != null) {
                entity.setAnalysisInfo(info);
                entity.setAnalysisText(getAnalysisText(info));
                info.getElement().setId(ANALYSIS_INFO + "-" + stId);
            }
        } else if (elementId.startsWith(OVERLAY)) {
            entity.setOverlay(element);
        }
        return entity;
    }

    protected OMSVGMatrix calculateFitAll(final float frame) {
        return calculateFitAll(frame, getOffsetWidth(), getOffsetHeight());
    }

    protected OMSVGMatrix calculateFitAll(final float frame, final int width, final int height) {
        OMSVGRect bb = svg.createSVGRect();
        // Add a frame around the image
        bb.setX(initialBB.getX() - frame);
        bb.setY(initialBB.getY() - frame);
        bb.setWidth(initialBB.getWidth() + (frame * 2));
        bb.setHeight(initialBB.getHeight() + (frame * 2));

        float rWidth = width / bb.getWidth();
        float rHeight = height / bb.getHeight();
        float zoom = Math.min(rWidth, rHeight);

        float vpCX = width * 0.5f;
        float vpCY = height * 0.5f;

        float newCX = bb.getX() + (bb.getWidth() * 0.5f);
        float newCY = bb.getY() + (bb.getHeight() * 0.5f);

        float corX = vpCX / zoom - newCX;
        float corY = vpCY / zoom - newCY;
        return svg.createSVGMatrix().scale(zoom).translate(corX, corY);
    }

    protected OMSVGPoint getCentrePoint() {
        return getTranslatedPoint(getOffsetWidth() / 2, getOffsetHeight() / 2);
    }

    protected OMSVGMatrix getInitialCTM() {
        return svg.createSVGMatrix(1, 0, 0, 1, 0, 0);
    }

    protected OMSVGPoint getTranslatedPoint(MouseEvent<?> event) {
        return getTranslatedPoint(MousePosition.getX(event), MousePosition.getY(event));
    }

    protected OMSVGPoint getTranslatedPoint(int x, int y) {
        OMSVGPoint p = svg.createSVGPoint();
        p.setX(x);
        p.setY(y);
        return p.matrixTransform(ctm.inverse());
    }

    protected List<OMSVGElement> getRootLayers(OMSVGSVGElement svg) {
        // Identify all layers by getting all top-level <g> elements
        List<OMSVGElement> svgLayers = new ArrayList<>();
        OMNodeList<OMNode> cNodes = svg.getChildNodes();
        for (OMNode node : cNodes) {
            if (node instanceof OMSVGGElement) {
                svgLayers.add((OMSVGGElement) node);
            }
        }
        return svgLayers;
    }

    protected String hex2Rgb(String colorStr, float alpha) {
        int r = Integer.valueOf(colorStr.substring(1, 3), 16);
        int g = Integer.valueOf(colorStr.substring(3, 5), 16);
        int b = Integer.valueOf(colorStr.substring(5, 7), 16);
        float a = alpha >= 0 && alpha <= 1 ? alpha : 1.0f;
        return "rgba(" + r + "," + g + "," + b + "," + NumberFormat.getFormat("#.#").format(a) + ")";
    }

    protected void initFilters() {
        baseDefs = new OMSVGDefsElement();
        baseDefs.appendChild(FilterFactory.getShadowFilter(HOVERING_FILTER));
        baseDefs.appendChild(FilterFactory.getOutlineFilter(SELECTION_FILTER, FilterColour.BLUE));
        baseDefs.appendChild(FilterFactory.getOutlineFilter(FLAGGING_FILTER, FilterColour.CYAN));

        baseDefs.appendChild(FilterFactory.getShadowWithOutlineFilter(SELECTION_HOVERING_FILTER, FilterColour.BLUE));
        baseDefs.appendChild(FilterFactory.getShadowWithOutlineFilter(FLAGGING_HOVERING_FILTER, FilterColour.CYAN));
        baseDefs.appendChild(FilterFactory.getDoubleOutlineFilter(SELECTION_FLAGGING_FILTER, FilterColour.BLUE, FilterColour.CYAN));
        baseDefs.appendChild(FilterFactory.getShadowWithDoubleOutlineFilter(SELECTION_FLAGGING_HOVERING_FILTER, FilterColour.BLUE, FilterColour.CYAN));

        baseDefs.appendChild(FilterFactory.getColouredOverlayFilter(HOVERING_OVERLAY_FILTER, FilterColour.YELLOW));
        baseDefs.appendChild(FilterFactory.getColouredOverlayFilter(SELECTION_OVERLAY_FILTER, FilterColour.BLUE));
    }

    protected void removeAttributeFromChildren(final OMNode root, final String attribute) {
        Iterator<OMNode> it = root.getChildNodes().iterator();
        while (it.hasNext()) {
            OMNode c = it.next();
            if (c instanceof OMElement) {
                OMElement el = (OMElement) c;
                el.removeAttribute(attribute);
                removeAttributeFromChildren(c, attribute);
            }
        }
    }

    protected List<OMElement> getAllTextElementsFrom(final OMNode root) {
        List<OMElement> rtn = new LinkedList<>();
        OMElement el = (OMElement) root;
        OMNodeList<OMElement> textEl = el.getElementsByTagName(SVGConstants.SVG_TEXT_TAG);
        for (OMElement element : textEl) {
            rtn.add(element);
        }
        return rtn;
    }

    protected boolean removeLogoFrom(final OMNode root) {
        boolean rtn = false;
        OMElement el = (OMElement) root;
        OMNodeList<OMElement> targetEl = el.getElementsByTagName(SVGConstants.SVG_G_TAG);
        for (OMElement element : targetEl) {
            if (element.getId().toLowerCase().startsWith("logo")) {
                element.getElement().removeFromParent();
                rtn = true;
            }
        }
        return rtn;
    }

    protected boolean removeTitleFrom(final OMNode root) {
        boolean rtn = false;
        OMElement el = (OMElement) root;
        OMNodeList<OMElement> targetEl = el.getElementsByTagName(SVGConstants.SVG_TITLE_TAG);
        for (OMElement element : targetEl) {
            element.getElement().removeFromParent();
            rtn = true;
        }
        return rtn;
    }

    private OMElement getAnalysisInfo(OMElement element) {
        OMElement rtn = null;
        OMNodeList<OMElement> els = element.getElementsByTagName(SVGConstants.SVG_G_TAG);
        if (els != null) {
            for (OMElement target : els) {
                if (target.getId().startsWith(ANALYSIS_INFO)) {
                    rtn = target;
                    break;
                }
            }
        }
        return rtn;
    }

    private OMElement getAnalysisText(OMElement element) {
        OMElement rtn = null;
        List<OMElement> textElements = getAllTextElementsFrom(element);
        if (textElements != null && textElements.size() > 0) {
            rtn = textElements.get(0);
        }
        return rtn;
    }

    private void resetAllTransformations(OMSVGSVGElement auxSVG) {
        auxSVG.setViewBox(initialBB);

        //Reset all transformations
        sb.setLength(0);
        sb.append("matrix(").append(initialTM.getA()).append(",").append(initialTM.getB()).append(",").append(initialTM.getC()).append(",")
                .append(initialTM.getD()).append(",").append(initialTM.getE()).append(",").append(initialTM.getF()).append(")");
        for (OMSVGElement svgLayer : getRootLayers(auxSVG)) {
            svgLayer.setAttribute(SVGConstants.SVG_TRANSFORM_ATTRIBUTE, sb.toString());
        }

    }
}
