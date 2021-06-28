package com.goodwy.messages.feature.conversations

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.goodwy.messages.R
import com.goodwy.messages.common.Navigator
import com.goodwy.messages.common.base.QkRealmAdapter
import com.goodwy.messages.common.base.QkViewHolder
import com.goodwy.messages.common.util.Colors
import com.goodwy.messages.common.util.DateFormatter
import com.goodwy.messages.common.util.extensions.resolveThemeColor
import com.goodwy.messages.common.util.extensions.setTint
import com.goodwy.messages.model.Conversation
import com.goodwy.messages.util.PhoneNumberUtils
import com.goodwy.messages.util.Preferences
import kotlinx.android.synthetic.main.conversation_list_item.*
import kotlinx.android.synthetic.main.conversation_list_item.view.*
import javax.inject.Inject

class ConversationsAdapter @Inject constructor(
        private val colors: Colors,
        private val context: Context,
        private val dateFormatter: DateFormatter,
        private val navigator: Navigator,
        private val phoneNumberUtils: PhoneNumberUtils,
        private val prefs: Preferences
) : QkRealmAdapter<Conversation>() {

    init {
        // This is how we access the threadId for the swipe actions
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QkViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.conversation_list_item, parent, false)

        if (viewType == 1) {
            val textColorPrimary = parent.context.resolveThemeColor(android.R.attr.textColorPrimary)

            view.title.setTypeface(view.title.typeface, Typeface.BOLD)

            view.snippet.setTypeface(view.snippet.typeface, Typeface.BOLD)
            view.snippet.setTextColor(textColorPrimary)
            view.snippet.maxLines = 5

            view.unread.isVisible = true

            view.date.setTypeface(view.date.typeface, Typeface.BOLD)
            view.date.setTextColor(textColorPrimary)
            }

        return QkViewHolder(view).apply {
            view.setOnClickListener {
                val conversation = getItem(adapterPosition) ?: return@setOnClickListener
                when (toggleSelection(conversation.id, false)) {
                    true -> view.isActivated = isSelected(conversation.id)
                    false -> navigator.showConversation(conversation.id)
                }
            }

            view.setOnLongClickListener {
                val conversation = getItem(adapterPosition) ?: return@setOnLongClickListener true
                toggleSelection(conversation.id)
                view.isActivated = isSelected(conversation.id)
                true
            }
        }
    }

    override fun onBindViewHolder(holder: QkViewHolder, position: Int) {
        val conversation = getItem(position) ?: return

        // If the last message wasn't incoming, then the colour doesn't really matter anyway
        val lastMessage = conversation.lastMessage
        val recipient = when {
            conversation.recipients.size == 1 || lastMessage == null -> conversation.recipients.firstOrNull()
            else -> conversation.recipients.find { recipient ->
                phoneNumberUtils.compare(recipient.address, lastMessage.address)
            }
        }
        val theme = colors.theme(recipient).theme

        holder.containerView.isActivated = isSelected(conversation.id)

        holder.avatars.recipients = conversation.recipients
        holder.title.collapseEnabled = conversation.recipients.size > 1
        holder.title.text = buildSpannedString {
            append(conversation.getTitle())
            if (conversation.draft.isNotEmpty()) {
                color(theme) { append(" " + context.getString(R.string.main_draft)) }
            }
        }
        holder.date.text = conversation.date.takeIf { it > 0 }?.let(dateFormatter::getConversationTimestamp)
        holder.snippet.text = when {
            conversation.draft.isNotEmpty() -> conversation.draft
            conversation.me -> context.getString(R.string.main_sender_you, conversation.snippet)
            else -> conversation.snippet
        }
        holder.pinned.isVisible = conversation.pinned
        holder.unread.setTint(theme)
        val sepPrefs = prefs.separator.get()
        if (sepPrefs) {
            holder.separator.isVisible = true
        } else {
            holder.separator.isVisible = false
        }
    }

    override fun getItemId(position: Int): Long {
        return getItem(position)?.id ?: -1
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position)?.unread == false) 0 else 1
    }
}
