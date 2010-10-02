package com.flat20.gui;

import java.io.File;

import java.io.InputStream;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.util.Log;

import com.flat20.fingerplay.midicontrollers.MidiController;
import com.flat20.fingerplay.midicontrollers.Parameter;
import com.flat20.gui.widgets.Pad;
import com.flat20.gui.widgets.SensorSlider;
import com.flat20.gui.widgets.SensorXYPad;
import com.flat20.gui.widgets.Slider;
import com.flat20.gui.widgets.Widget;
import com.flat20.gui.widgets.WidgetContainer;
import com.flat20.gui.widgets.XYPad;

/**
 * TODO Move to GUI project.
 * TODO Refactor completely. There are far better ways to parse XML.
 *
 * I created two parseLayout functions for version 1 and version 2 of the XML files.
 * TODO Get rid of version 1
 * 
 * The resulting WidgetContainer needs to go to NavigationOverlay but best 
 * would be to parse the XML in to a data format and parse that into both 
 * MidiControllers AND widgets. 
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
			Element root = doc.getDocumentElement();
			Element layout = null;
			
			System.out.println("here");

			if (!root.getNodeName().equals("layouts"))
				return;


			// Hardcoded defaults.. TODO: Load from the Widgets

			HashMap<String, Parameter[]> defaultParameters = new HashMap<String, Parameter[]>(3);
			Parameter parameters[];

			// Pad
			parameters = new Parameter[] {
					new Parameter(0, 0, 0, "Press", Parameter.TYPE_NOTE, false)
					};
			defaultParameters.put("pad", parameters);

			// Slider
			parameters = new Parameter[] {
					new Parameter(0, 0, 0, "Press", Parameter.TYPE_CONTROL_CHANGE, true),
					new Parameter(1, 0, 0, "Vertical", Parameter.TYPE_CONTROL_CHANGE, true)
					};
			defaultParameters.put("slider", parameters);

			// XYPad
			parameters = new Parameter[] {
					new Parameter(0, 0, 0, "Press", Parameter.TYPE_CONTROL_CHANGE, true),
					new Parameter(1, 0, 0, "Horizontal", Parameter.TYPE_CONTROL_CHANGE, true),
					new Parameter(2, 0, 0, "Vertical", Parameter.TYPE_CONTROL_CHANGE, true)
					};
			defaultParameters.put("xypad", parameters);

			int version = 1;
			try {
				version = Integer.parseInt( root.getAttribute("version") );
			} catch (Exception e) {
			}

			System.out.println("Layout version " + version);
			
			// Get controller defaults
			NodeList defaults = doc.getElementsByTagName("defaults");
			for (int l = 0; l < defaults.getLength(); l++) {
				if (defaults.item(l).getNodeType() == Node.ELEMENT_NODE) {
					Element tempDefault = (Element) defaults.item(l);

					// loop through defaults/controllers
					NodeList controllers = tempDefault.getChildNodes();
					for (int i = 0; i < controllers.getLength(); i++) {
						if (controllers.item(i).getNodeType() == Node.ELEMENT_NODE) {
							Element controller = (Element) controllers.item(i);

							String name = controller.getNodeName();
							//String widgetClass = getStringAttribute(controller, "class", null);

							// TODO Create the widget based on the class name.

							//System.out.println(name + " class = " + widgetClass);
							/*
							if (widgetClass != null) {
								try {
									Class<?> WidgetClass = Class.forName(widgetClass);
									Class parameterTypes[] = new Class[] { IMidiController.class };
									Constructor<?> ct = WidgetClass.getConstructor(parameterTypes);
									Object argumentList[] = new Object[] { null };

									Widget widget = (Widget) WidgetClass.newInstance();

									System.out.println(widget);
								} catch (Exception ex) {
									ex.printStackTrace();
								}
							}
								 */

							
							parameters = defaultParameters.get(name);
							if (parameters != null) {
								System.out.println("Default for " + name);
								updateParameters(parameters, controller);
							} else {
								Log.i("LayoutManager", "Couldn't find hardcoded defaults for " + name);
							}
						}
					}
				}
			}


			// Get the correct layout for the current resolution
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

			if (version == 1)
				parseLayoutV1(mainContainer, layout, width, height, defaultParameters);
			else
				parseLayoutV2(mainContainer, layout, width, height, defaultParameters);

		} catch (Exception e) {
			e.printStackTrace();
			//System.out.println(e);
		}
	}

	protected static void parseLayoutV1(WidgetContainer mainContainer, Element layout, int androidWidth, int androidHeight, HashMap<String, Parameter[]> defaultParameters) {

		int numTouchPads = 0;
		int numSliders = 0;
		int numButtons = 0;

		float deltaWidth = androidWidth / (float)getIntegerAttribute(layout, "screenWidth");
		float deltaHeight = androidHeight / (float)getIntegerAttribute(layout, "screenHeight");

		NodeList screens = layout.getElementsByTagName("screen");

		// V1 assigns controller numbers automatically.
		int autoControllerNumber = 0;

		// Loop through all screens and add any widgets to the WidgetContainer.
		// A bit of a mix between view and data here.
		final int length = screens.getLength();
		for (int s = 0; s < length; s++) {
			
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
				final int wlength =  widgets.getLength();
				for (int e = 0; e < wlength; e++) {

					if (widgets.item(e).getNodeType() == Node.ELEMENT_NODE) {

						Element widgetElement = (Element) widgets.item(e);
						String name = widgetElement.getNodeName();

						int widgetX = (int)(getIntegerAttribute(widgetElement, "x")*deltaWidth);
						int widgetY = (int)(getIntegerAttribute(widgetElement, "y")*deltaHeight);
						int widgetWidth = (int)(getIntegerAttribute(widgetElement, "width")*deltaWidth);
						int widgetHeight = (int)(getIntegerAttribute(widgetElement, "height")*deltaHeight);

						MidiController mc = new MidiController();
						Widget widget = null;
						if (name.equals("button") || name.equals("pad")) {
							mc.setName( "Button " + (++numButtons) );

							Parameter[] parameters = cloneParameters(defaultParameters.get("pad"));
							for (int i=0; i<parameters.length; i++) {
								parameters[i].controllerNumber = autoControllerNumber++;
							}

							mc.setParameters( parameters );
							widget = new Pad(mc);//"Button " + (++numButtons), widgetControllerNumber);

						} else if (name.equals("slider")) {
							mc.setName( "Slider " + (++numSliders) );

							Parameter[] parameters = cloneParameters(defaultParameters.get("slider"));
							for (int i=0; i<parameters.length; i++) {
								parameters[i].controllerNumber = autoControllerNumber++;
							}

							mc.setParameters( parameters );
							widget = new Slider(mc);//"Slider " + (++numSliders), widgetControllerNumber);

						} else if (name.equals("touchpad") || name.equals("xypad")) {
							mc.setName( "XY Pad " + (++numTouchPads) );

							Parameter[] parameters = cloneParameters(defaultParameters.get("xypad"));
							for (int i=0; i<parameters.length; i++) {
								parameters[i].controllerNumber = autoControllerNumber++;
							}

							mc.setParameters( parameters );
							widget = new XYPad(mc);//"XY Pad " + (++numTouchPads), widgetControllerNumber);
						}
 
						if (widget != null) {
							widget.x = widgetX;
							widget.y = widgetY;
							widget.setSize(widgetWidth, widgetHeight);
							wc.addSprite(widget);

							// Load any XML parameters
							updateParameters(mc.getParameters(), widgetElement);
							System.out.println(mc);

						}
					}
				}

				mainContainer.addSprite( wc );

			}
		}

	}

	protected static void parseLayoutV2(WidgetContainer mainContainer, Element layout, int androidWidth, int androidHeight, HashMap<String, Parameter[]> defaultParameters) {

		int numTouchPads = 0;
		int numSliders = 0;
		int numButtons = 0;

		float deltaWidth = androidWidth / (float)getIntegerAttribute(layout, "screenWidth");
		float deltaHeight = androidHeight / (float)getIntegerAttribute(layout, "screenHeight");

		NodeList screens = layout.getElementsByTagName("screen");

		// Loop through all screens and add any widgets to the WidgetContainer.
		// A bit of a mix between view and data here.
		final int length = screens.getLength();
		for (int s = 0; s < length; s++) {
			
			if (screens.item(s).getNodeType() == Node.ELEMENT_NODE) {

				Element screenElement = (Element) screens.item(s);

				int screenX = (int) (getIntegerAttribute(screenElement, "x")*deltaWidth);
				int screenY = (int) (getIntegerAttribute(screenElement, "y")*deltaHeight);
				int screenWidth = (int) (getIntegerAttribute(screenElement, "width")*deltaWidth);
				int screenHeight = (int) (getIntegerAttribute(screenElement, "height")*deltaHeight);
				WidgetContainer wc = new WidgetContainer(screenWidth, screenHeight);
				wc.x = screenX;
				wc.y = screenY;

				//int autoControllerNumber = 0;

				NodeList widgets = screenElement.getChildNodes();
				final int wlength =  widgets.getLength();
				for (int e = 0; e < wlength; e++) {

					if (widgets.item(e).getNodeType() == Node.ELEMENT_NODE) {

						Element widgetElement = (Element) widgets.item(e);
						String name = widgetElement.getNodeName();

						int widgetX = (int)(getIntegerAttribute(widgetElement, "x")*deltaWidth);
						int widgetY = (int)(getIntegerAttribute(widgetElement, "y")*deltaHeight);
						int widgetWidth = (int)(getIntegerAttribute(widgetElement, "width")*deltaWidth);
						int widgetHeight = (int)(getIntegerAttribute(widgetElement, "height")*deltaHeight);

						MidiController mc = new MidiController();
						Widget widget = null;
						if (name.equals("button") || name.equals("pad")) {
							mc.setName( "Button " + (++numButtons) );

							Parameter[] parameters = cloneParameters(defaultParameters.get("pad"));
							/*
							for (int i=0; i<parameters.length; i++) {
								parameters[i].controllerNumber = autoControllerNumber++;
							}*/

							mc.setParameters( parameters );
							widget = new Pad(mc);//"Button " + (++numButtons), widgetControllerNumber);

						} else if (name.equals("slider")) {
							mc.setName( "Slider " + (++numSliders) );

							Parameter[] parameters = cloneParameters(defaultParameters.get("slider"));
							/*
							for (int i=0; i<parameters.length; i++) {
								parameters[i].controllerNumber = autoControllerNumber++;
							}*/

							mc.setParameters( parameters );
							widget = new Slider(mc);//"Slider " + (++numSliders), widgetControllerNumber);

						} else if (name.equals("touchpad") || name.equals("xypad")) {
							mc.setName( "XY Pad " + (++numTouchPads) );

							Parameter[] parameters = cloneParameters(defaultParameters.get("xypad"));
							/*
							for (int i=0; i<parameters.length; i++) {
								parameters[i].controllerNumber = autoControllerNumber++;
							}*/

							mc.setParameters( parameters );
							widget = new XYPad(mc);//"XY Pad " + (++numTouchPads), widgetControllerNumber);
						}
						else if (name.equals("accelerometer") 
								|| name.equals("orientation") 
								|| name.equals("magfield")
								|| name.equals("gyroscope")) {	//3-axis
							mc.setName( "Sensor " + name );// + " " + (++numTouchPads) );
 
							Parameter[] parameters = cloneParameters(defaultParameters.get("xypad"));

							mc.setParameters( parameters );
							widget = new SensorXYPad( mc );
							//widget = new XYPad("Sensor " + name, widgetControllerNumber);
						}
						else if (name.equals("light")
								|| name.equals("pressure")
								|| name.equals("proximity")
								|| name.equals("temperature")) {	//single value
							mc.setName( "Sensor " + name);// + " " + (++numSliders) );

							Parameter[] parameters = cloneParameters(defaultParameters.get("slider"));

							mc.setParameters( parameters );
							widget = new SensorSlider(mc);//"Slider " + (++numSliders), widgetControllerNumber);
						}
 
						if (widget != null) {
							widget.x = widgetX;
							widget.y = widgetY;
							widget.setSize(widgetWidth, widgetHeight);
							wc.addSprite(widget);

							// Load any XML parameters
							updateParameters(mc.getParameters(), widgetElement);
							System.out.println(mc);

						}
					}
				}

				mainContainer.addSprite( wc );

			}
		}

	}
	
	// 
	/**
	 * Overrides the default parameters for any controller.
	 * 
	 */
	protected static void updateParameters(Parameter[] midiControllerParameters, Element widgetElement) {

		//ArrayList<Parameter> parsedParameters = new ArrayList<Parameter>();
		NodeList parameters = widgetElement.getChildNodes();

		// base values can be set on the widget element itself.
		int baseChannel = getIntegerAttribute(widgetElement, "channel", -1);
		int baseControllerNumber = getIntegerAttribute(widgetElement, "controllerNumber", -1);

		for (int id=0; id<midiControllerParameters.length; id++) {
			Parameter parameter = midiControllerParameters[ id ];
			if (baseChannel != -1)
				parameter.channel = baseChannel;
			if (baseControllerNumber != -1)
				parameter.controllerNumber = baseControllerNumber + id;
		}


		final int length = parameters.getLength();
		for (int e = 0; e < length; e++) {

			if (parameters.item(e).getNodeType() == Node.ELEMENT_NODE) {

				Element parameterElement = (Element) parameters.item(e);
				String name = parameterElement.getNodeName();

				if (name.equals("parameter")) {

					int parameterId = getIntegerAttribute(parameterElement, "id", -1);

					if (parameterId == -1) {
						System.out.println("unknown id");
						continue;
					}

					int channel = getIntegerAttribute(parameterElement, "channel", -1);
					int controllerNumber = getIntegerAttribute(parameterElement, "controllerNumber", -1);
					String parameterName = getStringAttribute(parameterElement, "name", null);
					String parameterTypeText = getStringAttribute(parameterElement, "type", null);

					Parameter parameter = midiControllerParameters[parameterId];

					//if (parameterId != -1)
						//parameter.i = controllerNumber;

					if (parameterName != null)
						parameter.name = parameterName;

					if (channel != -1)
						parameter.channel = channel;

					if (controllerNumber != -1)
						parameter.controllerNumber = controllerNumber;

					if (parameterTypeText != null) {
						if ("controlChange".equals(parameterTypeText)) {
							parameter.type = Parameter.TYPE_CONTROL_CHANGE;
						} else if ("note".equals(parameterTypeText)) {
							parameter.type = Parameter.TYPE_NOTE;
						}
					}
					
				}
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

	protected static String getStringAttribute(Element element, String attributeName, String defaultValue) {
		String result = defaultValue;

		if (element.hasAttribute(attributeName))
			result = element.getAttribute(attributeName);
		if (result == null)
			result = defaultValue;

		return result;
	}

	// TODO Create a ParameterList class wrapping Parameter[].  
	private static Parameter[] cloneParameters(Parameter[] parameters) {
		Parameter[] newParameters = new Parameter[parameters.length];
		for (int i=0; i<parameters.length; i++) {
			newParameters[i] = parameters[i].clone();
		}
		return newParameters;
	}
}
