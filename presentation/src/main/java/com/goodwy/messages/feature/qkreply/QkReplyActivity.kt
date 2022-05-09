package com.goodwy.messages.feature.qkreply

import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.textChanges
import com.goodwy.messages.R
import com.goodwy.messages.common.base.QkThemedActivity
import com.goodwy.messages.common.util.extensions.*
import com.goodwy.messages.feature.compose.MessagesAdapter
import dagger.android.AndroidInjection
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import kotlinx.android.synthetic.main.message_list_item_in.*
import kotlinx.android.synthetic.main.qkreply_activity.*
import kotlinx.android.synthetic.main.qkreply_activity.sim
import kotlinx.android.synthetic.main.qkreply_activity.simIndex
import javax.inject.Inject

class QkReplyActivity : QkThemedActivity(), QkReplyView {

    @Inject lateinit var adapter: MessagesAdapter
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    override val menuItemIntent: Subject<Int> = PublishSubject.create()
    override val textChangedIntent by lazy { message.textChanges() }
    override val changeSimIntent by lazy { sim.clicks() }
    override val sendIntent by lazy { send.clicks() }

    private val viewModel by lazy { ViewModelProviders.of(this, viewModelFactory)[QkReplyViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)

        setFinishOnTouchOutside(prefs.qkreplyTapDismiss.get())
        setContentView(R.layout.qkreply_activity)
        window.setBackgroundDrawable(null)
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        viewModel.bindView(this)

        toolbar.clipToOutline = true

        messages.adapter = adapter
        messages.adapter?.autoScrollToStart(messages)
        messages.adapter?.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() = messages.scrollToPosition(adapter.itemCount - 1)
        })

        // These theme attributes don't apply themselves on API 21
        if (Build.VERSION.SDK_INT <= 22) {
            toolbar.setBackgroundTint(resolveThemeColor(R.attr.colorPrimary))
            background.setBackgroundTint(resolveThemeColor(android.R.attr.windowBackground))
            messageBackground.setBackgroundTint(resolveThemeColor(R.attr.bubbleColor))
            composeBackgroundGradient.setBackgroundTint(resolveThemeColor(android.R.attr.windowBackground))
            composeBackgroundSolid.setBackgroundTint(resolveThemeColor(android.R.attr.windowBackground))
        }
    }

    override fun render(state: QkReplyState) {
        if (state.hasError) {
            finish()
        }

        threadId.onNext(state.threadId)

        title = state.title

        toolbar.menu.findItem(R.id.expand)?.isVisible = !state.expanded
        toolbar.menu.findItem(R.id.collapse)?.isVisible = state.expanded

        adapter.data = state.data

        counter.text = state.remaining
        counter.setVisible(counter.text.isNotBlank())

        sim.setVisible(state.subscription != null)
        sim.contentDescription = getString(R.string.compose_sim_cd, state.subscription?.displayName)
        simIndex.text = "${state.subscription?.simSlotIndex?.plus(1)}"

        /*val simColor = when (state.subscription?.simSlotIndex?.plus(1)?.toString()) {
            "1" -> getColorCompat(R.color.sim1)
            "2" -> getColorCompat(R.color.sim2)
            "3" -> getColorCompat(R.color.sim3)
            "4" -> getColorCompat(R.color.sim4)
            else -> getColorCompat(R.color.sim_other)
        }*/
        val simColor = when (state.subscription?.simSlotIndex?.plus(1)?.toString()) {
            "1" -> colors.colorForSim(this, 1)
            "2" -> colors.colorForSim(this, 2)
            "3" -> colors.colorForSim(this, 3)
            else -> colors.colorForSim(this, 1)
        }
        if (prefs.simColor.get()) {
            sim.setTint(simColor)
        }

        send.isEnabled = state.canSend
        send.imageAlpha = if (state.canSend) 255 else 128
    }

    override fun setDraft(draft: String) {
        message.setText(draft)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.qkreply, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        menuItemIntent.onNext(item.itemId)
        return true
    }

    override fun getActivityThemeRes(black: Boolean) = when {
        black -> R.style.AppThemeDialog_Black
        else -> R.style.AppThemeDialog
    }

}