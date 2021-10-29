package by.budanitskaya.l.quilixtest.presentation.ui.currencyinfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.budanitskaya.l.quilixtest.data.network.models.CurrencyInfo
import by.budanitskaya.l.quilixtest.data.network.models.ResponseData
import by.budanitskaya.l.quilixtest.data.network.safeapicall.ResponseStatus
import by.budanitskaya.l.quilixtest.presentation.models.CurrencyPresentationModel
import by.budanitskaya.l.quilixtest.data.repository.CurrencyRepository
import by.budanitskaya.l.quilixtest.data.repository.PrefsInterface
import by.budanitskaya.l.quilixtest.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurrencyInfoViewModel @Inject constructor(
    var currencyRepository: CurrencyRepository,
    val settingsRepository: SettingsRepository
) :
    ViewModel(), PrefsInterface {

    private val _currencyDataList = MutableLiveData<List<CurrencyPresentationModel>>()
    val currencyDataList: LiveData<List<CurrencyPresentationModel>> = _currencyDataList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isMenuVisible = MutableLiveData<Unit>()
    val isMenuVisible: LiveData<Unit> = _isMenuVisible

    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _isError

    lateinit var currencyInitialList: List<CurrencyPresentationModel>

    init {
        fetchData()
        settingsRepository.prefsInterface = this
    }

    private fun fetchData() {
        _isLoading.value = true
        viewModelScope.launch {
            val currentDayData = fetchSpecificDateData("10/28/2021")
            val nextDayData = fetchSpecificDateData("10/29/2021")
            if (currentDayData !is ResponseStatus.Success<ResponseData> || nextDayData !is ResponseStatus.Success<ResponseData>) {
                _isError.value = true
                _isMenuVisible.value = Unit
                _isLoading.value = false
            } else {
                val currentDayList =
                    currentDayData.value.listCurrencyInfo ?: emptyList()
                val nextDayList = nextDayData.value.listCurrencyInfo ?: emptyList()
                _isLoading.value = false
                currencyInitialList = getDisplayedList(currentDayList, nextDayList)
                _currencyDataList.value = applyPrefsToCurrencyList(currencyInitialList)
            }
        }
    }

    private fun applyPrefsToCurrencyList(currencyInitialList: List<CurrencyPresentationModel>): List<CurrencyPresentationModel> {
        val mutableCurrencyList = currencyInitialList.toMutableList()
        mutableCurrencyList.removeIf { !settingsRepository.getBoolean(it.charCode) }
        return mutableCurrencyList.toList()
    }


    private fun getDisplayedList(
        currentDayList: List<CurrencyInfo>,
        nextDayList: List<CurrencyInfo>
    ): List<CurrencyPresentationModel> {
        return currentDayList.zip(nextDayList) { currentDayData, nextDayData ->
            CurrencyPresentationModel(
                currentDayData.charCode,
                "${currentDayData.scale} ${currentDayData.name}",
                currentDayData.rate,
                nextDayData.rate
            )
        }
    }

    private suspend fun fetchSpecificDateData(date: String) = currencyRepository.fetchData(date)

    override fun onPrefsChanged() {
        _currencyDataList.value = applyPrefsToCurrencyList(currencyInitialList)
    }
}