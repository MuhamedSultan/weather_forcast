package com.example.weatherforcast.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.fragment.app.Fragment
import com.example.weatherforcast.R
import com.example.weatherforcast.databinding.FragmentSettingsBinding
import com.example.weatherforcast.db.PreferencesManager
import java.util.Locale

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private lateinit var preferencesManager: PreferencesManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        preferencesManager = PreferencesManager(requireContext())
        setupSpinners()
        setupSpinnerListeners()
        return binding.root
    }

    private fun setupSpinnerListeners() {
        binding.languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedLanguage = parent.getItemAtPosition(position).toString()
                val languageCode = if (selectedLanguage == "English") "en" else "ar"

                val currentLanguage = preferencesManager.getSelectedOption(PreferencesManager.KEY_LANGUAGE, "en")
                if (currentLanguage != languageCode) {
                    preferencesManager.saveSelectedOption(PreferencesManager.KEY_LANGUAGE, languageCode)
                    updateLanguage(languageCode)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        binding.tempSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedTemperatureUnit = parent.getItemAtPosition(position).toString()
                preferencesManager.saveTemperatureUnit(selectedTemperatureUnit)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        binding.windSpeedSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedWindSpeed = parent.getItemAtPosition(position).toString()
                preferencesManager.saveWindSpeedUnit(selectedWindSpeed)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun setupSpinners() {
        // Temperature Spinner
        val tempUnit = preferencesManager.getTemperatureUnit()
        val temperatureUnits = resources.getStringArray(R.array.temperature_unit)
        val tempAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, temperatureUnits)
        binding.tempSpinner.adapter = tempAdapter

        val tempIndex = temperatureUnits.indexOf(tempUnit)
        if (tempIndex >= 0) {
            binding.tempSpinner.setSelection(tempIndex)
        }

        // Wind Speed Spinner
        val windSpeedUnit = preferencesManager.getWindSpeedUnit()
        val windSpeedUnits = resources.getStringArray(R.array.wind_speed)
        val windSpeedAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, windSpeedUnits)
        binding.windSpeedSpinner.adapter = windSpeedAdapter

        val windSpeedIndex = windSpeedUnits.indexOf(windSpeedUnit)
        if (windSpeedIndex >= 0) {
            binding.windSpeedSpinner.setSelection(windSpeedIndex)
        }

        val language = preferencesManager.getSelectedOption(PreferencesManager.KEY_LANGUAGE, "en") ?: "en"
        val languages = resources.getStringArray(R.array.language_arr)
        val languageAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, languages)
        binding.languageSpinner.adapter = languageAdapter

        val languageIndex = languages.indexOf(if (language == "en") "English" else "Arabic")
        if (languageIndex >= 0) {
            binding.languageSpinner.setSelection(languageIndex)
        }
    }

    private fun updateLanguage(language: String) {
        changeAppLanguage(language)
        AppCompatDelegate.setApplicationLocales(
            LocaleListCompat.forLanguageTags(language)
        )
    }

    private fun changeAppLanguage(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}
