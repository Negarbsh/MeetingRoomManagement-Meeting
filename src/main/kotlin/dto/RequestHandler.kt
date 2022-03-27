package dto

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.mongodb.util.BsonUtils.toJson
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody

class RequestHandler {
    fun handleCreateMeeting(requestBody: String): ResponseEntity<String> {
        println(requestBody)
//        try {
//            val meetingRequest = ObjectMapper().readValue(requestBody, MeetingRequest::class.java)
//            print(meetingRequest)
//        } catch (e: Exception) {
//            println(e)
//        }
        return ResponseEntity("salam", HttpStatus.CREATED)
    }

    fun handleEarliestChance(requestBody: RequestBody): ResponseEntity<String> {
        return ResponseEntity("salam", HttpStatus.CREATED)
    }

    fun cancelMeeting(body: RequestBody): ResponseEntity<String> {
        TODO("Not yet implemented")
    }

    fun editMeeting(body: RequestBody): ResponseEntity<String> {
        TODO("Not yet implemented")
    }
}