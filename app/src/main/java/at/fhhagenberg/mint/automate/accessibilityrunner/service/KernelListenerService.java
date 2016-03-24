/*
 *     Copyright (C) 2016 Mobile Interactive Systems Research Group
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.fhhagenberg.mint.automate.accessibilityrunner.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import at.fhhagenberg.mint.automate.android.accessibility.service.AutomateAccessibilityService;


/**
 *
 */
public class KernelListenerService extends IntentService {
	private static final int NOTIFICATION_KERNEL_RUNNING = 1000;

	/**
	 *
	 */
	public static void startAction(Context context) {
		Intent intent = new Intent(context, KernelListenerService.class);
		context.startService(intent);
	}

	public KernelListenerService() {
		super("KernelListenerService");
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		AutomateAccessibilityService.disableKernel(this);
	}
}
