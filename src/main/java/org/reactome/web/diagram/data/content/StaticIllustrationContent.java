package org.reactome.web.diagram.data.content;

import org.reactome.web.analysis.client.model.EntityStatistics;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public abstract class StaticIllustrationContent implements Content {
    protected Long dbId;
    protected String stableId;
    protected String displayName;
    protected String speciesName;

    protected Boolean isDisease;
    protected Boolean forNormalDraw;

    protected EntityStatistics statistics;

    protected boolean graphLoaded = false;

    protected double minX, maxX, minY, maxY;

    public Long getDbId() {
        return dbId;
    }

    public String getStableId() {
        return stableId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getSpeciesName() {return speciesName;}

    public Boolean getIsDisease() {
        return isDisease;
    }

    public Boolean getForNormalDraw() {
        return forNormalDraw;
    }

    public double getMinX() {
        return minX;
    }

    public double getMaxX() {
        return maxX;
    }

    public double getMinY() {
        return minY;
    }

    public double getMaxY() {
        return maxY;
    }

    public boolean isGraphLoaded() {
        return graphLoaded;
    }

    public void setDbId(Long dbId) {
        this.dbId = dbId;
    }

    public void setStableId(String stableId) {
        this.stableId = stableId;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setSpeciesName(String speciesName) {
        this.speciesName = speciesName;
    }

    public void setIsDisease(Boolean isDisease) {
        this.isDisease = isDisease;
    }

    public void setForNormalDraw(Boolean forNormalDraw) {
        this.forNormalDraw = forNormalDraw;
    }

    public void setMinX(double minX) {
        this.minX = minX;
    }

    public void setMaxX(double maxX) {
        this.maxX = maxX;
    }

    public void setMinY(double minY) {
        this.minY = minY;
    }

    public void setMaxY(double maxY) {
        this.maxY = maxY;
    }

    public double getWidth() {
        return maxX - minX;
    }

    public double getHeight() {
        return maxY - minY;
    }

    public EntityStatistics getStatistics() {
        return statistics;
    }

    public void setStatistics(EntityStatistics statistics) {
        this.statistics = statistics;
    }
}
