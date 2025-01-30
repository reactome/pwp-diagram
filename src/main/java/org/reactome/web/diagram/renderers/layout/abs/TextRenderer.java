package org.reactome.web.diagram.renderers.layout.abs;

import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.NodeProperties;
import org.reactome.web.diagram.data.layout.impl.CoordinateFactory;
import org.reactome.web.diagram.data.layout.impl.NodePropertiesFactory;
import org.reactome.web.diagram.util.AdvancedContext2d;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Contains the methods for rendering text on a canvas in both single and multiple lines.
 *
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class TextRenderer {
    public static final int MAX_LINE = 4;
    private double fontSize;
    private double padding;

    public TextRenderer(double fontSize, double padding) {
        this.fontSize = fontSize;
        this.padding = padding;
    }

    public TextRenderer(double fontSize) {
        this.fontSize = fontSize;
        this.padding = 0;
    }

    public void drawTextSingleLine(AdvancedContext2d ctx, String message, Coordinate centerPosition, Double factor, Coordinate offset) {
        Coordinate newCenter = CoordinateFactory.get(centerPosition.getX(), centerPosition.getY()).transform(factor, offset);
        drawTextSingleLine(ctx, message, newCenter);
    }

    public void drawTextSingleLine(AdvancedContext2d ctx, String message, Coordinate centerPosition) {
        ctx.fillText(message, centerPosition.getX(), centerPosition.getY());
    }

    public void borderTextSingleLine(AdvancedContext2d ctx, String message, Coordinate centerPosition) {
        ctx.fillText(message, centerPosition.getX(), centerPosition.getY());
        ctx.strokeText(message, centerPosition.getX(), centerPosition.getY());
    }

    public void drawPreformattedText(AdvancedContext2d ctx, String message, NodeProperties properties, boolean centreVertically) {
        // Break the message into lines
        String[] lines = message.trim().split("\\n");
        if (lines.length > 2) {
            double centreX = properties.getX() + properties.getWidth() / 2;
            double baseY = properties.getY();
            if (centreVertically) {
                baseY = baseY + (properties.getHeight() / 2) - ((lines.length - 1) * fontSize) / 2;
            } else {
                baseY = baseY + fontSize / 2;
            }
            for (int i = 0; i < lines.length; i++) {
                ctx.fillText(lines[i], centreX, baseY + (i * fontSize));
            }
        }
    }

    public void drawTextMultiLine(AdvancedContext2d ctx, String message, NodeProperties properties, Double factor, Coordinate offset) {
        NodeProperties prop = NodePropertiesFactory.transform(properties, factor, offset);
        drawTextMultiLine(ctx, message, prop);
    }

    public void drawTextMultiLine(AdvancedContext2d ctx, String message, NodeProperties properties) {
        double availableWidth = properties.getWidth() - padding;
        List<String> textLines = splitText(ctx, message, availableWidth);

        double x = properties.getX() + properties.getWidth() / 2;
        double y = properties.getY() + properties.getHeight() / 2;

        if (textLines.size() == 1) {
            //Attempt to split at the end of CHEMBL
            textLines = splitAfterPrefix(message, "CHEMBL");
            if (textLines.size() == 1) {
                drawTextSingleLine(ctx, message, CoordinateFactory.get(x, y));
                return;
            }
        }

        textLines = ellipsisTextLines(textLines);

        // If multiple lines start drawing a bit higher
        y = y - ((textLines.size() - 1) * fontSize) / 2;

        for (int i = 0; i < textLines.size(); i++) {
            ctx.fillText(
                    textLines.get(i),
                    x,
                    y + (i * fontSize)
            );
        }
    }

    public void borderTextMultiLine(AdvancedContext2d ctx, String message, NodeProperties properties) {
        double availableWidth = properties.getWidth() - padding;
        List<String> textLines = splitText(ctx, message, availableWidth);

        double x = properties.getX() + properties.getWidth() / 2;
        double y = (properties.getY() + properties.getHeight() / 2);

        if (textLines.size() == 1) {
            drawTextSingleLine(ctx, message, CoordinateFactory.get(x, y));
            return;
        }

        textLines = ellipsisTextLines(textLines);

        // If multiple lines start drawing a bit higher
        y = y - ((textLines.size() - 1) * fontSize) / 2;


        for (int i = 0; i < textLines.size(); i++) {
            ctx.fillText(
                    textLines.get(i),
                    x,
                    y + (i * fontSize)
            );
            ctx.strokeText(
                    textLines.get(i),
                    x,
                    y + (i * fontSize)
            );
        }
    }

    private List<String> ellipsisTextLines(List<String> textLines) {
        if (textLines.size() > MAX_LINE) {
            textLines = textLines.subList(0, MAX_LINE - 1);
            textLines.add("...");
        }
        return textLines;
    }

    //**************************************************************//
    //*********** Methods used to split the long text  *************//
    //**************************************************************//

    private static List<String> splitText(AdvancedContext2d ctx, String fullName, double availableWidth) {
        List<String> rtn = new LinkedList<>();

        //1. split all words
        String[] words = fullName.trim().split(" ");
        if (words.length > 0) {
            StringBuilder singleLine = new StringBuilder();
            for (String word : words) {
                double wordWidth = measureText(ctx, word);

                // check if word is too large
                if (wordWidth > availableWidth) {
                    splitLongWord(ctx, rtn, singleLine, word, availableWidth);
                    continue;
                }

                String aux = singleLine.toString() + " " + word;

                if (singleLine.length() == 0) {
                    singleLine.append(word);
                } else if (Math.floor(measureText(ctx, aux)) <= Math.floor(availableWidth) + 2) {
                    singleLine.append(" ").append(word);
                } else {
                    rtn.add(singleLine.toString());
                    singleLine.setLength(0);
                    singleLine.append(word);

                }

            }
            if (singleLine.length() != 0) {
                rtn.add(singleLine.toString());
            }
        }
        return rtn;
    }

    private static void splitLongWord(AdvancedContext2d ctx, List<String> allLines, StringBuilder currentLine, String longWord, double availableWidth) {
        double currentLineWidth = measureText(ctx, currentLine.toString());

        //Attempt to split the word backwards
        for (int i = longWord.length() - 1; i >= 0; i--) {
            char letter = longWord.charAt(i);
            // The word can be spitted in the following points
            if (letter == ':' || letter == '.' || letter == '-' || letter == ',' || letter == ')' || letter == '/' || letter == '+') {
                // Split at this position
                String firstPart = longWord.substring(0, i + 1);
                String secondPart = longWord.substring(i + 1);

                if (currentLineWidth > 0) {
                    //need an extra space between the current line and the new word
                    firstPart = firstPart + " ";
                }

                String aux = currentLine.toString() + firstPart;

                if (Math.floor(measureText(ctx, aux)) <= Math.floor(availableWidth) + 1) {
                    // It fits!

                    // Add first part into the currentLine
                    if (currentLineWidth > 0) {
                        currentLine.append(" ").append(firstPart);
                    } else {
                        currentLine.append(firstPart);
                    }

                    // Add currentLine to the list of lines
                    allLines.add(currentLine.toString());

                    // Reset currentLine
                    currentLine.setLength(0);

                    // If word needs further splitting call the method again
                    if (measureText(ctx, secondPart) > availableWidth) {
                        splitLongWord(ctx, allLines, currentLine, secondPart, availableWidth);
                    } else {
                        // Add the second part of the split word into the new line
                        currentLine.append(secondPart);
                    }
                    break;
                }
            }
            if (i == 0 && currentLine.length() > 0) {
                //change line and try to split again
                allLines.add(currentLine.toString());
                currentLine.setLength(0);
                splitLongWord(ctx, allLines, currentLine, longWord, availableWidth);
            } else if (i == 0 && currentLine.length() == 0) {
                //It is impossible to split the word so just show it in new line
                allLines.add(longWord);
            }
        }
    }

    private static List<String> splitAfterPrefix(String fullName, final String prefix) {
        List<String> rtn = new LinkedList<>();

        boolean startsWithPrefix = fullName.toLowerCase().startsWith(prefix.toLowerCase());
        if (startsWithPrefix) {
            rtn.clear();
            rtn.add(fullName.substring(0, prefix.length())); //first part
            rtn.add(fullName.substring(prefix.length())); //second part
        } else {
            rtn = Collections.singletonList(fullName);
        }
        return rtn;
    }

    private static double measureText(AdvancedContext2d ctx, String message) {
        return ctx.measureText(message).getWidth();
    }
}
