package com.spark.settings.fragments;

import android.database.ContentObserver;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.PreferenceScreen;
import com.android.internal.logging.nano.MetricsProto;
import com.android.internal.util.spark.SparkUtils;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.search.SearchIndexable;
import com.android.settings.R;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.Utils;
import com.spark.settings.preferences.SystemSettingEditTextPreference;
import com.spark.support.preferences.SystemSettingListPreference;
import com.spark.support.preferences.SystemSettingMasterSwitchPreference;
import com.spark.support.preferences.SystemSettingSwitchPreference;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

@SearchIndexable(forTarget = SearchIndexable.ALL & ~SearchIndexable.ARC)
public class ThemeSettings extends DashboardFragment implements
        OnPreferenceChangeListener {

    public static final String TAG = "ThemeSettings";

    private static final String ALT_SETTINGS_LAYOUT = "alt_settings_layout";
    private static final String SETTINGS_DASHBOARD_STYLE = "settings_dashboard_style";
    private static final String USE_STOCK_LAYOUT = "use_stock_layout";
    private static final String DISABLE_USERCARD = "disable_usercard";

    private SystemSettingEditTextPreference mFooterString;
    private SystemSettingListPreference mSettingsDashBoardStyle;
    private SystemSettingSwitchPreference mAltSettingsLayout;
    private SystemSettingSwitchPreference mUseStockLayout;
    private SystemSettingSwitchPreference mDisableUserCard;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);


        final ContentResolver resolver = getActivity().getContentResolver();

        PreferenceScreen prefSet = getPreferenceScreen();
        mSettingsDashBoardStyle = (SystemSettingListPreference) findPreference(SETTINGS_DASHBOARD_STYLE);
        mSettingsDashBoardStyle.setOnPreferenceChangeListener(this);
        mAltSettingsLayout = (SystemSettingSwitchPreference) findPreference(ALT_SETTINGS_LAYOUT);
        mAltSettingsLayout.setOnPreferenceChangeListener(this);
        mUseStockLayout = (SystemSettingSwitchPreference) findPreference(USE_STOCK_LAYOUT);
        mUseStockLayout.setOnPreferenceChangeListener(this);
        mDisableUserCard = (SystemSettingSwitchPreference) findPreference(DISABLE_USERCARD);
        mDisableUserCard.setOnPreferenceChangeListener(this);
    }

    @Override
    protected int getPreferenceScreenResId() {
        return R.xml.spark_settings_themes;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mSettingsDashBoardStyle) {
            SparkUtils.showSettingsRestartDialog(getContext());
            return true;
        } else if (preference == mAltSettingsLayout) {
            SparkUtils.showSettingsRestartDialog(getContext());
            return true;
        } else if (preference == mUseStockLayout) {
            SparkUtils.showSettingsRestartDialog(getContext());
            return true;
        } else if (preference == mDisableUserCard) {
            SparkUtils.showSettingsRestartDialog(getContext());
            return true;
        }
        return false;
    }

    @Override
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, getSettingsLifecycle(), this);
    }

    private static List<AbstractPreferenceController> buildPreferenceControllers(
            Context context, Lifecycle lifecycle, Fragment fragment) {
        final List<AbstractPreferenceController> controllers = new ArrayList<>();
        return controllers;
    }

    @Override
    protected String getLogTag() {
        return TAG;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.SPARK_SETTINGS;
    }

    /**
     * For Search.
     */
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.xml.spark_settings_themes);
}
