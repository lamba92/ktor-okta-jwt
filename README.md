# Okta Jwt verifier for Ktor

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