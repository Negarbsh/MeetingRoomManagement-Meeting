package schedule.presentation.token.authorization

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import javax.servlet.http.HttpServletRequest


class AuthorizationHandlerImpl : AuthorizationHandler {
    override fun hasAccess(payload: String, httpRequest: HttpServletRequest): Boolean {
        val convertedObject = Gson().fromJson(payload, JsonObject::class.java)
        val isAdmin = convertedObject.get("is_admin") ?: return false
        if (isAdmin.asBoolean) return true
//        TODO("To be completed)
        return true
    }
}