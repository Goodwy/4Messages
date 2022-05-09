package com.goodwy.messages.feature.conversationinfo

import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import com.bluelinelabs.conductor.RouterTransaction
import com.goodwy.messages.R
import com.goodwy.messages.common.Navigator
import com.goodwy.messages.common.ChangeHandler
import com.goodwy.messages.common.base.QkController
import com.goodwy.messages.common.util.extensions.scrapViews
import com.goodwy.messages.common.widget.TextInputDialog
import com.goodwy.messages.feature.blocking.BlockingDialog
import com.goodwy.messages.feature.conversationinfo.injection.ConversationInfoModule
import com.goodwy.messages.feature.themepicker.ThemePickerController
import com.goodwy.messages.injection.appComponent
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.autoDisposable
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import kotlinx.android.synthetic.main.conversation_info_controller.*
import javax.inject.Inject

class ConversationInfoController(
    val threadId: Long = 0
) : QkController<ConversationInfoView, ConversationInfoState, ConversationInfoPresenter>(), ConversationInfoView {

    @Inject override lateinit var presenter: ConversationInfoPresenter
    @Inject lateinit var blockingDialog: BlockingDialog
    @Inject lateinit var navigator: Navigator
    @Inject lateinit var adapter: ConversationInfoAdapter

    private val nameDialog: TextInputDialog by lazy {
        TextInputDialog(activity!!, activity!!.getString(R.string.info_name), nameChangeSubject::onNext)
    }

    private val nameChangeSubject: Subject<String> = PublishSubject.create()
    private val confirmDeleteSubject: Subject<Unit> = PublishSubject.create()

    init {
        appComponent
                .conversationInfoBuilder()
                .conversationInfoModule(ConversationInfoModule(this))
                .build()
                .inject(this)

        layoutRes = R.layout.conversation_info_controller
    }

    override fun onViewCreated() {
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(GridSpacingItemDecoration(adapter, activity!!))
        recyclerView.layoutManager = GridLayoutManager(activity, 3).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int = if (adapter.getItemViewType(position) == 2) 1 else 3
            }
        }
        val margin = resources!!.getDimensionPixelSize(R.dimen.conversation_info_padding)
        recyclerView.setPadding(margin, 0, margin, margin)

        themedActivity?.theme
                ?.autoDisposable(scope())
                ?.subscribe { recyclerView.scrapViews() }
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        presenter.bindIntents(this)
        setTitle(R.string.info_title)
        showBackButton(true)
    }

    override fun render(state: ConversationInfoState) {
        if (state.hasError) {
            activity?.finish()
            return
        }

        adapter.data = state.data
    }

    override fun recipientClicks(): Observable<Long> = adapter.recipientClicks
    override fun recipientLongClicks(): Observable<Long> = adapter.recipientLongClicks
    override fun themeClicks(): Observable<Long> = adapter.themeClicks
    override fun nameClicks(): Observable<*> = adapter.nameClicks
    override fun nameChanges(): Observable<String> = nameChangeSubject
    override fun notificationClicks(): Observable<*> = adapter.notificationClicks
    override fun archiveClicks(): Observable<*> = adapter.archiveClicks
    override fun blockClicks(): Observable<*> = adapter.blockClicks
    override fun deleteClicks(): Observable<*> = adapter.deleteClicks
    override fun confirmDelete(): Observable<*> = confirmDeleteSubject
    override fun mediaClicks(): Observable<Long> = adapter.mediaClicks

    override fun showNameDialog(name: String) = nameDialog.setText(name).show()

    override fun showThemePicker(recipientId: Long) {
        router.pushController(RouterTransaction.with(ThemePickerController(recipientId))
                .pushChangeHandler(ChangeHandler())
                .popChangeHandler(ChangeHandler()))
    }

    override fun showBlockingDialog(conversations: List<Long>, block: Boolean) {
        blockingDialog.show(activity!!, conversations, block)
    }

    override fun requestDefaultSms() {
        navigator.showDefaultSmsDialog(activity!!)
    }

    override fun showDeleteDialog() {
        AlertDialog.Builder(activity!!)
                .setTitle(R.string.dialog_delete_title)
                .setMessage(resources?.getQuantityString(R.plurals.dialog_delete_message, 1))
                .setPositiveButton(R.string.button_delete) { _, _ -> confirmDeleteSubject.onNext(Unit) }
                .setNegativeButton(R.string.button_cancel, null)
                .show()
    }

}