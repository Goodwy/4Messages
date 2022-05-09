package com.goodwy.messages.feature.settings.simconfigure

import android.content.Context
import android.view.View
import androidx.core.view.isVisible
import com.goodwy.messages.R
import com.goodwy.messages.common.Dialog
import com.goodwy.messages.common.base.QkController
import com.goodwy.messages.common.util.Colors
import com.goodwy.messages.common.util.extensions.*
import com.goodwy.messages.common.widget.PreferenceView
import com.goodwy.messages.injection.appComponent
import com.jakewharton.rxbinding2.view.clicks
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.autoDisposable
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import kotlinx.android.synthetic.main.backup_controller.*
import kotlinx.android.synthetic.main.settings_controller.preferences
import kotlinx.android.synthetic.main.settings_switch_widget.view.*
import kotlinx.android.synthetic.main.sim_configure_controller.*
import kotlinx.android.synthetic.main.sim_configure_controller.fab
import kotlinx.android.synthetic.main.sim_configure_controller.fabIcon
import kotlinx.android.synthetic.main.sim_configure_controller.fabLabel
import javax.inject.Inject

class SimConfigureController : QkController<SimConfigureView, SimConfigureState, SimConfigurePresenter>(), SimConfigureView {

    @Inject override lateinit var presenter: SimConfigurePresenter
    @Inject lateinit var actionsDialog: Dialog
    @Inject lateinit var colors: Colors
    @Inject lateinit var context: Context

    /**
     * Allows us to subscribe to [actionClicks] more than once
     */
    private val actionClicks: Subject<SimConfigureView.Action> = PublishSubject.create()

    init {
        appComponent.inject(this)
        layoutRes = R.layout.sim_configure_controller

        actionsDialog.adapter.setData(R.array.settings_sim_colors)
    }

    override fun onViewCreated() {

        /*sim1_holder.postDelayed({ sim1_holder?.animateLayoutChanges = true }, 100)
        sim2_holder.postDelayed({ sim2_holder?.animateLayoutChanges = true }, 100)
        sim3_holder.postDelayed({ sim3_holder?.animateLayoutChanges = true }, 100)*/

        Observable.merge(
            sim1_holder.clicks().map { SimConfigureView.Action.SIM1 },
            sim2_holder.clicks().map { SimConfigureView.Action.SIM2 },
            sim3_holder.clicks().map { SimConfigureView.Action.SIM3 })
                .autoDisposable(scope())
                .subscribe(actionClicks)
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        presenter.bindIntents(this)
        setTitle(R.string.settings_sim_сonfigure_title)
        showBackButton(true)
    }

    override fun preferenceClicks(): Observable<PreferenceView> = (0 until preferences.childCount)
        .map { index -> preferences.getChildAt(index) }
        .mapNotNull { view -> view as? PreferenceView }
        .map { preference -> preference.clicks().map { preference } }
        .let { preferences -> Observable.merge(preferences) }

    override fun actionClicks(): Observable<SimConfigureView.Action> = actionClicks

    override fun actionSelected(): Observable<Int> = actionsDialog.adapter.menuItemClicks

    override fun fabClicks(): Observable<*> = fab.clicks()

    override fun showSimConfigure(selected: Int) {
        actionsDialog.adapter.selectedItem = selected
        activity?.let(actionsDialog::show)
    }

    override fun render(state: SimConfigureState) {
        simColor.checkbox.isChecked = state.simColor

        sim1_holder.isEnabled = state.simColor && state.upgraded
        sim1_title.isEnabled = state.simColor
        sim1_change.isEnabled = state.simColor && state.upgraded
        if (state.simColor) sim1_icon.setTint(state.sim1Color) else sim1_icon.setTint(colors.theme().textSecondary)

        sim2_holder.isEnabled = state.simColor && state.upgraded
        sim2_title.isEnabled = state.simColor
        sim2_change.isEnabled = state.simColor && state.upgraded
        if (state.simColor) sim2_icon.setTint(state.sim2Color) else sim2_icon.setTint(colors.theme().textSecondary)

        sim3_holder.isEnabled = state.simColor && state.upgraded
        sim3_title.isEnabled = state.simColor
        sim3_change.isEnabled = state.simColor && state.upgraded
        if (state.simColor) sim3_icon.setTint(state.sim3Color) else sim3_icon.setTint(colors.theme().textSecondary)

        themedActivity?.colors?.theme()?.let { theme ->
            fab.setBackgroundTint(theme.theme)
            fabIcon.setTint(theme.textPrimary)
            fabLabel.setTextColor(theme.textPrimary)
        }
        fab.isVisible = state.simColor && !state.upgraded
    }

}