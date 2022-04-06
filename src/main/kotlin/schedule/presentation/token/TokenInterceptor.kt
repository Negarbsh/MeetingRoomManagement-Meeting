package schedule.presentation.token

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class TokenInterceptor : HandlerInterceptor {
    val tokenHandler : TokenHandler = TokenHandlerImpl()

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        return try {
            tokenHandler.checkJWTToken(request, response)
            true
        } catch (e: Exception) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), e.message)
            false
        }
    }
}