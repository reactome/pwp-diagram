package org.reactome.web.diagram.client.visualisers.ehld.filters;

import org.vectomatic.dom.svg.*;
import org.vectomatic.dom.svg.utils.SVGConstants;

/**
 * This class is responsible for instantiating all the
 * SVG filters used by the SVGPanel
 *
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public abstract class FilterFactory {

    private static int SHADOW_RADIUS = 10;
    private static float OUTLINE_THICKNESS = 4f;

    /**
     * Returns a filter producing a simple shadow (like a halo)
     * around the object, having the same colour as the original object.
     * This filter is mainly used on the hovered item.
     *
     * @param id The id of the returned filter
     */
    public static OMSVGFilterElement getShadowFilter(String id) {
        // Add primitives
        OMSVGFEOffsetElement offSet = new OMSVGFEOffsetElement();
        offSet.setAttribute(SVGConstants.SVG_IN_ATTRIBUTE, OMSVGFilterElement.IN_SOURCE_GRAPHIC);
        offSet.setAttribute(SVGConstants.SVG_RESULT_ATTRIBUTE, "offOut");

        OMSVGFEGaussianBlurElement blur = new OMSVGFEGaussianBlurElement();
        blur.setAttribute(SVGConstants.SVG_IN_ATTRIBUTE, "offOut");
        blur.setAttribute(SVGConstants.SVG_STD_DEVIATION_ATTRIBUTE, "" + SHADOW_RADIUS);
        blur.setAttribute(SVGConstants.SVG_RESULT_ATTRIBUTE, "blurOut");

//        OMSVGFEBlendElement blend = new OMSVGFEBlendElement();
//        blend.setAttribute(SVGConstants.SVG_IN_ATTRIBUTE, OMSVGFilterElement.IN_SOURCE_GRAPHIC);
//        blend.setAttribute(SVGConstants.SVG_IN2_ATTRIBUTE, "blurOut");
//        blend.setAttribute(SVGConstants.SVG_MODE_ATTRIBUTE, "normal");
//        blend.setAttribute(SVGConstants.SVG_RESULT_ATTRIBUTE, "blendOut");

        OMSVGFEMergeElement merge = new OMSVGFEMergeElement();
        OMSVGFEMergeNodeElement mergeNode1 = new OMSVGFEMergeNodeElement();
        mergeNode1.setAttribute(SVGConstants.SVG_IN_ATTRIBUTE, "blurOut");
        OMSVGFEMergeNodeElement mergeNode2 = new OMSVGFEMergeNodeElement();
        mergeNode2.setAttribute(SVGConstants.SVG_IN_ATTRIBUTE, OMSVGFilterElement.IN_SOURCE_GRAPHIC);
        merge.setAttribute(SVGConstants.SVG_RESULT_ATTRIBUTE, "mergeOut");
        merge.appendChild(mergeNode1);
        merge.appendChild(mergeNode2);

        //Compose the filter from the primitives
        OMSVGFilterElement shadowFilter = new OMSVGFilterElement();
        shadowFilter.setId(id);
        shadowFilter.setAttribute(SVGConstants.SVG_X_ATTRIBUTE, "-25%");
        shadowFilter.setAttribute(SVGConstants.SVG_Y_ATTRIBUTE, "-25%");
        shadowFilter.setAttribute(SVGConstants.SVG_WIDTH_ATTRIBUTE, "150%");
        shadowFilter.setAttribute(SVGConstants.SVG_HEIGHT_ATTRIBUTE, "150%");

        shadowFilter.appendChild(offSet);
        shadowFilter.appendChild(blur);
        shadowFilter.appendChild(merge);

        return shadowFilter;
    }

    /**
     * Returns a filter that creates a thick coloured
     * outline around the original object. Used to represent
     * the selected item.
     *
     * @param id The id of the returned filter
     * @param colour The colour of the filter
     * @return
     */
    public static OMSVGFilterElement getOutlineFilter(String id, FilterColour colour) {
        OMSVGFEColorMatrixElement cMatrix = new OMSVGFEColorMatrixElement();
        cMatrix.setAttribute(SVGConstants.SVG_IN_ATTRIBUTE, OMSVGFilterElement.IN_SOURCE_GRAPHIC);
        cMatrix.setAttribute(SVGConstants.SVG_VALUES_ATTRIBUTE, colour.colourMatrix);
        cMatrix.setAttribute(SVGConstants.SVG_RESULT_ATTRIBUTE, "cMatrixOut");

        OMSVGFEMorphologyElement morpho = new OMSVGFEMorphologyElement();
        morpho.setAttribute(SVGConstants.SVG_IN_ATTRIBUTE, "cMatrixOut");
        morpho.setAttribute(SVGConstants.SVG_OPERATOR_ATTRIBUTE, SVGConstants.SVG_DILATE_VALUE);
        morpho.setAttribute(SVGConstants.SVG_RADIUS_ATTRIBUTE, "" + OUTLINE_THICKNESS);
        morpho.setAttribute(SVGConstants.SVG_RESULT_ATTRIBUTE, "morphoOut");

        OMSVGFEMergeElement merge = new OMSVGFEMergeElement();
        OMSVGFEMergeNodeElement mergeNode1 = new OMSVGFEMergeNodeElement();
        mergeNode1.setAttribute(SVGConstants.SVG_IN_ATTRIBUTE, "morphoOut");
        OMSVGFEMergeNodeElement mergeNode2 = new OMSVGFEMergeNodeElement();
        mergeNode2.setAttribute(SVGConstants.SVG_IN_ATTRIBUTE, OMSVGFilterElement.IN_SOURCE_GRAPHIC);
        merge.setAttribute(SVGConstants.SVG_RESULT_ATTRIBUTE, "mergeOut");
        merge.appendChild(mergeNode1);
        merge.appendChild(mergeNode2);

        //Compose the filter from the primitives
        OMSVGFilterElement selectionFilter = new OMSVGFilterElement();
        selectionFilter.setId(id);
        selectionFilter.setAttribute(SVGConstants.SVG_X_ATTRIBUTE, "-25%");
        selectionFilter.setAttribute(SVGConstants.SVG_Y_ATTRIBUTE, "-25%");
        selectionFilter.setAttribute(SVGConstants.SVG_WIDTH_ATTRIBUTE, "150%");
        selectionFilter.setAttribute(SVGConstants.SVG_HEIGHT_ATTRIBUTE, "150%");

        selectionFilter.appendChild(cMatrix);
        selectionFilter.appendChild(morpho);
        selectionFilter.appendChild(merge);

        return selectionFilter;
    }


    public static OMSVGFilterElement getDoubleOutlineFilter(String id, FilterColour colour1, FilterColour colour2) {
        OMSVGFEColorMatrixElement cMatrix1 = new OMSVGFEColorMatrixElement();
        cMatrix1.setAttribute(SVGConstants.SVG_IN_ATTRIBUTE, OMSVGFilterElement.IN_SOURCE_GRAPHIC);
        cMatrix1.setAttribute(SVGConstants.SVG_VALUES_ATTRIBUTE, colour1.colourMatrix);
        cMatrix1.setAttribute(SVGConstants.SVG_RESULT_ATTRIBUTE, "cMatrixOut1");

        OMSVGFEMorphologyElement morpho1 = new OMSVGFEMorphologyElement();
        morpho1.setAttribute(SVGConstants.SVG_IN_ATTRIBUTE, "cMatrixOut1");
        morpho1.setAttribute(SVGConstants.SVG_OPERATOR_ATTRIBUTE, SVGConstants.SVG_DILATE_VALUE);
        morpho1.setAttribute(SVGConstants.SVG_RADIUS_ATTRIBUTE, "" + OUTLINE_THICKNESS);
        morpho1.setAttribute(SVGConstants.SVG_RESULT_ATTRIBUTE, "morphoOut1");


        OMSVGFEColorMatrixElement cMatrix2 = new OMSVGFEColorMatrixElement();
        cMatrix2.setAttribute(SVGConstants.SVG_IN_ATTRIBUTE, OMSVGFilterElement.IN_SOURCE_GRAPHIC);
        cMatrix2.setAttribute(SVGConstants.SVG_VALUES_ATTRIBUTE, colour2.colourMatrix);
        cMatrix2.setAttribute(SVGConstants.SVG_RESULT_ATTRIBUTE, "cMatrixOut2");

        OMSVGFEMorphologyElement morpho2 = new OMSVGFEMorphologyElement();
        morpho2.setAttribute(SVGConstants.SVG_IN_ATTRIBUTE, "cMatrixOut2");
        morpho2.setAttribute(SVGConstants.SVG_OPERATOR_ATTRIBUTE, SVGConstants.SVG_DILATE_VALUE);
        morpho2.setAttribute(SVGConstants.SVG_RADIUS_ATTRIBUTE, "" + (OUTLINE_THICKNESS + 3));
        morpho2.setAttribute(SVGConstants.SVG_RESULT_ATTRIBUTE, "morphoOut2");

        OMSVGFEMergeElement merge = new OMSVGFEMergeElement();
        OMSVGFEMergeNodeElement mergeNode1 = new OMSVGFEMergeNodeElement();
        mergeNode1.setAttribute(SVGConstants.SVG_IN_ATTRIBUTE, "morphoOut2");

        OMSVGFEMergeNodeElement mergeNode2 = new OMSVGFEMergeNodeElement();
        mergeNode2.setAttribute(SVGConstants.SVG_IN_ATTRIBUTE, "morphoOut1");

        OMSVGFEMergeNodeElement mergeNode3 = new OMSVGFEMergeNodeElement();
        mergeNode3.setAttribute(SVGConstants.SVG_IN_ATTRIBUTE, OMSVGFilterElement.IN_SOURCE_GRAPHIC);

        merge.setAttribute(SVGConstants.SVG_RESULT_ATTRIBUTE, "mergeOut");
        merge.appendChild(mergeNode1);
        merge.appendChild(mergeNode2);
        merge.appendChild(mergeNode3);


        //Compose the filter from the primitives
        OMSVGFilterElement selectionFilter = new OMSVGFilterElement();
        selectionFilter.setId(id);
        selectionFilter.setAttribute(SVGConstants.SVG_X_ATTRIBUTE, "-25%");
        selectionFilter.setAttribute(SVGConstants.SVG_Y_ATTRIBUTE, "-25%");
        selectionFilter.setAttribute(SVGConstants.SVG_WIDTH_ATTRIBUTE, "150%");
        selectionFilter.setAttribute(SVGConstants.SVG_HEIGHT_ATTRIBUTE, "150%");

        selectionFilter.appendChild(cMatrix1);
        selectionFilter.appendChild(morpho1);
        selectionFilter.appendChild(cMatrix2);
        selectionFilter.appendChild(morpho2);
        selectionFilter.appendChild(merge);

        return selectionFilter;
    }

    /**
     * Returns a filter producing a simple shadow (like a halo)
     * around the object, having the specified colour.
     *
     * @param id The id of the returned filter
     * @param colour The colour of the shadow
     * @return
     */
    public static OMSVGFilterElement getColouredShadowFilter(String id, FilterColour colour) {
        // Add primitives
        OMSVGFEColorMatrixElement cMatrix = new OMSVGFEColorMatrixElement();
        cMatrix.setAttribute(SVGConstants.SVG_IN_ATTRIBUTE, OMSVGFilterElement.IN_SOURCE_GRAPHIC);
        cMatrix.setAttribute(SVGConstants.SVG_VALUES_ATTRIBUTE, colour.colourMatrix);
        cMatrix.setAttribute(SVGConstants.SVG_RESULT_ATTRIBUTE, "cMatrixOut");

        OMSVGFEOffsetElement offSet = new OMSVGFEOffsetElement();
        offSet.setAttribute(SVGConstants.SVG_IN_ATTRIBUTE, "cMatrixOut");
        offSet.setAttribute(SVGConstants.SVG_RESULT_ATTRIBUTE, "offOut");

        OMSVGFEGaussianBlurElement blur = new OMSVGFEGaussianBlurElement();
        blur.setAttribute(SVGConstants.SVG_IN_ATTRIBUTE, "offOut");
        blur.setAttribute(SVGConstants.SVG_STD_DEVIATION_ATTRIBUTE, "" + SHADOW_RADIUS);
        blur.setAttribute(SVGConstants.SVG_RESULT_ATTRIBUTE, "blurOut");

        OMSVGFEBlendElement blend = new OMSVGFEBlendElement();
        blend.setAttribute(SVGConstants.SVG_IN_ATTRIBUTE, OMSVGFilterElement.IN_SOURCE_GRAPHIC);
        blend.setAttribute(SVGConstants.SVG_IN2_ATTRIBUTE, "blurOut");
        blend.setAttribute(SVGConstants.SVG_MODE_ATTRIBUTE, "normal");

        //Compose the filter from the primitives
        OMSVGFilterElement shadowFilter = new OMSVGFilterElement();
        shadowFilter.setId(id);
        shadowFilter.setAttribute(SVGConstants.SVG_X_ATTRIBUTE, "-25%");
        shadowFilter.setAttribute(SVGConstants.SVG_Y_ATTRIBUTE, "-25%");
        shadowFilter.setAttribute(SVGConstants.SVG_WIDTH_ATTRIBUTE, "150%");
        shadowFilter.setAttribute(SVGConstants.SVG_HEIGHT_ATTRIBUTE, "150%");
//        shadowFilter.setAttribute(SVGConstants.SVG_FILTER_RES_ATTRIBUTE, "200");

        shadowFilter.appendChild(cMatrix);
        shadowFilter.appendChild(offSet);
        shadowFilter.appendChild(blur);
        shadowFilter.appendChild(blend);

        return shadowFilter;
    }

    /**
     * Returns a filter that combines the steps of a ShadowFilter with those
     * of an Outline Filter. The resulting filter is more efficient than the one
     * from the {@link #combine(String, OMSVGFilterElement, OMSVGFilterElement)} combine} method
     *
     * @param id The id of the returned filter
     * @param colour The colour of the outline
     * @return
     */
    public static OMSVGFilterElement getShadowWithOutlineFilter(String id, FilterColour colour) {
        //========== Selection Filter =========//
        OMSVGFEColorMatrixElement cMatrix = new OMSVGFEColorMatrixElement();
        cMatrix.setAttribute(SVGConstants.SVG_IN_ATTRIBUTE, OMSVGFilterElement.IN_SOURCE_GRAPHIC);
        cMatrix.setAttribute(SVGConstants.SVG_VALUES_ATTRIBUTE, colour.colourMatrix);
        cMatrix.setAttribute(SVGConstants.SVG_RESULT_ATTRIBUTE, "cMatrixOut");

        OMSVGFEMorphologyElement morpho = new OMSVGFEMorphologyElement();
        morpho.setAttribute(SVGConstants.SVG_IN_ATTRIBUTE, "cMatrixOut");
        morpho.setAttribute(SVGConstants.SVG_OPERATOR_ATTRIBUTE, SVGConstants.SVG_DILATE_VALUE);
        morpho.setAttribute(SVGConstants.SVG_RADIUS_ATTRIBUTE, "" + OUTLINE_THICKNESS);
        morpho.setAttribute(SVGConstants.SVG_RESULT_ATTRIBUTE, "morphoOut");

        OMSVGFEMergeElement merge = new OMSVGFEMergeElement();
        OMSVGFEMergeNodeElement mergeNode1 = new OMSVGFEMergeNodeElement();
        mergeNode1.setAttribute(SVGConstants.SVG_IN_ATTRIBUTE, "morphoOut");
        OMSVGFEMergeNodeElement mergeNode2 = new OMSVGFEMergeNodeElement();
        mergeNode2.setAttribute(SVGConstants.SVG_IN_ATTRIBUTE, OMSVGFilterElement.IN_SOURCE_GRAPHIC);
        merge.setAttribute(SVGConstants.SVG_RESULT_ATTRIBUTE, "mergeOut");
        merge.appendChild(mergeNode1);
        merge.appendChild(mergeNode2);

        //========== Shadow Filter =========//
        OMSVGFEOffsetElement offSet = new OMSVGFEOffsetElement();
        offSet.setAttribute(SVGConstants.SVG_IN_ATTRIBUTE, OMSVGFilterElement.IN_SOURCE_GRAPHIC);
        offSet.setAttribute(SVGConstants.SVG_RESULT_ATTRIBUTE, "offOut");

        OMSVGFEGaussianBlurElement blur = new OMSVGFEGaussianBlurElement();
        blur.setAttribute(SVGConstants.SVG_IN_ATTRIBUTE, "offOut");
        blur.setAttribute(SVGConstants.SVG_STD_DEVIATION_ATTRIBUTE, "" + SHADOW_RADIUS);
        blur.setAttribute(SVGConstants.SVG_RESULT_ATTRIBUTE, "blurOut");


        //Final combination of the two filters
        OMSVGFEMergeElement result = new OMSVGFEMergeElement();
        OMSVGFEMergeNodeElement resultNode1 = new OMSVGFEMergeNodeElement();
        resultNode1.setAttribute(SVGConstants.SVG_IN_ATTRIBUTE, "blurOut");
        OMSVGFEMergeNodeElement resultNode2 = new OMSVGFEMergeNodeElement();
        resultNode2.setAttribute(SVGConstants.SVG_IN_ATTRIBUTE, "mergeOut");
        result.appendChild(resultNode1);
        result.appendChild(resultNode2);

        //Compose the filter from the primitives
        OMSVGFilterElement combinedFilter = new OMSVGFilterElement();
        combinedFilter.setId(id);
        combinedFilter.setAttribute(SVGConstants.SVG_X_ATTRIBUTE, "-25%");
        combinedFilter.setAttribute(SVGConstants.SVG_Y_ATTRIBUTE, "-25%");
        combinedFilter.setAttribute(SVGConstants.SVG_WIDTH_ATTRIBUTE, "150%");
        combinedFilter.setAttribute(SVGConstants.SVG_HEIGHT_ATTRIBUTE, "150%");

        combinedFilter.appendChild(cMatrix);
        combinedFilter.appendChild(morpho);
        combinedFilter.appendChild(merge);
        combinedFilter.appendChild(offSet);
        combinedFilter.appendChild(blur);
        combinedFilter.appendChild(result);

        return combinedFilter;
    }

    /**
     * Returns a filter that combines the steps of a ShadowFilter with 2 outlines nested.
     * It is used for double highlighting purposes (e.g hovering and flagging)
     *
     * @param id The id of the returned filter
     * @param colour1 The colour of the inner outline
     * @param colour2 The colour of the outer outline
     * @return
     */

    public static OMSVGFilterElement getShadowWithDoubleOutlineFilter(String id, FilterColour colour1, FilterColour colour2) {
        OMSVGFEColorMatrixElement cMatrix1 = new OMSVGFEColorMatrixElement();
        cMatrix1.setAttribute(SVGConstants.SVG_IN_ATTRIBUTE, OMSVGFilterElement.IN_SOURCE_GRAPHIC);
        cMatrix1.setAttribute(SVGConstants.SVG_VALUES_ATTRIBUTE, colour1.colourMatrix);
        cMatrix1.setAttribute(SVGConstants.SVG_RESULT_ATTRIBUTE, "cMatrixOut1");

        OMSVGFEMorphologyElement morpho1 = new OMSVGFEMorphologyElement();
        morpho1.setAttribute(SVGConstants.SVG_IN_ATTRIBUTE, "cMatrixOut1");
        morpho1.setAttribute(SVGConstants.SVG_OPERATOR_ATTRIBUTE, SVGConstants.SVG_DILATE_VALUE);
        morpho1.setAttribute(SVGConstants.SVG_RADIUS_ATTRIBUTE, "" + OUTLINE_THICKNESS);
        morpho1.setAttribute(SVGConstants.SVG_RESULT_ATTRIBUTE, "morphoOut1");


        OMSVGFEColorMatrixElement cMatrix2 = new OMSVGFEColorMatrixElement();
        cMatrix2.setAttribute(SVGConstants.SVG_IN_ATTRIBUTE, OMSVGFilterElement.IN_SOURCE_GRAPHIC);
        cMatrix2.setAttribute(SVGConstants.SVG_VALUES_ATTRIBUTE, colour2.colourMatrix);
        cMatrix2.setAttribute(SVGConstants.SVG_RESULT_ATTRIBUTE, "cMatrixOut2");

        OMSVGFEMorphologyElement morpho2 = new OMSVGFEMorphologyElement();
        morpho2.setAttribute(SVGConstants.SVG_IN_ATTRIBUTE, "cMatrixOut2");
        morpho2.setAttribute(SVGConstants.SVG_OPERATOR_ATTRIBUTE, SVGConstants.SVG_DILATE_VALUE);
        morpho2.setAttribute(SVGConstants.SVG_RADIUS_ATTRIBUTE, "" + (OUTLINE_THICKNESS + 3));
        morpho2.setAttribute(SVGConstants.SVG_RESULT_ATTRIBUTE, "morphoOut2");

        //========== Shadow Filter =========//
        OMSVGFEOffsetElement offSet = new OMSVGFEOffsetElement();
        offSet.setAttribute(SVGConstants.SVG_IN_ATTRIBUTE, OMSVGFilterElement.IN_SOURCE_GRAPHIC);
        offSet.setAttribute(SVGConstants.SVG_RESULT_ATTRIBUTE, "offOut");

        OMSVGFEGaussianBlurElement blur = new OMSVGFEGaussianBlurElement();
        blur.setAttribute(SVGConstants.SVG_IN_ATTRIBUTE, "offOut");
        blur.setAttribute(SVGConstants.SVG_STD_DEVIATION_ATTRIBUTE, "" + SHADOW_RADIUS);
        blur.setAttribute(SVGConstants.SVG_RESULT_ATTRIBUTE, "blurOut");


        //Final combination of the two filters
        OMSVGFEMergeElement merge = new OMSVGFEMergeElement();

        OMSVGFEMergeNodeElement mergeNode1 = new OMSVGFEMergeNodeElement();
        mergeNode1.setAttribute(SVGConstants.SVG_IN_ATTRIBUTE, "blurOut");

        OMSVGFEMergeNodeElement mergeNode2 = new OMSVGFEMergeNodeElement();
        mergeNode2.setAttribute(SVGConstants.SVG_IN_ATTRIBUTE, "morphoOut2");

        OMSVGFEMergeNodeElement mergeNode3 = new OMSVGFEMergeNodeElement();
        mergeNode3.setAttribute(SVGConstants.SVG_IN_ATTRIBUTE, "morphoOut1");

        OMSVGFEMergeNodeElement mergeNode4 = new OMSVGFEMergeNodeElement();
        mergeNode4.setAttribute(SVGConstants.SVG_IN_ATTRIBUTE, OMSVGFilterElement.IN_SOURCE_GRAPHIC);

        merge.setAttribute(SVGConstants.SVG_RESULT_ATTRIBUTE, "mergeOut");
        merge.appendChild(mergeNode1);
        merge.appendChild(mergeNode2);
        merge.appendChild(mergeNode3);
        merge.appendChild(mergeNode4);

        //Compose the filter from the primitives
        OMSVGFilterElement selectionFilter = new OMSVGFilterElement();
        selectionFilter.setId(id);
        selectionFilter.setAttribute(SVGConstants.SVG_X_ATTRIBUTE, "-25%");
        selectionFilter.setAttribute(SVGConstants.SVG_Y_ATTRIBUTE, "-25%");
        selectionFilter.setAttribute(SVGConstants.SVG_WIDTH_ATTRIBUTE, "150%");
        selectionFilter.setAttribute(SVGConstants.SVG_HEIGHT_ATTRIBUTE, "150%");

        selectionFilter.appendChild(cMatrix1);
        selectionFilter.appendChild(morpho1);
        selectionFilter.appendChild(cMatrix2);
        selectionFilter.appendChild(morpho2);
        selectionFilter.appendChild(offSet);
        selectionFilter.appendChild(blur);
        selectionFilter.appendChild(merge);

        return selectionFilter;
    }

        /**
         * A generic method for combining two filters in one.
         *
         * @param id The id of the returned filter
         * @param f1 The first filter to be combined
         * @param f2 The second filter to be combined
         * @return
         */
    public static OMSVGFilterElement combine(String id, OMSVGFilterElement f1, OMSVGFilterElement f2) {
        OMSVGFilterElement rtn = new OMSVGFilterElement();

        OMSVGElement l1 = (OMSVGElement) f1.getLastChild();
        OMSVGElement l2 = (OMSVGElement) f2.getLastChild();

        for (OMNode n : f1.getChildNodes()) {
            rtn.appendChild(n.cloneNode(true));
        }

        for (OMNode n : f2.getChildNodes()) {
            rtn.appendChild(n.cloneNode(true));
        }

        OMSVGFEMergeElement result = new OMSVGFEMergeElement();
        OMSVGFEMergeNodeElement resultNode1 = new OMSVGFEMergeNodeElement();
        resultNode1.setAttribute(SVGConstants.SVG_IN_ATTRIBUTE, l1.getAttribute(SVGConstants.SVG_RESULT_ATTRIBUTE));
        OMSVGFEMergeNodeElement resultNode2 = new OMSVGFEMergeNodeElement();
        resultNode2.setAttribute(SVGConstants.SVG_IN_ATTRIBUTE, l2.getAttribute(SVGConstants.SVG_RESULT_ATTRIBUTE));
        result.appendChild(resultNode1);
        result.appendChild(resultNode2);

        rtn.appendChild(result);

        rtn.setId(id);
        rtn.setAttribute(SVGConstants.SVG_X_ATTRIBUTE, "-25%");
        rtn.setAttribute(SVGConstants.SVG_Y_ATTRIBUTE, "-25%");
        rtn.setAttribute(SVGConstants.SVG_WIDTH_ATTRIBUTE, "150%");
        rtn.setAttribute(SVGConstants.SVG_HEIGHT_ATTRIBUTE, "150%");

        return rtn;
    }

    /**
     * Returns a filter producing a simple color overlay
     * on the object, having the specified colour.
     *
     * @param id The id of the returned filter
     * @param colour The colour of the overlay
     * @return
     */
    public static OMSVGFilterElement getColouredOverlayFilter(String id, FilterColour colour) {
        // Add primitives
        OMSVGFEColorMatrixElement cMatrix = new OMSVGFEColorMatrixElement();
        cMatrix.setAttribute(SVGConstants.SVG_IN_ATTRIBUTE, OMSVGFilterElement.IN_SOURCE_GRAPHIC);
        cMatrix.setAttribute(SVGConstants.SVG_VALUES_ATTRIBUTE, colour.colourMatrix);
        cMatrix.setAttribute(SVGConstants.SVG_RESULT_ATTRIBUTE, "cMatrixOut");

        OMSVGFEOffsetElement offSet = new OMSVGFEOffsetElement();
        offSet.setAttribute(SVGConstants.SVG_IN_ATTRIBUTE, "cMatrixOut");
        offSet.setAttribute(SVGConstants.SVG_RESULT_ATTRIBUTE, "offOut");

        OMSVGFEBlendElement blend = new OMSVGFEBlendElement();
        blend.setAttribute(SVGConstants.SVG_IN2_ATTRIBUTE, OMSVGFilterElement.IN_SOURCE_GRAPHIC);
        blend.setAttribute(SVGConstants.SVG_IN_ATTRIBUTE, "offOut");
        blend.setAttribute(SVGConstants.SVG_MODE_ATTRIBUTE, "normal");

        //Compose the filter from the primitives
        OMSVGFilterElement shadowFilter = new OMSVGFilterElement();
        shadowFilter.setId(id);
        shadowFilter.setAttribute(SVGConstants.SVG_X_ATTRIBUTE, "-25%");
        shadowFilter.setAttribute(SVGConstants.SVG_Y_ATTRIBUTE, "-25%");
        shadowFilter.setAttribute(SVGConstants.SVG_WIDTH_ATTRIBUTE, "150%");
        shadowFilter.setAttribute(SVGConstants.SVG_HEIGHT_ATTRIBUTE, "150%");
//        shadowFilter.setAttribute(SVGConstants.SVG_FILTER_RES_ATTRIBUTE, "200");

        shadowFilter.appendChild(cMatrix);
        shadowFilter.appendChild(offSet);
        shadowFilter.appendChild(blend);

        return shadowFilter;
    }
}
