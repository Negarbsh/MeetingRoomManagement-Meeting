package scheduler.presentation

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.bson.types.ObjectId
import scheduler.dao.MeetingCRUD
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import scheduler.domain.Creator
import scheduler.domain.MeetingEdit
import scheduler.model.meeting.MeetingEditRequest
import scheduler.model.meeting.MeetingRequest
import scheduler.model.meeting.TimedMeetingRequest


//  TODO("check token")

@RestController
class RestControl(
    @Autowired val meetingEditor: MeetingEdit,
    @Autowired val meetingCreator: Creator
) {

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
            return ResponseEntity(HttpStatus.CREATED)
        return ResponseEntity(HttpStatus.CONFLICT)
    }

    @PutMapping("/edit")
    fun edit(
        @RequestBody editRequest: MeetingEditRequest,
        @RequestHeader("user-mail") userMail: String
    ): ResponseEntity<String> {
        val isEdited = meetingEditor.editMeeting(editRequest, userMail)
        if (isEdited)
            return ResponseEntity(HttpStatus.CREATED)
        return ResponseEntity(HttpStatus.CONFLICT)
    }
}
