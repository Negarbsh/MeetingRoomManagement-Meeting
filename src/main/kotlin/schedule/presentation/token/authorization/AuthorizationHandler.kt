package schedule.presentation.token.authorization

import javax.servlet.http.HttpServletRequest

interface AuthorizationHandler {
    fun hasAccess(payload: String, httpRequest: HttpServletRequest) : Boolean
}
