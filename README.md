# Okta JWT verifier for Ktor [![Build Status](https://travis-ci.org/lamba92/ktor-okta-jwt.svg?branch=master)](https://travis-ci.org/lamba92/ktor-okta-jwt) [![](https://jitpack.io/v/lamba92/ktor-okta-jwt.svg)](https://jitpack.io/#lamba92/ktor-okta-jwt)

Implementation of the authentication feature for the Okta JWT verifier. 

Written in Kotlin with ❤️

## Usage

Install the [Authentication](https://ktor.io/servers/features/authentication.html) feature in your application and call `okta()`

```kotlin
install(Authentication){
    okta("https://{yourOktaDomain}/oauth2/default", "api://default", name="apiV1"){
        connectionTimeout = 1000
        readTimeout = 1000
        realm = "Ktor Server"
    }
}
```

Then put your routes in the according auth name:

```kotlin
routing {
    route("whatev") { ... }
    route("api){
        authenticate("apiV1"){
            route("v1"){ ... }
        }
    }
}
``` 

## Install [![](https://jitpack.io/v/lamba92/ktor-okta-jwt.svg)](https://jitpack.io/#lamba92/ktor-okta-jwt)

Add the [JitPack.io](http://jitpack.io) repository to the project `build.grade`:
```groovy
repositories {
    maven { url 'https://jitpack.io' }
}
```

Then import the latest version in the `build.gradle` of the modules you need:

```groovy
dependencies {
    implementation 'com.github.lamba92:ktor-okta-jwt:{latest_version}'
}
```

If using Gradle Kotlin DSL:
```kotlin
repositories {
    maven(url = "https://jitpack.io")
}
...
dependencies {
    implementation("com.github.lamba92", "ktor-okta-jwt", "{latest_version}")
}
```
For Maven:
```xml
<repositories>
   <repository>
      <id>jitpack.io</id>
      <url>https://jitpack.io</url>
   </repository>
</repositories>
...
<dependency> 	 
   <groupId>com.github.Lamba92</groupId>
   <artifactId>ktor-okta-jwt</artifactId>
   <version>{latest_version}</version>
</dependency>
```
