package presentation

import dto.RequestHandler
import model.UserInfo
import org.springframework.boot.SpringApplication
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import com.google.gson.Gson
import dao.MeetingCRUD
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.http.HttpStatus


//  TODO("check token")


@org.springframework.web.bind.annotation.RestController
class RestController(
    @Autowired
    meetingDAO: MeetingCRUD
) {
    val gson = Gson()
    val requestHandler = RequestHandler(meetingDAO)

    private fun getMapByRequest(body: String): Map<*, *>? {
        return try {
            gson.fromJson(body, MutableMap::class.java)
        } catch (e: Exception) {
            null
        }
    }

    @PostMapping("/createMeeting")
    @ResponseBody
    fun createMeeting(@RequestBody body: String): ResponseEntity<String> {
        val map = getMapByRequest(body) ?: return ResponseEntity(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
        return requestHandler.handleCreateMeeting(body)
    }

    @GetMapping("/meetingEarliestChance")
    fun getMeetingEarliestChance(body: String): ResponseEntity<String> {
        val map = getMapByRequest(body) ?: return ResponseEntity(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
        return requestHandler.handleEarliestChance(map)
    }

    @PutMapping("/cancel")
    fun cancel(@RequestBody body: String, header: RequestHeader): ResponseEntity<String> {
        val map = getMapByRequest(body) ?: return ResponseEntity(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
        return requestHandler.cancelMeeting(map)
    }

    @PutMapping("/edit")
    fun edit(@RequestBody body: String, header: RequestHeader): ResponseEntity<String> {
        var userInfo = getUserInfoByHeader(header)
        val map = getMapByRequest(body) ?: return ResponseEntity(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
        return requestHandler.editMeeting(map)
    }


    private fun getUserInfoByHeader(header: RequestHeader): UserInfo {
        TODO("Not yet implemented")
    }

//    companion object {
//        @JvmStatic
//        fun main(args: Array<String>) {
//            SpringApplication.run(RestController::class.java, *args)
//        }
//    }
}
