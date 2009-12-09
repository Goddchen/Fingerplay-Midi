package com.flat20.gui;

import java.io.File;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
			//File file = new File("test.xml");
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

			int numTouchPads = 0;
			int numSliders = 0;
			int numButtons = 0;

			Element layout = doc.getDocumentElement();

			float deltaWidth = getIntegerAttribute(layout, "screenWidth") / (float)width;
			float deltaHeight = getIntegerAttribute(layout, "screenHeight") / (float)height;

			//System.out.println(deltaWidth + ", " + deltaHeight);

			NodeList screens = doc.getElementsByTagName("screen");

			for (int s = 0; s < screens.getLength(); s++) {
				//System.out.println("Name: " + screens.item(s).getNodeName());

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
							//System.out.println(" .x = " + widgetX);
							//System.out.println(" .y = " + widgetY);
							//System.out.println(" .width = " + widgetWidth);
							//System.out.println(" .height = " + widgetHeight);

							Widget widget = null;
							if (name.equals("button") || name.equals("pad")) {
								widget = new Pad("Button " + (++numButtons));
								//System.out.println("screen.addWidget( new Button() );");
							} else if (name.equals("slider")) {
								widget = new Slider("Slider " + (++numSliders));
								//System.out.println("screen.addWidget( new Slider() );");
							} else if (name.equals("touchpad") || name.equals("xypad")) {
								widget = new XYPad("Touch Pad " + (++numTouchPads));
								//System.out.println("screen.addWidget( new Touchpad() ); " + numTouchPads);
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
		} catch (Exception e) {
			System.out.println(e);
		}

	}
	protected static Integer getIntegerAttribute(Element element, String attributeName) {
		return Integer.parseInt( element.getAttribute(attributeName) );
	}
}
