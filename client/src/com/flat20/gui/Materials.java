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
	final private static Texture NAVIGATION_BAR_TEXTURE = TextureManager.createResourceTexture(R.drawable.navigation_bar, 32, 48);
	final public static NineSliceMaterial NAVIGATION_BAR = new NineSliceMaterial(NAVIGATION_BAR_TEXTURE, 0, 4, 28, 32,  0, 6, 10, 16);
	final public static NineSliceMaterial NAVIGATION_SCROLLER_BACKGROUND = new NineSliceMaterial(NAVIGATION_BAR_TEXTURE, 0, 4, 28, 32,  16, 22, 26, 32);
	final public static NineSliceMaterial NAVIGATION_SCROLLER_THUMB = new NineSliceMaterial(NAVIGATION_BAR_TEXTURE, 0, 4, 28, 32,  32, 38, 42, 48);

	// FPButton
	final private static ResourceTexture BUTTON_TEXTURE = TextureManager.createResourceTexture(R.drawable.buttons_ps, 32, 64);
	final public static NineSliceMaterial BUTTON = new NineSliceMaterial(BUTTON_TEXTURE, 0, 4, 28, 32,  0, 6, 10, 16);
	final public static NineSliceMaterial BUTTON_HIGHLIGHT = new NineSliceMaterial(BUTTON_TEXTURE, 0, 4, 28, 32,  16, 22, 26, 32);
	final public static NineSliceMaterial BUTTON_GREY = new NineSliceMaterial(BUTTON_TEXTURE, 0, 4, 28, 32,  32, 38, 42, 48);

	// MidiWidgets
	final private static ResourceTexture SHADOW_TEXTURE = TextureManager.createResourceTexture(R.drawable.dropshadow_50, 32, 32);
	final public static NineSliceMaterial SHADOW = new NineSliceMaterial(SHADOW_TEXTURE, 12, 12, 20, 20);

	final private static ResourceTexture MC_OUTLINE_TEXTURE = TextureManager.createResourceTexture(R.drawable.controllers_outlines, 64, 256);
	final public static NineSliceMaterial MC_OUTLINE = new NineSliceMaterial(MC_OUTLINE_TEXTURE, 0,9,52,58,  0,6,58,64);
	final public static NineSliceMaterial MC_OUTLINE_SELECTED = new NineSliceMaterial(MC_OUTLINE_TEXTURE, 0,16,48,64,	65,75,124,134);

	final private static ResourceTexture MC_BACKGROUND_TEXTURE = TextureManager.createResourceTexture(R.drawable.controllers_background, 4, 4);
	final public static TiledMaterial MC_BACKGROUND = new TiledMaterial(MC_BACKGROUND_TEXTURE);

	final private static ResourceTexture MC_TVSCANLINES_TEXTURE = TextureManager.createResourceTexture(R.drawable.controllers_tv_scanlines, 4, 4);
	final public static TiledMaterial MC_TVSCANLINES = new TiledMaterial(MC_TVSCANLINES_TEXTURE);

	final private static ResourceTexture MC_METER_TEXTURE = TextureManager.createResourceTexture(R.drawable.controllers_meter, 4, 4);
	final public static StretchedMaterial MC_METER = new StretchedMaterial(MC_METER_TEXTURE);

	final private static ResourceTexture MC_METER_OFF_TEXTURE = TextureManager.createResourceTexture(R.drawable.controllers_meter_off, 4, 4);
	final public static StretchedMaterial MC_METER_OFF = new StretchedMaterial(MC_METER_OFF_TEXTURE);

	final private static ResourceTexture MC_XYPAD_INDICATOR_TEXTURE = TextureManager.createResourceTexture(R.drawable.touchpad_meter, 32, 32);
	final public static StretchedMaterial MC_XYPAD_INDICATOR = new StretchedMaterial(MC_XYPAD_INDICATOR_TEXTURE);
	final private static ResourceTexture MC_XYPAD_INDICATOR_OFF_TEXTURE = TextureManager.createResourceTexture(R.drawable.touchpad_meter_off, 32, 32);
	final public static StretchedMaterial MC_XYPAD_INDICATOR_OFF = new StretchedMaterial(MC_XYPAD_INDICATOR_OFF_TEXTURE);

}
