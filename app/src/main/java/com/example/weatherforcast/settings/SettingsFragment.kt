package com.example.weatherforcast.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
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
        (activity as AppCompatActivity?)?.supportActionBar?.show()


        initializeSpinners()
        setSpinnerListeners()

        return binding.root
    }

    private fun initializeSpinners() {
        setupSpinner(
            binding.tempSpinner,
            R.array.temperature_unit,
            preferencesManager.getTemperatureUnit() ?: "Celsius"
        )
        setupSpinner(
            binding.windSpeedSpinner,
            R.array.wind_speed,
            preferencesManager.getWindSpeedUnit() ?: "Km/h"
        )
        setupSpinner(
            binding.languageSpinner,
            R.array.language_arr,
            getLanguageDisplayName(
                preferencesManager.getSelectedOption(
                    PreferencesManager.KEY_LANGUAGE,
                    "en"
                )
            )
        )
        setupSpinner(
            binding.locationSpinner,
            R.array.location,
            preferencesManager.getSavedLocation() ?: "Gps"
        )
    }

    private fun setupSpinner(spinner: Spinner, arrayResId: Int, selectedValue: String) {
        val items = resources.getStringArray(arrayResId)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items)
        spinner.adapter = adapter
        val selectedIndex = items.indexOf(selectedValue)
        if (selectedIndex >= 0) {
            spinner.setSelection(selectedIndex)
        }
    }

    private fun setSpinnerListeners() {
        binding.languageSpinner.onItemSelectedListener =
            createOnItemSelectedListener { selectedLanguage ->
                val languageCode = getLanguageCode(selectedLanguage)
                if (preferencesManager.getSelectedOption(
                        PreferencesManager.KEY_LANGUAGE,
                        "en"
                    ) != languageCode
                ) {
                    preferencesManager.saveSelectedOption(
                        PreferencesManager.KEY_LANGUAGE,
                        languageCode
                    )
                    updateLanguage(languageCode)
                }
            }

        binding.locationSpinner.onItemSelectedListener = createOnItemSelectedListener { selectedLocation ->
            preferencesManager.saveLocationOption(selectedLocation)

            if (selectedLocation == "Map" || selectedLocation == "خريطة") {
                if (!preferencesManager.isMapSelected()) {
                    preferencesManager.saveMapSelected(true)
                    Navigation.findNavController(requireView())
                        .navigate(R.id.action_settingsFragment_to_mapsFragment)
                }
            } else {
                preferencesManager.saveMapSelected(false)
            }
        }



        binding.tempSpinner.onItemSelectedListener =
            createOnItemSelectedListener { selectedTemperatureUnit ->
                preferencesManager.saveTemperatureUnit(selectedTemperatureUnit)
            }

        binding.windSpeedSpinner.onItemSelectedListener =
            createOnItemSelectedListener { selectedWindSpeed ->
                preferencesManager.saveWindSpeedUnit(selectedWindSpeed)
            }
    }

    private fun createOnItemSelectedListener(action: (String) -> Unit): AdapterView.OnItemSelectedListener {
        return object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedValue = parent.getItemAtPosition(position).toString()
                action(selectedValue)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun updateLanguage(language: String) {
        Locale.setDefault(Locale(language))
        val config = resources.configuration
        config.setLocale(Locale(language))
        resources.updateConfiguration(config, resources.displayMetrics)
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(language))
    }

    private fun getLanguageCode(displayName: String): String {
        return when (displayName) {
            "English" -> "en"
            "Arabic" -> "ar"
            else -> "en"
        }
    }

    private fun getLanguageDisplayName(languageCode: String?): String {
        return if (languageCode == "ar") "Arabic" else "English"
    }
}
