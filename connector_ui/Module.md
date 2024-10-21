# Module connector_ui

Offers a customizable dialog to ask the users what distributor they want to use before registering your application.

One of the main purpose of UnifiedPush is to let the users chose the way they receive their notifications. If many distributors are installed on the system, you will need to ask the users what they prefere to use.

## Import the library

Add the dependency to the module build.gradle. Replace {VERSION} with the [latest version](https://central.sonatype.com/artifact/org.unifiedpush.android/connector-ui).

```groovy
dependencies {
    // ...
    implementation 'org.unifiedpush.android:connector-ui:{VERSION}'
}
```

## Use the dialog

To use this dialog, you need to pass [`UnifiedPushFunctions`](org.unifiedpush.android.connector.ui.UnifiedPushFunctions) to the [`SelectDistributorDialogsBuilder`][org.unifiedpush.android.connector.ui.SelectDistributorDialogsBuilder], then call its [`run`][org.unifiedpush.android.connector.ui.SelectDistributorDialogsBuilder.run] method:
- If there is no distributor, it will inform the user they need one.
- If there is a single distributor, it will register to it.
- If there are many distributors, it will open a dialog to ask the user which one to use.

Once a distributor is saved, calling this method will register again to the saved distributor.

<div class="tabs">
<input class="tabs_control hidden" type="radio" id="tabs-0-receiver-0" name="tabs-0" checked>
<label class="tabs_label" for="tabs-0-receiver-0">Kotlin</label>
<div class="tabs_content">
<!-- CONTENT KOTLIN -->

```kotlin
import org.unifiedpush.android.connector.INSTANCE_DEFAULT
import org.unifiedpush.android.connector.UnifiedPush
import org.unifiedpush.android.connector.ui.SelectDistributorDialogsBuilder
import org.unifiedpush.android.connector.ui.UnifiedPushFunctions
/* ... */

// Options:
// "instances" can be used to handle multiple registrations

SelectDistributorDialogsBuilder(
    context,
    listOf<String>(INSTANCE_DEFAULT),
    object : UnifiedPushFunctions {
        override fun getAckDistributor(): String? =
            UnifiedPush.getAckDistributor(context)

        override fun getDistributors(): List<String> =
            UnifiedPush.getDistributors(context, UnifiedPush.DEFAULT_FEATURES)

        override fun registerApp(instance: String) =
            UnifiedPush.registerApp(context, instance, UnifiedPush.DEFAULT_FEATURES)

        override fun saveDistributor(distributor: String) =
            UnifiedPush.saveDistributor(context, distributor)
    }
).run()
```

<!-- END KOTLIN -->
</div>
<input class="tabs_control hidden" type="radio" id="tabs-0-receiver-1" name="tabs-0">
<label class="tabs_label" for="tabs-0-receiver-1">Java</label>
<div class="tabs_content">
<!-- CONTENT JAVA -->

```java
import static org.unifiedpush.android.connector.ConstantsKt.INSTANCE_DEFAULT;
import org.unifiedpush.android.connector.UnifiedPush;
import org.unifiedpush.android.connector.ui.SelectDistributorDialogsBuilder;
import org.unifiedpush.android.connector.ui.UnifiedPushFunctions;
/* ... */

// First you need to create a class that implements UnifiedPushFunctions:
private class UPFunctions implements UnifiedPushFunctions {
    Context context;
    public UPFunctions(Context context) {
        this.context = context;
    }
    @Nullable
    @Override
    public String getAckDistributor() {
        return UnifiedPush.getAckDistributor(context);
    }

    @NonNull
    @Override
    public List<String> getDistributors() {
        return UnifiedPush.getDistributors(context, UnifiedPush.getDEFAULT_FEATURES());
    }

    @Override
    public void registerApp(@NonNull String instance) {
        UnifiedPush.registerApp(context, instance, UnifiedPush.getDEFAULT_FEATURES(), "MyApp");
    }

    @Override
    public void saveDistributor(@NonNull String distributor) {
        UnifiedPush.saveDistributor(context, distributor);
    }
}

/* ... */

// Then you can use the dialog in a function:
// Options:
// "instances" can be used to handle multiple registrations

    SelectDistributorDialogsBuilder builder = new SelectDistributorDialogsBuilder(
        this,
        List.of(INSTANCE_DEFAULT),
        new UPFunctions(this)
    );
    builder.run();
```

<!-- END JAVA -->
</div>
</div>

## Customization

It is possible to customize this dialog by extending [`SelectDistributorDialogsBuilder`][org.unifiedpush.android.connector.ui.SelectDistributorDialogsBuilder]:

<div class="tabs">
<input class="tabs_control hidden" type="radio" id="tabs-1-receiver-0" name="tabs-1" checked>
<label class="tabs_label" for="tabs-1-receiver-0">Kotlin</label>
<div class="tabs_content">
<!-- CONTENT KOTLIN -->

```kotlin
SelectDistributorDialogsBuilder(
    context,
    listOf<String>(INSTANCE_DEFAULT),
    object : UnifiedPushFunctions {/*...*/}
){
    // See https://codeberg.org/UnifiedPush/android-connector-ui/src/branch/main/connector_ui/src/main/java/org/unifiedpush/android/connector/ui/RegistrationDialogContent.kt
    override var registrationDialogContent = MyDialogContent

    override fun onNoDistributorFound() {
        // TODO
    }

    override fun onDistributorSelected(distributor: String) {
        // TODO
    }

    override fun onManyDistributorsFound(distributors: List<String>) {
        // TODO
    }
}.run()
```

<!-- END KOTLIN -->
</div>
<input class="tabs_control hidden" type="radio" id="tabs-1-receiver-1" name="tabs-1">
<label class="tabs_label" for="tabs-1-receiver-1">Java</label>
<div class="tabs_content">
<!-- CONTENT JAVA -->

```java
private class MyDialogBuilder extends SelectDistributorDialogsBuilder {
        public MyDialogBuilder(@NonNull Context context, @NonNull List<String> instances, @NonNull UnifiedPushFunctions unifiedPushFunctions) {
            super(context, instances, unifiedPushFunctions);
        }
        // See https://codeberg.org/UnifiedPush/android-connector-ui/src/branch/main/connector_ui/src/main/java/org/unifiedpush/android/connector/ui/RegistrationDialogContent.kt
        RegistrationDialogContent registrationDialogContent = MyContent;
        @Override
        public void onNoDistributorFound() {
            // TODO
        }
        @Override
        public void onDistributorSelected(@NonNull String distributor) {
            // TODO
        }
        @Override
        public void onManyDistributorsFound(@NonNull List<String> distributors) {
            // TODO
        }
    }
```

<!-- END JAVA -->
</div>
</div>
