package org.reactome.web.diagram.profiles.diagram;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.Cookies;
import org.reactome.web.diagram.events.DiagramProfileChangedEvent;
import org.reactome.web.diagram.handlers.DiagramProfileChangedHandler;
import org.reactome.web.diagram.profiles.diagram.model.DiagramProfile;
import org.reactome.web.diagram.profiles.diagram.model.factory.DiagramProfileException;
import org.reactome.web.diagram.profiles.diagram.model.factory.DiagramProfileFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class DiagramColours implements DiagramProfileChangedHandler {

    @SuppressWarnings("SpellCheckingInspection")
    private static final String PROFILE_COOKIE = "pathwaybrowser_diagram_colour";
    private static DiagramColours diagramColours;

    public DiagramProfile PROFILE;
    private EventBus eventBus;

    /**
     * If there is not match then we load the default one.
     */
    private DiagramColours(EventBus eventBus) {
        this.eventBus = eventBus;
        initHandlers();

        String profileName = Cookies.getCookie(PROFILE_COOKIE);
        ProfileType type = ProfileType.getByName(profileName);
        setProfile(type.getDiagramProfile());
    }

    public static void initialise(EventBus eventBus) {
        if(diagramColours!=null){
            throw new RuntimeException("Diagram Colours has already been initialised. Only one initialisation is permitted per Diagram Viewer instance.");
        }
        diagramColours = new DiagramColours(eventBus);
    }

    public static DiagramColours get(){
        if (diagramColours == null) {
            throw new RuntimeException("Diagram Colours has not been initialised yet. Please call initialise before using 'get'");
        }
        return diagramColours;
    }

    @Override
    public void onDiagramProfileChanged(DiagramProfileChangedEvent event) {
        this.setProfile(event.getDiagramProfile());
    }

    private void setProfile(DiagramProfile diagramProfile){
        PROFILE = diagramProfile;

        //The strategy is to remove the cookie when the standard is selected so in case
        //we decide to change the standard profile in the future, that will propagate
        //automatically for those who have not changed to a different profile
        if(ProfileType.getStandard().getDiagramProfile().equals(diagramProfile)){
            Cookies.removeCookie(PROFILE_COOKIE);
        }else {
            Date expires = new Date();
            long nowLong = expires.getTime();
            nowLong = nowLong + (1000 * 60 * 60 * 24 * 365L); //One year time
            expires.setTime(nowLong);
            Cookies.setCookie(PROFILE_COOKIE, diagramProfile.getName(), expires);
        }
    }

    public String getSelectedProfileName(){
        String sel = Cookies.getCookie(PROFILE_COOKIE);
        return sel != null ? sel : ProfileType.getStandard().diagramProfile.getName();
    }

    private void initHandlers(){
        this.eventBus.addHandler(DiagramProfileChangedEvent.TYPE, this);
    }

    /**
     * To add a profile first please add the ProfileSource interface
     * and then add the corresponding entry in this enumeration.
     */
    public enum ProfileType {
        PROFILE_01(ProfileSource.SOURCE.profile01()),
        PROFILE_02(ProfileSource.SOURCE.profile02());

        DiagramProfile diagramProfile;

        ProfileType(TextResource resource) {
            try {
                diagramProfile = DiagramProfileFactory.getModelObject(DiagramProfile.class, resource.getText());
            } catch (DiagramProfileException e) {
                GWT.log(e.getMessage());
                diagramProfile = null;
            }
        }

        public static List<String> getProfiles() {
            List<String> rtn = new ArrayList<>();
            for (ProfileType value : values()) {
                rtn.add(value.diagramProfile.getName());
            }
            return rtn;
        }

        public static ProfileType getByName(String name){
            for (ProfileType value : values()) {
                if(value.diagramProfile.getName().equals(name)){
                    return value;
                }
            }
            return getStandard();
        }

        public static ProfileType getStandard(){
            return PROFILE_02;
        }

        public DiagramProfile getDiagramProfile() {
            return diagramProfile;
        }
    }

    interface ProfileSource extends ClientBundle {

        ProfileSource SOURCE = GWT.create(ProfileSource.class);

        @Source("profile_01.json")
        TextResource profile01();

        @Source("profile_02.json")
        TextResource profile02();
    }
}