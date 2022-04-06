package scheduler.domain.read

import org.springframework.stereotype.Component
import scheduler.dao.MeetingCRUD
import scheduler.model.MeetingSearchRequest
import scheduler.model.meeting.Meeting

@Component
class MeetingReader : MeetingRead {
    //todo move to dao layer
    override fun getByPeriod(meetingSearchRequest: MeetingSearchRequest?, meetingRepo: MeetingCRUD): List<Meeting> {
        if (meetingSearchRequest == null) return listOf()
        val meetings = meetingRepo.findAll()
        val searchResult = arrayListOf<Meeting>()
        for (meeting in meetings) {
            if (meeting.start.after(meetingSearchRequest.startTime) and meeting.end.before(meetingSearchRequest.endTime))
                searchResult.add(meeting)
        }
        return searchResult
    }
}