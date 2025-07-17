package org.reactome.web.diagram.context.dialogs.interactors;

import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.HasDirection;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.ImportedWithPrefix;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.CustomScrollPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HeaderPanel;
import com.google.gwt.view.client.ListDataProvider;
import org.reactome.web.analysis.client.model.AnalysisType;
import org.reactome.web.diagram.context.dialogs.molecules.ExpressionCell;
import org.reactome.web.diagram.data.interactors.model.DiagramInteractor;
import org.reactome.web.diagram.data.interactors.model.InteractorEntity;
import org.reactome.web.diagram.data.interactors.model.InteractorLink;
import org.reactome.web.diagram.data.interactors.raw.RawInteractor;
import org.reactome.web.diagram.profiles.analysis.AnalysisColours;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
@SuppressWarnings("Duplicates")
public class InteractorsTable<T extends RawInteractor> extends DataGrid<T> {
    private List<Column<T, String>> expression = new ArrayList<>();
    private ListDataProvider<T> dataProvider;
    private Column<T, String> type;
    private AnalysisType analysisType;
    private String name;

    private T hoveredItem;
    private double threshold;
    private static String  HIGHLIGHT_COLOUR = "#FFFF00";
    private static String  HIGHLIGHT_COLOUR_ORA = "#00AA00";

    public InteractorsTable(String name, double threshold, AnalysisType analysisType, List<String> expression, Double min, Double max, int sel) {
        super(0, (MoleculesTableResource) GWT.create(MoleculesTableResource.class));
        this.name = name;
        this.analysisType = analysisType;
        this.threshold = threshold;
        setAlwaysShowScrollBars(false);
        setEmptyTableWidget(new HTML("No interactors to display"));

        dataProvider = new ListDataProvider<>();
        dataProvider.addDataDisplay(this);
        setInteractorsLabels(false); // Show names by default

        addExpressionColumns(expression, min, max, sel);

        // Make the scrollbars invisible
        HeaderPanel panel = (HeaderPanel) this.getWidget();
        CustomScrollPanel scrollPanel = (CustomScrollPanel) panel.getContentWidget();
        scrollPanel.getHorizontalScrollbar().asWidget().getElement().getStyle().setOpacity(0);
        scrollPanel.getVerticalScrollbar().asWidget().getElement().getStyle().setOpacity(0);
    }

    public HandlerRegistration addTableItemSelectedHandler(TableItemSelectedHandler handler){
        return addHandler(handler, TableItemSelectedEvent.TYPE);
    }

    public void addExpressionColumns(List<String> expression, Double min, Double max, int sel) {
        if (expression != null && min!=null && max!=null) {
            this.setColumnWidth(0, 80, Unit.PX); // Resize the columns
            this.setColumnWidth(1, 80, Unit.PX);
            this.setColumnWidth(2, 30, Unit.PX);
            for (int i = 0; i < expression.size(); i++) {
                Column<T, String> exp = buildColumnExpression(i, min, max);
                exp.setHorizontalAlignment(HasHorizontalAlignment.HorizontalAlignmentConstant.endOf(HasDirection.Direction.LTR));
                this.expression.add(exp);
                this.addColumn(exp, expression.get(i));
                this.setColumnWidth(exp, 60, Unit.PX);
            }
        }
            highlightExpColumn(sel);
    }

    public void setInteractorsLabels(boolean showIds){
        if(type!=null) { removeColumn(type); }
        type = buildColumnTitle(showIds);
        insertColumn(0, type, name);

        //We need to add the extra columns only once
        if(getColumnCount() == 1) {
            insertColumn(1, buildEvidencesColumn(), "#Evidence");
            insertColumn(2, buildScoreColumn(), "Score");

            this.setColumnWidth(0, 140, Unit.PX);
            this.setColumnWidth(1, 60, Unit.PX);
            this.setColumnWidth(2, 50, Unit.PX);
        }
    }

    public void updateRows(List<T> newList){
        dataProvider.getList().clear();
        dataProvider.getList().addAll(newList);
        setVisibleRange(0, newList.size());
        setRowCount(newList.size());
    }

    public void setHovered(DiagramInteractor hovered) {
        this.hoveredItem = null;
        if (hovered != null){
            for (T object : dataProvider.getList()) {
                if (hovered instanceof InteractorEntity && hovered.getAccession().equals(object.getAcc())) {
                    this.hoveredItem = object;
                    break;
                } else if (hovered instanceof InteractorLink && ((InteractorLink) hovered).getId().equals(object.getId())) {
                    this.hoveredItem = object;
                    break;
                }
            }
        }
        applyThreshold();
        highlightHoveredItem();
    }

    public void setHovered(String acc){
        if (acc == null || acc.isEmpty()){
            hoveredItem = null;
        } else {
            for (T object : dataProvider.getList()) {
                if (acc.equals(object.getAcc())) {
                    this.hoveredItem = object;
                    break;
                }
            }
        }
        applyThreshold();
        highlightHoveredItem();
    }

    public void setThreshold(double threshold){
        this.threshold = threshold;
        applyThreshold();
    }

    public void highlightExpColumn(int col) {
        col = col + 3; //This correction is needed since there is a column in front of the expression columns
        for (int i = 0; i <= expression.size(); i++) {
            removeColumnStyleName(i + 3, RESOURCES.getCSS().selectedExpression());
            addColumnStyleName(i + 3, RESOURCES.getCSS().unselectedExpression());
            if (i + 3 == col) {
                addColumnStyleName(i + 3, RESOURCES.getCSS().selectedExpression());
            }
        }
        try {
            getRowElement(0).getCells().getItem(col).scrollIntoView();
        } catch (Exception e) {
            //Nothing here
        }
    }

    private void highlightHoveredItem(){
        String highlightColour = analysisType == null ? HIGHLIGHT_COLOUR : HIGHLIGHT_COLOUR_ORA;
        List<T> list = dataProvider.getList();
        for(int i=0;i<list.size();i++){
            T object = dataProvider.getList().get(i);
            if(object.equals(hoveredItem)){
                getRowElement(i).getCells().getItem(0).scrollIntoView();
                getRowElement(i).getCells().getItem(0).getStyle().setBackgroundColor(highlightColour);
                getRowElement(i).getCells().getItem(1).getStyle().setBackgroundColor(highlightColour);
                getRowElement(i).getCells().getItem(2).getStyle().setBackgroundColor(highlightColour);

                getRowElement(i).getCells().getItem(0).getStyle().setColor("#000000");
                getRowElement(i).getCells().getItem(1).getStyle().setColor("#000000");
                getRowElement(i).getCells().getItem(2).getStyle().setColor("#000000");
            }
        }
    }

    private void applyThreshold(){
        List<T> list = dataProvider.getList();
        for(int i=0;i<list.size();i++){
            T object = dataProvider.getList().get(i);
            boolean isHit = object.getIsHit() == null ? false : object.getIsHit();
            NodeList<TableCellElement> cells = getRowElement(i).getCells();
            String foreColour;
            String backColour;
            if(object.getScore() < threshold) {
                foreColour = "#AAAAAA";
                backColour = "transparent";
            } else {
                if(isHit && (analysisType==AnalysisType.OVERREPRESENTATION || analysisType==AnalysisType.SPECIES_COMPARISON)) {
                    foreColour = "#000000";
                    backColour = AnalysisColours.get().PROFILE.getEnrichment().getGradient().getMax();
                } else {
                    foreColour = "#FFFFFF";
                    backColour = "transparent";
                }
            }

            cells.getItem(0).getStyle().setColor(foreColour);
            cells.getItem(1).getStyle().setColor(foreColour);
            cells.getItem(2).getStyle().setColor(foreColour);

            cells.getItem(0).getStyle().setBackgroundColor(backColour);
            cells.getItem(1).getStyle().setBackgroundColor(backColour);
            cells.getItem(2).getStyle().setBackgroundColor(backColour);
        }
    }

    private Column<T, String> buildColumnTitle(final boolean showIds) {
        Column<T, String> columnTitle = new Column<T, String>(new InteractorClickableCell()) {
            @Override
            public String getValue(T object) {
                if(showIds){
                    return object.getAcc();
                }else{
                    return object.getAlias()!=null ? object.getAlias() : object.getAcc();
                }
            }
        };
        columnTitle.setFieldUpdater(new FieldUpdater<T, String>() {
            public void update(int index, T object, String value) {
                fireEvent(new TableItemSelectedEvent(object, TableItemSelectedEvent.Selection.ACCESSION));
            }
        });
        return columnTitle;
    }

    private Column<T, String> buildEvidencesColumn() {
        Column<T, String> columnTitle = new Column<T, String>(new ClickableTextCell()) {
            @Override
            public String getValue(T object) {
                Integer evidenceSize = object.getEvidences();
                return ( evidenceSize == null || evidenceSize.equals(0) ) ? "N/A" : "" + evidenceSize;
            }
        };
        columnTitle.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        columnTitle.setFieldUpdater(new FieldUpdater<T, String>() {
            public void update(int index, T object, String value) {
                fireEvent(new TableItemSelectedEvent(object, TableItemSelectedEvent.Selection.ID));
            }
        });
        return columnTitle;
    }

    private Column<T, String> buildScoreColumn() {
        Column<T, String> columnTitle = new Column<T, String>(new ClickableTextCell()) {
            @Override
            public String getValue(T object) {
                return NumberFormat.getFormat("0.000").format(object.getScore());
            }
        };
        // This is for setting the column text alignment BUT
        // to do the same for the header you have to go to the CSS
        columnTitle.setHorizontalAlignment(HasHorizontalAlignment.HorizontalAlignmentConstant.endOf(HasDirection.Direction.LTR));
        columnTitle.setFieldUpdater(new FieldUpdater<T, String>() {
            public void update(int index, T object, String value) {
                fireEvent(new TableItemSelectedEvent(object, TableItemSelectedEvent.Selection.SCORE));
            }
        });
        return columnTitle;
    }

    private Column<T, String> buildColumnExpression(final int col, double min, double max) {
        return new Column<T, String>(new ExpressionCell(min, max)) {
            @Override
            public String getValue(T object) {
                Double exp = (object.getExp() != null) ? object.getExp().get(col) : null;
                return exp != null ? NumberFormat.getFormat("#.##E0").format(exp) : "";
            }
        };
    }

    @Override
    public void redraw() {
        super.redraw();
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                applyThreshold();
            }
        });
    }

    public void removeExpressionColumns() {
        for (Column<T, String> expColumn : expression) {
            removeColumn(expColumn);
        }
        expression.clear();
        if(getColumnCount() == 3) {
            // Resize the 3 column to get all the available space
            this.setColumnWidth(0, 140, Unit.PX);
            this.setColumnWidth(2, 50, Unit.PX);
        }
        redraw();
    }

    public void setAnalysisType(AnalysisType analysisType){
        this.analysisType = analysisType;
        redraw();
    }


    public static Resources RESOURCES;
    static {
        RESOURCES = GWT.create(Resources.class);
        RESOURCES.getCSS().ensureInjected();
    }

    public interface Resources extends ClientBundle {
        @Source(ResourceCSS.CSS)
        ResourceCSS getCSS();
    }

    @ImportedWithPrefix("diagram-InteractorsTableExpressionSelection")
    public interface ResourceCSS extends CssResource {

        String CSS = "org/reactome/web/diagram/context/dialogs/TableSelectionStyle.css";

        String unselectedExpression();

        String selectedExpression();
    }

    public interface MoleculesTableResource extends DataGrid.Resources {

        @ImportedWithPrefix("diagram-InteractorsTable")
        interface MoleculesStyle extends Style {
            String CSS = "org/reactome/web/diagram/context/dialogs/InteractorsTable.css";
        }

        /**
         * The styles used in this widget.
         */
        @Override
        @Source(MoleculesStyle.CSS)
        MoleculesStyle dataGridStyle();
    }
}
