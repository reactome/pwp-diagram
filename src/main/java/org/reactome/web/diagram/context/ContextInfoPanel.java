package org.reactome.web.diagram.context;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.*;
import org.reactome.web.diagram.context.dialogs.InteractorsDialogPanel;
import org.reactome.web.diagram.context.dialogs.MoleculesDialogPanel;
import org.reactome.web.diagram.context.dialogs.PathwaysDialogPanel;
import org.reactome.web.diagram.data.Context;
import org.reactome.web.diagram.data.graph.model.GraphComplex;
import org.reactome.web.diagram.data.graph.model.GraphEntityWithAccessionedSequence;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.graph.model.GraphSimpleEntity;
import org.reactome.web.diagram.data.layout.DiagramObject;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class ContextInfoPanel extends Composite implements ClickHandler {

    private List<Button> btns = new LinkedList<>();
    private Button molecules;
    private Button pathways;
    private Button interactors;

    private DeckLayoutPanel container;
    
    private MoleculesDialogPanel moleculesDialogPanel;

    public ContextInfoPanel(ContextDialogPanel parent, EventBus eventBus, DiagramObject diagramObject, Context context) {
        FlowPanel buttonsPanel = new FlowPanel();
        buttonsPanel.setStyleName(RESOURCES.getCSS().buttonsPanel());
        buttonsPanel.add(this.molecules = getButton("Molecules", RESOURCES.molecules()));
        buttonsPanel.add(this.pathways = getButton("Pathways", RESOURCES.pathways()));
        buttonsPanel.add(this.interactors = getButton("Interactors", RESOURCES.interactors()));
        GraphObject graphObject = diagramObject.getGraphObject();

        // Disable the interactors' tab in case of anything else besides protein and chemical
        boolean enabled = graphObject instanceof GraphSimpleEntity || graphObject instanceof GraphEntityWithAccessionedSequence || graphObject instanceof GraphComplex;
        this.interactors.setEnabled(enabled);

        this.molecules.addStyleName(RESOURCES.getCSS().buttonSelected());

        this.container = new DeckLayoutPanel();
        this.container.setStyleName(RESOURCES.getCSS().container());
        moleculesDialogPanel = new MoleculesDialogPanel(eventBus, diagramObject, context.getAnalysisStatus());
        PathwaysDialogPanel pathwaysDialogPanel = new PathwaysDialogPanel(eventBus, diagramObject, context);
        InteractorsDialogPanel interactorsDialogPanel = new InteractorsDialogPanel(eventBus, diagramObject, context);
        this.container.add(moleculesDialogPanel);
        this.container.add(pathwaysDialogPanel);
        this.container.add(interactorsDialogPanel);
        this.container.showWidget(0);
        this.container.setAnimationVertical(true);
        this.container.setAnimationDuration(500);

        FlowPanel outerPanel = new FlowPanel();
        outerPanel.setStyleName(RESOURCES.getCSS().outerPanel());
        outerPanel.add(buttonsPanel);
        outerPanel.add(this.container);

        // add handlers
        parent.addChangeLabelsEventHandler(moleculesDialogPanel);
        parent.addChangeLabelsEventHandler(pathwaysDialogPanel);
        parent.addChangeLabelsEventHandler(interactorsDialogPanel);

        initWidget(outerPanel);
    }

    public Button getButton(String text, ImageResource imageResource){
        Image buttonImg = new Image(imageResource);
        Label buttonLbl = new Label(text);

        FlowPanel fp = new FlowPanel();
        fp.add(buttonImg);
        fp.add(buttonLbl);

        SafeHtml safeHtml = SafeHtmlUtils.fromSafeConstant(fp.toString());
        Button btn = new Button(safeHtml, this);
        this.btns.add(btn);
        return btn;
    }

    @Override
    public void onClick(ClickEvent event) {
        for (Button btn : btns) {
            btn.removeStyleName(RESOURCES.getCSS().buttonSelected());
        }
        Button btn = (Button) event.getSource();
        btn.addStyleName(RESOURCES.getCSS().buttonSelected());
        if(btn.equals(this.molecules)){
            this.container.showWidget(0);
            // The following addresses a known gub in GTW where
            // DataGrids inside DeckLayoutPanels fail to render properly
            ((MoleculesDialogPanel)this.container.getVisibleWidget()).forceDraw();
        }else if(btn.equals(this.pathways)){
            this.container.showWidget(1);
        }else if(btn.equals(this.interactors)){
            this.container.showWidget(2);
            ((InteractorsDialogPanel)this.container.getVisibleWidget()).forceDraw();
        }
    }
    
    public void redraw() {
    	this.moleculesDialogPanel.reset();
    }

    public static Resources RESOURCES;
    static {
        RESOURCES = GWT.create(Resources.class);
        RESOURCES.getCSS().ensureInjected();
    }

    public interface Resources extends ClientBundle {
        @Source(ResourceCSS.CSS)
        ResourceCSS getCSS();

        @Source("images/interactors.png")
        ImageResource interactors();

        @Source("images/molecules.png")
        ImageResource molecules();

        @Source("images/pathways.png")
        ImageResource pathways();
    }

    @CssResource.ImportedWithPrefix("diagram-ContextInfoPanel")
    public interface ResourceCSS extends CssResource {
        String CSS = "org/reactome/web/diagram/context/ContextInfoPanel.css";

        String outerPanel();

        String buttonsPanel();

        String buttonSelected();

        String container();
    }
}
