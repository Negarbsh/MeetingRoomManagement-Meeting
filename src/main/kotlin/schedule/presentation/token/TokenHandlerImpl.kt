package schedule.presentation.token

import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.impl.crypto.DefaultJwtSignatureValidator
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.spec.SecretKeySpec
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class TokenHandlerImpl : TokenHandler {

    private val header = "token"

    override fun checkJWTToken(request: HttpServletRequest, res: HttpServletResponse): String {
        val token = request.getHeader(header)
        val chunks = token.split("\\.")
        val decoder: Base64.Decoder = Base64.getUrlDecoder()
        val header = String(decoder.decode(chunks[0]))
        val payload = String(decoder.decode(chunks[1]))

        val tokenWithoutSignature: String = chunks[0] + "." + chunks[1]
        val signature: String = chunks[2]

        val signatureAlgorithm: SignatureAlgorithm = SignatureAlgorithm.HS256
        val secretKey = System.getenv("TOKEN_KEY")
        val secretKeySpec = SecretKeySpec(secretKey.toByteArray(), signatureAlgorithm.jcaName)
        val validator = DefaultJwtSignatureValidator(signatureAlgorithm, secretKeySpec)

        if (!validator.isValid(tokenWithoutSignature, signature)) {
            throw Exception("Could not verify JWT token integrity!")
        }
        return payload
    }

}