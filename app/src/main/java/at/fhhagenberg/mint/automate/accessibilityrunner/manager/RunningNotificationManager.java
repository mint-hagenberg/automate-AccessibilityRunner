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

package at.fhhagenberg.mint.automate.accessibilityrunner.manager;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import at.fh.hagenberg.mint.automate.loggingclient.androidextension.kernel.AndroidKernel;
import at.fhhagenberg.mint.automate.accessibilityrunner.R;
import at.fhhagenberg.mint.automate.accessibilityrunner.service.KernelListenerService;
import at.fhhagenberg.mint.automate.accessibilityrunner.ui.ManagerListActivity;
import at.fhhagenberg.mint.automate.loggingclient.javacore.kernel.AbstractManager;
import at.fhhagenberg.mint.automate.loggingclient.javacore.kernel.KernelListener;
import at.fhhagenberg.mint.automate.loggingclient.javacore.kernel.ManagerException;
import at.fhhagenberg.mint.automate.loggingclient.javacore.name.Id;

/**
 *
 */
public class RunningNotificationManager extends AbstractManager implements KernelListener {
	public static Id ID = new Id(RunningNotificationManager.class);

	private static final int NOTIFICATION_KERNEL_RUNNING = 1000;

	public RunningNotificationManager() {
	}

	@Override
	protected void doStart() throws ManagerException {
		super.doStart();

		getKernel().addListener(this);
	}

	@Override
	protected void doStop() {
		getKernel().removeListener(this);
		super.doStop();
	}

	@Override
	public Id getId() {
		return ID;
	}

	@Override
	public void startupFinished() {
		Context context = ((AndroidKernel) getKernel()).getContext();
		Intent clickIntent = new Intent(context, ManagerListActivity.class);
		PendingIntent clickPendingIntent = PendingIntent.getActivity(context, 0, clickIntent, 0);

		Intent stopIntent = new Intent(context, KernelListenerService.class);
		PendingIntent stopPendingIntent = PendingIntent.getService(context, 0, stopIntent, 0);

		Notification notification = new NotificationCompat.Builder(context)
				.setSmallIcon(R.drawable.ic_running_notification)
				.setContentTitle("automate Kernel is running")
				.setContentText("This means we're collecting data about app usage")
				.setOngoing(true)
				.setContentIntent(clickPendingIntent)
				.addAction(0, "Stop automate", stopPendingIntent)
				.build();
		NotificationManagerCompat.from(context)
				.notify(NOTIFICATION_KERNEL_RUNNING, notification);
	}

	@Override
	public void onPrepareShutdown() {
	}

	@Override
	public void onShutdown() {
		NotificationManagerCompat.from(((AndroidKernel) getKernel()).getContext())
				.cancel(NOTIFICATION_KERNEL_RUNNING);
	}
}
