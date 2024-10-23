package org.unifiedpush.android.connector.ui

import android.content.Context

/** Defines content that can be shown during [SelectDistributorDialogsBuilder.run]. */
interface RegistrationDialogContent {
    /** Content for the dialog if the OS picker is going to be called.*/
    val introDialog: IntroDialog

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
    override val introDialog = DefaultIntroDialog(context)
    override val noDistributorDialog = DefaultNoDistributorDialog(context)
    override val chooseDialog = DefaultChooseDialog(context)
}

/** Defines content for the dialog explaining they are going to choose a distributor */
interface IntroDialog {
    /** Dialog title. */
    var title: String

    /** Dialog message. */
    var message: String

    /** Text on positive button */
    var okButton: String
}

/**
 * Default [IntroDialog]
 *
 * @param context Context for fetching resources.
 */
data class DefaultIntroDialog(val context: Context): IntroDialog {
    override var title = context.getString(R.string.unifiedpush_dialog_intro_title)
    override var message = context.getString(R.string.unifiedpush_dialog_intro_message)
    override var okButton = context.getString(android.R.string.ok)
}

/** Defines content for the dialog if no distributor are installed. */
interface NoDistributorDialog {
    /** Dialog title. */
    var title: String

    /** Dialog message. */
    var message: String

    /** Text on positive button */
    var okButton: String

    /** Text on negative button. */
    var ignoreButton: String
}

/**
 * Default [NoDistributorDialog].
 *
 * @param context Context for fetching resources.
 */
data class DefaultNoDistributorDialog(val context: Context) : NoDistributorDialog {
    override var title = context.getString(R.string.unifiedpush_dialog_no_distributor_title)
    override var message = context.getString(R.string.unifiedpush_dialog_no_distributor_message)
    override var okButton = context.getString(android.R.string.ok)
    override var ignoreButton =
        context.getString(R.string.unifiedpush_dialog_no_distributor_negative)
}

/** Defines content for the dialog if multiple distributors are installed. */
interface ChooseDialog {
    /** Dialog title. */
    var title: String
}

/**
 * Default [ChooseDialog].
 *
 * @param context Context for fetching resources.
 */
data class DefaultChooseDialog(val context: Context) : ChooseDialog {
    override var title = context.getString(R.string.unifiedpush_dialog_choose_title)
}