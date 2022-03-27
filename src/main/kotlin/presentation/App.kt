package presentation

import com.fasterxml.jackson.annotation.JsonCreator
import dto.RequestHandler
import model.UserInfo
import model.meeting.*
import net.minidev.json.JSONObject
import org.bson.json.JsonObject
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

//import kotlinx.serialization.*
//import kotlinx.serialization.json.JSON


@RestController
@EnableAutoConfiguration
class App {

    val requestHandler = RequestHandler()

    //    @RequestMapping("/")
//    fun home(): String {
//        return "Hello World!"
//    }
//
    @RequestMapping("/test")
    fun readd(@RequestBody body: String): String {
        println(body)
//        var obj = JSONObject(body)
//        val parser = Parser.default() //todo: what should the parser be?!
        val stringBuilder: StringBuilder = StringBuilder("{\"name\":\"Cedric Beust\", \"age\":23}")
//        val json: JsonObject = parser.parse(stringBuilder) as JsonObject

        return "$body hi"
    }
//
//    @RequestMapping("/test2")
//    fun getToken(@RequestHeader("token") token: String): String {
//        return myf(token)
//    }
//
////    @RequestMapping(method = RequestMethod.POST, "/test3")
//    @PostMapping("./wer")
//    fun test3(): ResponseEntity<String> {
//        return ResponseEntity<String>("sdsf", HttpStatus.CREATED)
//    }
//
//    @ResponseStatus(HttpStatus.CREATED)
//    fun myf(token: String): String {
//        return token

    //        TODO("check token")


    @PostMapping("/createMeeting")
    fun createMeeting(@RequestBody body: String): ResponseEntity<String> {
        return requestHandler.handleCreateMeeting(body)
    }

    @GetMapping("/meetingEarliestChance")
    fun getMeetingEarliestChance(body: RequestBody): ResponseEntity<String> {
        return requestHandler.handleEarliestChance(body)
    }

    @PutMapping("/cancel")
    fun cancel(body: RequestBody, header: RequestHeader): ResponseEntity<String> {
        return requestHandler.cancelMeeting(body)


    }

    @PutMapping("/edit")
    fun edit(body: RequestBody, header: RequestHeader): ResponseEntity<String> {
        var userInfo = getUserInfoByHeader(header)
        return requestHandler.editMeeting(body)
    }


    private fun getUserInfoByHeader(header: RequestHeader): UserInfo {
        TODO("Not yet implemented")
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(App::class.java, *args)
//            val meetingRequest : TimedMeetingRequest = TimedMeetingRequest("sdf", "sdfd" , listOf(), MeetingOrganizer(), MeetingPurpose.GROOMING, Timestamp(12345), Timestamp(13456))
//            println(meetingRequest.purpose)
        }
    }
}
