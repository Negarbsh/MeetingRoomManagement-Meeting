package scheduler.dto

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import scheduler.dao.MeetingCRUD
import scheduler.domain.Creator
import scheduler.domain.MeetingCreator
import scheduler.model.meeting.TimedMeetingRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

class RequestHandler(val meetingDAO : MeetingCRUD) {
    private val mapper = jacksonObjectMapper()

    fun handleCreateMeeting(createRequest: String): ResponseEntity<String> {
        try {
            val request: TimedMeetingRequest = mapper.readValue(createRequest)
            println(request)
            val meetingCreator : Creator = MeetingCreator(meetingDAO)
            val meetingId = meetingCreator.createFixedTimeMeeting(request)
            if(meetingId == null) return ResponseEntity("message: Meeting creation failed. Try choosing another time", HttpStatus.NOT_ACCEPTABLE) //TODO is the status correct?
            return ResponseEntity("$meetingId", HttpStatus.CREATED)
        } catch (e: Exception) {
            println(e)
            return ResponseEntity(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
        }
    }

    fun handleEarliestChance(requestBody: Map<*, *>): ResponseEntity<String> {
        return ResponseEntity("salam", HttpStatus.CREATED)
    }

    fun cancelMeeting(body: Map<*, *>): ResponseEntity<String> {
        TODO("Not yet implemented")
    }

    fun editMeeting(body: Map<*, *>): ResponseEntity<String> {
        TODO("Not yet implemented")
    }
}