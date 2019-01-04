# Okta JWT verifier for Ktor [![Build Status](https://travis-ci.org/lamba92/ktor-okta-jwt.svg?branch=master)](https://travis-ci.org/lamba92/ktor-okta-jwt) [![](https://jitpack.io/v/lamba92/ktor-okta-jwt.svg)](https://jitpack.io/#lamba92/ktor-okta-jwt)

Implementation of the authentication feature for the Okta JWT verifier. 

**EARLY WORK IN PROGRESS. DO NOT EXPECT THIS TO WORK AS IT SHOULD**

Written in Kotlin with ❤️

## Usage

Install the [Authentication](https://ktor.io/servers/features/authentication.html) feature in your application and call `oktaJWT()`

```
install(Authentication){
    oktaJWT("apiV1"){
        realm = "test"
        issuerUrl = "test"
        audience = "test"
        connectionTimeout = 1000
        readTimeout = 1000
        clientId = "test"
    }
}
```

Then put your routes in the according auth name:

```
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
```
repositories {
    maven { url 'https://jitpack.io' }
}
```

Then import the latest version in the `build.gradle` of the modules you need:

```
dependencies {
    implementation 'com.github.lamba92:ktor-spa:{latest_version}'
}
```

If using Gradle Kotlin DSL:
```
repositories {
    maven(url = "https://jitpack.io")
}
...
dependencies {
    implementation("com.github.lamba92", "ktor-spa", "{latest_version}")
}
```
For Maven:
```
<repositories>
   <repository>
      <id>jitpack.io</id>
      <url>https://jitpack.io</url>
   </repository>
</repositories>
...
<dependency> 	 
   <groupId>com.github.Lamba92</groupId>
   <artifactId>ktor-spa</artifactId>
   <version>{latest_version}</version>
</dependency>
```