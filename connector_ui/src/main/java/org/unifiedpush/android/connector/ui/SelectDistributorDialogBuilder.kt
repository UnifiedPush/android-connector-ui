package org.unifiedpush.android.connector.ui

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AlertDialog

private val TAG = SelectDistributorDialogBuilder::class.simpleName
private const val PREF_MASTER = "org.unifiedpush.android.connector.ui"
private const val PREF_MASTER_DISTRIBUTOR_ACK = "distributor_ack"

interface UnifiedPushFunctions {
    fun getAckDistributor(): String?
    fun getDistributors(): List<String>
    fun registerApp(instance: String)
    fun saveDistributor(distributor: String)
}

open class SelectDistributorDialogBuilder(
    private val context: Context,
    private val instances: List<String>,
    private val unifiedPushFunctions: UnifiedPushFunctions
) {
    open var registrationDialogContent: RegistrationDialogContent = DefaultRegistrationDialogContent(context)

    open fun onNoDistributorFound() {
        if (!this.getNoDistributorAck()) {
            val builder = AlertDialog.Builder(context).apply {
                setTitle(registrationDialogContent.noDistributorDialog.title)
                val msg =
                    SpannableString(registrationDialogContent.noDistributorDialog.message)
                Linkify.addLinks(msg, Linkify.WEB_URLS)
                setMessage(msg)
                setPositiveButton(registrationDialogContent.noDistributorDialog.okButton) { _, _ -> }
                setNegativeButton(registrationDialogContent.noDistributorDialog.ignoreButton) { _, _ ->
                    this@SelectDistributorDialogBuilder.setNoDistributorAck(true)
                }
            }
            val dialog = builder.create()
            dialog.setOnShowListener {
                dialog.findViewById<TextView>(android.R.id.message)?.let {
                    it.movementMethod = LinkMovementMethod.getInstance()
                }
            }
            dialog.show()
        } else {
            Log.d(TAG, "User already know there isn't any distributor")
        }
    }

    open fun onDistributorSelected(distributor: String) {
        Log.d(TAG, "saving: $distributor")
        unifiedPushFunctions.saveDistributor(distributor)
        instances.forEach { unifiedPushFunctions.registerApp(it) }
    }

    open fun onManyDistributorsFound(distributors: List<String>) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle(registrationDialogContent.chooseDialog.title)

        val distributorsArray = distributors.toTypedArray()
        val distributorsNameArray = distributorsArray.map{
            getApplicationName(it)
        }.toTypedArray()
        builder.setItems(distributorsNameArray) { _, which ->
            onDistributorSelected(distributorsArray[which])
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    fun show() {
        unifiedPushFunctions.getAckDistributor()?.let {
            instances.forEach { unifiedPushFunctions.registerApp(it) }
            return
        }
        val distributors = unifiedPushFunctions.getDistributors()
        when (distributors.size) {
            0 -> onNoDistributorFound()
            1 -> onDistributorSelected(distributors.first())
            else -> onManyDistributorsFound(distributors)
        }
    }

    private fun getApplicationName(applicationId: String): String {
        return try {
            val ai = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getApplicationInfo(
                    applicationId,
                    PackageManager.ApplicationInfoFlags.of(
                        PackageManager.GET_META_DATA.toLong()
                    )
                )
            } else {
                context.packageManager.getApplicationInfo(applicationId, 0)
            }
            context.packageManager.getApplicationLabel(ai)
        } catch (e: PackageManager.NameNotFoundException) {
            applicationId
        } as String
    }

    private fun getNoDistributorAck(): Boolean {
        return context.getSharedPreferences(PREF_MASTER, Context.MODE_PRIVATE)
            .getBoolean(PREF_MASTER_DISTRIBUTOR_ACK, false)
    }

    private fun setNoDistributorAck(value: Boolean) {
        context.getSharedPreferences(PREF_MASTER, Context.MODE_PRIVATE).edit().putBoolean(PREF_MASTER_DISTRIBUTOR_ACK, value).apply()
    }
}
