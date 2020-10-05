# Starling Capture

[![build](https://github.com/numbersprotocol/starling-capture/workflows/build/badge.svg)](https://github.com/numbersprotocol/starling-capture/actions?query=workflow%3Abuild)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/1713765ab0ea4f068d40db53c44ca488)](https://www.codacy.com/gh/numbersprotocol/starling-capture?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=numbersprotocol/starling-capture&amp;utm_campaign=Badge_Grade)

## Highlight Features

* Generate digital proof on media assets created.
* Support [HTC Zion](https://www.htcexodus.com/tw/zion/) for hardware signature on HTC Exodus devices.
* Support [Canon Camera Control API (CCAPI)](https://developercommunity.usa.canon.com/canon?id=canon_index).

## Getting Started

Android Studio 4.0 or later is required.

Open the Build Variants tool window by clicking __View > Tool Windows > Build Variants__. Then, set the active build variant to `masterQa` in Android Studio. Now, you can build the debuggable APK(s). Or, you can use the following commands to build the APK with the main product flavor:

```bash
./gradlew assembleMasterQa
```

To install the build to your device (or emulator), follow the steps in [the Android documentation](https://developer.android.com/studio/run). Or, use the following command:

```bash
./gradlew installMasterQa
```

### Verification

To verify the signature, create the JSON string from the `SortedProofInformation` class and use it as the message. 

#### Examples

* Python: See the [README](./util/verification/README.md) in `/util/verifcation/`.
* Kotlin: See the [Crypto.kt#String.verifyWithSha256AndEcdsa()](./app/src/main/java/io/numbersprotocol/starlingcapture/util/Crypto.kt#L56) method.

## Development

### Product Flavors

#### Master (Main) Flavor

* This is the public flavor. 
* It should be always buildable without manual configurations.
* This flavor only contains a sample publisher.

#### Internal Flavor

* __This is an internal product flavor, which cannot be build directly.__
* Crashlytics is enabled.

##### Build Requirements

Set the system environment variable `NUMBERS_STORAGE_BASE_URL` before build the app by appending the following string in `~/.profile`:

``` txt
export NUMBERS_STORAGE_BASE_URL="THE PRIVATE BASE URL"
```

### Build Types

#### Debug

* LeakCanary is enabled.

#### QA

* The quality of this build type should be product-ready. Thus, it should not contain any visible debugging artifacts (e.g. LeakCanary) except error messages. 

### Tech Stack

* Kotlin
  * Coroutine
  * [Flow](https://kotlinlang.org/docs/reference/coroutines/flow.html)
* AndroidX
* [MVVM Architecture](https://developer.android.com/jetpack/guide)
* Android Architecture Component
  * Paging Library
  * Work Manager
  * Preference Library
  * Navigation Component
  * Room Database
* [Material Component](https://material.io/develop/android/docs/getting-started/)
* [Koin](https://insert-koin.io/) - dependecy injection
* Retrofit - networking
* Moshi - proof serialization
* Coil - image loading
* Timber - logging
* LeakCanary - memory leak detection

### Contribution

* The committed codes should pass all GitHub workflows.
* The committed codes should not have warnings from Android Studio linter. You can use `./gradlew lint` to verify.
* The committed codes should not have memory leak reports by LeakCanary, which is enabled in the _debug_ variant. 

#### Convention

The name in `string.xml` should match the English content in snake case. If the content of the string is a long message, the name should start with `message_`. For example:

``` xml
<string name="public_key_signature">Public Key Signature</string>
<string name="message_are_you_sure">The action cannot be undone.</string>
```

### Architecture

![architecture](https://user-images.githubusercontent.com/14951000/84884284-1a53ce80-b0c4-11ea-99c3-4aea6a4cf276.png)

#### Source Providers

Currently, three types of media source are implemented:

1. Internal camera (image)
1. Internal camera (video)
1. Canon Camera with CCAPI (image and video)

The components regarding media source should be placed in `./app/src/main/.../source/`.

#### Information Providers

Currently, we only use [android-info-snapshot](https://github.com/numbersprotocol/android-info-snapshot) as information provider. The components regarding information collection should be placed in `./app/src/main/.../collector/information/`.

To add new information provider, 

1. Extends the `InformationProvider`.
1. Override the `provideInformation()` method.
1. Store the information to the DB with `InformationRepository` class.

#### Signature Providers

Currently, two types of signature provider are implemented:

1. AndroidOpenSSL (default signature)
1. Zion signature (opt-in)

The components regarding information collection should be placed in `./app/src/main/.../collector/signature/`.

To add new signature provider, 

1. Extends the `SignatureProvider`.
1. Override the `provideSignature()` method.
1. Sign the `SortedProofInformation` from the given proof hash.
1. Store the signature to the DB with `SignatureRepository` class.

#### Publishers

Currently, we only provide a sample publisher which does nothing.

The components regarding information collection should be placed in `./app/src/main/.../publisher/`.

To add new signature provider, see the `SampleProofPublisher` class for details.

### Data Flow

![dataflow](https://user-images.githubusercontent.com/14951000/86208732-af19fa00-bba3-11ea-97fe-0b8c2064c96a.png)

#### Steps in `ProofCollector`

1. Store the proof raw file into the internal directory.
1. Store the hash of proof into proof repository.
1. Collect information.
1. Sign the proof and its collected information even if some information providers failed.

### Serialization Schema

#### `SortedProofInformation` (Metadata)

The `SortedProofInformation` class provides the message of signature provider.

``` txt
{
    proof: {
        hash: String,
        mimeType: String,
        timestamp: Long
    },
    information: [{
        provider: String,
        name: String,
        value: String
    },
    ...
  ]
}
```

Example:

``` json
{
   "proof":{
      "hash":"1837bc2c546d46c705204cf9f857b90b1dbffd2a7988451670119945ba39a10b",
      "mimeType":"image/jpeg",
      "timestamp":123456789
   },
   "information":[
      {
         "provider":"ProofMode",
         "name":"Current Location",
         "value":"121.0, 23.0"
      },
      ...
   ]
}
```

#### Signature

``` txt
[
    {
        proofHash: String,
        provider: String,
        signature: String,
        publicKey: String
    },
    ...
]
```

Example:

``` json
[
  {
    "proofHash": "845ace0144620a18abf1d73c1dceaa51ea78cd5d791dbbbd2368d75260431bd9",
    "provider": "AndroidOpenSSL",
    "signature": "3046022100b96babf7fb1a374792ce47cebdf0b5a40166352a4e8aed2d8e84a04699898c72022100ec0646e318a0701f794fd0a3dc28da41ff864a1f9156e694b890a1800edd86b0",
    "publicKey": "3059301306072a8648ce3d020106082a8648ce3d030107034200043e4ba565aa9158b9aeafc1bb4a970bfc7fcdcc398c35bb525aedd37bbf459dbd30868b909ec6b78f7904474c225e02f45c2384b0f4ece0d68e2c3c84fce04686"
  },
  {
    "proofHash": "845ace0144620a18abf1d73c1dceaa51ea78cd5d791dbbbd2368d75260431bd9",
    "provider": "Zion",
    "signature": "3045022100dbfe89fe13a4758f2124fc35d440d6ca8a6b3c3d72429a3a70b3a8146695c0db02204d8d387bba770d16e100e05109061897013317682a8cd2ac45162d381effa1ee",
    "publicKey": "Session:\n3059301306072a8648ce3d020106082a8648ce3d03010703420004b4b85c26384dda113f029cfb3b71c1769a44f78093b91b8bd5965506dc3ea00b4abc780d93a23f4dda5ae65c95f61a31a808b2e22f654cfcf2b76905046f4992\n\nReceive:\n03583ea032d0607a9d0d9748b445e4e170277921ed3eebec10795521be11a2f04d\n\nSend:\n03583ea032d0607a9d0d9748b445e4e170277921ed3eebec10795521be11a2f04d"
  },
  ...
]
```

## Release

1. Increase the `versionName` and `versionCode` in app/build.gradle file.
1. Update the CHANGELOG.md file.
1. Commit and push the update.
1. Go to __Action > release > Run workflow > Branch: develop > Run workflow__ to automatically create GitHub Release with development tag and deploy apps on the Google Play Console.

## Caveat

* The default keypair is not encrypted in AndroidKeyStore. Thus, if the device is rooted, your default key pair could be compromised.