package it.lamba.ktor.features

import com.okta.jwt.Jwt
import com.okta.jwt.JwtVerifiers
import io.ktor.application.call
import io.ktor.auth.*
import io.ktor.http.auth.HttpAuthHeader
import io.ktor.request.ApplicationRequest
import io.ktor.response.respond
import java.time.Duration

private val OktaJWTAuthKey: Any = "OktaJWTAuth"

fun Authentication.Configuration.okta(
    issuer: String,
    audience: String,
    name: String? = null,
    configure: OktaJwtVerifierConfiguration.() -> Unit
) {
    val provider = OktaJwtVerifierConfiguration(issuer, audience, name).apply(configure)

    provider.pipeline.intercept(AuthenticationPipeline.RequestAuthentication) { context ->
        val token = call.request.parseAuthorizationHeaderOrNull()
        if (token == null) {
            context.bearerChallenge(AuthenticationFailedCause.NoCredentials, provider.realm, provider.schemes)
            return@intercept
        }
        try {
            val principal = OktaPrincipal(provider.underlyingVerifier.decode(token.getBlob(provider.schemes)))
            context.principal(principal)
        } catch (cause: Throwable) {
            val message = cause.message ?: cause.javaClass.simpleName
            context.error(OktaJWTAuthKey, AuthenticationFailedCause.Error(message))
        }
    }
    register(provider)
}

class OktaJwtVerifierConfiguration(issuer: String, audience: String, name: String?) : AuthenticationProvider(name) {

    var connectionTimeout: Long = 1000
    var readTimeout: Long = 1000
    var realm: String = "Ktor Server"
    internal var schemes = JWTAuthSchemes("Bearer")

    internal val underlyingVerifier = JwtVerifiers.accessTokenVerifierBuilder().apply {
        setIssuer(issuer)
        setAudience(audience)
        setConnectionTimeout(Duration.ofMillis(connectionTimeout))
        setReadTimeout(Duration.ofMillis(readTimeout))
    }.build()!!
}

private fun ApplicationRequest.parseAuthorizationHeaderOrNull() = try {
    parseAuthorizationHeader()
} catch (ex: IllegalArgumentException) {
    null
}

private fun AuthenticationContext.bearerChallenge(
    cause: AuthenticationFailedCause,
    realm: String,
    schemes: JWTAuthSchemes
) = challenge(OktaJWTAuthKey, cause) {
    call.respond(UnauthorizedResponse(HttpAuthHeader.bearerAuthChallenge(realm, schemes)))
    it.complete()
}

private fun HttpAuthHeader.Companion.bearerAuthChallenge(realm: String, schemes: JWTAuthSchemes): HttpAuthHeader =
    HttpAuthHeader.Parameterized(schemes.defaultScheme, mapOf(HttpAuthHeader.Parameters.Realm to realm))

class JWTAuthSchemes(val defaultScheme: String, vararg additionalSchemes: String) {
    val schemes = (arrayOf(defaultScheme) + additionalSchemes).toSet()
    val schemesLowerCase = schemes.map { it.toLowerCase() }.toSet()

    operator fun contains(scheme: String): Boolean = scheme.toLowerCase() in schemesLowerCase
}

private fun HttpAuthHeader.getBlob(schemes: JWTAuthSchemes) = when {
    this is HttpAuthHeader.Single && authScheme.toLowerCase() in schemes -> blob
    else -> null
}

class OktaPrincipal(parsedToken: Jwt) : Principal