package org.reactome.web.diagram.profiles.analysis;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.Cookies;
import org.reactome.web.diagram.events.AnalysisProfileChangedEvent;
import org.reactome.web.diagram.handlers.AnalysisProfileChangedHandler;
import org.reactome.web.diagram.profiles.analysis.model.AnalysisProfile;
import org.reactome.web.diagram.profiles.analysis.model.factory.AnalysisProfileException;
import org.reactome.web.diagram.profiles.analysis.model.factory.AnalysisProfileFactory;
import org.reactome.web.diagram.util.ColorMap;
import org.reactome.web.diagram.util.gradient.ThreeColorGradient;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class AnalysisColours implements AnalysisProfileChangedHandler {
    private EventBus eventBus;

    @SuppressWarnings("SpellCheckingInspection")
    private static final String PROFILE_COOKIE = "pathwaybrowser_analysis_colour";
    public static final Double THRESHOLD = 0.05;

    private static AnalysisColours analysisColours;

    public AnalysisProfile PROFILE;
    public ThreeColorGradient enrichmentGradient;
    public ThreeColorGradient expressionGradient;
    public ColorMap regulationColorMap;

    /**
     * If there is not match then we load the default one.
     */
    private AnalysisColours(EventBus eventBus) {
        this.eventBus = eventBus;
        initHandlers();

        String profileName = Cookies.getCookie(PROFILE_COOKIE);
        AnalysisColours.ProfileType type = AnalysisColours.ProfileType.getByName(profileName);
        setProfile(type.getAnalysisProfile());
    }

    public static void initialise(EventBus eventBus) {
        if(analysisColours!=null){
            throw new RuntimeException("Analysis Colours has already been initialised. Only one initialisation is permitted per Diagram Viewer instance.");
        }
        analysisColours = new AnalysisColours(eventBus);
    }

    public static AnalysisColours get(){
        if (analysisColours == null) {
            throw new RuntimeException("Analysis Colours has not been initialised yet. Please call initialise before using 'get'");
        }
        return analysisColours;
    }

    @Override
    public void onAnalysisProfileChanged(AnalysisProfileChangedEvent event) {
        this.setProfile(event.getAnalysisProfile());
    }

    private void setProfile(AnalysisProfile analysisProfile){
        PROFILE = analysisProfile;
        enrichmentGradient = new ThreeColorGradient(PROFILE.getEnrichment().getGradient());
        expressionGradient = new ThreeColorGradient(PROFILE.getExpression().getGradient());
        regulationColorMap = ColorMap.fromGradient(expressionGradient);

        //The strategy is to remove the cookie when the standard is selected so in case
        //we decide to change the standard profile in the future, that will propagate
        //automatically for those who have not changed to a different profile
        if (ProfileType.getStandard().getAnalysisProfile().equals(analysisProfile)) {
            Cookies.removeCookie(PROFILE_COOKIE);
        } else {
            Date expires = new Date();
            long nowLong = expires.getTime();
            nowLong = nowLong + (1000 * 60 * 60 * 24 * 365L); //One year time
            expires.setTime(nowLong);
            Cookies.setCookie(PROFILE_COOKIE, analysisProfile.getName(), expires);
        }
    }

    public String getSelectedProfileName(){
        String sel = Cookies.getCookie(PROFILE_COOKIE);
        return sel != null ? sel : ProfileType.getStandard().analysisProfile.getName();
    }

    private void initHandlers(){
        this.eventBus.addHandler(AnalysisProfileChangedEvent.TYPE, this);
    }


    /**
     * To add a profile first please add the ProfileSource interface
     * and then add the corresponding entry in this enumeration.
     */
    @SuppressWarnings("UnusedDeclaration")
    public enum ProfileType {
        PROFILE_01(ProfileSource.SOURCE.profile01()),
        PROFILE_02(ProfileSource.SOURCE.profile02()),
        PROFILE_03(ProfileSource.SOURCE.profile03());

        AnalysisProfile analysisProfile;

        ProfileType(TextResource resource) {
            try {
                analysisProfile = AnalysisProfileFactory.getModelObject(AnalysisProfile.class, resource.getText());
            } catch (AnalysisProfileException e) {
                GWT.log(e.getMessage());
                analysisProfile = null;
            }
        }

        public static List<String> getProfiles() {
            List<String> rtn = new ArrayList<>();
            for (ProfileType value : values()) {
                rtn.add(value.analysisProfile.getName());
            }
            return rtn;
        }

        public static ProfileType getByName(String name){
            for (ProfileType value : values()) {
                if(value.analysisProfile.getName().equals(name)){
                    return value;
                }
            }
            return getStandard();
        }

        public static ProfileType getStandard(){
            return PROFILE_01;
        }

        public AnalysisProfile getAnalysisProfile() {
            return analysisProfile;
        }
    }

    interface ProfileSource extends ClientBundle {

        ProfileSource SOURCE = GWT.create(ProfileSource.class);

        @Source("profile_01.json")
        TextResource profile01();

        @Source("profile_02.json")
        TextResource profile02();

        @Source("profile_03.json")
        TextResource profile03();
    }
}
