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
package com.goodwy.messages.feature.backup

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.view.children
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.goodwy.messages.BuildConfig
import com.jakewharton.rxbinding2.view.clicks
import com.goodwy.messages.R
import com.goodwy.messages.common.base.QkController
import com.goodwy.messages.common.util.DateFormatter
import com.goodwy.messages.common.util.extensions.*
import com.goodwy.messages.common.widget.PreferenceView
import com.goodwy.messages.injection.appComponent
import com.goodwy.messages.model.BackupFile
import com.goodwy.messages.repository.BackupRepository
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import kotlinx.android.synthetic.main.backup_controller.*
import kotlinx.android.synthetic.main.backup_list_dialog.view.*
import kotlinx.android.synthetic.main.preference_view.view.*
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class BackupController : QkController<BackupView, BackupState, BackupPresenter>(), BackupView {

    @Inject lateinit var adapter: BackupAdapter
    @Inject lateinit var dateFormatter: DateFormatter
    @Inject override lateinit var presenter: BackupPresenter

    private val PICK_IMPORT_SOURCE_INTENT = 11
    private val PICK_EXPORT_SOURCE_INTENT = 22
    private val PICK_OPEN_SOURCE_INTENT = 33
    private val activityVisibleSubject: Subject<Unit> = PublishSubject.create()
    private val confirmRestoreSubject: Subject<Unit> = PublishSubject.create()
    private val stopRestoreSubject: Subject<Unit> = PublishSubject.create()

    private val backupFilesDialog by lazy {
        val view = View.inflate(activity, R.layout.backup_list_dialog, null)
                .apply {
                    themedActivity?.colors?.theme()?.let { theme ->
                        select.setTextColor(theme.theme)
                    }
                    val directoryName = if (isRPlus()) Environment.getExternalStorageDirectory().toString() + "/Documents/4Messages/Backups"
                                        else Environment.getExternalStorageDirectory().toString() + "/4Messages/Backups"
                    directory.text = directoryName
                    files.adapter = adapter.apply { emptyView = empty }
                    select.setOnClickListener {
                        tryRestoreBackup()
                    }
                }

        AlertDialog.Builder(activity!!)
                .setView(view)
                .setCancelable(true)
                .create()
    }

    private val confirmRestoreDialog by lazy {
        AlertDialog.Builder(activity!!)
                .setTitle(R.string.backup_restore_confirm_title)
                .setMessage(R.string.backup_restore_confirm_message)
                .setPositiveButton(R.string.backup_restore_title, confirmRestoreSubject)
                .setNegativeButton(R.string.button_cancel, null)
                .create()
    }

    private val stopRestoreDialog by lazy {
        AlertDialog.Builder(activity!!)
                .setTitle(R.string.backup_restore_stop_title)
                .setMessage(R.string.backup_restore_stop_message)
                .setPositiveButton(R.string.button_stop, stopRestoreSubject)
                .setNegativeButton(R.string.button_cancel, null)
                .create()
    }

    init {
        appComponent.inject(this)
        layoutRes = R.layout.backup_controller
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        presenter.bindIntents(this)
        setTitle(R.string.backup_title)
        showBackButton(true)
    }

    override fun onViewCreated() {
        super.onViewCreated()

        themedActivity?.colors?.theme()?.let { theme ->
            progressBar.indeterminateTintList = ColorStateList.valueOf(theme.theme)
            progressBar.progressTintList = ColorStateList.valueOf(theme.theme)
            fab.setBackgroundTint(theme.theme)
            fabIcon.setTint(theme.textPrimary)
            fabLabel.setTextColor(theme.textPrimary)
        }

        // Make the list titles bold
        linearLayout.children
                .mapNotNull { it as? PreferenceView }
                .map { it.titleView }
                .forEach { it.setTypeface(it.typeface, Typeface.BOLD) }
    }

    override fun onActivityResumed(activity: Activity) {
        super.onActivityResumed(activity)
        activityVisibleSubject.onNext(Unit)
    }

    override fun render(state: BackupState) {
        when {
            state.backupProgress.running -> {
                progressIcon.setImageResource(R.drawable.ic_file_upload_black_24dp)
                progressTitle.setText(R.string.backup_backing_up)
                progressSummary.text = state.backupProgress.getLabel(activity!!)
                progressSummary.isVisible = progressSummary.text.isNotEmpty()
                progressCancel.isVisible = false
                val running = (state.backupProgress as? BackupRepository.Progress.Running)
                progressBar.isVisible = state.backupProgress.indeterminate || running?.max ?: 0 > 0
                progressBar.isIndeterminate = state.backupProgress.indeterminate
                progressBar.max = running?.max ?: 0
                progressBar.progress = running?.count ?: 0
                progress.isVisible = true
                fab.isVisible = false
            }

            state.restoreProgress.running -> {
                progressIcon.setImageResource(R.drawable.ic_file_download_black_24dp)
                progressTitle.setText(R.string.backup_restoring)
                progressSummary.text = state.restoreProgress.getLabel(activity!!)
                progressSummary.isVisible = progressSummary.text.isNotEmpty()
                progressCancel.isVisible = true
                val running = (state.restoreProgress as? BackupRepository.Progress.Running)
                progressBar.isVisible = state.restoreProgress.indeterminate || running?.max ?: 0 > 0
                progressBar.isIndeterminate = state.restoreProgress.indeterminate
                progressBar.max = running?.max ?: 0
                progressBar.progress = running?.count ?: 0
                progress.isVisible = true
                fab.isVisible = false
            }

            else -> {
                progress.isVisible = false
                fab.isVisible = true
            }
        }

        backup.summary = state.lastBackup

        adapter.data = state.backups

        fabIcon.setImageResource(when (state.upgraded) {
            true -> R.drawable.ic_file_upload_black_24dp
            false -> R.drawable.ic_star_black_24dp
        })

        fabLabel.setText(when (state.upgraded) {
            true -> R.string.backup_now
            false -> R.string.title_messages_plus
        })
    }

    override fun activityVisible(): Observable<*> = activityVisibleSubject

    override fun restoreClicks(): Observable<*> = restore.clicks()

    override fun restoreFileSelected(): Observable<BackupFile> = adapter.backupSelected
            .doOnNext { backupFilesDialog.dismiss() }

    override fun restoreConfirmed(): Observable<*> = confirmRestoreSubject

    override fun stopRestoreClicks(): Observable<*> = progressCancel.clicks()

    override fun stopRestoreConfirmed(): Observable<*> = stopRestoreSubject

    override fun fabClicks(): Observable<*> = fab.clicks()

    override fun requestStoragePermission() {
        ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val uri = Uri.parse("package:${BuildConfig.APPLICATION_ID}")
            startActivity(
                Intent(
                    Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                    uri
                )
            )
        } else {
            ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)
        }*/
    }

    override fun selectFile() = backupFilesDialog.show()

    override fun confirmRestore() = confirmRestoreDialog.show()

    override fun stopRestore() = stopRestoreDialog.show()

    override fun openDirectory() {
        backupFilesDialog.cancel()
        Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
            addFlags(
                Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION or
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION or
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            startActivityForResult(this, PICK_OPEN_SOURCE_INTENT)
        }
    }

    override fun tryRestoreBackup() {
        backupFilesDialog.cancel()
        Intent(Intent.ACTION_GET_CONTENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/json"
            startActivityForResult(this, PICK_IMPORT_SOURCE_INTENT)
        }
    }

    override fun tryPerformBackup() {
        val timestamp = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(System.currentTimeMillis())
        Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/json"
            putExtra(Intent.EXTRA_TITLE, "backup-$timestamp.json")
            //putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
            startActivityForResult(this, PICK_EXPORT_SOURCE_INTENT)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        if (requestCode == PICK_IMPORT_SOURCE_INTENT && resultCode == Activity.RESULT_OK && resultData != null && resultData.data != null) {
            val file = resultData!!.data!!
            val tempFile = getTempFile("messages", "backup.json")

            try {
                val inputStream = activity!!.contentResolver.openInputStream(file)
                val out = FileOutputStream(tempFile)
                inputStream!!.copyTo(out)
                //activity!!.makeToast(tempFile!!.absolutePath)
            } catch (e: Exception) {
                activity!!.makeToast(e.toString())
            }

            RestoreBackupService.start(activity!!, tempFile!!.absolutePath)
        }
    }

    override fun isRPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R

    private fun getTempFile(folderName: String, fileName: String): File? {
        val folder = File(activity!!.cacheDir, folderName)
        if (!folder.exists()) {
            if (!folder.mkdir()) {
                activity!!.makeToast(folder.mkdir().toString())
                return null
            }
        }

        return File(folder, fileName)
    }

}