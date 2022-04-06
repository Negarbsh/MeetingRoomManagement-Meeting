package schedule.model

import org.bson.types.ObjectId
import schedule.model.meeting.TimeInterval

class MeetingSearchRequest(val timeInterval: TimeInterval, val roomId: ObjectId? = null) {
}
