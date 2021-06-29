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
- Add below code sniplets to your gradle.build file and update values

```java
implementation 'org.ahlab.kiwrious.android.kiwrious-sdk:0.0.4'
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


## Kiwrious reader usage
```java
new Application(getApplicationContext());
Plugin plugin = Plugin.getInstance();
plugin.initiateReader();
plugin.startSerialReader();
```

