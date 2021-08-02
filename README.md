# Kiwrious Android Library Project

## Library development
- run `gradle build`

## Copy aar plugin
- update `into` field value inside `copyPlugin` gradle task
- run `copyPlugin` gradle task


## Package publishing
- Add github credentials
- run `gradle publish`

## Package integration
- Grab the latest package name and version from [here](https://github.com/augmented-human-lab/kiwrious-android-library/packages/872446)
- Create `github.properties` file in your project root folder and add below values into `github.properties` file
```java 
gpr.usr=GITHUB_USER
gpr.key=GITHUB_TOKEN
```
- Create github developer token with `package read` , `repo` permissions
- Replace `GITHUB_USER` and `GITHUB_TOKEN` with github username and developer token
- Add/Merge below code sniplets to your gradle.build file and update values

```java
implementation 'org.ahlab.kiwrious.android:kiwrious-sdk:0.0.8'
```

```java
new File('github.properties').withInputStream {new Properties().load(it)}
```

```java
repositories {
   maven {
       name = "GitHubPackages"
       url = "https://maven.pkg.github.com/augmented-human-lab/kiwrious-android-library"
       credentials {
           username = properties['gpr.usr'] ?: System.getenv("GPR_USER")
           password = properties['gpr.key'] ?: System.getenv("GPR_API_KEY")
       }
   }
}
```

# Kiwrious reader usage

### Modify AndroidManifest.xml
```xml
<intent-filter>
   <action android:name="android.hardware.usb.action.USB_DEVICE_ATTACHED" />
</intent-filter>
<meta-data
    android:name="android.hardware.usb.action.USB_DEVICE_ATTACHED"
    android:resource="@xml/device_filter" />
```

### Import packages
```java
import org.ahlab.kiwrious.android.Application;
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



