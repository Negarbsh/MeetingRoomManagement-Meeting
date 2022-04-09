package schedule.presentation.token.authorization

import javax.servlet.http.HttpServletRequest

class AuthorizationHandlerImpl : AuthorizationHandler {
    override fun hasAccess(payload: String, httpRequest: HttpServletRequest): Boolean {
//        TODO("Not yet implemented")
        return true
    }
}