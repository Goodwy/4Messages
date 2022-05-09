package com.goodwy.messages.feature.blocking.numbers

import android.view.LayoutInflater
import android.view.ViewGroup
import com.goodwy.messages.R
import com.goodwy.messages.common.base.QkRealmAdapter
import com.goodwy.messages.common.base.QkViewHolder
import com.goodwy.messages.model.BlockedNumber
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import kotlinx.android.synthetic.main.blocked_number_list_item.*
import kotlinx.android.synthetic.main.blocked_number_list_item.view.*

class BlockedNumbersAdapter : QkRealmAdapter<BlockedNumber>() {

    val unblockAddress: Subject<Long> = PublishSubject.create()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QkViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.blocked_number_list_item, parent, false)
        return QkViewHolder(view).apply {
            containerView.unblock.setOnClickListener {
                val number = getItem(adapterPosition) ?: return@setOnClickListener
                unblockAddress.onNext(number.id)
            }
        }
    }

    override fun onBindViewHolder(holder: QkViewHolder, position: Int) {
        val item = getItem(position)!!

        holder.number.text = item.address
    }

}
