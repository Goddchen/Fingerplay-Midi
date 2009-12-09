package com.flat20.fingerplay;

import java.io.File;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.flat20.fingerplay.midicontrollers.MidiControllerManager;
import com.flat20.fingerplay.network.ConnectionManager;
import com.flat20.fingerplay.settings.Settings;
import com.flat20.fingerplay.settings.SettingsModel;
import com.flat20.gui.InteractiveActivity;
import com.flat20.gui.NavigationButtons;
import com.flat20.gui.animations.AnimationManager;
import com.flat20.gui.animations.Slide;
import com.flat20.gui.sprites.Logo;
import com.flat20.gui.widgets.MidiWidgetContainer;
import com.flat20.gui.LayoutManager;

public class FingerPlayActivity extends InteractiveActivity {

	private SettingsModel mSettingsModel;

    private MidiControllerManager mMidiControllerManager;

    private MidiWidgetContainer mMidiWidgetsContainer;
    
    private Logo mLogo;

    private Slide mWidgetsSlide = null;
    //private Slide mLogoSlide = null;

    private NavigationButtons mNavigationButtons; 

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

    	// Init needs to be done first!
		mSettingsModel = SettingsModel.getInstance();
		mSettingsModel.init(this);

		mMidiControllerManager = MidiControllerManager.getInstance();        

		super.onCreate(savedInstanceState);

        Runtime r = Runtime.getRuntime();
        r.gc();

        Toast info = Toast.makeText(this, "Go to http://thesundancekid.net/ for help.", Toast.LENGTH_LONG);
        info.show();
    }



	@Override
	protected void onCreateGraphics() {

		// Draw the FingerPlay logo as our background.
		mLogo = new Logo();
		mLogo.x = 0;
		mLogo.y = -24;
		mRenderer.addSprite(mLogo);


		// Navigation
        mNavigationButtons = new NavigationButtons(64, mHeight-16, mNavigationListener);
        mNavigationButtons.x = mWidth - mNavigationButtons.width+2;
        mNavigationButtons.y = 8;//dm.heightPixels/2 - navigationScreen.height/2;


        // We're drawing all controller screens in their own container so we can move them
        // separately from the navigation and the background.
        // MidiWidgetContainer calculates its height depending on the content added.
        mMidiWidgetsContainer = new MidiWidgetContainer(mWidth, mHeight);

        // TODO Make LayoutManager part of GUI lib
        //File xmlFile = new File(Environment.getExternalStorageDirectory() + "/FingerPlayMIDI/layout.xml");
        File xmlFile = new File(Environment.getExternalStorageDirectory() + "/FingerPlayMIDI/" + mSettingsModel.layoutFile);
        if (xmlFile != null && xmlFile.canRead())
        	LayoutManager.loadXML(mMidiWidgetsContainer, xmlFile, mWidth, mHeight);
        else
        	LayoutManager.loadXML(mMidiWidgetsContainer,  getApplicationContext().getResources().openRawResource(R.raw.layout_default), mWidth, mHeight);

        // Add all midi controllers to the manager
        mMidiControllerManager.addMidiControllersIn(mMidiWidgetsContainer);

        mRenderer.addSprite( mMidiWidgetsContainer );


        // Navigation goes on top.
        mRenderer.addSprite( mNavigationButtons );
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		mMidiWidgetsContainer.onKeyDown(keyCode, event);
		return super.onKeyDown(keyCode, event);
	}


	NavigationButtons.IListener mNavigationListener = new NavigationButtons.IListener() {

		@Override
		public void onReleaseAllSelected() {
			mMidiControllerManager.releaseAllHeld();
		}

		@Override
		public void onPadsSelected() {
			AnimationManager.getInstance().remove(mWidgetsSlide);
			mWidgetsSlide = new Slide(mMidiWidgetsContainer, 0, -640);
			AnimationManager.getInstance().add( mWidgetsSlide );

			//mNavigationButtons.setActiveScreen();
			//Slide moveBackground = new Slide(mLogo, 0, -192);
			//AnimationManager.getInstance().add(moveBackground);
		}

		@Override
		public void onSlidersSelected() {
			AnimationManager.getInstance().remove(mWidgetsSlide);
			mWidgetsSlide = new Slide(mMidiWidgetsContainer, 0, -320);
			AnimationManager.getInstance().add( mWidgetsSlide ); 
			//Slide moveBackground = new Slide(mLogo, 0, -96);
			//AnimationManager.getInstance().add(moveBackground);
		}

		@Override
		public void onXYPadSelected() {
			AnimationManager.getInstance().remove(mWidgetsSlide);
			mWidgetsSlide = new Slide(mMidiWidgetsContainer, 0, 0);
			AnimationManager.getInstance().add( mWidgetsSlide );
			//Slide moveBackground = new Slide(mLogo, 0, 0);
			//AnimationManager.getInstance().add(moveBackground);
		}

		@Override
		public void onSettingsSelected() {
			Intent settingsIntent = new Intent(getApplicationContext(), Settings.class);
			settingsIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity( settingsIntent );
		}

	};

	@Override
	protected void onDestroy() {
    	//ConnectionManager.getInstance().disconnect();
    	ConnectionManager.getInstance().cleanup();
    	Log.i("fpa", "onDestroy! System.exit");
		super.onDestroy();

		System.runFinalizersOnExit(true);
		System.exit(0);
	}

}