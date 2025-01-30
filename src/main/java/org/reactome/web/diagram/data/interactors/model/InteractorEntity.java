package org.reactome.web.diagram.data.interactors.model;

import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.ui.Image;
import org.reactome.web.diagram.data.interactors.raw.RawInteractor;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.Node;
import org.reactome.web.diagram.data.layout.impl.CoordinateFactory;
import org.reactome.web.diagram.profiles.interactors.InteractorColours;
import org.reactome.web.diagram.profiles.interactors.model.InteractorProfileNode;
import org.reactome.web.diagram.util.chemical.ChemicalImageLoader;
import org.reactome.web.diagram.util.pdbe.PDBeLoader;
import org.reactome.web.diagram.util.pdbe.model.PDBObject;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class InteractorEntity extends DiagramInteractor implements Draggable, PDBeLoader.Handler, ChemicalImageLoader.Handler {

    private final String accession;
    private final String alias;
    private final boolean chemical;
    private final boolean disease;

    private PDBObject pdbObject;
    private ImageElement image;

    private boolean isHit;
    private List<Double> exp;

    private final Set<InteractorLink> links = new HashSet<>();

    public InteractorEntity(RawInteractor rawInteractor) {
        super(rawInteractor.getAccURL());
        this.accession = rawInteractor.getAcc();
        this.alias = rawInteractor.getAlias();
        this.isHit = rawInteractor.getIsHit() != null && rawInteractor.getIsHit();
        this.exp = rawInteractor.getExp();
        this.chemical = isChemical(accession);
        this.disease = isDisease(accession);
    }

    public InteractorLink addLink(Node node, Long id, Integer evidences, String url, double score) {
        InteractorLink link = new DynamicLink(node, this, id, evidences, url, score);
        links.add(link);
        return link;
    }

    public boolean removeLink(DynamicLink link) {
        return links.remove(link);
    }

    @Override
    public String getAccession() {
        return accession;
    }

    public String getAlias() {
        return alias;
    }

    public static Type getType(String acc) {
        return isChemical(acc) ? Type.CHEMICAL : Type.PROTEIN;
    }

    public boolean isChemical() {
        return chemical;
    }

    public boolean isDisease() {
        return disease;
    }

    private static boolean isChemical(String accession) {
        return accession.matches("^(CHEBI|CHEMBL|ZINC).*");
    }

    private static boolean isDisease(String accession) {
        return accession.matches("^C\\d{7}$");
    }

    public InteractorProfileNode getProfile() {
        if (disease) return InteractorColours.get().PROFILE.getDisease();
        if (chemical) return InteractorColours.get().PROFILE.getChemical();
        return InteractorColours.get().PROFILE.getProtein();
    }

    public Coordinate getCentre() {
        return CoordinateFactory.get(minX + (maxX - minX) / 2.0, minY + (maxY - minY) / 2.0);
    }

    public String getDisplayName() {
        return alias != null ? alias : accession;
    }

    public String getDetails() {
        if (pdbObject != null) {
            return "PDBe: " + pdbObject.getPdbid() + "    " + "Chain: " + pdbObject.getChain() +
                    "\n" + "Resolution: " + pdbObject.getResolution() +
                    "\n" + "Coverage: " + pdbObject.getCoverage() +
                    "\n" + "PDBe Range: " + pdbObject.getPdbRange() +
                    "\n" + "UniProt Range: " + pdbObject.getUniprotRange();
        }
        return null;
    }

    public ImageElement getImage() {
        if (image == null) setImageURL();
        return image;
    }

    public Collection<InteractorLink> getLinks() {
        return links;
    }

    public Boolean getIsHit() {
        return isHit;
    }

    public List<Double> getExp() {
        return exp;
    }

    /**
     * Creates a string with the names of the nodes that
     * this entity interacts with (comma separated)
     */
    public String getAltText() {
        StringBuilder builder = new StringBuilder();
        Set<String> names = new HashSet<>();
        for (InteractorLink link : links) {
            //Avoid repeated names in the alternative name (for the tooltips)
            if (names.add(link.getNodeFrom().getDisplayName())) {
                builder.append(link.getNodeFrom().getDisplayName()).append(",");
            }
        }
        return builder.substring(0, builder.length() - 1);
    }

    public boolean isLaidOut() {
        return minX != null && maxX != null && minY != null && maxY != null;
    }

    @Override
    public boolean isHovered(Coordinate pos) {
        return pos.getX() >= minX && pos.getX() <= maxX && pos.getY() >= minY && pos.getY() <= maxY;
    }

    @Override
    public boolean isVisible() {
        for (InteractorLink link : links) {
            if (link.isVisible()) return true;
        }
        return false;
    }

    public void resetAnalysis() {
        this.isHit = false;
        this.exp = null;
    }

    public void setIsHit(boolean ishit) {
        isHit = ishit;
    }

    public void setExp(List<Double> exp) {
        this.exp = exp;
    }

    @Override
    public void setMinX(double minX) {
        this.minX = minX;
    }

    @Override
    public void setMinY(double minY) {
        this.minY = minY;
    }

    @Override
    public void setMaxX(double maxX) {
        this.maxX = maxX;
    }

    @Override
    public void setMaxY(double maxY) {
        this.maxY = maxY;
    }

    @Override
    public void drag(double deltaX, double deltaY) {
        minX += deltaX;
        maxX += deltaX;
        minY += deltaY;
        maxY += deltaY;
        for (InteractorLink interactorLink : links) {
            interactorLink.setBoundaries();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InteractorEntity entity = (InteractorEntity) o;

        return accession != null ? accession.equals(entity.accession) : entity.accession == null;
    }

    @Override
    public int hashCode() {
        return accession != null ? accession.hashCode() : 0;
    }

    private void setImageURL() {
        if (disease) return;
        image = ImageElement.as(ChemicalImageLoader.LOADING.getElement());
        if (chemical) {
            ChemicalImageLoader.get().loadImage(this, accession);
        } else {
            PDBeLoader.get().loadBestStructure(this, accession);
        }
    }

    @Override
    public void onChemicalImageLoaded(Image image) {
        this.image = ImageElement.as(image.getElement());
    }

    @Override
    public void onPDBObjectLoaded(PDBObject pdbObject) {
        this.pdbObject = pdbObject;
    }

    @Override
    public void onImageLoaded(Image image) {
        this.image = ImageElement.as(image.getElement());
    }
}
