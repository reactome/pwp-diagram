package org.reactome.web.diagram.controls.settings.tabs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.*;
import org.reactome.web.diagram.data.Context;
import org.reactome.web.diagram.events.AnalysisProfileChangedEvent;
import org.reactome.web.diagram.events.ContentLoadedEvent;
import org.reactome.web.diagram.events.DiagramProfileChangedEvent;
import org.reactome.web.diagram.events.InteractorProfileChangedEvent;
import org.reactome.web.diagram.handlers.ContentLoadedHandler;
import org.reactome.web.diagram.profiles.analysis.AnalysisColours;
import org.reactome.web.diagram.profiles.analysis.model.AnalysisProfile;
import org.reactome.web.diagram.profiles.diagram.DiagramColours;
import org.reactome.web.diagram.profiles.diagram.model.DiagramProfile;
import org.reactome.web.diagram.profiles.interactors.InteractorColours;
import org.reactome.web.diagram.profiles.interactors.model.InteractorProfile;

import java.util.List;

import static org.reactome.web.diagram.data.content.Content.Type.SVG;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public class ProfilesTabPanel extends Composite implements ChangeHandler, ContentLoadedHandler {
    private EventBus eventBus;
    private ListBox colourProfiles;
    private ListBox analysisProfiles;
    private ListBox interactorProfiles;

    public ProfilesTabPanel(EventBus eventBus) {
        this.eventBus = eventBus;
        FlowPanel main = new FlowPanel();
        main.setStyleName(RESOURCES.getCSS().profilesPanel());

        colourProfiles = new ListBox();
        analysisProfiles = new ListBox();
        interactorProfiles = new ListBox();

        Label tabHeader = new Label("Colour Profiles");
        tabHeader.setStyleName(RESOURCES.getCSS().tabHeader());
        main.add(tabHeader);
        main.add(getProfilesWidget("Diagram Colour Profile:", colourProfiles, DiagramColours.ProfileType.getProfiles()));
        main.add(getProfilesWidget("Analysis Overlay Colour Profile:", analysisProfiles, AnalysisColours.ProfileType.getProfiles()));
        main.add(getProfilesWidget("Interactors Colour Profile:", interactorProfiles, InteractorColours.ProfileType.getProfiles()));

        setSelection(colourProfiles, DiagramColours.get().getSelectedProfileName());
        setSelection(analysisProfiles, AnalysisColours.get().getSelectedProfileName());
        setSelection(interactorProfiles, InteractorColours.get().getSelectedProfileName());
        initHandlers();

        initWidget(main);
    }

    @Override
    public void onChange(ChangeEvent event) {
        ListBox lb = (ListBox) event.getSource();
        String aux = lb.getSelectedValue();
        if(lb.equals(colourProfiles)){
            DiagramProfile profile = DiagramColours.ProfileType.getByName(aux).getDiagramProfile();
            eventBus.fireEventFromSource(new DiagramProfileChangedEvent(profile), this);
        } else if(lb.equals(analysisProfiles)){
            AnalysisProfile profile = AnalysisColours.ProfileType.getByName(aux).getAnalysisProfile();
            eventBus.fireEventFromSource(new AnalysisProfileChangedEvent(profile), this);
        } else if(lb.equals(interactorProfiles)){
            InteractorProfile profile = InteractorColours.ProfileType.getByName(aux).getDiagramProfile();
            eventBus.fireEventFromSource(new InteractorProfileChangedEvent(profile), this);
        }
    }

    @Override
    public void onContentLoaded(ContentLoadedEvent event) {
        Context context = event.getContext();
        if(context.getContent().getType().equals(SVG)) {
            // Disable irrelevant colour profile options for SVG
            colourProfiles.setEnabled(false);
            interactorProfiles.setEnabled(false);
        } else {
            colourProfiles.setEnabled(true);
            interactorProfiles.setEnabled(true);
        }
    }

    private Widget getProfilesWidget(String title, ListBox profileListBox, List<String> profileNames){
        profileListBox.setMultipleSelect(false);
        for(String name : profileNames){
            profileListBox.addItem(name);
        }

        Label lb = new Label(title);
        lb.setStyleName(RESOURCES.getCSS().profileLabel());

        FlowPanel fp = new FlowPanel();
        fp.add(lb);
        fp.add(profileListBox);
        return fp;
    }

    private void setSelection(ListBox profileListBox, String selection){
        if(selection==null){
            return;
        }
        for(int i=0; i<profileListBox.getItemCount(); i++){
            if(profileListBox.getValue(i).equals(selection)){
                profileListBox.setSelectedIndex(i);
            }
        }
    }

    private void initHandlers(){
        colourProfiles.addChangeHandler(this);
        analysisProfiles.addChangeHandler(this);
        interactorProfiles.addChangeHandler(this);

        eventBus.addHandler(ContentLoadedEvent.TYPE, this);
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

    @CssResource.ImportedWithPrefix("diagram-ProfilesTabPanel")
    public interface ResourceCSS extends CssResource {
        String CSS = "org/reactome/web/diagram/controls/settings/tabs/ProfilesTabPanel.css";

        String profilesPanel();

        String profileLabel();

        String tabHeader();
    }
}
