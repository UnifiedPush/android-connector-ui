# UnifiedPush android-connector-ui
![Release](https://jitpack.io/v/UnifiedPush/android-connector-ui.svg)

This library provides a dialog to let the user pick a distributor and register to it.

## Documentation

Documentation available at <https://unifiedpush.org>

v1.0.x requires minSdk > 16

v1.1.x requires minSdk > 21 and uses a more recent version of appcompat

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

        override fun register(instance: String) =
            UnifiedPush.register(this@MainActivity, instance, features)

        override fun saveDistributor(distributor: String) =
            UnifiedPush.saveDistributor(this@MainActivity, distributor)
    }
).show()
```

It is possible to customize your dialog. For instance, to change the content of the dialog and to do nothing if there isn't any distributor:

```
SelectDistributorDialogBuilder(
    this,
    listOf("default"),
    object : UnifiedPushFunctions {
        override fun getAckDistributor(): String? =
            UnifiedPush.getAckDistributor(this@MainActivity)

        override fun getDistributors(): List<String> =
            UnifiedPush.getDistributors(this@MainActivity, features)

        override fun register(instance: String) =
            UnifiedPush.register(this@MainActivity, instance, features)

        override fun saveDistributor(distributor: String) =
            UnifiedPush.saveDistributor(this@MainActivity, distributor)
    }
){
    override var registrationDialogContent: MyRegistrationDialogContent

    override fun onNoDistributorFound() {
        // do nothing
    }
}.show()
```

# Funding

This project is funded through [NGI Zero Core](https://nlnet.nl/core), a fund established by [NLnet](https://nlnet.nl) with financial support from the European Commission's [Next Generation Internet](https://ngi.eu) program. Learn more at the [NLnet project page](https://nlnet.nl/project/UnifiedPush).

[<img src="https://codeberg.org/UnifiedPush/documentation/raw/branch/main/static/img/nlnet_banner.png" alt="NLnet foundation logo" width="20%" />](https://nlnet.nl)
[<img src="https://codeberg.org/UnifiedPush/documentation/raw/branch/main/static/img/NGI0_tag.svg" alt="NGI Zero Logo" width="20%" />](https://nlnet.nl/core)
