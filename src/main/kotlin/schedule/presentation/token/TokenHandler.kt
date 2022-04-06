package schedule.presentation.token

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


interface TokenHandler {
    fun checkJWTToken(request: HttpServletRequest, res: HttpServletResponse): Boolean
}