package com.hyp.viewlibrary;

import android.app.Activity;
import android.content.Context;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.view.View;

/**
 * @author: hyp
 * @date: 2018-01-07
 */
public class ViewFinder {
    private View view;
    private Activity activity;
    private PreferenceGroup preferenceGroup;
    private PreferenceActivity preferenceActivity;

    public ViewFinder(View view) {
        this.view = view;
    }

    public ViewFinder(Activity activity) {
        this.activity = activity;
    }

    public ViewFinder(PreferenceGroup preferenceGroup) {
        this.preferenceGroup = preferenceGroup;
    }

    public ViewFinder(PreferenceActivity preferenceActivity) {
        this.preferenceActivity = preferenceActivity;
        this.activity = preferenceActivity;
    }

    public View findViewById(int id) {
        return this.activity == null ? this.view.findViewById(id) : this.activity.findViewById(id);
    }

    public View findViewByInfo(ViewInjectInfo info) {
        return this.findViewById((Integer) info.value, info.parentId);
    }

    public View findViewById(int id, int pid) {
        View pView = null;
        if (pid > 0) {
            pView = this.findViewById(pid);
        }

        View view = null;
        if (pView != null) {
            view = pView.findViewById(id);
        } else {
            view = this.findViewById(id);
        }

        return view;
    }

    public Preference findPreference(CharSequence key) {
        return this.preferenceGroup == null ? this.preferenceActivity.findPreference(key) : this.preferenceGroup.findPreference(key);
    }

    public Context getContext() {
        if (this.view != null) {
            return this.view.getContext();
        } else if (this.activity != null) {
            return this.activity;
        } else {
            return this.preferenceActivity;
        }
    }
}
