package org.unifiedpush.android.connector.ui

import android.content.Context

/** Defines content that can be shown during [SelectDistributorDialogsBuilder.run]. */
interface RegistrationDialogContent {
    /** Content for the dialog if no distributor is installed. */
    val noDistributorDialog: NoDistributorDialog

    /** Content for the dialog if many distributors are installed. */
    val chooseDialog: ChooseDialog
}

/**
 * Default [RegistrationDialogContent]
 *
 * @param context Context for fetching resources.
 */
data class DefaultRegistrationDialogContent(val context: Context) : RegistrationDialogContent {
    override val noDistributorDialog = DefaultNoDistributorDialog(context)
    override val chooseDialog = DefaultChooseDialog(context)
}

/** Defines content for the dialog if no distributor are installed. */
interface NoDistributorDialog {
    /** Dialog title. */
    val title: String

    /** Dialog message. */
    var message: String

    /** Text on positive button */
    val okButton: String

    /** Text on negative button. */
    val ignoreButton: String
}

/**
 * Default [NoDistributorDialog].
 *
 * @param context Context for fetching resources.
 */
data class DefaultNoDistributorDialog(val context: Context) : NoDistributorDialog {
    override val title = context.getString(R.string.unified_push_dialog_no_distributor_title)
    override var message = context.getString(R.string.unified_push_dialog_no_distributor_message)
    override val okButton = context.getString(android.R.string.ok)
    override val ignoreButton =
        context.getString(R.string.unified_push_dialog_no_distributor_negative)
}

/** Defines content for the dialog if multiple distributors are installed. */
interface ChooseDialog {
    /** Dialog title. */
    val title: String
}

/**
 * Default [ChooseDialog].
 *
 * @param context Context for fetching resources.
 */
data class DefaultChooseDialog(val context: Context) : ChooseDialog {
    override val title = context.getString(R.string.unified_push_dialog_choose_title)
}