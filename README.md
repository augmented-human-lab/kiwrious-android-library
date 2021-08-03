# Kiwrious Android Library Project

## Library development
- run `gradle build`

## Copy aar plugin
- update `into` field value inside `copyPlugin` gradle task
- run `copyPlugin` gradle task


## Package publishing
- Add github credentials
- Increment version code inside `publications` gradle block
- Run `gradle publish`

## Package integration
- Grab the latest package name and version from [here](https://github.com/augmented-human-lab/kiwrious-android-library/packages/872446)
- Create `github.properties` file in your project root folder and add below values into `github.properties` file
```java 
gpr.usr=GITHUB_USER
gpr.key=GITHUB_TOKEN
```
- Make sure `github.properties` file is added to gitIgnore
- Create github developer token with `package read` , `repo` permissions
- Replace `GITHUB_USER` and `GITHUB_TOKEN` with github username and developer token
- Add/Merge below code sniplets to your gradle.build file and update values

```java
android {
   defaultConfig {
      minSdkVersion 26
   }
}
```

```java
dependencies {
   implementation 'org.ahlab.kiwrious.android:kiwrious-sdk:0.0.18'
}
```

```java
def githubProperties = new Properties()
githubProperties.load(new FileInputStream(rootProject.file("github.properties")))
```

```java
repositories {
   maven {
       name = "GitHubPackages"
       url = "https://maven.pkg.github.com/augmented-human-lab/kiwrious-android-library"
       credentials {
           username = githubProperties['gpr.usr'] ?: System.getenv("GPR_USER")
           password = githubProperties['gpr.key'] ?: System.getenv("GPR_API_KEY")
       }
   }
}
```

# Kiwrious reader usage

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
import org.ahlab.kiwrious.android.Plugin;
import android.content.Context;
```
### Initiate Reader
```java
Context context = getApplicationContext(); // get your application context
Plugin plugin = Plugin.getInstance(context); // pass it to kiwrious library
```

### Start Reader
```java
plugin.startSerialReader();
```

### Stop Reader
```java
plugin.stopSerialReader();
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
```

### Get Sensor status
```java
boolean isHumidityOnline()
boolean isUvOnline()
boolean isConductivityOnline()
boolean isVocOnline()
```

### Get Connected Sensor
```java
String getConnectedSensorName()
```



