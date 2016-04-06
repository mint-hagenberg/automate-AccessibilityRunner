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

package at.fhhagenberg.mint.automate.accessibilityrunner.adapter;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.fhhagenberg.mint.automate.accessibilityrunner.R;
import at.fhhagenberg.mint.automate.loggingclient.javacore.kernel.KernelBase;
import at.fhhagenberg.mint.automate.loggingclient.javacore.kernel.Manager;
import at.fhhagenberg.mint.automate.loggingclient.javacore.kernel.ManagerException;
import at.fhhagenberg.mint.automate.loggingclient.javacore.kernel.annotation.ExternalManager;

/**
 * Adapter to list all managers registered in the Kernel.
 */
public class ManagerAdapter extends RecyclerView.Adapter<ManagerAdapter.ViewHolder> {
    /**
     * View holder pattern from the recycler view.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener {
        public TextView mTextView;
        public SwitchCompat mCheckbox;

        public Manager mManager;

        public ViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.text);
            mCheckbox = (SwitchCompat) v.findViewById(R.id.checkbox);
            mCheckbox.setOnCheckedChangeListener(this);
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (mManager != null) {
                if (isChecked) {
                    try {
                        KernelBase.getKernel().enableManager(mManager.getId());
                    } catch (ManagerException e) {
                        // Ignore for now
                    }
                } else {
                    KernelBase.getKernel().disableManager(mManager.getId());
                }
            }
        }
    }

    private List<Manager> mManager;
    private boolean mInteractivityEnabled = true;

    /**
     * Constructor.
     */
    public ManagerAdapter() {
        mManager = new ArrayList<>();
        if (KernelBase.isInitialized()) {
            initManagers();
        }
    }

    /**
     * Allows to get the set managers from the kernel again and relpace the displayed list.
     */
    public void initManagers() {
        mManager.clear();
        for (int i = 0, len = KernelBase.getKernel().numManager(); i < len; ++i) {
            Manager manager = KernelBase.getKernel().getManager(i);
            if (manager.getClass().isAnnotationPresent(ExternalManager.class)) {
                mManager.add(manager);
            }
        }
        notifyDataSetChanged();
    }

    /**
     * Set the checkboxes to enabled or disabled.
     *
     * @param enabled -
     */
    public void setInteractivityEnabled(boolean enabled) {
        if (mInteractivityEnabled != enabled) {
            mInteractivityEnabled = enabled;
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return mManager.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem_manager, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mManager = mManager.get(position);
        holder.mTextView.setText(holder.mManager.getName());
        holder.mCheckbox.setChecked(holder.mManager.getStatus() == Manager.Status.STARTED);
        holder.mCheckbox.setVisibility(holder.mManager.getClass().getAnnotation(ExternalManager.class).allowsUserStatusChange() ? View.VISIBLE : View.GONE);
        holder.mCheckbox.setEnabled(mInteractivityEnabled);
    }
}
