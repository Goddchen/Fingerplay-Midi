package com.flat20.gui;

import com.flat20.fingerplay.R;
import com.flat20.gui.textures.NineSliceMaterial;
import com.flat20.gui.textures.ResourceTexture;
import com.flat20.gui.textures.StretchedMaterial;
import com.flat20.gui.textures.Texture;
import com.flat20.gui.textures.TextureManager;
import com.flat20.gui.textures.TiledMaterial;

public class Materials {

	// Logo
	final public static Texture LOGO_TEXTURE = TextureManager.createResourceTexture(R.drawable.fingerplay_background_darker, 512, 512);
	
	// NavigationBar
	final private static Texture NAVIGATION_BAR_TEXTURE = TextureManager.createResourceTexture(R.drawable.navigation_bar, 32, 64);
	final public static NineSliceMaterial NAVIGATION_BAR = new NineSliceMaterial(NAVIGATION_BAR_TEXTURE, 0, 4, 28, 32,  0, 6, 10, 16);
	final public static NineSliceMaterial NAVIGATION_SCROLLER_BACKGROUND = new NineSliceMaterial(NAVIGATION_BAR_TEXTURE, 0, 4, 28, 32,  16, 22, 26, 32);
	final public static NineSliceMaterial NAVIGATION_SCROLLER_THUMB = new NineSliceMaterial(NAVIGATION_BAR_TEXTURE, 0, 4, 28, 32,  32, 38, 42, 48);
	final public static NineSliceMaterial NAVIGATION_SCROLLER_THUMB_HIGHLIGHT = new NineSliceMaterial(NAVIGATION_BAR_TEXTURE, 0, 4, 28, 32,  48, 54, 58, 64);

	// FPButton
	final private static ResourceTexture BUTTON_TEXTURE = TextureManager.createResourceTexture(R.drawable.buttons_ps, 64, 64);
	final public static NineSliceMaterial BUTTON = new NineSliceMaterial(BUTTON_TEXTURE, 0, 4, 28, 32,  0, 6, 10, 16);
	final public static NineSliceMaterial BUTTON_HIGHLIGHT = new NineSliceMaterial(BUTTON_TEXTURE, 0, 4, 28, 32,  16, 22, 26, 32);
	final public static NineSliceMaterial BUTTON_GREY = new NineSliceMaterial(BUTTON_TEXTURE, 0, 4, 28, 32,  32, 36, 44, 48);
	final public static NineSliceMaterial BUTTON_GREY_HIGHLIGHT = new NineSliceMaterial(BUTTON_TEXTURE, 0, 4, 28, 32,  48, 52, 60, 64);

	final public static StretchedMaterial BUTTON_ICON_SETTINGS = new StretchedMaterial(BUTTON_TEXTURE, 32, 0, 64, 32);
	final public static StretchedMaterial BUTTON_ICON_RELEASE_ALL = new StretchedMaterial(BUTTON_TEXTURE, 32, 32, 64, 64);

	// MidiWidgets

	// Contains the red fill colour for Sliders, circle for XYPad and the "tv scanlines".
	final private static ResourceTexture MC_DEFAULT_TEXTURE = TextureManager.createResourceTexture(R.drawable.controllers_default, 64, 64);

	// Shadow outline and dark background 
	final public static NineSliceMaterial MC_BACKGROUND = new NineSliceMaterial(MC_DEFAULT_TEXTURE, 0,7,24,32, 32,32+7,32+24,32+32);

	// Red Slider fill colour.
	final public static StretchedMaterial MC_INDICATOR = new StretchedMaterial(MC_DEFAULT_TEXTURE, 1, 1, 15, 15);
	final public static StretchedMaterial MC_INDICATOR_OFF = new StretchedMaterial(MC_DEFAULT_TEXTURE, 1, 17, 7, 31);

	// XYPad circle thumb
	final public static StretchedMaterial MC_XYPAD_INDICATOR = new StretchedMaterial(MC_DEFAULT_TEXTURE, 32, 0, 64, 32);
	final public static StretchedMaterial MC_XYPAD_INDICATOR_OFF = new StretchedMaterial(MC_DEFAULT_TEXTURE, 32, 32, 64, 64);

	// Outline
	final private static ResourceTexture MC_OUTLINE_TEXTURE = TextureManager.createResourceTexture(R.drawable.controllers_outlines, 64, 256);
	final public static NineSliceMaterial MC_OUTLINE = new NineSliceMaterial(MC_OUTLINE_TEXTURE, 0,9,52,58,  0,6,58,64);
	final public static NineSliceMaterial MC_OUTLINE_SELECTED = new NineSliceMaterial(MC_OUTLINE_TEXTURE, 0,16,48,64,	65,75,124,134);


	final private static ResourceTexture MC_TVSCANLINES_TEXTURE = TextureManager.createResourceTexture(R.drawable.controllers_tv_scanlines, 4, 4);
	final public static TiledMaterial MC_TVSCANLINES = new TiledMaterial(MC_TVSCANLINES_TEXTURE);

}
