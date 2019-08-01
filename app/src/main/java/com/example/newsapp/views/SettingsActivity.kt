package com.example.newsapp.views

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.ListPreference
import android.preference.Preference
import android.preference.PreferenceFragment
import android.preference.PreferenceManager
import com.example.newsapp.R

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
    }

    class ArticlesPreferenceFragment : PreferenceFragment(), Preference.OnPreferenceChangeListener {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.settings_main)

            val filterBy: Preference = findPreference(getString(R.string.settings_filter_by_key))
            bindPreferenceSummaryToValue(filterBy)

            val orderBy: Preference = findPreference(getString(R.string.settings_order_by_key))
            bindPreferenceSummaryToValue(orderBy)
        }

        override fun onPreferenceChange(preference: Preference, value: Any): Boolean {
            val stringValue: String = value.toString()
            if (preference is ListPreference) {
                val prefIndex: Int = preference.findIndexOfValue(stringValue)
                if (prefIndex >= 0) {
                    val labels = preference.entries
                    preference.setSummary(labels[prefIndex])
                }
            } else {
                preference.summary = stringValue
            }
            return true
        }

        private fun bindPreferenceSummaryToValue(preference: Preference) {
            preference.onPreferenceChangeListener = this
            val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(preference.context)
            val preferenceString: String? = preferences.getString(preference.key, "")
            onPreferenceChange(preference, preferenceString as Any)
        }
    }
}
