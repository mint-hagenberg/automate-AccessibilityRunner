/*
 *     Copyright (C) 2016 Research Group Mobile Interactive Systems
 *     Email: mint@fh-hagenberg.at, Website: http://mint.fh-hagenberg.at
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.fhhagenberg.mint.automate.accessibilityrunner.ui;


import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import at.fhhagenberg.mint.automate.accessibilityrunner.R;
import at.fhhagenberg.mint.automate.android.accessibility.service.AutomateAccessibilityService;

/**
 *
 */
public class SetupDialogFragment extends DialogFragment {
	public SetupDialogFragment() {
		// Required empty public constructor
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
				.setTitle(getActivity().getString(R.string.dialog_setup_title))
				.setMessage(getActivity().getString(R.string.dialog_setup_message))
				.setCancelable(false)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
						startActivity(intent);
					}
				})
				.setNegativeButton(getActivity().getString(R.string.dialog_setup_negative), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						System.exit(0);
					}
				});
		return builder.create();
	}

	/**
	 * @param context -
	 * @return -
	 */
	public static boolean isInstalledAsAccessibilityService(Context context) {
		try {
			ContentResolver contentResolver = context.getContentResolver();
			String automateService = context.getApplicationInfo().packageName + "/" + AutomateAccessibilityService.class.getName();
			return Settings.Secure.getInt(contentResolver, Settings.Secure.ACCESSIBILITY_ENABLED) == 1 && Settings.Secure.getString(contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES).contains(automateService);
		} catch (Settings.SettingNotFoundException e) {
			return false;
		}
	}
}
