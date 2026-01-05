package com.thundenet.admin.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.thundenet.admin.ui.fragments.*

class ModulesPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    private val fragments: List<Fragment> = listOf(
        PlayersFragment(), CharactersFragment(), EconomyFragment(), ItemsFragment(), ServerFragment(),
        TicketsFragment(), EventsFragment(), CommandsFragment(), BroadcastFragment(), HomeStoneFragment(),
        ConfigFragment(), LogsFragment(), StatsFragment(), AboutFragment()
    )

    override fun getItemCount(): Int = fragments.size
    override fun createFragment(position: Int): Fragment = fragments[position]
}