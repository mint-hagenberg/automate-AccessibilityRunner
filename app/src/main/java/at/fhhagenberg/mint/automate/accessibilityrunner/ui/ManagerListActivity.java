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

package at.fhhagenberg.mint.automate.accessibilityrunner.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import at.fh.hagenberg.mint.automate.loggingclient.androidextension.fileexport.FileExportManager;
import at.fh.hagenberg.mint.automate.loggingclient.androidextension.fileexport.action.RequestFileExportIntentAction;
import at.fh.hagenberg.mint.automate.loggingclient.androidextension.fileexport.impl.CSVFileExportManager;
import at.fh.hagenberg.mint.automate.loggingclient.androidextension.kernel.AndroidKernel;
import at.fh.hagenberg.mint.automate.loggingclient.androidextension.userid.CredentialManager;
import at.fh.hagenberg.mint.automate.loggingclient.androidextension.util.KernelManagerHelper;
import at.fh.hagenberg.mint.automate.loggingclient.androidextension.util.PropertiesHelper;
import at.fhhagenberg.mint.automate.accessibilityrunner.R;
import at.fhhagenberg.mint.automate.accessibilityrunner.adapter.ManagerAdapter;
import at.fhhagenberg.mint.automate.android.accessibility.service.AutomateAccessibilityService;
import at.fhhagenberg.mint.automate.android.basemanager.deviceinfo.DeviceInfoManager;
import at.fhhagenberg.mint.automate.loggingclient.javacore.kernel.KernelBase;
import at.fhhagenberg.mint.automate.loggingclient.javacore.kernel.KernelListener;

/**
 * A simple activity that lists all registered managers with the possibility to disable or enable them.
 */
public class ManagerListActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private ManagerAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private TextView mDeviceIdText;

    private KernelListener mKernelListener = new KernelListener() {
        @Override
        public void startupFinished() {
            updateKernelStatus();
            updateUserId();
            mAdapter.initManagers();
            mAdapter.setInteractivityEnabled(true);
        }

        @Override
        public void onPrepareShutdown() {
            getSupportActionBar().setTitle("Status: shutting down");
        }

        @Override
        public void onShutdown() {
            updateKernelStatus();
            updateUserId();
            mAdapter.setInteractivityEnabled(false);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_list);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mDeviceIdText = (TextView) findViewById(R.id.deviceIdText);

        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new ManagerAdapter();
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!SetupDialogFragment.isInstalledAsAccessibilityService(this)) {
            SetupDialogFragment dialogFragment = new SetupDialogFragment();
            dialogFragment.show(getSupportFragmentManager(), "setup");
        }

        if (KernelBase.isInitialized()) {
            KernelBase.getKernel().addListener(mKernelListener);
        }
        updateKernelStatus();
        updateUserId();
        supportInvalidateOptionsMenu();

        if (KernelBase.isKernelUpRunning()) {
            mAdapter.initManagers();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (KernelBase.isInitialized()) {
            try {
                KernelBase.getKernel().removeListener(mKernelListener);
            } catch (Exception ex) {
                // Ignore
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_manager_list, menu);
        menu.findItem(R.id.action_start).setVisible(!KernelBase.isKernelUpRunning());
        menu.findItem(R.id.action_stop).setVisible(KernelBase.isKernelUpRunning());
        menu.findItem(R.id.action_export).setVisible(KernelBase.isKernelUpRunning() && PropertiesHelper.getProperty(this, "fileexport.enabled", Boolean.class, false));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_start: {
                setKernelDisabled(false);
                return true;
            }

            case R.id.action_stop: {
                setKernelDisabled(true);
                return true;
            }

            case R.id.action_export: {
                new RequestFileExportIntentAction().execute();
                return true;
            }

            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    private void setKernelDisabled(boolean disabled) {
        Intent intent = new Intent(AutomateAccessibilityService.ACTION_SET_KERNEL_DISABLED_STATE);
        intent.putExtra(AutomateAccessibilityService.EXTRA_KERNEL_DISABLED_VALUE, disabled);
        sendBroadcast(intent);
    }

    private void updateKernelStatus() {
        if (KernelBase.isKernelUpRunning()) {
            getSupportActionBar().setTitle("Status: running");
        } else {
            getSupportActionBar().setTitle("Status: stopped");
        }
        supportInvalidateOptionsMenu();
    }

    private void updateUserId() {
        CredentialManager credentialManager = (CredentialManager) KernelBase.getKernel().getManager(CredentialManager.ID);
        if (credentialManager != null) {
            mDeviceIdText.setText("Device id: " + credentialManager.getUserId());
        } else {
            mDeviceIdText.setText("Device id: unknown");
        }
    }
}
