package scheduler.presentation

import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import scheduler.dao.MeetingCRUD
import scheduler.domain.create.Creator
import scheduler.domain.edit.MeetingEdit
import scheduler.domain.read.MeetingRead
import scheduler.model.MeetingSearchRequest
import scheduler.model.meeting.MeetingEditRequest
import scheduler.model.meeting.MeetingRequest
import scheduler.model.meeting.TimedMeetingRequest
import java.util.stream.Collectors

@RestController
class RestControl(
    @Autowired val meetingEditor: MeetingEdit,
    @Autowired val meetingCreator: Creator,
    @Autowired val reader: MeetingRead,
    @Autowired val meetingCRUD: MeetingCRUD,
    @Autowired val myInter : HandlerInterceptor

) : WebMvcConfigurer{

    @PostMapping("/createMeeting")
    @ResponseBody
    fun createMeeting(@RequestBody timedMeetingRequest: TimedMeetingRequest): ResponseEntity<String> {
        val meetingId = meetingCreator.createFixedTimeMeeting(timedMeetingRequest)
            ?: return ResponseEntity(
                "Meeting creation failed. Try choosing another time",
                HttpStatus.NOT_ACCEPTABLE               //TODO is the status correct?
            )
        return ResponseEntity("$meetingId", HttpStatus.CREATED)
    }

    @GetMapping("/meetingEarliestChance")
    fun getMeetingEarliestChance(@RequestBody meetingRequest: MeetingRequest): ResponseEntity<String> {
        val response = meetingCreator.getEarliestMeetingChance(meetingRequest = meetingRequest)
            ?: return ResponseEntity("No room is free for this meeting.", HttpStatus.NOT_ACCEPTABLE)
        return ResponseEntity(
            "Earliest chance is at ${response.second} in room with Id ${response.first}",
            HttpStatus.ACCEPTED
        )
    }

    @PutMapping("/cancel")
    fun cancel(@RequestBody meetingId: ObjectId): ResponseEntity<String> {
        val isCanceled = meetingEditor.cancelMeeting(meetingId)
        if (isCanceled)
            return ResponseEntity(HttpStatus.OK)
        return ResponseEntity(HttpStatus.CONFLICT)
    }

    @PutMapping("/edit")
    fun edit(
        @RequestBody editRequest: MeetingEditRequest,
        @RequestHeader("user-mail") userMail: String
    ): ResponseEntity<String> {
        val isEdited = meetingEditor.editMeeting(editRequest, userMail)
        if (isEdited)
            return ResponseEntity(HttpStatus.OK)
        return ResponseEntity(HttpStatus.CONFLICT)
    }

    @GetMapping("/searchMeetings")
    fun getMeetings(@RequestBody meetingSearchRequest: MeetingSearchRequest): ResponseEntity<String> {
        val meetings = reader.getByPeriod(meetingSearchRequest, meetingCRUD)
        val listString: String = meetings.stream().map { obj: Any -> obj.toString() }
            .collect(Collectors.joining(",\n"))

        return ResponseEntity(listString, HttpStatus.OK)
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        super.addInterceptors(registry)
        registry.addInterceptor(myInter)
    }

}
