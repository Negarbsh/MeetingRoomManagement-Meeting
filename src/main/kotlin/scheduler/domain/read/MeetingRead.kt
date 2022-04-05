package scheduler.domain.read

import scheduler.dao.MeetingCRUD
import scheduler.model.MeetingSearchRequest
import scheduler.model.meeting.Meeting

interface MeetingRead {
    fun getByPeriod(meetingSearchRequest: MeetingSearchRequest?, meetingCRUD: MeetingCRUD): List<Meeting>
}
