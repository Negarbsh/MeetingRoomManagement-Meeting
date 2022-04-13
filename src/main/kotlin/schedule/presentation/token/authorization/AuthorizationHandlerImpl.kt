package schedule.presentation.token.authorization

import com.google.gson.Gson
import com.google.gson.JsonObject
import javax.servlet.http.HttpServletRequest

class AuthorizationHandlerImpl : AuthorizationHandler {
    override fun hasAccess(payload: String, httpRequest: HttpServletRequest): Boolean {
        val convertedObject = Gson().fromJson(payload, JsonObject::class.java)
        val isAdmin = convertedObject.get("is_admin") ?: return false
        if (isAdmin.asBoolean) return true
        val requestURL = httpRequest.requestURL.split("/").last()
        return !needsAdminRole(requestURL)
    }

    private fun needsAdminRole(requestURL: String): Boolean {
        when (requestURL) {
            "test" -> return false
            "schedule" -> return false
            "getEarliestChance" -> return false
            "cancel" -> return false
            "edit" -> return false
            "search" -> return true
        }
        return true
    }
}