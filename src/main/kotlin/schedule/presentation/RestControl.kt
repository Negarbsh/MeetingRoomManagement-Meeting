package schedule.presentation

import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

import schedule.domain.MeetingServiceImpl
import schedule.model.MeetingSearchRequest
import schedule.model.enums.MeetingPurpose
import schedule.model.enums.Office
import schedule.model.meeting.*
import java.sql.Timestamp
import java.util.stream.Collectors

@RestController
class RestControl(
    @Autowired val tokenInterceptor: HandlerInterceptor,
    @Autowired val meetingService: MeetingServiceImpl,
) : WebMvcConfigurer {

    @PostMapping("/test")
    fun someting() {
        val meeting = Meeting(
            title = "a",
            description = "b",
            participants = listOf(Participant("my mail!")),
            meetingOrganizer = MeetingOrganizer(),
            purpose = MeetingPurpose.GROOMING,
            timeInterval = TimeInterval(Timestamp(20000000), Timestamp(300000000)),
            features = null,
            roomId = ObjectId(100, 100),
            office = Office.TEHRAN
        )
        meetingService.meetingCRUD.save(meeting)
        println(meetingService.meetingCRUD.findAllByOffice(Timestamp(20000000), Office.TEHRAN))
    }


    @PostMapping("/schedule")
    fun createMeeting(@RequestBody timedMeetingRequest: TimedMeetingRequest): ResponseEntity<String> {
        val meetingId = meetingService.schedule(timedMeetingRequest)
            ?: return ResponseEntity(
                "Meeting creation failed. Try choosing another time",
                HttpStatus.NOT_ACCEPTABLE
            )
        return ResponseEntity("$meetingId", HttpStatus.CREATED)
    }

    @GetMapping("/scheduleEarliestChance")
    fun getMeetingEarliestChance(@RequestBody meetingRequest: MeetingRequest): ResponseEntity<String> {
        val response = meetingService.getEarliestMeetingChance(meetingRequest = meetingRequest)
            ?: return ResponseEntity("No room is free for this meeting.", HttpStatus.NOT_ACCEPTABLE)
        return ResponseEntity(
            "Earliest chance is at ${response.second} in room with Id ${response.first}",
            HttpStatus.ACCEPTED
        )
    }

    @PutMapping("/cancel")
    fun cancel(@RequestBody meetingId: ObjectId): ResponseEntity<String> {
        val isCanceled = meetingService.cancel(meetingId)
        if (isCanceled)
            return ResponseEntity(HttpStatus.OK)
        return ResponseEntity("The meeting Id is invalid", HttpStatus.NOT_ACCEPTABLE)
    }

    @PutMapping("/edit")
    fun edit(
        @RequestBody editRequest: MeetingEditRequest,
        @RequestHeader("user-mail") userMail: String
    ): ResponseEntity<String> {
        val roomId = meetingService.edit(editRequest, userMail)
        if (roomId != null)
            return ResponseEntity("room id: $roomId", HttpStatus.OK)
        return ResponseEntity(HttpStatus.CONFLICT)
    }

    @GetMapping("/searchMeeting")
    fun getMeetings(@RequestBody meetingSearchRequest: MeetingSearchRequest): ResponseEntity<String> {
        val meetings = meetingService.searchMeeting(meetingSearchRequest)
        val listString: String = meetings.stream().map { obj: Any -> obj.toString() }
            .collect(Collectors.joining(",\n"))
        return ResponseEntity(listString, HttpStatus.OK)
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        super.addInterceptors(registry)
        registry.addInterceptor(tokenInterceptor)
    }

}
