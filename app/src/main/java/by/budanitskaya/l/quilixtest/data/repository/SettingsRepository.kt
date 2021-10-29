package by.budanitskaya.l.quilixtest.data.repository

import android.content.Context
import android.content.SharedPreferences
import by.budanitskaya.l.quilixtest.R
import by.budanitskaya.l.quilixtest.presentation.models.CurrencyPresentationModel
import by.budanitskaya.l.quilixtest.presentation.models.SettingsModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext val context: Context,
    private val sharedPreferences: SharedPreferences
) {

    private val tempChangesHashMap = hashMapOf<String, Boolean>()
    lateinit var prefsCallback: PrefsCallback

    fun clearTemporaryStorage() {
        tempChangesHashMap.clear()
    }

    fun initializeApp() {
        if (!sharedPreferences.contains(KEY_APP_INITIALISED)) {
            setBoolean(KEY_APP_INITIALISED, true)
            context.resources.getStringArray(R.array.currency_char_codes).forEach {
                val item = it.split(", ")[0]
                if (item != USD && item != RUB && item != EUR) {
                    setBoolean(item, false)
                } else {
                    setBoolean(item, true)
                }
            }
        }
    }

    private fun setBoolean(key: String, value: Boolean) {
        with(sharedPreferences.edit()) {
            putBoolean(key, value)
            apply()
        }
        prefsCallback.onPrefsChanged()
    }

    private fun getBoolean(key: String): Boolean {
        return sharedPreferences.getBoolean(key, false)
    }

    fun getSettingsInfo(): List<SettingsModel> {
        val settingsList = mutableListOf<SettingsModel>()
        val charCodeList = context.resources.getStringArray(R.array.currency_char_codes)
        charCodeList.forEach {
            val charCode = it.split(", ")[0]
            val name = it.split(", ")[1]
            val isOn = getBoolean(charCode)
            settingsList.add(SettingsModel(charCode, name, isOn))
        }
        return settingsList
    }

    fun temporarilySave(charCode: String, isActive: Boolean) {
        tempChangesHashMap[charCode] = isActive
    }

    fun persistTemporaryChanges() {
        for ((key, value) in tempChangesHashMap) {
            setBoolean(key, value)
        }
    }

    fun tempApplyTemporaryChanges(model: SettingsModel): SettingsModel {
        for ((key, value) in tempChangesHashMap) {
            if (key == model.charCode) {
                model.isOn = value
                return model
            }
        }
        return model
    }

    fun applyPrefsToCurrencyList(currencyInitialList: List<CurrencyPresentationModel>)
            : List<CurrencyPresentationModel> {
        val mutableCurrencyList = currencyInitialList.toMutableList()
        mutableCurrencyList.removeIf { !getBoolean(it.charCode) }
        return mutableCurrencyList.toList()
    }

    companion object {
        const val KEY_APP_INITIALISED = "Is app initialized?"
        const val USD = "USD"
        const val EUR = "EUR"
        const val RUB = "RUB"
    }
}

interface PrefsCallback {
    fun onPrefsChanged()
}