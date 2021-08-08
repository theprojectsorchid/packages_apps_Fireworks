package com.spark.settings.fragments;

import com.android.internal.logging.nano.MetricsProto;

import android.os.Bundle;
import android.os.UserHandle;
import com.android.settings.development.OverlayCategoryPreferenceController;
import android.content.ContentResolver;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.content.om.IOverlayManager;
import android.content.res.Resources;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.PreferenceCategory;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.PreferenceFragment;
import android.provider.Settings;
import com.spark.support.preferences.SystemSettingListPreference;
import com.android.settings.R;
import com.android.settings.dashboard.DashboardFragment;
import android.content.Context;
import androidx.fragment.app.Fragment;
import android.view.View;
import android.os.UserHandle;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import android.util.Log;
import com.android.internal.util.spark.SparkUtils;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import org.json.JSONException;
import org.json.JSONObject;
import static android.os.UserHandle.USER_SYSTEM;
import android.os.RemoteException;
import android.os.ServiceManager;
import static android.os.UserHandle.USER_CURRENT;
import java.util.List;
import java.util.ArrayList;

public class ThemeSettings extends DashboardFragment implements
        OnPreferenceChangeListener {

    public static final String TAG = "ThemeSettings";
    private Context mContext;

    private static final String CUSTOM_CLOCK_FACE = Settings.Secure.LOCK_SCREEN_CUSTOM_CLOCK_FACE;
    private static final String DEFAULT_CLOCK = "com.android.keyguard.clock.DefaultClockController";
    private static final String QS_CLOCK_PICKER = "qs_clock_picker";
    private static final String SETTINGS_DASHBOARD_STYLE = "settings_dashboard_style";

    private ListPreference mLockClockStyles;
    private SystemSettingListPreference mQsClockPicker;
    private SystemSettingListPreference mSettingsDashBoardStyle;

    private IOverlayManager mOverlayManager;
    private IOverlayManager mOverlayService;
    private Handler mHandler;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        final ContentResolver resolver = getActivity().getContentResolver();

        PreferenceScreen prefSet = getPreferenceScreen();
        mOverlayManager = IOverlayManager.Stub.asInterface(
                ServiceManager.getService(Context.OVERLAY_SERVICE));
        mOverlayService = IOverlayManager.Stub
                .asInterface(ServiceManager.getService(Context.OVERLAY_SERVICE));
        mLockClockStyles = (ListPreference) findPreference(CUSTOM_CLOCK_FACE);
        String mLockClockStylesValue = getLockScreenCustomClockFace();
        mLockClockStyles.setValue(mLockClockStylesValue);
        mLockClockStyles.setSummary(mLockClockStyles.getEntry());
        mLockClockStyles.setOnPreferenceChangeListener(this);

        mQsClockPicker = (SystemSettingListPreference) findPreference(QS_CLOCK_PICKER);
        mQsClockPicker.setOnPreferenceChangeListener(this);
        mCustomSettingsObserver.observe();

        mSettingsDashBoardStyle = (SystemSettingListPreference) findPreference(SETTINGS_DASHBOARD_STYLE);
        mSettingsDashBoardStyle.setOnPreferenceChangeListener(this);

    }

    @Override
    protected int getPreferenceScreenResId() {
        return R.xml.spark_settings_themes;
    }

    private CustomSettingsObserver mCustomSettingsObserver = new CustomSettingsObserver(mHandler);
    private class CustomSettingsObserver extends ContentObserver {

        CustomSettingsObserver(Handler handler) {
            super(handler);
        }

        void observe() {
            Context mContext = getContext();
            ContentResolver resolver = mContext.getContentResolver();
            resolver.registerContentObserver(Settings.System.getUriFor(
                    Settings.System.QS_CLOCK_PICKER ),
                    false, this, UserHandle.USER_ALL);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            if (uri.equals(Settings.System.getUriFor(Settings.System.QS_CLOCK_PICKER ))) {
                updateQsClock();
            }
        }
    }

    private void updateQsClock() {
        ContentResolver resolver = getActivity().getContentResolver();

        boolean AospClock = Settings.System.getIntForUser(getContext().getContentResolver(),
                Settings.System.QS_CLOCK_PICKER , 0, UserHandle.USER_CURRENT) == 5;
        boolean ColorOsClock = Settings.System.getIntForUser(getContext().getContentResolver(),
                Settings.System.QS_CLOCK_PICKER , 0, UserHandle.USER_CURRENT) == 6;

        if (AospClock) {
            updateQsClockPicker(mOverlayManager, "com.spark.qsclockoverlays.aosp");
        } else if (ColorOsClock) {
            updateQsClockPicker(mOverlayManager, "com.spark.qsclockoverlays.coloros");
        } else {
            setDefaultClock(mOverlayManager);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mLockClockStyles) {
            setLockScreenCustomClockFace((String) newValue);
            int index = mLockClockStyles.findIndexOfValue((String) newValue);
            mLockClockStyles.setSummary(mLockClockStyles.getEntries()[index]);
            return true;
        } else if (preference == mQsClockPicker) {
            int SelectedClock = Integer.valueOf((String) newValue);
            Settings.System.putInt(resolver, Settings.System.QS_CLOCK_PICKER, SelectedClock);
            mCustomSettingsObserver.observe();
            return true;
        } else if (preference == mSettingsDashBoardStyle) {
            SparkUtils.showSettingsRestartDialog(getContext());
            return true;
        }
        return false;
    }


    public static void setDefaultClock(IOverlayManager overlayManager) {
        for (int i = 0; i < CLOCKS.length; i++) {
            String clocks = CLOCKS[i];
            try {
                overlayManager.setEnabled(clocks, false, USER_SYSTEM);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public static void updateQsClockPicker(IOverlayManager overlayManager, String overlayName) {
        try {
            for (int i = 0; i < CLOCKS.length; i++) {
                String clocks = CLOCKS[i];
                try {
                    overlayManager.setEnabled(clocks, false, USER_SYSTEM);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            overlayManager.setEnabled(overlayName, true, USER_SYSTEM);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void handleOverlays(String packagename, Boolean state, IOverlayManager mOverlayManager) {
        try {
            mOverlayManager.setEnabled(packagename,
                    state, USER_SYSTEM);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static final String[] CLOCKS = {
        "com.spark.qsclockoverlays.aosp",
        "com.spark.qsclockoverlays.coloros",
    };

    @Override
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, getSettingsLifecycle(), this);
    }

    private static List<AbstractPreferenceController> buildPreferenceControllers(
            Context context, Lifecycle lifecycle, Fragment fragment) {
        final List<AbstractPreferenceController> controllers = new ArrayList<>();
        controllers.add(new OverlayCategoryPreferenceController(context,
                "android.theme.customization.font"));
        controllers.add(new OverlayCategoryPreferenceController(context,
                "android.theme.customization.icon_pack"));
        return controllers;
    }

    private String getLockScreenCustomClockFace() {
        mContext = getActivity();
        String value = Settings.Secure.getStringForUser(mContext.getContentResolver(),
                CUSTOM_CLOCK_FACE, USER_CURRENT);

        if (value == null || value.isEmpty()) value = DEFAULT_CLOCK;

        try {
            JSONObject json = new JSONObject(value);
            return json.getString("clock");
        } catch (JSONException ex) {
        }
        return value;
    }

    private void setLockScreenCustomClockFace(String value) {
        try {
            JSONObject json = new JSONObject();
            json.put("clock", value);
            Settings.Secure.putStringForUser(mContext.getContentResolver(), CUSTOM_CLOCK_FACE,
                    json.toString(), USER_CURRENT);
        } catch (JSONException ex) {
        }
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
