package com.appmaker.app

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appmaker.app.databinding.ActivityMainBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.tabs.TabLayout

// --- Data Model ---
data class ZikrItem(
    val id: Int,
    val text: String,
    val maxCount: Int,
    var currentCount: Int,
    val category: String,
    val note: String
)

// --- ViewModel ---
class AzkarViewModel : ViewModel() {

    private val _azkarList = MutableLiveData<List<ZikrItem>>()
    val azkarList: LiveData<List<ZikrItem>> = _azkarList

    private val _currentCategory = MutableLiveData<String>("morning")
    val currentCategory: LiveData<String> = _currentCategory

    private val _tasbeehCounter = MutableLiveData<Int>(0)
    val tasbeehCounter: LiveData<Int> = _tasbeehCounter

    private val allData = mutableListOf(
        // Morning
        ZikrItem(101, "أَصْبَحْنَا وَأَصْبَحَ الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ لاَ إِلَهَ إِلاَّ اللَّهُ وَحْدَهُ لاَ شَرِيكَ لَهُ.", 1, 1, "morning", "مرة واحدة"),
        ZikrItem(102, "اللَّهُمَّ بِكَ أَصْبَحْنَا وَبِكَ أَمْسَيْنَا وَبِكَ نَحْيَا وَبِكَ نَمُوتُ وَإِلَيْكَ النُّشُورُ.", 1, 1, "morning", "مرة واحدة"),
        ZikrItem(103, "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ: عَدَدَ خَلْقِهِ، وَرِضَا نَفْسِهِ، وَزِنَةَ عَرْشِهِ، وَمِدَادَ كَلِمَاتِهِ.", 3, 3, "morning", "3 مرات"),
        ZikrItem(104, "اللَّهُمَّ عَفِني فِي بَدَنِي، اللَّهُمَّ عَافِنِي فِي سَمْعِي، اللَّهُمَّ عَافِنِي فِي بَصَرِي، لاَ إِلَهَ إِلاَّ أَنْتَ.", 3, 3, "morning", "3 مرات"),
        ZikrItem(105, "يَا حَيُّ يَا قَيُّومُ بِرَحْمَتِكَ أَسْتَغِيثُ أَصْلِحْ لِي شَأْنِي كُلَّهُ وَلاَ تَكِلْنِي إِلَى نَفْسِي طَرْفَةَ عَيْنٍ.", 1, 1, "morning", "مرة واحدة"),
        
        // Evening
        ZikrItem(201, "أَمْسَيْنَا وَأَمْسَى الْمُلْكُ لِلَّهِ وَالْحَمْدُ لِلَّهِ، لاَ إِلَهَ إِلاَّ اللَّهُ وَحْدَهُ لاَ شَرِيكَ لَهُ.", 1, 1, "evening", "مرة واحدة"),
        ZikrItem(202, "اللَّهُمَّ بِكَ أَمْسَيْنَا وَبِكَ أَصْبَحْنَا وَبِكَ نَحْيَا وَبِكَ نَمُوتُ وَإِلَيْكَ الْمَصِيرُ.", 1, 1, "evening", "مرة واحدة"),
        ZikrItem(203, "أَعُوذُ بِكَلِمَاتِ اللَّهِ التَّامَّاتِ مِنْ شَرِّ مَا خَلَقَ.", 3, 3, "evening", "3 مرات"),
        ZikrItem(204, "اللَّهُمَّ إِنِّي أَسْأَلُكَ الْعَفْوَ وَالْعَافِيَةَ فِي الدُّنْيَا وَالآخِرَةِ.", 1, 1, "evening", "مرة واحدة"),

        // Post Prayer
        ZikrItem(301, "أَسْتَغْفِرُ اللَّهَ (ثَلاَثاً)، اللَّهُمَّ أَنْتَ السَّلاَمُ وَمِنْكَّ السَّلاَمُ، تَبَارَكْتَ يَا ذَا الْجَلاَلِ وَالإِكْرَامِ.", 1, 1, "post_prayer", "مرة واحدة"),
        ZikrItem(302, "سُبْحَانَ اللَّهِ", 33, 33, "post_prayer", "33 مرة"),
        ZikrItem(303, "الْحَمْدُ لِلَّهِ", 33, 33, "post_prayer", "33 مرة"),
        ZikrItem(304, "اللَّهُ أَكْبَرُ", 33, 33, "post_prayer", "33 مرة"),
        ZikrItem(305, "لاَ إِلَهَ إِلاَّ اللَّهُ وَحْدَهُ لاَ شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ.", 1, 1, "post_prayer", "مرة واحدة")
    )

    init {
        filterCategory("morning")
    }

    fun selectCategory(category: String) {
        _currentCategory.value = category
        filterCategory(category)
    }

    private fun filterCategory(category: String) {
        if (category != "tasbeeh") {
            _azkarList.value = allData.filter { it.category == category }
        }
    }

    fun decrementZikr(item: ZikrItem) {
        if (item.currentCount > 0) {
            item.currentCount--
            filterCategory(_currentCategory.value ?: "morning")
        }
    }

    fun resetItem(item: ZikrItem) {
        item.currentCount = item.maxCount
        filterCategory(_currentCategory.value ?: "morning")
    }

    fun resetCurrentCategory() {
        val cat = _currentCategory.value
        if (cat == "tasbeeh") {
            _tasbeehCounter.value = 0
        } else {
            allData.filter { it.category == cat }.forEach { it.currentCount = it.maxCount }
            filterCategory(cat ?: "morning")
        }
    }

    fun incrementTasbeeh() {
        val current = _tasbeehCounter.value ?: 0
        _tasbeehCounter.value = current + 1
    }

    fun resetTasbeeh() {
        _tasbeehCounter.value = 0
    }

    fun getTotalProgress(): Pair<Int, Int> {
        var total = 0
        var completed = 0
        allData.forEach {
            total += it.maxCount
            completed += (it.maxCount - it.currentCount)
        }
        total += 100 // Include default goal for tasbeeh
        completed += (_tasbeehCounter.value ?: 0)
        return Pair(completed, total)
    }
}

// --- RecyclerView Adapter ---
class AzkarAdapter(
    private var list: List<ZikrItem>,
    private val onItemClick: (ZikrItem) -> Unit,
    private val onResetClick: (ZikrItem) -> Unit
) : RecyclerView.Adapter<AzkarAdapter.AzkarViewHolder>() {

    fun updateData(newList: List<ZikrItem>) {
        list = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AzkarViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_zikr, parent, false)
        return AzkarViewHolder(view)
    }

    override fun onBindViewHolder(holder: AzkarViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    inner class AzkarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val card: MaterialCardView = itemView.findViewById(R.id.cardZikr)
        private val tvText: TextView = itemView.findViewById(R.id.tvZikrText)
        private val tvNote: TextView = itemView.findViewById(R.id.tvZikrNote)
        private val btnCount: MaterialButton = itemView.findViewById(R.id.btnCounter)
        private val btnReset: View = itemView.findViewById(R.id.btnResetSingle)

        fun bind(item: ZikrItem) {
            tvText.text = item.text
            tvNote.text = item.note
            btnCount.text = item.currentCount.toString()

            if (item.currentCount == 0) {
                card.alpha = 0.5f
                btnCount.setBackgroundColor(0xFF0F5132.toInt())
                btnCount.setTextColor(0xFFA3E635.toInt())
            } else {
                card.alpha = 1.0f
                btnCount.setBackgroundColor(0xFFFFC107.toInt())
                btnCount.setTextColor(0xFF000000.toInt())
            }

            card.setOnClickListener { onItemClick(item) }
            btnCount.setOnClickListener { onItemClick(item) }
            btnReset.setOnClickListener { onResetClick(item) }
        }
    }
}

// Custom programmatic View creation for List Item to avoid external XML errors
class ItemZikrLayout(context: Context) {
    companion object {
        fun createLayout(parent: ViewGroup): View {
            val inflater = LayoutInflater.from(parent.context)
            val xmlString = """
            <?xml version="1.0" encoding="utf-8"?>
            <com.google.android.material.card.MaterialCardView 
                xmlns:android="http://schemas.android.com/apk/res/android"
                xmlns:app="http://schemas.android.com/apk/res-auto"
                android:id="@+id/cardZikr"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:layout_marginBottom="10dp"
                android:clickable="true"
                android:focusable="true"
                app:cardBackgroundColor="#113826"
                app:cardCornerRadius="16dp"
                app:cardElevation="3dp"
                app:strokeColor="#1F5A3E"
                app:strokeWidth="1dp">

                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:orientation="vertical"
                    android:padding="16dp">

                    <TextView
                        android:id="@+id/tvZikrText"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:textColor="#FFFFFF"
                        android:textSize="16sp"
                        android:lineSpacingExtra="4dp" />

                    <RelativeLayout
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:layout_marginTop="12dp">

                        <TextView
                            android:id="@+id/tvZikrNote"
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:layout_centerVertical="true"
                            android:textColor="#A3E635"
                            android:textSize="12sp" />

                        <LinearLayout
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:layout_alignParentEnd="true"
                            android:gravity="center_vertical"
                            android:orientation="horizontal">

                            <ImageView
                                android:id="@+id/btnResetSingle"
                                android:layout_width="28dp"
                                android:layout_height="28dp"
                                android:layout_marginEnd="8dp"
                                android:background="?selectableItemBackgroundBorderless"
                                android:padding="4dp"
                                android:src="@android:drawable/ic_menu_rotate"
                                app:tint="#A3E635" />

                            <com.google.android.material.button.MaterialButton
                                android:id="@+id/btnCounter"
                                android:layout_width="64dp"
                                android:layout_height="40dp"
                                android:textColor="#000000"
                                android:textSize="16sp"
                                android:textStyle="bold"
                                app:cornerRadius="12dp" />
                        </LinearLayout>
                    </RelativeLayout>
                </LinearLayout>
            </com.google.android.material.card.MaterialCardView>
            """.trimIndent()
            // DynamicInflate fallback layout
            val view = MaterialCardView(parent.context).apply {
                id = R.id.cardZikr
                layoutParams = ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(0, 0, 0, 16) }
                radius = 32f
                setCardBackgroundColor(0xFF113826.toInt())
                strokeColor = 0xFF1F5A3E.toInt()
                strokeWidth = 2
                
                val rootLayout = android.widget.LinearLayout(parent.context).apply {
                    orientation = android.widget.LinearLayout.VERTICAL
                    setPadding(32, 32, 32, 32)
                }

                val txt = TextView(parent.context).apply {
                    id = R.id.tvZikrText
                    setTextColor(0xFFFFFFFF.toInt())
                    textSize = 16f
                }

                val bottomLayout = android.widget.RelativeLayout(parent.context).apply {
                    setPadding(0, 16, 0, 0)
                }

                val note = TextView(parent.context).apply {
                    id = R.id.tvZikrNote
                    setTextColor(0xFFA3E635.toInt())
                    textSize = 12f
                }

                val btn = MaterialButton(parent.context).apply {
                    id = R.id.btnCounter
                    val params = android.widget.RelativeLayout.LayoutParams(180, 110)
                    params.addRule(android.widget.RelativeLayout.ALIGN_PARENT_END)
                    layoutParams = params
                    textSize = 16f
                    cornerRadius = 24
                }

                val reset = android.widget.ImageView(parent.context).apply {
                    id = R.id.btnResetSingle
                    val params = android.widget.RelativeLayout.LayoutParams(80, 80)
                    params.addRule(android.widget.RelativeLayout.LEFT_OF, R.id.btnCounter)
                    params.setMargins(0, 15, 20, 0)
                    layoutParams = params
                    setImageResource(android.R.drawable.ic_menu_rotate)
                    setColorFilter(0xFFA3E635.toInt())
                }

                bottomLayout.addView(note)
                bottomLayout.addView(reset)
                bottomLayout.addView(btn)

                rootLayout.addView(txt)
                rootLayout.addView(bottomLayout)
                addView(rootLayout)
            }
            return view
        }
    }
}

// --- Main Activity ---
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: AzkarViewModel by viewModels()
    private lateinit var adapter: AzkarAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupTabs()
        setupRecyclerView()
        setupTasbeeh()
        observeViewModel()

        binding.btnResetAll.setOnClickListener {
            viewModel.resetCurrentCategory()
            triggerVibration()
        }
    }

    private fun setupTabs() {
        val tabs = listOf(
            Pair("morning", getString(R.string.tab_morning)),
            Pair("evening", getString(R.string.tab_evening)),
            Pair("post_prayer", getString(R.string.tab_post_prayer)),
            Pair("tasbeeh", getString(R.string.tab_tasbeeh))
        )

        tabs.forEach { tab ->
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText(tab.second).setTag(tab.first))
        }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val tag = tab?.tag as? String ?: "morning"
                viewModel.selectCategory(tag)
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupRecyclerView() {
        adapter = AzkarAdapter(
            list = emptyList(),
            onItemClick = { item ->
                viewModel.decrementZikr(item)
                triggerVibration()
            },
            onResetClick = { item ->
                viewModel.resetItem(item)
                triggerVibration()
            }
        )
        binding.rvAzkar.layoutManager = LinearLayoutManager(this)
        binding.rvAzkar.adapter = adapter
    }

    private fun setupTasbeeh() {
        binding.btnTasbeehClick.setOnClickListener {
            viewModel.incrementTasbeeh()
            triggerVibration()
        }
        binding.btnResetTasbeeh.setOnClickListener {
            viewModel.resetTasbeeh()
            triggerVibration()
        }
    }

    private fun observeViewModel() {
        viewModel.currentCategory.observe(this) { category ->
            if (category == "tasbeeh") {
                binding.rvAzkar.visibility = View.GONE
                binding.layoutTasbeeh.visibility = View.VISIBLE
            } else {
                binding.rvAzkar.visibility = View.VISIBLE
                binding.layoutTasbeeh.visibility = View.GONE
            }
            updateTotalProgress()
        }

        viewModel.azkarList.observe(this) { list ->
            adapter.updateData(list)
            updateTotalProgress()
        }

        viewModel.tasbeehCounter.observe(this) { count ->
            binding.tvTasbeehCount.text = count.toString()
            updateTotalProgress()
        }
    }

    private fun updateTotalProgress() {
        val (completed, total) = viewModel.getTotalProgress()
        binding.tvTotalProgress.text = "$completed / $total"
    }

    private fun triggerVibration() {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(CONTEXT_IGNORE_SECURITY) as? Vibrator ?: getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(40)
        }
    }
}