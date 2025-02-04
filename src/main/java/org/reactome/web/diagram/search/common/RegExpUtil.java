package org.reactome.web.diagram.search.common;

import com.google.gwt.regexp.shared.RegExp;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Used to generate and cash the regular expression used to highlight
 * both the autocomplete and search results.
 *
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public final class RegExpUtil {

    private static List<String> terms = new ArrayList<>();
    private static RegExp cashedExpression;

    private RegExpUtil() {
    }

    public static RegExp getHighlightingExpression(List<String> terms){
        RegExp highlightingRegExp = cashedExpression;
        if(terms != null) {
            if (!RegExpUtil.terms.equals(terms)) {
                RegExpUtil.terms = terms;
                highlightingRegExp = compile(terms);
                cashedExpression = highlightingRegExp;
            }
        }
        return highlightingRegExp;
    }

    private static RegExp compile(List<String> terms) {
        RegExp highlightingRegExp;
        /*
         * (term1|term2)    : term is between "(" and ")" because we are creating a group, so this group can
         *                    be referred later.
         * gi               : global search and case insensitive
         * <b><u>$1</u></b> : instead of replacing by input, that would change the case, we replace it by $1,
         *                    that is the reference to the first matched group. This means that we want to
         *                    replace it using the exact word that was found.
         */
        String aux = terms.stream()
                .map(term -> RegExp.quote(term))
                .collect(Collectors.joining("|", "(", ")"));
        try {
            highlightingRegExp = RegExp.compile(aux, "gi");
        } catch (RuntimeException e) {
            //In case something goes wrong do not highlight anything
            highlightingRegExp = RegExp.compile("/.^/");
        }
        return highlightingRegExp;
    }
}
