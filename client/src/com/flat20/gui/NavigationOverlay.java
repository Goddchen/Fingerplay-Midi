package com.flat20.gui;

import com.flat20.gui.sprites.MaterialSprite;
import com.flat20.gui.widgets.Button;
import com.flat20.gui.widgets.FPButton;
import com.flat20.gui.widgets.Scrollbar;
import com.flat20.gui.widgets.WidgetContainer;
import com.flat20.gui.widgets.Scrollbar.IScrollable;

public class NavigationOverlay extends WidgetContainer {

	final private IListener mListener;

	private FPButton mButtonSettings;

	private Scrollbar mScreenNavigator;

	public NavigationOverlay(int width, int height, IListener listener, IScrollable scrollTarget, WidgetContainer widgetContainer, int screenHeight) {
		super(width, height);

		mListener = listener;

		MaterialSprite buttonBar = new MaterialSprite(Materials.NAVIGATION_BAR, width, height);

        // Maximum button size is 48 for now.
		int buttonHeight = width - 8 - 8;
        mButtonSettings = new FPButton(Materials.BUTTON_ICON_SETTINGS, width-16, buttonHeight);
        mButtonSettings.x = 8;
        mButtonSettings.y = height-8-buttonHeight;
        mButtonSettings.setListener(mButtonSettingsListener);

        // NEW
        mScreenNavigator = new Scrollbar(width-16, height - 8-buttonHeight-8-8, scrollTarget, widgetContainer, screenHeight);
        mScreenNavigator.x = 8;
        mScreenNavigator.y = 8;

        // top of screen

        addSprite(buttonBar);
        addSprite(mButtonSettings);

        addSprite( mScreenNavigator );
	}



	// Handle button actions.

	Button.IListener mButtonSettingsListener = new Button.IListener() {
		public void onClick(Button button) {
			mListener.onSettingsSelected();
		}
		public void onPress(Button button) {}
		public void onRelease(Button button) {}
	};

	public interface IListener {
		public void onSettingsSelected();
	}
}
