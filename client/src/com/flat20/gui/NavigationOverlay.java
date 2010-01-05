package com.flat20.gui;

import com.flat20.fingerplay.R;
import com.flat20.gui.sprites.MaterialSprite;
import com.flat20.gui.widgets.Button;
import com.flat20.gui.widgets.FPButton;
import com.flat20.gui.widgets.Scrollbar;
import com.flat20.gui.widgets.WidgetContainer;
import com.flat20.gui.widgets.Scrollbar.IScrollable;

public class NavigationOverlay extends WidgetContainer {

	final private IListener mListener;

	private FPButton mButtonSettings;
	private FPButton mButtonReleaseAll;

	private Scrollbar mScreenNavigator;

	private FPButton mButtonActive;

	public NavigationOverlay(int width, int height, IListener listener, IScrollable scrollTarget, int screenHeight) {
		super(width, height);

		mListener = listener;

		MaterialSprite buttonBar = new MaterialSprite(Materials.NAVIGATION_BAR, width, height);

        // Maximum button size is 48 for now.
        int buttonHeight = Math.min( (height-8-8)/3, 48);

        mButtonReleaseAll = new FPButton(Materials.BUTTON_ICON_RELEASE_ALL, width-16, buttonHeight);
        mButtonReleaseAll.x = 8;
        mButtonReleaseAll.y = height-8-buttonHeight-8-buttonHeight;
        mButtonReleaseAll.setListener(mButtonReleaseAllListener);

        mButtonSettings = new FPButton(Materials.BUTTON_ICON_SETTINGS, width-16, buttonHeight);
        mButtonSettings.x = 8;
        mButtonSettings.y = height-8-buttonHeight;
        mButtonSettings.setListener(mButtonSettingsListener);

        // NEW
        mScreenNavigator = new Scrollbar(width-16, height - 8-buttonHeight-8-buttonHeight-8-8, scrollTarget, screenHeight);
        mScreenNavigator.x = 8;
        mScreenNavigator.y = 8;

        // top of screen

        addSprite(buttonBar);
        addSprite(mButtonSettings);
        addSprite(mButtonReleaseAll);

        addSprite( mScreenNavigator );

        mButtonActive = mButtonSettings;
        mButtonActive.setActive(true);
	}


	// Handle button actions.

	Button.IListener mButtonSettingsListener = new Button.IListener() {
		public void onClick(Button button) {
			mListener.onSettingsSelected();
		}
		public void onPress(Button button) {}
		public void onRelease(Button button) {}
	};

	Button.IListener mButtonReleaseAllListener = new Button.IListener() {
		public void onClick(Button button) {
			mListener.onReleaseAllSelected();
		}
		public void onPress(Button button) {}
		public void onRelease(Button button) {}
	};

	public interface IListener {
		public void onSettingsSelected();
		public void onReleaseAllSelected();
	}
}
