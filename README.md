# UnifiedPush android-connector-ui
![Release](https://jitpack.io/v/UnifiedPush/android-connector-ui.svg)

This library provides a dialog to let the user pick a distributor and register to it.

## Documentation

Documentation available at <https://unifiedpush.org>

v1.x.x requires minSdk > 16

v2.x.x requires minSdk > 21 and uses androidx library

## Usage

```
SelectDistributorDialogBuilder(
    this,
    listOf("default"),
    object : UnifiedPushFunctions {
        override fun getAckDistributor(): String? =
            UnifiedPush.getAckDistributor(this@MainActivity)

        override fun getDistributors(): List<String> =
            UnifiedPush.getDistributors(this@MainActivity, features)

        override fun registerApp(instance: String) =
            UnifiedPush.registerApp(this@MainActivity, instance, features)

        override fun saveDistributor(distributor: String) =
            UnifiedPush.saveDistributor(this@MainActivity, distributor)
    }
).show()
```
