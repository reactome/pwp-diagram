package org.reactome.web.diagram.data.interactors.common;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class LinkCommon {
    private String id;
    private double score;

    public LinkCommon(String id, double score) {
        this.id = id;
        this.score = score;
    }

    public String getId() {
        return id;
    }

    public double getScore() {
        return score;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LinkCommon that = (LinkCommon) o;

        if (Double.compare(that.score, score) != 0) return false;
        return id != null ? id.equals(that.id) : that.id == null;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id != null ? id.hashCode() : 0;
        temp = Double.doubleToLongBits(score);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
