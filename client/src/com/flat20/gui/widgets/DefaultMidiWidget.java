package com.flat20.gui.widgets;

import com.flat20.gui.Materials;
import com.flat20.gui.sprites.MaterialSprite;

/**
 * Awful! Need a way to skin widgets so we don't get caught in
 * these nested inheritance messes.
 * 
 * @author andreas.reuterberg
 *
 */
public abstract class DefaultMidiWidget extends MidiWidget {

	final protected static int SHADOW_PADDING = 6;

	final protected MaterialSprite mShadow;

	// We need a skinning class
	final protected MaterialSprite mBackground;
	final protected MaterialSprite mOutline;
	final protected MaterialSprite mOutlineSelected;
	final protected MaterialSprite mTvScanlines;
	
	public DefaultMidiWidget(String name) {
		super(name);

		mShadow = new MaterialSprite(Materials.SHADOW);
        mShadow.x = -SHADOW_PADDING;
        mShadow.y = -SHADOW_PADDING;
        addSprite( mShadow );

		mBackground = new MaterialSprite(Materials.MC_BACKGROUND);

		mOutline = new MaterialSprite(Materials.MC_OUTLINE);

		mOutlineSelected = new MaterialSprite(Materials.MC_OUTLINE_SELECTED);
		mOutlineSelected.visible = false;

		mTvScanlines = new MaterialSprite(Materials.MC_TVSCANLINES);

	}
	
	@Override
	public void setSize(int w, int h) {
		super.setSize(w, h);

		mShadow.setSize(w + (SHADOW_PADDING*2), h + (SHADOW_PADDING*2));

		mBackground.setSize(w-2, h-2);

		mOutline.setSize(w, h);

        mOutlineSelected.setSize(w+6, h+6);

		mTvScanlines.setSize(w-4, h-4);
	}

	@Override
	public void onFocusChanged(boolean focus) {
		super.onFocusChanged(focus);

		if (focus) {
			mOutline.visible = false;
			mOutlineSelected.visible = true;
		} else {
			mOutline.visible = true;
			mOutlineSelected.visible = false;
		}
	}

}
