package schedule.presentation.token

import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.impl.crypto.DefaultJwtSignatureValidator
import org.springframework.stereotype.Component
import javax.crypto.spec.SecretKeySpec
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class TokenHandlerImpl : TokenHandler {

    private val header = "token"

    override fun checkJWTToken(request: HttpServletRequest, res: HttpServletResponse): Boolean {
        val token = request.getHeader(header)
        val chunks = token.split("\\.")
        val tokenWithoutSignature: String = chunks[0] + "." + chunks[1]
        val signature: String = chunks[2]

        val signatureAlgorithm: SignatureAlgorithm = SignatureAlgorithm.HS256
        val secretKey = "Negar" //todo use environment variable
        val secretKeySpec = SecretKeySpec(secretKey.toByteArray(), signatureAlgorithm.jcaName)
        val validator = DefaultJwtSignatureValidator(signatureAlgorithm, secretKeySpec)

        if (!validator.isValid(tokenWithoutSignature, signature)) {
            throw Exception("Could not verify JWT token integrity!")
        }

        return true
//
//        try {
//            val jwtAuthToken = request.getHeader(header)
//            val isTokenValid = validateJwtToken(jwtAuthToken)
//            if (!isTokenValid) res.status = HttpStatus.UNAUTHORIZED.value()
//            return isTokenValid
//        } catch (e: Exception) {
//            res.status = HttpStatus.UNAUTHORIZED.value()
//            return false
//        }
    }

    private fun validateJwtToken(jwtAuthToken: String?): Boolean {
//        try {
//            // This is ultimately a JSON map and any values can be added to it, but JWT
//            // standard names are provided as type-safe getters and setters for convenience.
//            var jws: Jws<Claims?>? = null
//            jws = Jwts.parserBuilder().setSigningKey(tokenPublicKey).build().parseClaimsJws(jwtAuthToken)
//            // Here we are checking "iss" (Issuer) Claim , "exp" (Expiration Time) Claim,
//            // "nbf" (Not Before) Claim
//            if (jws.getBody().getIssuer().equals(issuerName)
//                && jws.getBody().getExpiration().compareTo(Date()) > 0
//                && jws.getBody().getNotBefore().compareTo(Date()) < 0
//            ) {
//                return true
//            }
//            throw AuthenticationException("Invalid JWT token")
//        } catch (e: JwtException) {

//        throw AuthenticationException("Invalid JWT token signature")
//        }

        return true
    }

}