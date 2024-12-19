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

var builder = SelectDistributorDialogsBuilder(
    context,
    object : UnifiedPushFunctions {
        override fun tryUseDefaultDistributor(callback: (Boolean) -> Unit) =
            UnifiedPush.tryUseDefaultDistributor(context, callback)

        override fun getAckDistributor(): String? =
            UnifiedPush.getAckDistributor(context)

        override fun getDistributors(): List<String> =
            UnifiedPush.getDistributors(context)

        override fun register(instance: String) =
            UnifiedPush.register(context, instance)

        override fun saveDistributor(distributor: String) =
            UnifiedPush.saveDistributor(context, distributor)
    }
)
builder.apply {
    // If you use multiple registrations for your application
    instances = listOf("registration1","registration2")
    // If you are calling the function to let the user changing their distributor
    // We should not use default nor current
    // /!\ Keep them true if you subscribe to UnifiedPush for the first time, or want to do a
    // basic re-registration.
    mayUseCurrent = false
    mayUseDefault = false
}
builder.run()
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
import kotlin.jvm.functions.Function1;
/* ... */

// First you need to create a class that implements UnifiedPushFunctions:
private class UPFunctions implements UnifiedPushFunctions {
    Context context;
    public UPFunctions(Context context) {
        this.context = context;
    }
    @Override
    public void tryUseDefaultDistributor(@NonNull Function1<? super Boolean, Unit> callback) {
        UnifiedPush.tryUseDefaultDistributor(context, callback);

    }

    @Nullable
    @Override
    public String getAckDistributor() {
        return UnifiedPush.getAckDistributor(context);
    }

    @NonNull
    @Override
    public List<String> getDistributors() {
        return UnifiedPush.getDistributors(context);
    }

    @Override
    public void register(@NonNull String instance) {
        UnifiedPush.register(context, instance, "MyApp", VAPIDKey);
    }

    @Override
    public void saveDistributor(@NonNull String distributor) {
        UnifiedPush.saveDistributor(context, distributor);
    }
}

/* ... */

    SelectDistributorDialogsBuilder builder = new SelectDistributorDialogsBuilder(
        this,
        new UPFunctions(this)
    );
    // If you use multiple registrations for your application
    builder.setInstances(List.of("registration1", "registration2"));
    // If you are calling the function to let the user changing their distributor
    // We should not use default nor current
    // /!\ Keep them true if you subscribe to UnifiedPush for the first time, or want to do a
    // basic re-registration.
    builder.setMayUseCurrent(false);
    builder.setMayUseDefault(false);
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
object : SelectDistributorDialogsBuilder(
    context,
    object : UnifiedPushFunctions {/*...*/}
){
    // See RegistrationDialogContent doc.
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
        // See RegistrationDialogContent doc.
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
