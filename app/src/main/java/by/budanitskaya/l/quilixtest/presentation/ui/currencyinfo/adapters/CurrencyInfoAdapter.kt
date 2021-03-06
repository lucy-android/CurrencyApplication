package by.budanitskaya.l.quilixtest.presentation.ui.currencyinfo.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.budanitskaya.l.quilixtest.databinding.CurrencyInfoItemBinding
import by.budanitskaya.l.quilixtest.databinding.HeaderItemBinding
import by.budanitskaya.l.quilixtest.presentation.models.CurrencyPresentationModel

class CurrencyInfoAdapter(
    private val dates: Pair<String, String>,
    private val currencyData: List<CurrencyPresentationModel>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            0 -> {
                val binding = HeaderItemBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                CurrencyHeaderViewHolder(binding, dates)
            }
            else -> {
                val binding = CurrencyInfoItemBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                CurrencyInfoViewHolder(binding, currencyData)
            }
        }
    }

    override fun getItemViewType(position: Int) = position

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CurrencyInfoViewHolder -> holder.bind()
            is CurrencyHeaderViewHolder -> holder.bind()
        }
    }

    override fun getItemCount(): Int = currencyData.size + 1

    class CurrencyInfoViewHolder(
        private val binding: CurrencyInfoItemBinding,
        private val currencyData: List<CurrencyPresentationModel>
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind() {
            with(binding) {
                textViewCharCode.text = currencyData[adapterPosition - 1].charCode
                val charCode = currencyData[adapterPosition - 1].charCode
                textViewCharCode.text = charCode
                val name = currencyData[adapterPosition - 1].name
                textViewNumCodeName.text = "$name"
                val currentCurrencyInfo = currencyData[adapterPosition - 1].currentDayRate
                textViewYesterdayRate.text = currentCurrencyInfo.toString()
                val tomorrowCurrencyInfo = currencyData[adapterPosition - 1].nextDayRate
                textViewTodayRate.text = tomorrowCurrencyInfo.toString()
            }
        }
    }

    class CurrencyHeaderViewHolder(
        private val binding: HeaderItemBinding,
        private val dates: Pair<String, String>
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind() {
            with(binding){
                textViewTodayDate.text = dates.second
                textViewYesterdayDate.text = dates.first
            }
        }
    }
}