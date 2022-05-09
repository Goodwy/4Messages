package com.goodwy.messages.feature.themepicker

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.goodwy.messages.R
import com.goodwy.messages.manager.BillingManager
import javax.inject.Inject

class ThemePagerAdapter @Inject constructor(
    private val context: Context,
    private val billingManager: BillingManager
) : PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        return when (position) {
            0 -> container.findViewById(R.id.messagesColors)
            1 -> container.findViewById(R.id.materialColors)
            2 -> container.findViewById(R.id.iosColors)
            else -> container.findViewById(R.id.hsvPicker)
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> context.getString(R.string.app_name)
            1 -> context.getString(R.string.theme_material)
            2 -> if (billingManager.upgradeStatus.blockingFirst()) context.getString(R.string.theme_ios)
                    else context.getString(R.string.theme_ios) + "+"
            else -> if (billingManager.upgradeStatus.blockingFirst()) context.getString(R.string.theme_color_picker)
                    else context.getString(R.string.theme_color_picker) + "+" //theme_plus
        }
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getCount(): Int {
        return 4
    }

}