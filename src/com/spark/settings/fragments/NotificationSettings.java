package com.spark.settings.fragments;

import com.android.internal.logging.nano.MetricsProto;

import android.os.Bundle;
import android.os.UserHandle;
import android.content.ContentResolver;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;
import com.spark.support.preferences.SystemSettingMasterSwitchPreference;
import com.android.settings.R;
import com.android.settings.dashboard.DashboardFragment;
import android.content.Context;
import androidx.fragment.app.Fragment;
import android.os.UserHandle;
import android.util.Log;

import static android.os.UserHandle.USER_CURRENT;

public class NotificationSettings extends DashboardFragment implements
        OnPreferenceChangeListener {

    public static final String TAG = "NotificationSettings";
    private Context mContext;

    private static final String KEY_EDGE_LIGHTNING = "pulse_ambient_light";

    private SystemSettingMasterSwitchPreference mEdgeLightning;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        final ContentResolver resolver = getActivity().getContentResolver();

        mEdgeLightning = (SystemSettingMasterSwitchPreference)
                findPreference(KEY_EDGE_LIGHTNING);
        boolean enabled = Settings.System.getIntForUser(resolver,
                KEY_EDGE_LIGHTNING, 0, UserHandle.USER_CURRENT) == 1;
        mEdgeLightning.setChecked(enabled);
        mEdgeLightning.setOnPreferenceChangeListener(this);

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mEdgeLightning) {
            boolean value = (Boolean) newValue;
            Settings.System.putIntForUser(resolver, KEY_EDGE_LIGHTNING,
                    value ? 1 : 0, UserHandle.USER_CURRENT);
            return true;
        }
        return false;
    }

    @Override
    protected int getPreferenceScreenResId() {
        return R.xml.spark_settings_notifications;
    }

    @Override
    protected String getLogTag() {
        return TAG;
    }
    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.SPARK_SETTINGS;
    }

}
