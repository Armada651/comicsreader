/*
 * ComicsReader is an Android application to read comics
 * Copyright (C) 2011-2012 Cedric OCHS
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package net.kervala.comicsreader;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

public class Overlay {
	private final class HideThread implements Runnable {
		public void run() {
			hide();
		}
	}

	protected TextView mView;
	protected HideThread mHideThread = new HideThread();
	protected WindowManager mWindowManager;
	protected WeakReference<Activity> mActivity;
	protected WeakReference<Handler> mHandler;
	protected boolean mAdded = false;
	
	public Overlay(Activity activity, Handler handler) {
		mActivity = new WeakReference<Activity>(activity);
		mHandler = new WeakReference<Handler>(handler);

		mWindowManager = (WindowManager)mActivity.get().getSystemService(Context.WINDOW_SERVICE);
		final LayoutInflater inflate = mActivity.get().getLayoutInflater();

		mView = (TextView) inflate.inflate(R.layout.overlay, null);
		mView.setVisibility(View.INVISIBLE);
	}
	
	public void add() {
		if (mWindowManager != null && mView != null && !mAdded) {
			// set basic layout parameters
			WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_APPLICATION,
				WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);

			// set position to top right corner
			lp.gravity = Gravity.TOP|Gravity.RIGHT;
					
			// add view to window
			mWindowManager.addView(mView, lp);
					
			mAdded = true;
		}
	}

	public void remove() {
		if (mWindowManager != null && mView != null && mAdded) {
			mHandler.get().removeCallbacks(mHideThread);
			mWindowManager.removeView(mView);
			mAdded = false;
		}
	}

	public void show(final String string, int duration) {
		if (mView == null) return;

		if (!mAdded) add();

		mHandler.get().removeCallbacks(mHideThread);
		
		// show overlay
		mView.setVisibility(View.VISIBLE);
		mView.setText(string);

		if (duration > 0) {
			// send a message to hide toast after 1 second
			mHandler.get().postDelayed(mHideThread, duration);
		}
	}

	public void hide() {
		if (mView == null) return;

		mView.setVisibility(View.INVISIBLE);

		mHandler.get().removeCallbacks(mHideThread);
	}
}