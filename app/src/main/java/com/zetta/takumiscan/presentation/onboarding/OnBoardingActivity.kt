package com.zetta.takumiscan.presentation.onboarding

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.zetta.takumiscan.R
import com.zetta.takumiscan.databinding.ActivityOnBoardingBinding
import com.zetta.takumiscan.presentation.onboarding.fragment.FirstPageFragment
import com.zetta.takumiscan.presentation.onboarding.fragment.SecondPageFragment
import com.zetta.takumiscan.presentation.onboarding.fragment.ThirdPageFragment

class OnBoardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnBoardingBinding
    private val listPages = listOf(
        FirstPageFragment(),
        SecondPageFragment(),
        ThirdPageFragment()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.pager.adapter = object : FragmentStateAdapter(this){
            override fun getItemCount(): Int = listPages.size
            override fun createFragment(position: Int): Fragment = listPages[position]
        }

        binding.pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                refreshCircle()
                if (binding.pager.currentItem == 2) binding.btnNext.visibility = View.INVISIBLE
                else binding.btnNext.visibility = View.VISIBLE
            }
        })

        binding.btnNext.setOnClickListener {
            binding.pager.currentItem++
            refreshCircle()
            if (binding.pager.currentItem == 2) binding.btnNext.visibility = View.INVISIBLE
        }
    }

    private fun refreshCircle(){
        binding.circle1.setCardBackgroundColor(getColor(R.color.gray))
        binding.circle2.setCardBackgroundColor(getColor(R.color.gray))
        binding.circle3.setCardBackgroundColor(getColor(R.color.gray))

        if (binding.pager.currentItem == 0) binding.circle1.setCardBackgroundColor(getColor(R.color.navy))
        if (binding.pager.currentItem == 1) binding.circle2.setCardBackgroundColor(getColor(R.color.navy))
        if (binding.pager.currentItem == 2) binding.circle3.setCardBackgroundColor(getColor(R.color.navy))
    }
}