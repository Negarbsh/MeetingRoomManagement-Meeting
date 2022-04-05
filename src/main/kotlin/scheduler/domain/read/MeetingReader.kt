package scheduler.domain.read

import org.springframework.stereotype.Component
import scheduler.dao.MeetingCRUD
import scheduler.model.MeetingSearchRequest
import scheduler.model.meeting.Meeting

@Component
class MeetingReader : MeetingRead {
    override fun getByPeriod(meetingSearchRequest: MeetingSearchRequest?, meetingCRUD: MeetingCRUD): List<Meeting> {
        TODO("Not yet implemented")
    }
}