# Kiwrious Android Library Project

## Library development
- run `gradle build`

## Copy aar plugin
- update `into` field value inside `copyPlugin` gradle task
- run `copyPlugin` gradle task


## Package publishing
- Add github credentials
- run `gradle publish`

## AAR integration
- grab the latest aar plugin from below directory and configure gradle
https://github.com/augmented-human-lab/kiwrious-android-library/tree/master/kiwrious/build/outputs/aar

## Package integration
- Grab the latest package name and version from [here](https://github.com/augmented-human-lab/kiwrious-android-library/packages/872446)
- Add below code sniplets to your gradle.build file and update values

```java
implementation 'org.ahlab.kiwrious.android:kiwrious-sdk:0.0.4'
```

```java
repositories {
   maven {
       url = "https://maven.pkg.github.com/augmented-human-lab/kiwrious-android-library"
       credentials {
           username = GITHUB_USER
           password = GITHUB_TOKEN
       }
   }
}
```

- Create github developer token with `package read` , `repo` permissions
- Replace GITHUB_USER and GITHUB_TOKEN with github developer token and username  


# Kiwrious reader usage

### import packages
```java
import org.ahlab.kiwrious.android.Application;
import org.ahlab.kiwrious.android.Plugin;
```

### Start Reader
```java
new Application(getApplicationContext());
Plugin plugin = Plugin.getInstance();
plugin.initiateReader();
plugin.startSerialReader();
```

### Stop Reader
```java
plugin.startSerialReader();
```

### Sensor value get methods
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

### Sensor status get methods
```java
boolean isHumidityOnline()
boolean isUvOnline()
boolean isConductivityOnline()
boolean isVocOnline()
```

### Get Connected Sensor Name
```java
String getConnectedSensorName()
```



