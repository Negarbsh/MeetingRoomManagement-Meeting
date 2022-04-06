package scheduler.domain.read

import org.springframework.stereotype.Component
import scheduler.dao.MeetingCRUD
import scheduler.model.MeetingSearchRequest
import scheduler.model.meeting.Meeting

@Component
class MeetingReader : MeetingRead {
    //todo move to dao layer
    override fun getByPeriod(meetingSearchRequest: MeetingSearchRequest?, meetingCRUD: MeetingCRUD): List<Meeting> {
        if (meetingSearchRequest == null) return listOf()
        val meetings = meetingCRUD.findAll()
        val searchResult = arrayListOf<Meeting>()
        for (meeting in meetings) {
            if(meeting.timeInterval.isInterfering(meetingSearchRequest.timeInterval))
                searchResult.add(meeting)
        }
        return searchResult
    }
}