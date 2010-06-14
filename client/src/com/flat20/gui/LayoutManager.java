package com.flat20.gui;

import java.io.File;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.flat20.fingerplay.midicontrollers.IMidiController;
import com.flat20.gui.widgets.Pad;
import com.flat20.gui.widgets.Slider;
import com.flat20.gui.widgets.Widget;
import com.flat20.gui.widgets.WidgetContainer;
import com.flat20.gui.widgets.XYPad;

/**
 * TODO Move to GUI project.
 * 
 * @author andreas
 *
 */
public class LayoutManager {

	final public static void loadXML(WidgetContainer mainContainer, InputStream xmlStream, int width, int height) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse( xmlStream );

			parseXML(mainContainer, doc, width, height);
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	final public static void loadXML(WidgetContainer mainContainer, File xmlFile, int width, int height) {

		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse( xmlFile );

			parseXML(mainContainer, doc, width, height);
		} catch (Exception e) {
			System.out.println(e);
		}


	}

	final public static void parseXML(WidgetContainer mainContainer, Document doc, int width, int height) {

		try {

			doc.getDocumentElement().normalize();

			// Figure out if it's new format or not based on the layout width compared to screen width.
			Element layout = doc.getDocumentElement();

			if (layout.getNodeName().equals("layouts")) {
				NodeList layouts = doc.getElementsByTagName("layout");
				int bestDiff = 10000;

				for (int l = 0; l < layouts.getLength(); l++) {
					if (layouts.item(l).getNodeType() == Node.ELEMENT_NODE) {
						Element tempLayout = (Element) layouts.item(l);
						int layoutWidth = getIntegerAttribute(tempLayout, "screenWidth");
						int diff = Math.abs(width - layoutWidth);
						if (diff < bestDiff) {
							bestDiff = diff;
							layout = tempLayout;
						}
					}
				}
			} else {
			}

			parseLayout(mainContainer, layout, width, height);

		} catch (Exception e) {
			System.out.println(e);
		}
	}

	protected static void parseLayout(WidgetContainer mainContainer, Element layout, int androidWidth, int androidHeight) {

		int numTouchPads = 0;
		int numSliders = 0;
		int numButtons = 0;

		float deltaWidth = androidWidth / (float)getIntegerAttribute(layout, "screenWidth");
		float deltaHeight = androidHeight / (float)getIntegerAttribute(layout, "screenHeight");

		NodeList screens = layout.getElementsByTagName("screen");

		// Loop through all screens and add any widgets to the WidgetContainer.
		// A bit of a mix between view and data here.  
		for (int s = 0; s < screens.getLength(); s++) {

			if (screens.item(s).getNodeType() == Node.ELEMENT_NODE) {

				Element screenElement = (Element) screens.item(s);

				int screenX = (int) (getIntegerAttribute(screenElement, "x")*deltaWidth);
				int screenY = (int) (getIntegerAttribute(screenElement, "y")*deltaHeight);
				int screenWidth = (int) (getIntegerAttribute(screenElement, "width")*deltaWidth);
				int screenHeight = (int) (getIntegerAttribute(screenElement, "height")*deltaHeight);
				WidgetContainer wc = new WidgetContainer(screenWidth, screenHeight);
				wc.x = screenX;
				wc.y = screenY;

				NodeList widgets = screenElement.getChildNodes();
				for (int e = 0; e < widgets.getLength(); e++) {

					if (widgets.item(e).getNodeType() == Node.ELEMENT_NODE) {

						Element widgetElement = (Element) widgets.item(e);
						String name = widgetElement.getNodeName();

						int widgetX = (int)(getIntegerAttribute(widgetElement, "x")*deltaWidth);
						int widgetY = (int)(getIntegerAttribute(widgetElement, "y")*deltaHeight);
						int widgetWidth = (int)(getIntegerAttribute(widgetElement, "width")*deltaWidth);
						int widgetHeight = (int)(getIntegerAttribute(widgetElement, "height")*deltaHeight);
						System.out.println(" ?? " + widgetElement.getAttribute("controllerNumber"));
						int widgetControllerNumber = getIntegerAttribute(widgetElement, "controllerNumber", IMidiController.CONTROLLER_NUMBER_UNASSIGNED);

						System.out.println(name + ": " + widgetControllerNumber);

						Widget widget = null;
						if (name.equals("button") || name.equals("pad")) {
							widget = new Pad("Button " + (++numButtons), widgetControllerNumber);

						} else if (name.equals("slider")) {
							widget = new Slider("Slider " + (++numSliders), widgetControllerNumber);

						} else if (name.equals("touchpad") || name.equals("xypad")) {
							widget = new XYPad("XY Pad " + (++numTouchPads), widgetControllerNumber);

						}
						if (widget != null) {
							widget.x = widgetX;
							widget.y = widgetY;
							widget.setSize(widgetWidth, widgetHeight);
							wc.addSprite(widget);
						}
					}
				}

				mainContainer.addSprite( wc );

			}
		}

	}

	protected static Integer getIntegerAttribute(Element element, String attributeName) {
		return Integer.parseInt( element.getAttribute(attributeName) );
	}

	// Gets attribute integer value if the attribute is set and is a valid integer. Otherwise returns defaultValue.
	protected static Integer getIntegerAttribute(Element element, String attributeName, int defaultValue) {
		int result = defaultValue;

		try {
			if (element.hasAttribute(attributeName))
				result = Integer.parseInt( element.getAttribute(attributeName) );
		} catch (Exception e) {
		}

		return result;
	}

}
