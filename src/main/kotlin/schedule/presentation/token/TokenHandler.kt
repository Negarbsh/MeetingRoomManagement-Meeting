package schedule.presentation.token

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


interface TokenHandler {

    /* If the token was valid, returns the payload. Else, throws Exception */
    fun checkJWTToken(request: HttpServletRequest, res: HttpServletResponse): String
}