package org.unifiedpush.android.connector.ui

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AlertDialog

private val TAG = SelectDistributorDialogsBuilder::class.simpleName
private const val PREF_MASTER = "org.unifiedpush.android.connector.ui"
private const val PREF_MASTER_DISTRIBUTOR_ACK = "distributor_ack"

/**
 * Interface containing UnifiedPush functions
 */
interface UnifiedPushFunctions {
    /**
     * Try to use default distributor.
     *
     * ```
     * tryUseDefaultDistributor(callback: (Boolean) -> Unit) =
     *             UnifiedPush.tryUseDefaultDistributor(context, callback)
     * ```
     */
    fun tryUseDefaultDistributor(callback: (Boolean) -> Unit)

    /**
     * Get acknowledged distributor.
     *
     * ```
     * getAckDistributor(): String? =
     *             UnifiedPush.getAckDistributor(context)
     * ```
     */
    fun getAckDistributor(): String?

    /**
     * Get list of installed distributors.
     *
     * ```
     * getDistributors(): List<String> =
     *             UnifiedPush.getDistributors(context, UnifiedPush.DEFAULT_FEATURES)
     * ```
     */
    fun getDistributors(): List<String>

    /**
     * Send a registration request to the distributor.
     *
     * ```
     * registerApp(instance: String) =
     *             UnifiedPush.registerApp(context, instance)
     * ```
     */
    fun registerApp(instance: String)

    /**
     * Save a distributor to use.
     *
     * ```
     * saveDistributor(distributor: String) =
     *             UnifiedPush.saveDistributor(context, distributor)
     * ```
     */
    fun saveDistributor(distributor: String)
}

/**
 * Main dialog builder, use [`run`][SelectDistributorDialogsBuilder.run] to select a distributor and register
 *
 * Extend and override functions or attributes if needed.
 *
 * @param context Context for fetching resources.
 * @param unifiedPushFunctions UnifiedPush functions to interact with the distributors.
 */
open class SelectDistributorDialogsBuilder(
    private val context: Context,
    private val unifiedPushFunctions: UnifiedPushFunctions
) {
    /** List of instances to request registration for. */
    open var instances: List<String> = listOf("default")

    /**
     * Use saved distributor if available.
     *
     * Set to `true` if you wish to re-register your app if it is available.
     *
     * Set to `false` if you wish to let the user change their distributor. It will still use the default
     * distributor if [mayUseDefault] is `true`.
     */
    open var mayUseCurrent: Boolean = true

    /**
     * Use default distributor if available.
     *
     * Set to `true` if you wish to fallback to the default distributor if available.
     *
     * Set to `false` if you wish to let the user change their distributor.
     */
    open var mayUseDefault: Boolean = true

    /** Contains content of the different dialogs. */
    open var registrationDialogContent: RegistrationDialogContent = DefaultRegistrationDialogContent(context)

    /**
     * Whether the dialog with the [IntroDialog] should be shown to the user.
     *
     * Called only if [mayUseDefault] is `true`.
     *
     * @return By default, `true` if the deeplink "unifiedpush://link" may be open by more
     * than one application, and the OS dialog request user choice
     */
    open fun shouldShowIntro(): Boolean {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("unifiedpush://link")
        }
        val pm = context.packageManager
        val resolveInfo = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
        // return true if this is "android"
        return resolveInfo?.activityInfo?.let {
            Log.d(TAG, "Package=${it.packageName}")
            it.packageName == "android"
        } ?: false
    }

    open fun showIntro() {
        val dialog = AlertDialog.Builder(context).apply {
            setTitle(registrationDialogContent.introDialog.title)
            val msg =
                SpannableString(registrationDialogContent.introDialog.message)
            Linkify.addLinks(msg, Linkify.WEB_URLS)
            setMessage(msg)
            setPositiveButton(registrationDialogContent.introDialog.okButton) { _, _ ->
                tryUseDefault()
            }
            create()
        }
        dialog.show()
    }

    open fun tryUseDefault() {
        unifiedPushFunctions.tryUseDefaultDistributor { success ->
            if (success) {
                registerAllInstance()
            } else {
                selectDistributorWithoutDefault()
            }
        }
    }

    /**
     * Called when no distributor are found. By default, it shows a dialog with [RegistrationDialogContent.noDistributorDialog] content.
     */
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
                    this@SelectDistributorDialogsBuilder.setNoDistributorAck(true)
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

    /**
     * Called when a distributor is selected. By default, it calls [UnifiedPushFunctions.saveDistributor] then [UnifiedPushFunctions.registerApp] for each instance.
     */
    open fun onDistributorSelected(distributor: String) {
        Log.d(TAG, "saving: $distributor")
        unifiedPushFunctions.saveDistributor(distributor)
        registerAllInstance()
    }

    /**
     * Called when many distributors are found. By default, it shows a dialog to ask which distributor to pick with [RegistrationDialogContent.chooseDialog] content.
     */
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

    open fun registerAllInstance() {
        instances.forEach { unifiedPushFunctions.registerApp(it) }
    }

    /**
     * Show a dialog if needed to ask user's distributor and request registration for all instances
     *
     * Calling this method for the first time, or if the distributor has been removed:
     * - If there is no distributor, it will inform the user they need one.
     * - If there is a single distributor, it will register to it.
     * - If there are many distributors, it will open a dialog to ask the user which one to use.
     *
     * Once a distributor is saved, calling this method will register again to the saved distributor.
     */
    fun run() {
        // 1. If a distributor is already saved, use it and register again all instances
        if (mayUseCurrent) {
            unifiedPushFunctions.getAckDistributor()?.let {
                registerAllInstance()
                return
            }
        }
        // 2. If we want to try the user's default
        // It can be used when the user enable UnifiedPush for the first time,
        // or when the main activity is started
        if (mayUseDefault) {
            // 2.a We inform the user they
            if (shouldShowIntro()) {
                showIntro()
            } else {
                tryUseDefault()
            }
        } else {
            selectDistributorWithoutDefault()
        }
    }

    /**
     * Select distributor without trying to use the user's default one.
     *
     * This can be used when the users want to change the distributor they
     * want to use for the app.
     *
     * Run when [mayUseDefault] is `false` or when [UnifiedPushFunctions.tryUseDefaultDistributor]
     * doesn't succeed.
     */
    open fun selectDistributorWithoutDefault() {
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
