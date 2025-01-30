package org.reactome.web.diagram.controls.settings.tabs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import org.reactome.web.diagram.client.DiagramFactory;
import org.reactome.web.diagram.common.ConfirmationButton;
import org.reactome.web.diagram.common.IconButton;
import org.reactome.web.diagram.context.popups.InsertItemDialog;
import org.reactome.web.diagram.controls.settings.common.InfoLabel;
import org.reactome.web.diagram.data.Context;
import org.reactome.web.diagram.data.InteractorsContent;
import org.reactome.web.diagram.data.interactors.common.OverlayResource;
import org.reactome.web.diagram.data.interactors.custom.ResourcesManager;
import org.reactome.web.diagram.data.interactors.custom.model.CustomResource;
import org.reactome.web.diagram.data.interactors.custom.raw.RawInteractorError;
import org.reactome.web.diagram.data.interactors.custom.raw.RawSummary;
import org.reactome.web.diagram.data.interactors.raw.RawInteractor;
import org.reactome.web.diagram.data.interactors.raw.RawResource;
import org.reactome.web.diagram.data.loader.InteractorsResourceLoader;
import org.reactome.web.diagram.events.*;
import org.reactome.web.diagram.handlers.*;
import org.reactome.web.diagram.util.MapSet;
import org.reactome.web.diagram.util.interactors.InteractorsExporter;

import java.util.*;
import java.util.stream.Stream;

import static org.reactome.web.diagram.data.content.Content.Type.DIAGRAM;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
@SuppressWarnings("FieldCanBeLocal")
public class InteractorsTabPanel extends Composite implements ClickHandler, ValueChangeHandler, InteractorsResourceLoader.Handler,
        InteractorsResourceChangedHandler, InteractorsLoadedHandler, InteractorsErrorHandler,
        ContentLoadedHandler, ContentRequestedHandler,
        InsertItemDialog.Handler {
    private static int RESOURCES_REFRESH = 600000; // Update every 10 minutes

    private EventBus eventBus;
    private Context context;
    private Map<String, OverlayResource> resourcesMap = new HashMap<>();
    private Map<String, List<Widget>> resourceWidgets = new HashMap<>();
    private OverlayResource selectedResource;
    private OverlayResource intactResource;
    private OverlayResource disgenetResource;

    private List<RadioButton> staticResourceBtns = new ArrayList<>();
    private RadioButton intactResourceBtn;
    private FlowPanel liveResourcesFP;
    private FlowPanel customResourcesFP;
    private FlowPanel loadingPanel;
    private FlowPanel errorPanel;
    private Label errorLb;
    private IconButton downloadBtn;
    private IconButton addNewResourceBtn;

    private String resourcesError = null;

    public InteractorsTabPanel(EventBus eventBus) {
        this.eventBus = eventBus;
        InfoLabel tabHeader = new InfoLabel("Data Overlays", RESOURCES.aboutThis());

        Label lb = new Label("Available data source:");
        lb.setStyleName(RESOURCES.getCSS().interactorLabel());

        intactResource = new OverlayResource(DiagramFactory.INTERACTORS_INITIAL_RESOURCE, DiagramFactory.INTERACTORS_INITIAL_RESOURCE_NAME, OverlayResource.ResourceType.STATIC);
        disgenetResource = new OverlayResource(DiagramFactory.DISEASE_RESOURCE, DiagramFactory.DISEASE_RESOURCE_NAME, OverlayResource.ResourceType.STATIC);

        Stream.of(intactResource, disgenetResource).forEach((staticResource) -> {
            RadioButton btn = new RadioButton("Resources", staticResource.getName());
            btn.setFormValue(staticResource.getIdentifier()); //use FormValue to keep the value
            btn.setTitle("Select " + staticResource.getName() + " as a resource");
            btn.setStyleName(RESOURCES.getCSS().interactorResourceBtn());
            btn.addValueChangeHandler(this);

            resourcesMap.put(staticResource.getIdentifier(), staticResource);

            Label summaryLabel = new Label();
            summaryLabel.setStyleName(RESOURCES.getCSS().summaryLb());
            summaryLabel.setTitle("Total number of unique interactors for this diagram");
            summaryLabel.setVisible(false);
            resourceWidgets.put(staticResource.getIdentifier(), Arrays.asList(btn, summaryLabel));
            staticResourceBtns.add(btn);
        });

        intactResourceBtn = (RadioButton) resourceWidgets.get(DiagramFactory.INTERACTORS_INITIAL_RESOURCE).get(0);
        intactResourceBtn.setValue(true);
        selectedResource = intactResource;

        // Loading panel
        Image loadingSpinner = new Image(RESOURCES.loadingSpinner());
        loadingPanel = new FlowPanel();
        loadingPanel.add(loadingSpinner);
        loadingPanel.add(new InlineLabel(" Updating resources..."));
        loadingPanel.setStyleName(RESOURCES.getCSS().loadingPanel());

        errorLb = new Label();
        errorPanel = new FlowPanel();
        errorPanel.add(errorLb);
        errorPanel.setStyleName(RESOURCES.getCSS().errorPanel());

        Label liveResourcesLabel = new Label("PSICQUIC:");
        liveResourcesLabel.setTitle("Select one of the PSICQUIC resources");
        liveResourcesLabel.setStyleName(RESOURCES.getCSS().liveInteractorLabel());

        Label customResourcesLabel = new Label("Custom Resources:");
        customResourcesLabel.setTitle("Select one of the custom resources");
        customResourcesLabel.setStyleName(RESOURCES.getCSS().liveInteractorLabel());

        Label tuplesLabel = new Label("Custom Tuples:");
        tuplesLabel.setTitle("Select one of the custom tuples");
        tuplesLabel.setStyleName(RESOURCES.getCSS().interactorResourceBtn());

        downloadBtn = new IconButton(selectedResource.getName(), RESOURCES.downloadNormal());
        downloadBtn.addClickHandler(this);
        downloadBtn.setTitle("Click to download all diagram interactors from " + selectedResource.getName());
        downloadBtn.setStyleName(RESOURCES.getCSS().downloadBtn());
        downloadBtn.setEnabled(false);

        addNewResourceBtn = new IconButton("Add a new resource", RESOURCES.addNewItem());
        addNewResourceBtn.addClickHandler(this);
        addNewResourceBtn.setTitle("Click to add a new resource");
        addNewResourceBtn.setStyleName(RESOURCES.getCSS().addNewResourceBtn());

        FlowPanel main = new FlowPanel();
        main.setStyleName(RESOURCES.getCSS().interactorsPanel());
        main.add(tabHeader);
        main.add(lb);
        resourceWidgets.forEach((s, widgets) -> widgets.forEach(main::add));
        main.add(liveResourcesLabel);
        main.add(loadingPanel);
        main.add(getOptionsPanel());
        main.add(customResourcesLabel);
        main.add(getCustomResourcesPanel());
        main.add(downloadBtn);

        initWidget(main);
        initialiseHandlers();

        loadLiveResources(); //Load resources for the first time
        Timer refreshTimer = new Timer() {
            @Override
            public void run() {
                loadLiveResources(); // Set the timer to update the resources
            }
        };
        refreshTimer.scheduleRepeating(RESOURCES_REFRESH);

        updateCustomResources(ResourcesManager.get().getResources());
    }

    @Override
    public void onClick(ClickEvent event) {
        Button btn = (Button) event.getSource();
        if (btn.equals(downloadBtn)) {
            if (context != null) {
                MapSet<String, RawInteractor> interactors = context.getInteractors().getRawInteractorsPerResource(selectedResource.getIdentifier());
                if (hasContents(interactors)) {
                    String filename = context.getContent().getStableId() + "_Interactors_" + selectedResource.getName() + ".csv";
                    InteractorsExporter.exportInteractors(filename, interactors);
                }
            }
        } else if (btn.equals(addNewResourceBtn)) {
            InsertItemDialog dialog = new InsertItemDialog(this);
            dialog.show();
        }
    }

    @Override
    public void onContentRequested(ContentRequestedEvent event) {
        context = null;
        enableDownloadButton(false);
    }

    @Override
    public void onContentLoaded(ContentLoadedEvent event) {
        if (event.getContext().getContent().getType() == DIAGRAM) {
            context = event.getContext();
            enableStaticResourceBtn(true);
        } else {
            enableStaticResourceBtn(false);
            populateResourceListPanel();
        }
        populateCustomResourceListPanel();
    }

    @Override
    public void interactorsResourcesLoaded(List<RawResource> resourceList, long time) {
        resourcesError = null;
        updateLiveResources(resourceList);
        populateResourceListPanel();
        showLoading(false);
    }

    @Override
    public void onInteractorsResourcesLoadError(RawInteractorError error) {
        showLoading(false);
        updateLiveResources(Collections.EMPTY_LIST);
        StringBuilder sb = new StringBuilder();
        List<String> messages = error.getMessages();
        if (messages != null && !messages.isEmpty()) {
            for (String line : messages) {
                sb.append(line).append("\n");
            }
        }
        resourcesError = sb.toString();
        populateResourceListPanel();
    }

    @Override
    public void onInteractorsResourcesLoadException(String message) {
        showLoading(false);
        updateLiveResources(Collections.EMPTY_LIST);
        resourcesError = message;
        populateResourceListPanel();
    }

    @Override
    public void onInteractorsError(InteractorsErrorEvent event) {
        enableDownloadButton(false);
    }

    @Override
    public void onInteractorsLoaded(InteractorsLoadedEvent event) {
        enableDownloadButton(true);
        updateStaticSummary();
        populateResourceListPanel();

    }

    @Override
    public void onInteractorsResourceChanged(InteractorsResourceChangedEvent event) {
        if (context != null) {
            OverlayResource resource = event.getResource();
            downloadBtn.setText(resource.getName());
            downloadBtn.setTitle("Click to download all diagram interactors from " + resource.getName());
            enableDownloadButton(context.getInteractors().isResourceLoaded(resource.getIdentifier()));
            updateStaticSummary();
            populateResourceListPanel();
        }
    }

    @Override
    public void onValueChange(ValueChangeEvent event) {
        RadioButton selectedBtn = (RadioButton) event.getSource();
        selectedResource = resourcesMap.get(selectedBtn.getFormValue());
        if (selectedResource != null) {
            // Fire event for a Resource selection
            eventBus.fireEventFromSource(new InteractorsResourceChangedEvent(selectedResource), this);
        }
    }

    @Override
    public void onResourceAdded(RawSummary summary) {
        //Add the new custom resource and select it
        ResourcesManager.get().createAndAddResource(summary.getName(), summary.getToken(), summary.getFileName());
        updateCustomResources(ResourcesManager.get().getResources());
        selectedResource = resourcesMap.get(summary.getToken());
        populateCustomResourceListPanel();
        if (selectedResource != null) {
            // Fire event for a Resource selection
            eventBus.fireEventFromSource(new InteractorsResourceChangedEvent(selectedResource), this);
        }
    }

    private void initialiseHandlers() {
        eventBus.addHandler(ContentLoadedEvent.TYPE, this);
        eventBus.addHandler(ContentRequestedEvent.TYPE, this);
        eventBus.addHandler(InteractorsLoadedEvent.TYPE, this);
        eventBus.addHandler(InteractorsErrorEvent.TYPE, this);
        eventBus.addHandler(InteractorsResourceChangedEvent.TYPE, this);
    }

    private void updateLiveResources(List<RawResource> inputList) {
        // Clean up all previously added PSIQUIC resources
        List<OverlayResource> toBeDeleted = new LinkedList<>();
        for (OverlayResource resource : resourcesMap.values()) {
            if (resource.getType().equals(OverlayResource.ResourceType.PSICQUIC)) {
                toBeDeleted.add(resource);
            }
        }
        for (OverlayResource resource : toBeDeleted) {
            resourcesMap.remove(resource.getIdentifier());
        }
        // Add all new resources;
        for (RawResource rawResource : inputList) {
            OverlayResource resource = new OverlayResource(rawResource.getName(), rawResource.getName(), null, OverlayResource.ResourceType.PSICQUIC, rawResource.getActive());
            resourcesMap.put(resource.getIdentifier(), resource);
        }
    }

    private void updateCustomResources(Collection<CustomResource> inputList) {
        // Clean up all previously added CUSTOM resources
        List<OverlayResource> toBeDeleted = new LinkedList<>();
        for (OverlayResource resource : resourcesMap.values()) {
            if (resource.getType().equals(OverlayResource.ResourceType.CUSTOM)) {
                toBeDeleted.add(resource);
            }
        }
        for (OverlayResource resource : toBeDeleted) {
            resourcesMap.remove(resource.getIdentifier());
        }

        // Add all new CUSTOM resources
        for (CustomResource customResource : inputList) {
            OverlayResource resource = new OverlayResource(customResource.getToken(), customResource.getName(), customResource.getFilename(), OverlayResource.ResourceType.CUSTOM);
            resourcesMap.put(resource.getIdentifier(), resource);
        }
    }

    private void populateResourceListPanel() {
        liveResourcesFP.clear();
        // In case of an error simply show the message
        if (resourcesError != null) {
            errorLb.setText(resourcesError);
            liveResourcesFP.add(errorPanel);
            return;
        }

        for (OverlayResource resource : resourcesMap.values()) {
            if (resource.getType().equals(OverlayResource.ResourceType.PSICQUIC)) {
                RadioButton radioBtn = new RadioButton("Resources", resource.getName());
                radioBtn.setFormValue(resource.getIdentifier()); //use FormValue to keep the value
                radioBtn.addValueChangeHandler(this);
                radioBtn.setStyleName(RESOURCES.getCSS().interactorResourceListBtn());

                if (!resource.isActive()) {
                    radioBtn.setEnabled(false);
                    radioBtn.setTitle(resource.getName() + " is not currently available");
                    radioBtn.addStyleName(RESOURCES.getCSS().interactorResourceListBtnDisabled());
                } else {
                    radioBtn.setTitle("Select " + resource.getName() + " as a resource");
                }

                // Restore previous selection
                if (radioBtn.getFormValue().equals(selectedResource.getIdentifier())) {
                    radioBtn.setValue(true);
                }

                FlowPanel row = new FlowPanel();
                row.add(radioBtn);

                //Check if this resource is already loaded
                if (context != null) {
                    InteractorsContent iContent = context.getInteractors();
                    if (iContent.isResourceLoaded(resource.getIdentifier())) {
                        Label summaryLb = new Label();
                        summaryLb.setStyleName(RESOURCES.getCSS().summaryLb());
                        summaryLb.setTitle("Total number of unique interactors for this diagram");
                        summaryLb.setText("" + iContent.getUniqueRawInteractorsCountPerResource(resource.getIdentifier()));
                        row.add(summaryLb);
                    }
                } else {
                    radioBtn.setEnabled(false);
                    radioBtn.addStyleName(RESOURCES.getCSS().interactorResourceListBtnDisabled());
                }

                liveResourcesFP.add(row);
            }
        }
    }

    private void populateCustomResourceListPanel() {
        customResourcesFP.clear();
        for (OverlayResource resource : resourcesMap.values()) {
            if (resource.getType().equals(OverlayResource.ResourceType.CUSTOM)) {
                String name = resource.getFilename() == null ? resource.getName() : resource.getName() + " (" + resource.getFilename() + ")";
                final RadioButton radioBtn = new RadioButton("Resources", name);
                radioBtn.setFormValue(resource.getIdentifier()); //use FormValue to keep the value
                radioBtn.addValueChangeHandler(this);
                radioBtn.setStyleName(RESOURCES.getCSS().interactorResourceListBtn());
                radioBtn.setTitle("Select " + resource.getName() + " as a resource");

                // Restore previous selection
                if (radioBtn.getFormValue().equals(selectedResource.getIdentifier())) {
                    radioBtn.setValue(true);
                }

                final ConfirmationButton deleteBtn = new ConfirmationButton("Click here to delete this resource", RESOURCES.deleteNormal(), new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent clickEvent) {
                        ResourcesManager.get().deleteResource(radioBtn.getFormValue());
                        updateCustomResources(ResourcesManager.get().getResources());
                        populateCustomResourceListPanel();
                        if (radioBtn.getFormValue().equals(selectedResource.getIdentifier())) {
                            selectedResource = intactResource;
                            intactResourceBtn.setValue(true);
                            // Fire event for static resource selection
                            eventBus.fireEventFromSource(new InteractorsResourceChangedEvent(selectedResource), this);
                        }
                    }
                });

                if (context == null) {
                    radioBtn.setEnabled(false);
                    radioBtn.addStyleName(RESOURCES.getCSS().interactorResourceListBtnDisabled());
                }

                FlowPanel row = new FlowPanel();
                row.add(radioBtn);
                row.add(deleteBtn);

                customResourcesFP.add(row);
            }
        }
        //Add the "add new custom resource" button
        customResourcesFP.add(addNewResourceBtn);
    }

    private void updateStaticSummary() {
        if (context != null) {
            InteractorsContent iContent = context.getInteractors();
            iContent.getLoadedResources().forEach(s -> {
                Label label = (Label) resourceWidgets.get(s).get(1);
                label.setText("" + iContent.getUniqueRawInteractorsCountPerResource(s));
                label.setVisible(true);
            });
        }
    }

    private Widget getOptionsPanel() {
        liveResourcesFP = new FlowPanel();
        liveResourcesFP.setStyleName(RESOURCES.getCSS().liveResourcesInnerPanel());

        SimplePanel sp = new SimplePanel();
        sp.setStyleName(RESOURCES.getCSS().liveResourcesOuterPanel());
        sp.add(liveResourcesFP);
        return sp;
    }

    private Widget getCustomResourcesPanel() {
        customResourcesFP = new FlowPanel();
        customResourcesFP.setStyleName(RESOURCES.getCSS().customResourcesInnerPanel());

        SimplePanel sp = new SimplePanel();
        sp.setStyleName(RESOURCES.getCSS().customResourcesOuterPanel());
        sp.add(customResourcesFP);
        return sp;
    }

    private void loadLiveResources() {
        showLoading(true);
        InteractorsResourceLoader.loadResources(InteractorsTabPanel.this);
    }

    private void showLoading(boolean loading) {
        if (loading) {
            liveResourcesFP.clear();
            liveResourcesFP.add(loadingPanel);
        }
    }

    private void enableStaticResourceBtn(boolean isEnabled) {
        for (RadioButton btn : staticResourceBtns) {
            btn.setEnabled(isEnabled);
            if (isEnabled) {
                btn.removeStyleName(RESOURCES.getCSS().interactorResourceBtnDisabled());
            } else {
                btn.addStyleName(RESOURCES.getCSS().interactorResourceBtnDisabled());
            }
        }
    }

    /***
     * Enable the download button only if the rawInteractors mapset
     * for the selected resource contains entries
     * @param enable
     */
    private void enableDownloadButton(boolean enable) {
        if (downloadBtn != null) {
            if (enable) {
                if (context != null) {
                    MapSet<String, RawInteractor> interactors = context.getInteractors().getRawInteractorsPerResource(selectedResource.getIdentifier());
                    if (hasContents(interactors)) {
                        downloadBtn.setEnabled(true);
                    } else {
                        downloadBtn.setEnabled(false);
                    }
                }
            } else {
                downloadBtn.setEnabled(false);
            }
        }
    }

    private boolean hasContents(MapSet<String, RawInteractor> input) {
        boolean rtn = false;
        if (input != null && !input.isEmpty()) {
            for (String acc : input.keySet()) {
                if (!input.getElements(acc).isEmpty()) {
                    rtn = true;
                    break;
                }
            }
        }
        return rtn;
    }


    public static Resources RESOURCES;

    static {
        RESOURCES = GWT.create(Resources.class);
        RESOURCES.getCSS().ensureInjected();
    }

    public interface Resources extends ClientBundle {
        @Source(ResourceCSS.CSS)
        ResourceCSS getCSS();

        @Source("../images/loader.gif")
        ImageResource loadingSpinner();

        @Source("../tabs/InteractorsInfo.txt")
        TextResource aboutThis();

        @Source("../images/download_normal.png")
        ImageResource downloadNormal();

        @Source("../images/addNewItem.png")
        ImageResource addNewItem();

        @Source("../images/bin_normal.png")
        ImageResource deleteNormal();

    }

    @CssResource.ImportedWithPrefix("diagram-InteractorsTabPanel")
    public interface ResourceCSS extends CssResource {
        String CSS = "org/reactome/web/diagram/controls/settings/tabs/InteractorsTabPanel.css";

        String interactorsPanel();

        String interactorLabel();

        String liveInteractorLabel();

        String interactorResourceBtn();

        String interactorResourceBtnDisabled();

        String liveResourcesOuterPanel();

        String liveResourcesInnerPanel();

        String customResourcesOuterPanel();

        String customResourcesInnerPanel();

        String loadingPanel();

        String errorPanel();

        String interactorResourceListBtn();

        String interactorResourceListBtnDisabled();

        String summaryLb();

        String downloadBtn();

        String addNewResourceBtn();

    }
}
