package schedule.presentation.token

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import schedule.presentation.token.authorization.AuthorizationHandler
import schedule.presentation.token.authorization.AuthorizationHandlerImpl
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class TokenInterceptor : HandlerInterceptor {
    val tokenHandler: TokenHandler = TokenHandlerImpl()
    val authorizationHandler: AuthorizationHandler = AuthorizationHandlerImpl()

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        return try {
            val payLoad: String = tokenHandler.checkJWTToken(request, response)
            return authorizationHandler.hasAccess(payLoad, request)
        } catch (e: Exception) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), e.message)
            false
        }
//        return true
    }
}