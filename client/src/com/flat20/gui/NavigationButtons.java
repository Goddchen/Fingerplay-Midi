package com.flat20.gui;

import com.flat20.fingerplay.R;
import com.flat20.gui.sprites.MaterialSprite;
import com.flat20.gui.textures.NineSliceMaterial;
import com.flat20.gui.textures.Texture;
import com.flat20.gui.textures.TextureManager;
import com.flat20.gui.widgets.Button;
import com.flat20.gui.widgets.FPButton;
import com.flat20.gui.widgets.WidgetContainer;

public class NavigationButtons extends WidgetContainer {

	final private static Texture BACKGROUND_TEXTURE = TextureManager.createResourceTexture(R.drawable.navigation_bar, 32, 16);
	final private static NineSliceMaterial BACKGROUND_MATERIAL = new NineSliceMaterial(BACKGROUND_TEXTURE, 4, 4, 28, 12);

	final private IListener mListener;

	private FPButton mButtonSettings;
	private FPButton mButtonReleaseAll;
	private FPButton mButtonSliders;
	private FPButton mButtonXYPad;
	private FPButton mButtonPads;

	// Which screen is active.
	private FPButton mButtonActive;

	public NavigationButtons(int width, int height, IListener listener) {
		super(width, height);

		mListener = listener;

		MaterialSprite buttonBar = new MaterialSprite(BACKGROUND_MATERIAL, width, height);
        //GLSprite buttonBar = new GLSprite(R.drawable.navigation_bar);
        //buttonBar.width = width;
        //buttonBar.height = height;
		/*
        int[] textureCoords = {                   
        		0, 4, 28, 32,
                0, 4, 12, 16};
        buttonBar.setGrid( Grid.create9SliceGrid((int)buttonBar.width, (int)buttonBar.height, textureCoords, 32, 16) );
        */

        // Maximum button size is 48 for now.
        int buttonHeight = Math.min( (height-8-8)/3, 48);

        // bottom of screen
        mButtonXYPad = new FPButton(R.drawable.icon_touchpad_new, width-16, buttonHeight);
        mButtonXYPad.x = 8;
        mButtonXYPad.y = 8;
        mButtonXYPad.setListener(mButtonXYPadListener);

        mButtonSliders = new FPButton(R.drawable.icon_sliders, width-16, buttonHeight);
        mButtonSliders.x = 8;
        mButtonSliders.y = 8+buttonHeight+8;
        mButtonSliders.setListener(mButtonSlidersListener);

        mButtonPads = new FPButton(R.drawable.icon_touchpad, width-16, buttonHeight);
        mButtonPads.x = 8;
        mButtonPads.y = 8+buttonHeight+8+buttonHeight+8;
        mButtonPads.setListener(mButtonPadsListener);

        mButtonReleaseAll = new FPButton(R.drawable.icon_controller_held, width-16, buttonHeight);
        mButtonReleaseAll.x = 8;
        mButtonReleaseAll.y = height-8-buttonHeight-8-buttonHeight;
        mButtonReleaseAll.setListener(mButtonReleaseAllListener);

        mButtonSettings = new FPButton(R.drawable.icon_settings, width-16, buttonHeight);
        mButtonSettings.x = 8;
        mButtonSettings.y = height-8-buttonHeight;
        mButtonSettings.setListener(mButtonSettingsListener);

        // top of screen

        addSprite(buttonBar);
        addSprite(mButtonSettings);
        addSprite(mButtonReleaseAll);
        addSprite(mButtonSliders);
        addSprite(mButtonXYPad);
        addSprite(mButtonPads);
        //setFixed(true);

        mButtonActive = mButtonXYPad;
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

	Button.IListener mButtonPadsListener = new Button.IListener() {
		public void onClick(Button button) {
			mButtonActive.setActive(false);
			mButtonActive = mButtonPads;
			mButtonActive.setActive(true);

			mListener.onPadsSelected();
		}
		public void onPress(Button button) {}
		public void onRelease(Button button) {}
	};

	Button.IListener mButtonSlidersListener = new Button.IListener() {
		public void onClick(Button button) {
			mButtonActive.setActive(false);
			mButtonActive = mButtonSliders;
			mButtonActive.setActive(true);

			mListener.onSlidersSelected();
		}
		public void onPress(Button button) {}
		public void onRelease(Button button) {}
	};

	Button.IListener mButtonXYPadListener = new Button.IListener() {
		public void onClick(Button button) {
			mButtonActive.setActive(false);
			mButtonActive = mButtonXYPad;
			mButtonActive.setActive(true);

			mListener.onXYPadSelected();
		}
		public void onPress(Button button) {}
		public void onRelease(Button button) {}
	};

	public interface IListener {
		public void onSettingsSelected();
		public void onReleaseAllSelected();
		public void onPadsSelected();
		public void onSlidersSelected();
		public void onXYPadSelected();
	}
}
