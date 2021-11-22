# Kiwrious Android Library Project


## Library installation

```groovy
android {
   defaultConfig {
      minSdkVersion 26
   }
}
```

```groovy
dependencies {
   implementation 'com.kiwrious.sdk.android:kiwrious-android-library:1.0.2'
}
```

```groovy
 repositories {
    mavenCentral()
}
```

## Library usage

### Modify AndroidManifest.xml
```xml
<manifest>
   <application>
      <activity>
         <!-- add new intent filter -->
         <intent-filter>
            <action android:name="android.hardware.usb.action.USB_DEVICE_ATTACHED" />
         </intent-filter>
         <!-- add new meta data -->
         <meta-data
             android:name="android.hardware.usb.action.USB_DEVICE_ATTACHED"
             android:resource="@xml/device_filter" />
      </activity>
   </application>
</manifest>
```

### Import packages
```java
import org.ahlab.kiwrious.android.KiwriousReader;
import android.content.Context;
```
### Initiate Reader
```java
Context context = getApplicationContext(); // get your application context
KiwriousReader kiwriousReader = KiwriousReader.getInstance(context); // pass it to kiwrious library
```

### Start Reader
```java
kiwriousReader.startSerialReader();
```

### Stop Reader
```java
kiwriousReader.stopSerialReader();
```

### Get Sensor value
```java
float getConductivity()
long getResistance()
int getVoc()
int getCo2()
float getUV()
float getLux()
float getHumidity()
float getTemperature()
int getAmbientTemperature()
int getInfraredTemperature()
```

### Get Sensor status
```java
boolean isHumidityOnline()
boolean isUvOnline()
boolean isConductivityOnline()
boolean isVocOnline()
boolean isBodyTempOnline()
```

### Get Connected Sensor
```java
String getConnectedSensorName()
```

## Copy aar plugin (as Unity3D android plugin)
- Update `into` field value inside `copyPlugin` gradle task
- Run `copyPlugin` gradle task


## Package publishing
* create a signing key (RSA and RSA, 4096 bits long)
```linux
    gpg --full-gen-key
```

* send key to a key server (keyserver.ubuntu.com, keys.openpgp.org, hkp://keys.openpgp.org)
```linux
    gpg --keyserver [key server] --send-keys [key]
```

* receive same key from a key server (only for verification)
```linux
   gpg --keyserver [key server] --recv-key [key]
```

* export key and copy base64 key value
```linux
    gpg --export-secret-keys [key] | base64
```

* add signing properties and sonatype properties into `local.properties`
```groovy
signing.keyId=[key]
signing.password=[passphrase]
signing.key=[base64_key_value]

ossrhUsername=[sonatype_username]
ossrhPassword=[sonatype_password]
sonatypeStagingProfileId=[sonatype_staging_id]
```

* update library version inside `build.gradle`
```groovy
ext {
    PUBLISH_VERSION = 'x.y.z'
}
```

* run publish command
```groovy
gradlew kiwrious:publishReleasePublicationToSonatypeRepository
```

* go to [nexus repository manager](https://s01.oss.sonatype.org/) and login

* under the staging repositories, find recently uploaded repository, close it and release it

* releases are available [here](https://repo1.maven.org/maven2/com/kiwrious/sdk/android/kiwrious-android-library/)

* releases are indexed [here](https://search.maven.org/)







