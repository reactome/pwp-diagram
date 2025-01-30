package org.reactome.web.diagram.util.svg;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import org.vectomatic.dom.svg.*;
import org.vectomatic.dom.svg.utils.SVGConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public abstract class SVGUtil {

    private static final String STID_PATTERN = "R-(?!ICO)[A-Z]{3}-[0-9]{3,}(\\.[0-9]+)?";
    private static final String STID_PATTERN_LITE = "R-[A-Z]{3}-[0-9]{3,}";
    private static RegExp regExp = RegExp.compile(STID_PATTERN);
    private static RegExp regExpLite = RegExp.compile(STID_PATTERN_LITE);

    public static List<OMElement> getAnnotatedOMElements(OMSVGSVGElement svg) {
        List<OMElement> rtn = new ArrayList<>();
        for (OMElement child : svg.getElementsByTagName(new OMSVGGElement().getTagName())) {
            if(isAnnotated(child.getId())) {
                rtn.add(child);
            }
        }
        return rtn;
    }

    public static boolean isAnnotated(String input) {
        return input != null && regExp.test(input);
    }

    /***
     * Takes as input a string like "OVERVIEW-R-SSS-NNNNNNN"
     * or "REGION-R-SSS-NNNNNN, filters out the first part and
     * keeps only the stable identifier.
     *
     * @param identifier the id of the SVG element
     * @return the stable identifier
     */
    public static String keepStableId(String identifier) {
        String rtn = null;
        if(identifier!=null && !identifier.isEmpty()) {
            MatchResult result = regExpLite.exec(identifier);
            if(result.getGroupCount()>0) {
                rtn = result.getGroup(0);
            }
        }
        return rtn;
    }

    /***
     * Checks whether two transformation matrices are equal.
     *
     * @param m1 the first transformation matrix
     * @param m2 the second transformation matrix
     * @return True only in case the two matrices are equal
     */
    public static boolean areEqual(OMSVGMatrix m1, OMSVGMatrix m2) {
        boolean rtn;
        if (m1 == null || m2 == null ) {
            rtn = false;
        } else if (m1 == m2) {
            rtn = true;
        } else if (
                m1.getA() != m2.getA() ||
                m1.getB() != m2.getB() ||
                m1.getC() != m2.getC() ||
                m1.getD() != m2.getD() ||
                m1.getE() != m2.getE() ||
                m1.getF() != m2.getF()
                ) {
            rtn = false;
        } else {
            rtn = true;
        }
        return rtn;
    }


    public static void addClassName(OMElement element, String className) {
        if(element != null) {
            String classAtr = element.getAttribute(SVGConstants.SVG_CLASS_ATTRIBUTE);
            if(classAtr != null && !classAtr.contains(className)) {
                StringBuilder sb = new StringBuilder(classAtr);
                sb.append(" ").append(className);
                element.setAttribute(SVGConstants.SVG_CLASS_ATTRIBUTE, sb.toString());
            }
        }
    }

    public static void removeClassName(OMElement element, String className) {
        if(element != null) {
            String aux = element.getAttribute(SVGConstants.SVG_CLASS_ATTRIBUTE);
            if(aux !=null && !aux.isEmpty()) {
                element.setAttribute(SVGConstants.SVG_CLASS_ATTRIBUTE, aux.replaceAll(className, "").trim());
            }
        }
    }

    public static void addInlineStyle(OMSVGSVGElement svg, String className, String cssStyle){
        OMNodeList<OMElement> styles = svg.getElementsByTagName(SVGConstants.SVG_STYLE_TAG);
        if (styles!=null && styles.getLength()>0) {
            OMSVGStyleElement style = (OMSVGStyleElement) styles.getItem(0);
            OMNode omNode  = style.getFirstChild();
            if (omNode != null) {
                StringBuilder sb = new StringBuilder(omNode.getNodeValue());
                sb.append(".").append(className).append(cssStyle);
                omNode.setNodeValue(sb.toString());
            }
        }
    }

    public static void addInlineWithChildrenStyle(OMSVGSVGElement svg, String className, String cssStyle){
        OMNodeList<OMElement> styles = svg.getElementsByTagName(SVGConstants.SVG_STYLE_TAG);
        if (styles!=null && styles.getLength()>0) {
            OMSVGStyleElement style = (OMSVGStyleElement) styles.getItem(0);
            OMNode omNode  = style.getFirstChild();
            if (omNode != null) {
                StringBuilder sb = new StringBuilder(omNode.getNodeValue());
                sb.append(".").append(className).append(", .").append(className).append(" > * ").append(cssStyle);
                omNode.setNodeValue(sb.toString());
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    public static OMSVGDefsElement getOrCreateDefs(OMSVGSVGElement svg, OMSVGDefsElement baseDefs) {
        OMSVGDefsElement rtn;
        OMNodeList<OMElement> defs = svg.getElementsByTagName(SVGConstants.SVG_DEFS_TAG);
        if (defs != null && defs.getLength() > 0) {
            rtn = (OMSVGDefsElement) defs.getItem(0);
        } else {
            rtn = new OMSVGDefsElement();
            svg.appendChild(rtn);
        }

        for (OMNode omNode : baseDefs.getChildNodes()) {
            rtn.insertBefore(omNode.cloneNode(true), rtn.getFirstChild());
        }
        return rtn;
    }
}
