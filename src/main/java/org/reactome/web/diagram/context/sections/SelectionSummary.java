package org.reactome.web.diagram.context.sections;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public class SelectionSummary {
    private int rowIndex;
    private int colIndex;
    private String text;

    public SelectionSummary(int rowIndex, int colIndex, String text) {
        this.rowIndex = rowIndex;
        this.colIndex = colIndex;
        this.text = text;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public int getColIndex() {
        return colIndex;
    }

    public void setColIndex(int colIndex) {
        this.colIndex = colIndex;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text.trim();
    }
}
