/*
 * Created by JFormDesigner on Wed Mar 05 17:37:26 EET 2008
 */

package ua.com.fielden.platform.swing.components.smart.autocompleter.renderer.development;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.jexl.Expression;
import org.apache.commons.jexl.ExpressionFactory;
import org.apache.commons.jexl.JexlContext;
import org.apache.commons.jexl.JexlHelper;

import ua.com.fielden.platform.swing.components.smart.autocompleter.development.AutocompleterLogic;
import ua.com.fielden.platform.swing.components.smart.development.Hover;



/**
 * This list cell renderer is designed to represent instances with two specified in the constructor properties: First property is in bold and located above the second property; the
 * second property has a smaller font size.
 * 
 * @author 01es
 */
public class TwoPropertyListCellRenderer<T> extends JPanel implements ListCellRenderer {
    private static final long serialVersionUID = 1L;

    private final Expression[] propertyExpressions = new Expression[2];

    private AutocompleterLogic<T> auto;

    private final JLabel jlFirstProperty;
    private final JLabel jlSecondProperty;
    private final Color hoverColour = new Color(201, 227, 251, 150);
    private final Color hoverSelectedColour = new Color(hoverColour.getRed(), hoverColour.getGreen(), hoverColour.getBlue(), 255);

    //Determines whether highlight first property or not.
    private boolean highlightFirstValue = true;
    //Determines whether highlight second property or not.
    private boolean highlightSecondValue = false;

    public TwoPropertyListCellRenderer(final String firstExpression, final String secondExpression) {
	// create expression for firstExpression
	try {
	    propertyExpressions[0] = ExpressionFactory.createExpression(("entity." + firstExpression.trim()));
	} catch (final Exception e) {
	    e.printStackTrace();
	    throw new IllegalArgumentException("Failed to create expression " + firstExpression + ": " + e.getMessage());
	}
	// create expression for secondExpression
	try {
	    propertyExpressions[1] = ExpressionFactory.createExpression(("entity." + secondExpression.trim()));
	} catch (final Exception e) {
	    e.printStackTrace();
	    throw new IllegalArgumentException("Failed to create expression " + secondExpression + ": " + e.getMessage());
	}

	// create visual components
	jlFirstProperty = createNameLabel();
	jlSecondProperty = createDescLabel();

	setLayout(new MigLayout("fill, insets 5", "[]", "[][]5")); // there will be a gap after each entry in the list
	add(jlFirstProperty, "growx, wrap");
	add(jlSecondProperty, "grow");
    }

    private JLabel createNameLabel() {
	final JLabel jlName = new JLabel();
	jlName.setText("text");
	jlName.setFont(jlName.getFont().deriveFont(jlName.getFont().getStyle() | Font.BOLD));
	jlName.setBorder(new EmptyBorder(0, 5, 0, 0));
	jlName.setPreferredSize(new Dimension(150, 17));
	return jlName;
    }

    private JLabel createDescLabel() {
	final JLabel jlDesc = new JLabel();
	jlDesc.setText("text");
	jlDesc.setBorder(new EmptyBorder(0, 5, 0, 0));
	jlDesc.setFont(new Font("DejaVu Sans", Font.PLAIN, 10));
	jlDesc.setPreferredSize(new Dimension(150, 13));
	return jlDesc;
    }

    @SuppressWarnings("unchecked")
    private String value(final Object entity, final int index) {
	final JexlContext jc = JexlHelper.createContext();
	jc.getVars().put("entity", entity);
	try {
	    return propertyExpressions[index].evaluate(jc) + "";
	} catch (final Exception e) {
	    e.printStackTrace();
	    throw new RuntimeException("Failed to evaluate expression " + propertyExpressions[index] + ": " + e.getMessage());
	}
    }

    public Component getListCellRendererComponent(final JList list, final Object value, final int index, final boolean isSelected, final boolean cellHasFocus) {
	if (isSelected) {
	    if (Hover.index(list) == index) {
		setBackground(hoverSelectedColour);
	    } else {
		setBackground(list.getSelectionBackground());
	    }
	    setForeground(list.getSelectionForeground());
	} else {
	    if (Hover.index(list) == index) {
		setBackground(hoverColour);
	    } else {
		setBackground(list.getBackground());
	    }
	    setForeground(list.getForeground());
	}
	//////////////////////////////////////////////////////////////////////////////
	//// set values to be rendered with highlighting of the matched portion //////
	//////////////////////////////////////////////////////////////////////////////
	// * if string starts with % then remove it
	// * if string ends with % then also remove it
	// * if string does not end with % then append $
	// * substitute all occurrences of % with .*
	final String fullPattern = auto.getPrevValue().toUpperCase();
	final String fullPatternS1 = removeFromStart(fullPattern, "%");
	final String fullPatternS2 = fullPatternS1.endsWith("%") ? removeFromEnd(fullPatternS1, "%") : fullPatternS1 + "$";
	final String strPattern = fullPatternS2.replaceAll("\\%", ".*");
	final Pattern pattern = Pattern.compile(strPattern);

	jlFirstProperty.setText(isHighlightFirstValue() ? matchValue(value(value, 0), pattern) : value(value, 0).toString());
	jlSecondProperty.setText(isHighlightSecondValue() ? matchValue(value(value, 1), pattern) : value(value, 1).toString());
	return this;
    }

    /**
     * Returns highlighted matched value.
     * 
     * @param value
     *            - value to highlight.
     * @param pattern
     *            - {@link Pattern} to which value value should be matched.
     * @return
     */
    private String matchValue(final String value, final Pattern pattern) {
	final String fullNameUpper = value.toUpperCase();
	final Matcher matcher = pattern.matcher(fullNameUpper);
	final StringBuffer buffer = new StringBuffer();
	buffer.append("<html>");
	if (matcher.find()) {
	    buffer.append(value.substring(0, matcher.start()));
	    buffer.append("<font bgcolor=#fffec7>");
	    buffer.append(value.substring(matcher.start(), matcher.end()));
	    buffer.append("</font>");
	    buffer.append(value.substring(matcher.end()));
	} else {
	    buffer.append(value);
	}
	buffer.append("</html>");
	return buffer.toString();
    }

    /**
     * A convenient method to remove leading wild cards.
     * 
     * @param value
     * @param whatToRemove
     * @return
     */
    private String removeFromStart(final String value, final String whatToRemove) {
	final String result = value.startsWith("%") ? value.substring(1) : value;
	return value.startsWith("%") ? removeFromStart(result, whatToRemove) : result;
    }

    /**
     * A convenient method to remove trailing wild cards.
     * 
     * @param value
     * @param whatToRemove
     * @return
     */
    private String removeFromEnd(final String value, final String whatToRemove) {
	final String result = value.endsWith("%") ? value.substring(0, value.length() - 1) : value;
	return value.endsWith("%") ? removeFromEnd(result, whatToRemove) : result;
    }

    public AutocompleterLogic<T> getAuto() {
	return auto;
    }

    public void setAuto(final AutocompleterLogic<T> auto) {
	this.auto = auto;
    }

    public boolean isHighlightFirstValue() {
	return highlightFirstValue;
    }

    public void setHighlightFirstValue(final boolean highlightFirstValue) {
	this.highlightFirstValue = highlightFirstValue;
    }

    public boolean isHighlightSecondValue() {
	return highlightSecondValue;
    }

    public void setHighlightSecondValue(final boolean highlightSecondValue) {
	this.highlightSecondValue = highlightSecondValue;
    }

}
