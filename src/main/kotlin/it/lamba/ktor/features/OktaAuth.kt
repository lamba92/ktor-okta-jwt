package it.lamba.ktor.features

import com.okta.jwt.Jwt
import com.okta.jwt.JwtHelper
import com.typesafe.config.ConfigFactory
import io.ktor.application.call
import io.ktor.auth.*
import io.ktor.config.HoconApplicationConfig
import io.ktor.http.HttpStatusCode
import io.ktor.http.auth.HttpAuthHeader
import io.ktor.request.ApplicationRequest
import io.ktor.response.respond
import io.ktor.util.KtorExperimentalAPI
import java.lang.Exception

class OktaJwtAuthenticationProvider(name: String?, configuration: Configuration) :
    AuthenticationProvider(name) {

    data class Configuration(
        var realm: String = "Ktor Server",
        var issuerUrl: String = "",
        var audience: String = "",
        var connectionTimeout: Int = 1000,
        var readTimeout: Int = 1000,
        var clientId: String = ""
    )

    internal val jwtVerifier = JwtHelper()
        .applyIf(configuration.issuerUrl.isNotBlank()) {
            setIssuerUrl(configuration.issuerUrl)
        }
        .applyIf(configuration.audience.isNotBlank()) {
            setAudience(configuration.audience)
        }
        .applyIf(configuration.clientId.isNotBlank()) {
            setClientId(configuration.clientId)
        }
        .setConnectionTimeout(configuration.connectionTimeout)
        .setReadTimeout(configuration.readTimeout)
        .build()!!

}

@KtorExperimentalAPI
fun Authentication.Configuration.oktaJWT(
    name: String? = null,
    configure: (OktaJwtAuthenticationProvider.Configuration.() -> Unit)? = null
) {

    val defaultConfig = OktaJwtAuthenticationProvider.Configuration()

    val hocon = HoconApplicationConfig(ConfigFactory.load())

    hocon.propertyOrNull("okta.audience")?.toString()?.let { defaultConfig.audience = it }
    hocon.propertyOrNull("okta.issuerUrl")?.toString()?.let { defaultConfig.issuerUrl = it }
    hocon.propertyOrNull("okta.clientId")?.toString()?.let { defaultConfig.clientId = it }
    hocon.propertyOrNull("okta.connectionTimeout")?.toString()?.toInt()?.let { defaultConfig.connectionTimeout = it }
    hocon.propertyOrNull("okta.readTimeout")?.toString()?.toInt()?.let { defaultConfig.readTimeout = it }

    if (configure != null) defaultConfig.apply(configure)

    val provider = OktaJwtAuthenticationProvider(name, defaultConfig)

    provider.pipeline.intercept(AuthenticationPipeline.CheckAuthentication) { context ->
        val token = context.call.request.jwtToken()

        // TODO i have no idea what to do here... check ktor's JWTAuth.kt
//        if (token == null) {
//            context.bearerChallenge(AuthenticationFailedCause.NoCredentials, provider.configuration.realm, provider.schemes)
//            return@intercept
//        }

        val decodedToken = try {
            provider.jwtVerifier.decodeAccessToken(token)!!
        } catch (e: Exception) {
            null
        }

        if (decodedToken == null) {
            context.call.respond(HttpStatusCode.Unauthorized)
            context.call.authentication
        } else
            context.principal(JwtPrincipal(decodedToken))

    }

    register(provider)
}

internal fun ApplicationRequest.jwtToken(): String? {
    val parsed = try {
        parseAuthorizationHeader()
    } catch (e: Throwable) {
        null
    }
    return if (parsed is HttpAuthHeader.Single && parsed.authScheme.equals("Bearer", ignoreCase = true))
        parsed.blob
    else
        null
}

inline class JwtPrincipal(val jwtToken: Jwt) : Principal

fun AuthenticationContext.jwtPrincipal() = principal<JwtPrincipal>()?.jwtToken

inline fun <T> T.applyIf(condition: Boolean, block: T.() -> Unit) = if (condition) apply(block) else this

