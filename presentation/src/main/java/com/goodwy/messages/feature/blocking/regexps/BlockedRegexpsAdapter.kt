package com.goodwy.messages.feature.blocking.regexps

import android.view.LayoutInflater
import android.view.ViewGroup
import com.goodwy.messages.R
import com.goodwy.messages.common.base.QkRealmAdapter
import com.goodwy.messages.common.base.QkViewHolder
import com.goodwy.messages.model.BlockedRegex
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import kotlinx.android.synthetic.main.blocked_regex_list_item.*
import kotlinx.android.synthetic.main.blocked_regex_list_item.view.*

class BlockedRegexpsAdapter : QkRealmAdapter<BlockedRegex>() {

    val unblockRegex: Subject<Long> = PublishSubject.create()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QkViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.blocked_regex_list_item, parent, false)
        return QkViewHolder(view).apply {
            containerView.unblock.setOnClickListener {
                val regex = getItem(adapterPosition) ?: return@setOnClickListener
                unblockRegex.onNext(regex.id)
            }
        }
    }

    override fun onBindViewHolder(holder: QkViewHolder, position: Int) {
        val item = getItem(position)!!

        holder.regex.text = item.regex
    }

}
