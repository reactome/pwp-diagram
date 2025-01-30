package org.reactome.web.diagram.renderers.layout;

import com.google.gwt.event.shared.EventBus;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.events.ContentLoadedEvent;
import org.reactome.web.diagram.events.DiagramZoomEvent;
import org.reactome.web.diagram.handlers.ContentLoadedHandler;
import org.reactome.web.diagram.handlers.DiagramZoomHandler;
import org.reactome.web.diagram.renderers.common.RendererProperties;
import org.reactome.web.diagram.renderers.layout.s000.*;
import org.reactome.web.diagram.renderers.layout.s050.*;
import org.reactome.web.diagram.renderers.layout.s100.*;
import org.reactome.web.diagram.renderers.layout.s300.*;
import org.reactome.web.diagram.renderers.layout.s800.*;

import java.util.HashMap;
import java.util.Map;

import static org.reactome.web.diagram.data.content.Content.Type.DIAGRAM;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class RendererManager implements DiagramZoomHandler, ContentLoadedHandler {

    private static RendererManager manager;

    private final EventBus eventBus;

    private final Map<String, Renderer> s000 = new HashMap<>();
    private final Map<String, Renderer> s050 = new HashMap<>();
    private final Map<String, Renderer> s100 = new HashMap<>();
    private final Map<String, Renderer> s300 = new HashMap<>();
    private final Map<String, Renderer> s800 = new HashMap<>();

    private Map<String, Renderer> current = s100;
    private ConnectorRenderer connectorRenderer = new ConnectorRenderer100();

    private RendererManager(EventBus eventBus) {
        this.eventBus = eventBus;
        initialiseRenderers();
        initHandlers();
    }

    private void initHandlers(){
        this.eventBus.addHandler(DiagramZoomEvent.TYPE, this);
        this.eventBus.addHandler(ContentLoadedEvent.TYPE, this);
    }

    public static void initialise(EventBus eventBus) {
        if (RendererManager.manager != null) {
            throw new RuntimeException("Renderer Manager has already been initialised. Only one initialisation is permitted per Diagram Viewer instance.");
        }
        RendererManager.manager = new RendererManager(eventBus);
    }

    public static RendererManager get() {
        if (RendererManager.manager == null) {
            throw new RuntimeException("Renderer Manager has not been initialised yet. Please call initialise before using 'get'");
        }
        return RendererManager.manager;
    }

    public Renderer getRenderer(DiagramObject item) {
        if (item == null) return null;
        return this.current.get(item.getRenderableClass());
    }

    public Renderer getRenderer(String renderableClass) {
        if (renderableClass == null) return null;
        return this.current.get(renderableClass);
    }

    public Renderer getDiagramKeyRenderer(DiagramObject item){
        if (item == null) return null;
        return this.s100.get(item.getRenderableClass());
    }

    public ConnectorRenderer getConnectorRenderer() {
        return connectorRenderer;
    }

    public void initialiseRenderers() {
        Renderer aux;

        s000.put("Note", new NoteRenderer000());
        s000.put("Compartment", new CompartmentRenderer000());
        s000.put("Protein", new ProteinRenderer000());
        s000.put("Cell", new CellRenderer000());
        s000.put("Chemical", new ChemicalRenderer000());
        s000.put("ChemicalDrug", new ChemicalDrugRenderer000());
        s000.put("ProteinDrug", new ProteinDrugRenderer000());
        s000.put("RNADrug", new RNADrugRenderer000());
        s000.put("Reaction", new ReactionRenderer000());
        s000.put("Complex", new ComplexRenderer000());
        s000.put("ComplexDrug", new ComplexDrugRenderer000());
        s000.put("Entity", new OtherEntityRenderer000());
        s000.put("EntitySet", new SetRenderer000());
        s000.put("EntitySetDrug", new SetDrugRenderer000());
        s000.put("ProcessNode", new ProcessNodeRenderer000());
        s000.put("EncapsulatedNode", new EncapsulatedNodeRenderer000());
        s000.put("FlowLine", new FlowlineRenderer000());
        s000.put("Interaction", new InteractionRenderer000());
        s000.put("RNA", new RNARenderer000());
        s000.put("Gene", new GeneRenderer000());
        s000.put("Shadow", new ShadowRenderer000());
        aux = new LinkRenderer000();
        s000.put("EntitySetAndMemberLink", aux);
        s000.put("EntitySetAndEntitySetLink", aux);

        s050.put("Note", new NoteRenderer050());
        s050.put("Compartment", new CompartmentRenderer050());
        s050.put("Protein", new ProteinRenderer050());
        s050.put("Cell", new CellRenderer050());
        s050.put("Chemical", new ChemicalRenderer050());
        s050.put("ChemicalDrug", new ChemicalDrugRenderer050());
        s050.put("ProteinDrug", new ProteinDrugRenderer050());
        s050.put("RNADrug", new RNADrugRenderer050());
        s050.put("Reaction", new ReactionRenderer050());
        s050.put("Complex", new ComplexRenderer050());
        s050.put("ComplexDrug", new ComplexDrugRenderer050());
        s050.put("Entity", new OtherEntityRenderer050());
        s050.put("EntitySet", new SetRenderer050());
        s050.put("EntitySetDrug", new SetDrugRenderer050());
        s050.put("ProcessNode", new ProcessNodeRenderer050());
        s050.put("EncapsulatedNode", new EncapsulatedNodeRenderer050());
        s050.put("FlowLine", new FlowlineRenderer050());
        s050.put("Interaction", new InteractionRenderer050());
        s050.put("RNA", new RNARenderer050());
        s050.put("Gene", new GeneRenderer050());
        s050.put("Shadow", new ShadowRenderer050());
        aux = new LinkRenderer050();
        s050.put("EntitySetAndMemberLink", aux);
        s050.put("EntitySetAndEntitySetLink", aux);

        s100.put("Note", new NoteRenderer100());
        s100.put("Compartment", new CompartmentRenderer100());
        s100.put("Protein", new ProteinRenderer100());
        s100.put("Cell", new CellRenderer100());
        s100.put("Chemical", new ChemicalRenderer100());
        s100.put("ChemicalDrug", new ChemicalDrugRenderer100());
        s100.put("ProteinDrug", new ProteinDrugRenderer100());
        s100.put("RNADrug", new RNADrugRenderer100());
        s100.put("Reaction", new ReactionRenderer100());
        s100.put("Complex", new ComplexRenderer100());
        s100.put("ComplexDrug", new ComplexDrugRenderer100());
        s100.put("Entity", new OtherEntityRenderer100());
        s100.put("EntitySet", new SetRenderer100());
        s100.put("EntitySetDrug", new SetDrugRenderer100());
        s100.put("ProcessNode", new ProcessNodeRenderer100());
        s100.put("EncapsulatedNode", new EncapsulatedNodeRenderer100());
        s100.put("FlowLine", new FlowlineRenderer100());
        s100.put("Interaction", new InteractionRenderer100());
        s100.put("RNA", new RNARenderer100());
        s100.put("Gene", new GeneRenderer100());
        s100.put("Shadow", new ShadowRenderer100());
        aux = new LinkRenderer100();
        s100.put("EntitySetAndMemberLink", aux);
        s100.put("EntitySetAndEntitySetLink", aux);

        s300.put("Note", new NoteRenderer300());
        s300.put("Compartment", new CompartmentRenderer300());
        s300.put("Protein", new ProteinRenderer300());
        s300.put("Cell", new CellRenderer300());
        s300.put("Chemical", new ChemicalRenderer300());
        s300.put("ChemicalDrug", new ChemicalDrugRenderer300());
        s300.put("ProteinDrug", new ProteinDrugRenderer300());
        s300.put("RNADrug", new RNADrugRenderer300());
        s300.put("Reaction", new ReactionRenderer300());
        s300.put("Complex", new ComplexRenderer300());
        s300.put("ComplexDrug", new ComplexDrugRenderer300());
        s300.put("Entity", new OtherEntityRenderer300());
        s300.put("EntitySet", new SetRenderer300());
        s300.put("EntitySetDrug", new SetDrugRenderer300());
        s300.put("ProcessNode", new ProcessNodeRenderer300());
        s300.put("EncapsulatedNode", new EncapsulatedNodeRenderer300());
        s300.put("FlowLine", new FlowlineRenderer300());
        s300.put("Interaction", new InteractionRenderer300());
        s300.put("RNA", new RNARenderer300());
        s300.put("Gene", new GeneRenderer300());
        s300.put("Shadow", new ShadowRenderer300());
        aux = new LinkRenderer300();
        s300.put("EntitySetAndMemberLink", aux);
        s300.put("EntitySetAndEntitySetLink", aux);


        s800.put("Note", new NoteRenderer800());
        s800.put("Compartment", new CompartmentRenderer800());
        s800.put("Protein", new ProteinRenderer800());
        s800.put("Cell", new CellRenderer800());
        s800.put("Chemical", new ChemicalRenderer800());
        s800.put("ChemicalDrug", new ChemicalDrugRenderer800());
        s800.put("ProteinDrug", new ProteinDrugRenderer800());
        s800.put("RNADrug", new RNADrugRenderer800());
        s800.put("Reaction", new ReactionRenderer800());
        s800.put("Complex", new ComplexRenderer800());
        s800.put("ComplexDrug", new ComplexDrugRenderer800());
        s800.put("Entity", new OtherEntityRenderer800());
        s800.put("EntitySet", new SetRenderer800());
        s800.put("EntitySetDrug", new SetDrugRenderer800());
        s800.put("ProcessNode", new ProcessNodeRenderer800());
        s800.put("EncapsulatedNode", new EncapsulatedNodeRenderer800());
        s800.put("FlowLine", new FlowlineRenderer800());
        s800.put("Interaction", new InteractionRenderer800());
        s800.put("RNA", new RNARenderer800());
        s800.put("Gene", new GeneRenderer800());
        s800.put("Shadow", new ShadowRenderer800());
        aux = new LinkRenderer800();
        s800.put("EntitySetAndMemberLink", aux);
        s800.put("EntitySetAndEntitySetLink", aux);

    }

    @Override
    public void onDiagramZoomEvent(DiagramZoomEvent event) {
        this.setFactor(event.getFactor());
    }

    @Override
    public void onContentLoaded(ContentLoadedEvent event) {
        if (event.getContext().getContent().getType() == DIAGRAM) {
            this.setFactor(event.getContext().getDiagramStatus().getFactor());
        }
    }

    private void setFactor(double factor) {
        RendererProperties.setFactor(factor);
        if (factor < 0.5) {
            this.connectorRenderer = new ConnectorRenderer000();
            this.current = this.s000;
        } else if (factor < 1) {
            this.connectorRenderer = new ConnectorRenderer050();
            this.current = this.s050;
        } else if (factor < 3) {
            connectorRenderer = new ConnectorRenderer100();
            this.current = this.s100;
        } else if (factor < 8) {
            connectorRenderer = new ConnectorRenderer300();
            this.current = this.s300;
        } else {
            connectorRenderer = new ConnectorRenderer800();
            this.current = this.s800;
        }
    }
}
