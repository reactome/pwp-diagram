package org.reactome.web.diagram.client.visualisers.ehld;

import org.vectomatic.dom.svg.OMElement;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public class SVGEntity {
    private String stId;
    private OMElement region;
    private OMElement overlay;
    private OMElement analysisInfo;
    private OMElement analysisText;
    private String tooltipText;

    public SVGEntity(String stId) {
        this.stId = stId;
    }

    public OMElement getAnalysisInfo() {
        return analysisInfo;
    }

    public OMElement getAnalysisText() {
        return analysisText;
    }

    public String getStId() {
        return stId;
    }

    public OMElement getRegion() {
        return region;
    }

    public OMElement getOverlay() {
        return overlay;
    }

    public OMElement getHoverableElement() {
        return hasRegion() ? region : overlay;
    }

    public boolean hasAnalysisInfo() {
        return analysisInfo!=null;
    }

    public boolean hasAnalysisText() {
        return analysisText!=null;
    }

    public boolean hasRegion() {
        return region!=null;
    }

    public boolean hasOverlay() {
        return overlay!=null;
    }

    public void setAnalysisInfo(OMElement analysisInfo) {
        this.analysisInfo = analysisInfo;
    }

    public void setAnalysisText(OMElement analysisText) {
        this.analysisText = analysisText;
    }

    public void setRegion(OMElement region) {
        this.region = region;
    }

    public void setOverlay(OMElement overlay) {
        this.overlay = overlay;
    }

    public String getTooltipText() {
        return tooltipText;
    }

    public void setTooltipText(String tooltipText) {
        this.tooltipText = tooltipText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SVGEntity svgEntity = (SVGEntity) o;

        return stId != null ? stId.equals(svgEntity.stId) : svgEntity.stId == null;

    }

    @Override
    public int hashCode() {
        return stId != null ? stId.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "SVGEntity{" +
                "stId='" + stId + '\'' +
                ", hasRegion=" + hasRegion() +
                ", hasOverlay=" + hasOverlay() +
                ", hasAnalysisInfo=" + hasAnalysisInfo() +
                ", hasAnalysisText=" + hasAnalysisText() +
                '}';
    }
}
