package com.example.unfilterednoise.views.mainbottomnavigation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.unfilterednoise.R
import com.example.unfilterednoise.databinding.ActivityMainNavBinding
import com.example.unfilterednoise.views.mainbottomnavigation.communityviews.CommunityFragment
import com.example.unfilterednoise.views.mainbottomnavigation.feedviews.FeedFragment
import com.example.unfilterednoise.views.onboardingviews.ProfileFragment
import com.example.unfilterednoise.views.mainbottomnavigation.feedviews.TopicsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainNavActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainNavBinding
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainNavBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        bottomNavigationView=binding.bottomNavMain

        bottomNavigationView.setOnItemSelectedListener{
            when(it.itemId){
                R.id.ForYouPage ->{
                    replaceFragment(FeedFragment())
                    true
                }
                R.id.TopicsPage ->{
                    replaceFragment(TopicsFragment())
                    true
                }
                R.id.CommunityPage ->{
                    replaceFragment(CommunityFragment())
                    true
                }
                R.id.ProfilePage ->{
                    replaceFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }

        replaceFragment(FeedFragment())
    }

    private fun replaceFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit()
    }
}