package org.reactome.web.diagram.context.dialogs.interactors;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.data.interactors.raw.RawInteractor;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public class TableItemSelectedEvent<T extends RawInteractor> extends GwtEvent<TableItemSelectedHandler> {
    public static Type<TableItemSelectedHandler> TYPE = new Type<>();

    private T value;
    private Selection selectedColumn;

    public TableItemSelectedEvent(T value, Selection selectedColumn) {
        this.value = value;
        this.selectedColumn = selectedColumn;
    }

    @Override
    public Type<TableItemSelectedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(TableItemSelectedHandler handler) {
        handler.onTableItemSelected(this);
    }

    public T getValue() {
        return value;
    }

    public Selection getSelectedColumn() {
        return selectedColumn;
    }

    public enum Selection {
        ACCESSION,
        ID,
        SCORE
    }
}
