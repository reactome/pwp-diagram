package org.reactome.web.diagram.client;

import org.reactome.web.diagram.client.visualisers.diagram.DiagramCanvas;
import org.reactome.web.diagram.client.visualisers.ehld.SVGVisualiser;
import org.reactome.web.diagram.controls.navigation.MainControlPanel;
import org.reactome.web.diagram.controls.settings.HideableContainerPanel;
import org.reactome.web.diagram.controls.top.LeftTopLauncherPanel;
import org.reactome.web.diagram.controls.top.RightTopLauncherPanel;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public enum OptionalWidget {
    SEARCH("search", LeftTopLauncherPanel.class),

    SHOW_ALL("fit-all", MainControlPanel.class),
    FIREWORKS("fireworks", MainControlPanel.class),

    EXPORT("export", RightTopLauncherPanel.class),
    LEGEND("legend", RightTopLauncherPanel.class),

    NAVIGATION("navigation", ViewerContainer.class),
    BOTTOM_POP_UP("bottom-pop-up", ViewerContainer.class),
    COLLAPSABLE_MENU("collapsable-menu", ViewerContainer.class),
    INFO("info", ViewerContainer.class, false),

    THUMBNAIL("thumbnail", Arrays.asList(DiagramCanvas.class, SVGVisualiser.class), true),

    COLOUR_PROFILE("colour-profile", HideableContainerPanel.class),
    INTERACTORS("interactors", HideableContainerPanel.class),
    ABOUT("about", HideableContainerPanel.class);

    private final String identifier;
    private final List<Class<? extends Handler>> handlers;
    private boolean visible;
    private static final Map<String, OptionalWidget> idToOptionalWidget =
            Arrays.stream(OptionalWidget.values())
                    .collect(Collectors.toMap(
                            o -> o.identifier,
                            o -> o)
                    );

    public static OptionalWidget findById(String identifier) {
        return idToOptionalWidget.get(identifier);
    }

    OptionalWidget(String identifier, Class<? extends Handler> handler) {
        this(identifier, Collections.singletonList(handler), true);
    }

    OptionalWidget(String identifier, Class<? extends Handler> handler, boolean visible) {
        this(identifier, Collections.singletonList(handler), visible);
    }

    OptionalWidget(String identifier, List<Class<? extends Handler>> handlers, boolean visible) {
        this.identifier = identifier;
        this.handlers = handlers;
        this.visible = visible;
    }

    public String getIdentifier() {
        return identifier;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public interface Handler {
    }
}
