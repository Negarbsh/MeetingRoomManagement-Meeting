package scheduler.domain.edit

import org.bson.types.ObjectId
import org.springframework.stereotype.Component
import scheduler.model.meeting.MeetingEditRequest

@Component
class MeetingEditor : MeetingEdit {
    override fun editMeeting(editRequest: MeetingEditRequest, clientMail: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun cancelMeeting(meetingId: ObjectId): Boolean {
        TODO("Not yet implemented")
    }
}