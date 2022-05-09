package com.goodwy.messages.feature.conversationinfo

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.goodwy.messages.R
import com.goodwy.messages.common.Navigator
import com.goodwy.messages.common.base.QkAdapter
import com.goodwy.messages.common.base.QkViewHolder
import com.goodwy.messages.common.util.Colors
import com.goodwy.messages.common.util.extensions.getColorCompat
import com.goodwy.messages.common.util.extensions.setVisible
import com.goodwy.messages.extensions.isVideo
import com.goodwy.messages.feature.conversationinfo.ConversationInfoItem.*
import com.goodwy.messages.util.GlideApp
import com.goodwy.messages.util.Preferences
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import kotlinx.android.synthetic.main.conversation_info_settings.*
import kotlinx.android.synthetic.main.conversation_media_list_item.*
import kotlinx.android.synthetic.main.conversation_recipient_list_item.*
import javax.inject.Inject

class ConversationInfoAdapter @Inject constructor(
    private val context: Context,
    private val navigator: Navigator,
    private val colors: Colors,
    private val prefs: Preferences
) : QkAdapter<ConversationInfoItem>() {

    val recipientClicks: Subject<Long> = PublishSubject.create()
    val recipientLongClicks: Subject<Long> = PublishSubject.create()
    val themeClicks: Subject<Long> = PublishSubject.create()
    val nameClicks: Subject<Unit> = PublishSubject.create()
    val notificationClicks: Subject<Unit> = PublishSubject.create()
    val archiveClicks: Subject<Unit> = PublishSubject.create()
    val blockClicks: Subject<Unit> = PublishSubject.create()
    val deleteClicks: Subject<Unit> = PublishSubject.create()
    val mediaClicks: Subject<Long> = PublishSubject.create()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QkViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            0 -> QkViewHolder(inflater.inflate(R.layout.conversation_recipient_list_item, parent, false)).apply {

                /*itemView.setOnClickListener {
                    val item = getItem(adapterPosition) as? ConversationInfoRecipient
                    item?.value?.id?.run(recipientClicks::onNext)
                }*/

                /*itemView.setOnLongClickListener {
                    val item = getItem(adapterPosition) as? ConversationInfoRecipient
                    item?.value?.id?.run(recipientLongClicks::onNext)
                    true
                }*/

                oneButton.setOnClickListener {
                    val item = getItem(adapterPosition) as? ConversationInfoRecipient
                    navigator.makePhoneCall(item!!.value.address)
                }

                twoButton.setOnClickListener {
                    val item = getItem(adapterPosition) as? ConversationInfoRecipient
                    item?.value?.id?.run(recipientLongClicks::onNext)
                }

                /*theme.setOnClickListener {
                    val item = getItem(adapterPosition) as? ConversationInfoRecipient
                    item?.value?.id?.run(themeClicks::onNext)
                }*/

                threeButton.setOnClickListener {
                    val item = getItem(adapterPosition) as? ConversationInfoRecipient
                    item?.value?.id?.run(recipientClicks::onNext)
                }

                fourButton.setOnClickListener {
                    val item = getItem(adapterPosition) as? ConversationInfoRecipient
                    item?.value?.id?.run(themeClicks::onNext)
                }
            }

            1 -> QkViewHolder(inflater.inflate(R.layout.conversation_info_settings, parent, false)).apply {
                /*groupName.clicks().subscribe(nameClicks)
                notifications.clicks().subscribe(notificationClicks)
                archive.clicks().subscribe(archiveClicks)
                block.clicks().subscribe(blockClicks)
                delete.clicks().subscribe(deleteClicks)*/

                groupNameButton.clicks().subscribe(nameClicks)
                notificationsButton.clicks().subscribe(notificationClicks)
                archiveButton.clicks().subscribe(archiveClicks)
                blockButton.clicks().subscribe(blockClicks)
                deleteButton.clicks().subscribe(deleteClicks)
            }

            2 -> QkViewHolder(inflater.inflate(R.layout.conversation_media_list_item, parent, false)).apply {
                itemView.setOnClickListener {
                    val item = getItem(adapterPosition) as? ConversationInfoMedia
                    item?.value?.id?.run(mediaClicks::onNext)
                }
            }

            else -> throw IllegalStateException()
        }
    }

    @SuppressLint("NewApi")
    override fun onBindViewHolder(holder: QkViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is ConversationInfoRecipient -> {
                val recipient = item.value
                val theme = colors.theme(recipient)

                if (!isNightMode()) {
                    val white = getDrawable(context, R.drawable.tab_background_white)
                    holder.oneButton.background = white
                    holder.twoButton.background = white
                    holder.threeButton.background = white
                    holder.fourButton.background = white
                }

                holder.avatar.setRecipient(recipient)

                holder.name.text = recipient.contact?.name ?: recipient.address

                holder.address.text = recipient.address
                holder.address.setVisible(recipient.contact != null)

                holder.oneButton.foreground = getColoredDrawableWithColor(R.drawable.ic_call_white_24dp, theme.theme)

                holder.twoButton.foreground = getColoredDrawableWithColor(R.drawable.ic_content_copy_black_24dp, theme.theme)

                //holder.add.setVisible(recipient.contact == null)
                if (recipient.contact == null) {
                    holder.threeButton.foreground = getColoredDrawableWithColor(R.drawable.ic_person_add_black_24dp, theme.theme)
                } else {
                    holder.threeButton.foreground = getColoredDrawableWithColor(R.drawable.ic_person_black_24dp, theme.theme)
                }

                //holder.theme.setTint(theme.theme)
                holder.fourButton.foreground = getColoredDrawableWithColor(R.drawable.ic_palette_black_24dp, theme.theme)
            }

            is ConversationInfoSettings -> {
                /*holder.groupName.isVisible = item.recipients.size > 1
                holder.groupName.summary = item.name

                holder.notifications.isEnabled = !item.blocked

                holder.archive.isEnabled = !item.blocked
                holder.archive.title = context.getString(when (item.archived) {
                    true -> R.string.info_unarchive
                    false -> R.string.info_archive
                })

                holder.block.title = context.getString(when (item.blocked) {
                    true -> R.string.info_unblock
                    false -> R.string.info_block
                })*/
                val recipient = item.recipients.first()
                val theme = colors.theme(recipient)

                holder.groupNameButton.isVisible = item.recipients.size > 1
                holder.groupNameButton.setTextColor(theme.theme)
                //holder.groupNameButtonSummary.isGone = item.name == ""
                val nameText = if (item.name == "") context.getString(R.string.info_name) else item.name
                holder.groupNameButton.text = nameText

                holder.notificationsButton.isEnabled = !item.blocked
                holder.notificationsButton.setTextColor(theme.theme)
                //val chevron = getColoredDrawableWithColor(R.drawable.ic_chevron_right_black_24dp, theme.textSecondary)
                //holder.notificationsButton.setCompoundDrawablesWithIntrinsicBounds(null,null,chevron,null)

                holder.archiveButton.isEnabled = !item.blocked
                holder.archiveButton.setTextColor(theme.theme)
                holder.archiveButton.text = context.getString(when (item.archived) {
                    true -> R.string.info_unarchive
                    false -> R.string.info_archive
                })

                holder.blockButton.isGone = item.recipients.size > 1
                holder.blockButton.text = context.getString(when (item.blocked) {
                    true -> R.string.info_unblock
                    false -> R.string.info_block
                })
                holder.blockButton.setTextColor(when (item.blocked) {
                    true -> theme.theme
                    false -> context.getColorCompat(R.color.red)
                })

                if (item.blocked) {
                    holder.notificationsButton.alpha = 0.6f
                    holder.archiveButton.alpha = 0.6f
                } else {
                    holder.notificationsButton.alpha = 1f
                    holder.archiveButton.alpha = 1f
                }

                if (!isNightMode()) {
                    val white = getDrawable(context, R.drawable.tab_background_white)
                    holder.groupNameButton.background = white
                    holder.notificationsButton.background = white
                    holder.archiveButton.background = white
                    holder.blockButton.background = white
                    holder.deleteButton.background = white
                }
            }

            is ConversationInfoMedia -> {
                val part = item.value

                GlideApp.with(context)
                        .load(part.getUri())
                        .fitCenter()
                        .into(holder.thumbnail)

                holder.video.isVisible = part.isVideo()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (data[position]) {
            is ConversationInfoRecipient -> 0
            is ConversationInfoSettings -> 1
            is ConversationInfoMedia -> 2
        }
    }

    override fun areItemsTheSame(old: ConversationInfoItem, new: ConversationInfoItem): Boolean {
        return when {
            old is ConversationInfoRecipient && new is ConversationInfoRecipient -> {
                old.value.id == new.value.id
            }

            old is ConversationInfoSettings && new is ConversationInfoSettings -> {
                true
            }

            old is ConversationInfoMedia && new is ConversationInfoMedia -> {
                old.value.id == new.value.id
            }

            else -> false
        }
    }

    fun getColoredDrawableWithColor(drawableId: Int, color: Int, alpha: Int = 255): Drawable? {
        val drawable = getDrawable(context, drawableId)
        drawable?.mutate()?.applyColorFilter(color)
        drawable?.mutate()?.alpha = alpha
        return drawable
    }

    private fun isNightMode(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (prefs.nightMode.get() == Preferences.NIGHT_MODE_SYSTEM) context.resources.configuration.isNightModeActive
            else prefs.night.get()
        } else {
            prefs.night.get()
        }
    }

    fun Drawable.applyColorFilter(color: Int) = mutate().setColorFilter(color, PorterDuff.Mode.SRC_IN)

}