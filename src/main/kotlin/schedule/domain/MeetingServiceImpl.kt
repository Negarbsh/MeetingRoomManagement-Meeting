package schedule.domain

import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import schedule.dao.MeetingCRUD
import schedule.domain.assignment.Assigner
import schedule.model.MeetingSearchRequest
import schedule.model.meeting.*
import java.sql.Timestamp

@Component
class MeetingServiceImpl(
    @Autowired private val meetingAssigner: Assigner,
    @Autowired private val meetingCRUD: MeetingCRUD
) : MeetingService {

    override fun schedule(timedMeetingRequest: TimedMeetingRequest): ObjectId? {
        return meetingAssigner.createFixedTimeMeeting(timedMeetingRequest)
    }

    override fun getEarliestMeetingChance(
        timeLowerBound: Timestamp?,
        meetingRequest: MeetingRequest
    ): Pair<ObjectId, Timestamp>? {
        return meetingAssigner.getEarliestMeetingChance(timeLowerBound, meetingRequest, 15, 1000)
    }

    override fun edit(editRequest: MeetingEditRequest, editorMail: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun cancel(meetingId: ObjectId): Boolean {
        TODO("Not yet implemented")
    }

    override fun searchMeeting(meetingSearchRequest: MeetingSearchRequest): List<Meeting> {
//        val meetings = meetingCRUD.findAll()
//        val searchResult = arrayListOf<Meeting>()
//        for (meeting in meetings) {
//            if (meeting.timeInterval.isInterfering(meetingSearchRequest.timeInterval))
//                searchResult.add(meeting)
//        }
//        return searchResult
        TODO("Not yet implemented")
    }

    override fun getMeetingsInInterval(interval: TimeInterval): List<Meeting> {
        TODO("Not yet implemented")
    }
}