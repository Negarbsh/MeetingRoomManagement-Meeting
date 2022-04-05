package scheduler.domain.edit

import org.bson.types.ObjectId
import scheduler.model.meeting.MeetingEditRequest

interface MeetingEdit {
    fun editMeeting(editRequest: MeetingEditRequest, clientMail: String): Boolean

    fun cancelMeeting(meetingId: ObjectId): Boolean
}
