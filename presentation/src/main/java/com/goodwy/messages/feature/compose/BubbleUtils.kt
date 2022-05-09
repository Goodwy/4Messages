package com.goodwy.messages.feature.compose

import com.goodwy.messages.R
import com.goodwy.messages.model.Message
import java.util.concurrent.TimeUnit

object BubbleUtils {

    const val TIMESTAMP_THRESHOLD = 10

    fun canGroup(message: Message, other: Message?): Boolean {
        if (other == null) return false
        val diff = TimeUnit.MILLISECONDS.toMinutes(Math.abs(message.date - other.date))
        return message.compareSender(other) && diff < TIMESTAMP_THRESHOLD
    }

    fun getBubble(emojiOnly: Boolean, canGroupWithPrevious: Boolean, canGroupWithNext: Boolean, isMe: Boolean, style: Int = 0): Int {
        if (style == 1) {
            return when {
                emojiOnly -> R.drawable.message_emoji
                !canGroupWithPrevious && canGroupWithNext -> R.drawable.message_ios_no_last
                canGroupWithPrevious && canGroupWithNext -> R.drawable.message_ios_no_last
                canGroupWithPrevious && !canGroupWithNext -> if (isMe) R.drawable.message_ios_out_last else R.drawable.message_ios_in_last
                else -> if (isMe) R.drawable.message_ios_out_last else R.drawable.message_ios_in_last
            }
        } else if (style == 2) {
            return when {
                emojiOnly -> R.drawable.message_emoji
                !canGroupWithPrevious && canGroupWithNext -> R.drawable.message_only
                canGroupWithPrevious && canGroupWithNext -> R.drawable.message_only
                canGroupWithPrevious && !canGroupWithNext -> if (isMe) R.drawable.message_simple_out_last else R.drawable.message_simple_in_last
                else -> if (isMe) R.drawable.message_simple_out_last else R.drawable.message_simple_in_last
            }
        } else if (style == 3) {
            return when {
                emojiOnly -> R.drawable.message_emoji
                !canGroupWithPrevious && canGroupWithNext -> if (isMe) R.drawable.message_triangle_out_only else R.drawable.message_triangle_in_only
                canGroupWithPrevious && canGroupWithNext -> if (isMe) R.drawable.message_triangle_out_only else R.drawable.message_triangle_in_only
                canGroupWithPrevious && !canGroupWithNext -> if (isMe) R.drawable.message_triangle_out_last else R.drawable.message_triangle_in_last
                else -> if (isMe) R.drawable.message_triangle_out_last else R.drawable.message_triangle_in_last
            }
        } else {
            return when {
                emojiOnly -> R.drawable.message_emoji
                !canGroupWithPrevious && canGroupWithNext -> if (isMe) R.drawable.message_out_first else R.drawable.message_in_first
                canGroupWithPrevious && canGroupWithNext -> if (isMe) R.drawable.message_out_middle else R.drawable.message_in_middle
                canGroupWithPrevious && !canGroupWithNext -> if (isMe) R.drawable.message_out_last else R.drawable.message_in_last
                else -> R.drawable.message_only
            }
        }
    }

}