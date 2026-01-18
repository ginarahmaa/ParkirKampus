package com.example.parkir

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.parkir.databinding.CardItemParkirBinding
import java.text.NumberFormat
import java.util.Locale

class ParkirAdapter(
    private var listParkir: List<DatabaseHelper.DataParkir>,
    private val onDelete: (String) -> Unit,
    private val onEdit: (DatabaseHelper.DataParkir) -> Unit
) : RecyclerView.Adapter<ParkirAdapter.ParkirViewHolder>() {

    class ParkirViewHolder(val binding: CardItemParkirBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParkirViewHolder {
        val binding = CardItemParkirBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ParkirViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ParkirViewHolder, position: Int) {
        val data = listParkir[position]

        holder.binding.tvPlat.text = data.plat

        holder.binding.tvJenis.text = "${data.jenis} (Jam: ${data.masuk} - ${data.keluar})"

        val localeID = Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        val hargaFormatted = NumberFormat.getNumberInstance(localeID).format(data.biaya)

        holder.binding.tvBiaya.text = "Rp $hargaFormatted"

        holder.binding.btnDelete.setOnClickListener {
            onDelete(data.plat) // Kirim plat ke MainActivity buat dihapus
        }

        holder.binding.btnEdit.setOnClickListener {
            onEdit(data) // Kirim data lengkap ke FormParkirActivity buat diedit
        }
    }

    override fun getItemCount() = listParkir.size

    @SuppressLint("NotifyDataSetChanged")
    fun refreshData(newList: List<DatabaseHelper.DataParkir>) {
        listParkir = newList
        notifyDataSetChanged()
    }
}