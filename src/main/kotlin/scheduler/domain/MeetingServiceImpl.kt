package scheduler.domain

import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import scheduler.dao.MeetingCRUD
import scheduler.domain.create.Assigner
import scheduler.domain.read.MeetingRead
import scheduler.model.MeetingSearchRequest
import scheduler.model.meeting.*
import java.sql.Timestamp

@Component
class MeetingServiceImpl(
    @Autowired private val meetingAssigner: Assigner,
    @Autowired private val reader: MeetingRead,
    @Autowired private val meetingCRUD: MeetingCRUD
) : MeetingService {

    override fun schedule(timedMeetingRequest: TimedMeetingRequest): ObjectId? {
        TODO("Not yet implemented")
    }

    override fun getEarliestMeetingChance(
        timeLowerBound: Timestamp?,
        meetingRequest: MeetingRequest
    ): Pair<ObjectId, Timestamp>? {
        TODO("Not yet implemented")
    }

    override fun edit(editRequest: MeetingEditRequest, editorMail: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun cancel(meetingId: ObjectId): Boolean {
        TODO("Not yet implemented")
    }

    override fun searchMeeting(meetingSearchRequest: MeetingSearchRequest): List<Meeting> {
        TODO("Not yet implemented")
    }

    override fun getMeetingsInInterval(interval: TimeInterval): List<Meeting> {
        TODO("Not yet implemented")
    }
}