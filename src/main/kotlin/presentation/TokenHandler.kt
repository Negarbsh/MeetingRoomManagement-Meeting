package presentation

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


class TokenHandler {

    private val header = "token"

    private fun checkJWTToken(request: HttpServletRequest, res: HttpServletResponse): Boolean {
        val authenticationHeader = request.getHeader(header)
        return authenticationHeader != null
    }

}