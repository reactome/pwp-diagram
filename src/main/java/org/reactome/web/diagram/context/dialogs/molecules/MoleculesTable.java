package org.reactome.web.diagram.context.dialogs.molecules;

import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.HasDirection;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.ImportedWithPrefix;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.CustomScrollPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HeaderPanel;
import com.google.gwt.view.client.ListDataProvider;
import org.reactome.web.analysis.client.model.AnalysisType;
import org.reactome.web.diagram.data.graph.model.GraphPhysicalEntity;
import org.reactome.web.diagram.profiles.analysis.AnalysisColours;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class MoleculesTable<T extends GraphPhysicalEntity> extends DataGrid<T> {
    private List<Column<T, String>> expression = new ArrayList<>();
    private ListDataProvider<T> dataProvider;
    private Column<T, String> type;
    private AnalysisType analysisType;
    private String name;

    public MoleculesTable(String name, List<T> molecules, AnalysisType analysisType, List<String> expression, Double min, Double max, int sel) {
        super(molecules.size(), (MoleculesTableResource) GWT.create(MoleculesTableResource.class));
        this.name = name;
        this.analysisType = analysisType;
        setAlwaysShowScrollBars(false);

        List<T> list = sortMolecules(molecules, analysisType);
        dataProvider = new ListDataProvider<>(list);
        dataProvider.addDataDisplay(this);
        setMoleculesLabels(false); // Show names by default

        addExpressionColumns(expression, min, max, sel);

        // Make the scrollbars invisible
        HeaderPanel panel = (HeaderPanel) this.getWidget();
        CustomScrollPanel scrollPanel = (CustomScrollPanel) panel.getContentWidget();
        scrollPanel.getHorizontalScrollbar().asWidget().getElement().getStyle().setOpacity(0);
    }

    public HandlerRegistration addMoleculeSelectedHandler(MoleculeSelectedHandler handler){
        return addHandler(handler, MoleculeSelectedEvent.TYPE);
    }

    public void addExpressionColumns(List<String> expression, Double min, Double max, int sel) {
        if (expression != null && min!=null && max!=null) {
            this.setColumnWidth(0, 80, Unit.PX); // Resize the 1st column
            for (int i = 0; i < expression.size(); i++) {
                Column<T, String> exp = buildColumnExpression(i, min, max);
                exp.setHorizontalAlignment(HasHorizontalAlignment.HorizontalAlignmentConstant.endOf(HasDirection.Direction.LTR));
                this.expression.add(exp);
                this.addColumn(exp, expression.get(i));
                this.setColumnWidth(exp, 60, Unit.PX);
            }
        }
        if(this.getRowCount()>0) {
            highlightExpColumn(sel);
        }
    }

    public void setMoleculesLabels(boolean showIds){
        if(type!=null) { removeColumn(type); }
        type = buildColumnTitle(showIds);
        insertColumn(0, type, name);
        redraw();
    }

    public void highlightExpColumn(int col) {
        col++; //This correction is needed since there is a column in front of the expression columns
        for (int i = 1; i <= expression.size(); i++) {
            removeColumnStyleName(i, RESOURCES.getCSS().selectedExpression());
            addColumnStyleName(i, RESOURCES.getCSS().unselectedExpression());
            if (i == col) {
                addColumnStyleName(i, RESOURCES.getCSS().selectedExpression());
            }
        }
        try {
            getRowElement(0).getCells().getItem(col).scrollIntoView();
        } catch (Exception e) {
            //Nothing here
        }
    }

    public void removeExpressionColumns() {
        for (Column<T, String> expColumn : expression) {
            removeColumn(expColumn);
        }
        expression.clear();
        if(getColumnCount() == 1) {
            // Resize the 1st column so that it gets all the available space
            this.setColumnWidth(0, 257, Unit.PX);
        }
    }

    public void setAnalysisType(AnalysisType analysisType){
        this.analysisType = analysisType;
        // Re-sort the list
        List<T> list = dataProvider.getList();
        dataProvider.setList(sortMolecules(list, analysisType));
        redraw();
    }

    private List<T> sortMolecules(List<T> molecules, AnalysisType analysisType){
        Collections.sort(molecules, GraphPhysicalEntity.getDisplayNameComparator());
        List<T> list = new LinkedList<>();
        List<T> tailList = new LinkedList<>();
        if(analysisType==null || analysisType == AnalysisType.OVERREPRESENTATION || analysisType==AnalysisType.SPECIES_COMPARISON) {
            // We have to move all the NOT HIT molecules at the very end
            for (T molecule : molecules) {
                if (!molecule.isHit()) tailList.add(molecule);
                else list.add(molecule);
            }
        }else{
            // We have to move all the molecules without expression at the very end
            for (T molecule : molecules) {
                if (molecule.getExpression() == null || molecule.getExpression().isEmpty()) tailList.add(molecule);
                else list.add(molecule);
            }
        }
        list.addAll(tailList);
        return list;
    }

    private void applyORAColour(){
        List<T> list = dataProvider.getList();
        for(int i=0;i<list.size();i++){
            T object = dataProvider.getList().get(i);
            if(object.isHit()){
                getRowElement(i).getCells().getItem(0).getStyle().setBackgroundColor(AnalysisColours.get().PROFILE.getEnrichment().getGradient().getMax());
                getRowElement(i).getCells().getItem(0).getStyle().setColor("#000000");
            }else{
                getRowElement(i).getCells().getItem(0).getStyle().setBackgroundColor("transparent");
            }
        }
    }

    private Column<T, String> buildColumnTitle(final boolean showIds) {
        Column<T, String> columnTitle = new Column<T, String>(new ClickableTextCell()) {
            @Override
            public String getValue(T object) {
                if(showIds){
                    return object.getIdentifier() != null && !object.getIdentifier().isEmpty() ? object.getIdentifier() : object.getStId();
                }else{
                    return object.getDisplayName();
                }
            }
        };
        columnTitle.setSortable(true);
        columnTitle.setFieldUpdater(new FieldUpdater<T, String>() {
            public void update(int index, T object, String value) {
                //noinspection unchecked
                fireEvent(new MoleculeSelectedEvent(object));
            }
        });
        return columnTitle;
    }

    private Column<T, String> buildColumnExpression(final int col, double min, double max) {
        return new Column<T, String>(new ExpressionCell(min, max)) {
            @Override
            public String getValue(T object) {
                Double exp = (object.getExpression() != null) ? object.getExpression().get(col) : null;
                return exp != null ? NumberFormat.getFormat("#.##E0").format(exp) : "";
            }
        };
    }

    @Override
    public void redraw() {
        super.redraw();
        if(this.analysisType==AnalysisType.OVERREPRESENTATION || this.analysisType==AnalysisType.SPECIES_COMPARISON ) {
            Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                @Override
                public void execute() {
                    applyORAColour();
                }
            });
        }
    }


    public ListDataProvider<T> getDataProvider() {
		return dataProvider;
	}


	public static Resources RESOURCES;
    static {
        RESOURCES = GWT.create(Resources.class);
        RESOURCES.getCSS().ensureInjected();
    }

    public interface Resources extends ClientBundle {
        @ClientBundle.Source(ResourceCSS.CSS)
        ResourceCSS getCSS();
    }

    @ImportedWithPrefix("diagram-MoleculesTableExpressionSelection")
    public interface ResourceCSS extends CssResource {

        String CSS = "org/reactome/web/diagram/context/dialogs/TableSelectionStyle.css";

        String unselectedExpression();

        String selectedExpression();
    }

    public interface MoleculesTableResource extends DataGrid.Resources {

        @ImportedWithPrefix("diagram-MoleculesTable")
        interface MoleculesStyle extends Style {
            String CSS = "org/reactome/web/diagram/context/dialogs/MoleculesTable.css";
        }

        /**
         * The styles used in this widget.
         */
        @Override
        @Source(MoleculesStyle.CSS)
        MoleculesStyle dataGridStyle();
    }
}
