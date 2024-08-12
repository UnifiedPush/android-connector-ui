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

class SelectDistributorDialogBuilder(
    private val context: Context,
    private val instances: List<String>,
    private val unifiedPushFunctions: UnifiedPushFunctions
) {
    var registrationDialogContent: RegistrationDialogContent = DefaultRegistrationDialogContent(context)

    fun show() {
        unifiedPushFunctions.getAckDistributor()?.let {
            instances.forEach { unifiedPushFunctions.registerApp(it) }
            return
        }
        val distributors = unifiedPushFunctions.getDistributors()
        when (distributors.size) {
            0 -> {
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

            1 -> {
                unifiedPushFunctions.saveDistributor(distributors.first())
                instances.forEach { unifiedPushFunctions.registerApp(it) }
            }

            else -> {
                val builder: AlertDialog.Builder = AlertDialog.Builder(context)
                builder.setTitle(registrationDialogContent.chooseDialog.title)

                val distributorsArray = distributors.toTypedArray()
                val distributorsNameArray = distributorsArray.map {
                    try {
                        val ai = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            context.packageManager.getApplicationInfo(
                                it,
                                PackageManager.ApplicationInfoFlags.of(
                                    PackageManager.GET_META_DATA.toLong()
                                )
                            )
                        } else {
                            context.packageManager.getApplicationInfo(it, 0)
                        }
                        context.packageManager.getApplicationLabel(ai)
                    } catch (e: PackageManager.NameNotFoundException) {
                        it
                    } as String
                }.toTypedArray()
                builder.setItems(distributorsNameArray) { _, which ->
                    val distributor = distributorsArray[which]
                    unifiedPushFunctions.saveDistributor(distributor)
                    Log.d(TAG, "saving: $distributor")
                    instances.forEach { unifiedPushFunctions.registerApp(it) }
                }
                val dialog: AlertDialog = builder.create()
                dialog.show()
            }
        }
    }

    private fun getNoDistributorAck(): Boolean {
        return context.getSharedPreferences(PREF_MASTER, Context.MODE_PRIVATE)
            .getBoolean(PREF_MASTER_DISTRIBUTOR_ACK, false)
    }

    private fun setNoDistributorAck(value: Boolean) {
        context.getSharedPreferences(PREF_MASTER, Context.MODE_PRIVATE).edit().putBoolean(PREF_MASTER_DISTRIBUTOR_ACK, value).apply()
    }
}