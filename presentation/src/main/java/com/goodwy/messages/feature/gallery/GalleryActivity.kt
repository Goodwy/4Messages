/*
 * Copyright (C) 2017 Moez Bhatti <moez.bhatti@gmail.com>
 *
 * This file is part of QKSMS.
 *
 * QKSMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * QKSMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with QKSMS.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.goodwy.messages.feature.gallery

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.goodwy.messages.BuildConfig
import com.goodwy.messages.R
import com.goodwy.messages.common.base.QkActivity
import com.goodwy.messages.common.util.DateFormatter
import com.goodwy.messages.common.util.extensions.setVisible
import com.goodwy.messages.model.MmsPart
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import kotlinx.android.synthetic.main.gallery_activity.*
import javax.inject.Inject

class GalleryActivity : QkActivity(), GalleryView {

    @Inject lateinit var dateFormatter: DateFormatter
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject lateinit var pagerAdapter: GalleryPagerAdapter

    val partId by lazy { intent.getLongExtra("partId", 0L) }

    private val optionsItemSubject: Subject<Int> = PublishSubject.create()
    private val pageChangedSubject: Subject<MmsPart> = PublishSubject.create()
    private val viewModel by lazy { ViewModelProviders.of(this, viewModelFactory)[GalleryViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gallery_activity)
        showBackButton(false)
        viewModel.bindView(this)

        pager.adapter = pagerAdapter
        pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                this@GalleryActivity.onPageSelected(position)
            }
        })

        pagerAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                pagerAdapter.data?.takeIf { pagerAdapter.itemCount > 0 }
                        ?.indexOfFirst { part -> part.id == partId }
                        ?.let { index ->
                            onPageSelected(index)
                            pager.setCurrentItem(index, false)
                            pagerAdapter.unregisterAdapterDataObserver(this)
                        }
            }
        })
    }

    fun onPageSelected(position: Int) {
        toolbarSubtitle.text = pagerAdapter.getItem(position)?.messages?.firstOrNull()?.date
                ?.let(dateFormatter::getDetailedTimestamp)
        toolbarSubtitle.isVisible = toolbarTitle.text.isNotBlank()

        pagerAdapter.getItem(position)?.run(pageChangedSubject::onNext)
    }

    override fun render(state: GalleryState) {
        toolbar.setVisible(state.navigationVisible)

        title = state.title
        pagerAdapter.updateData(state.parts)
    }

    override fun optionsItemSelected(): Observable<Int> = optionsItemSubject

    override fun screenTouched(): Observable<*> = pagerAdapter.clicks

    override fun pageChanged(): Observable<MmsPart> = pageChangedSubject

    override fun requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val uri = Uri.parse("package:${BuildConfig.APPLICATION_ID}")
            startActivity(
                Intent(
                    Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                    uri
                )
            )
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.gallery, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
            else -> optionsItemSubject.onNext(item.itemId)
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        pagerAdapter.destroy()
    }

}