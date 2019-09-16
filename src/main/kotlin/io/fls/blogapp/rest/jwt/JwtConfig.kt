package io.fls.blogapp.rest.jwt

data class JwtConfig(
    val secret: String = "zAP5MBA4B4Ijz0MZaS48",
    val issuer: String = "fls.io",
    val validityInMs: Int = 36_000_00 * 10 // 10 hours
)