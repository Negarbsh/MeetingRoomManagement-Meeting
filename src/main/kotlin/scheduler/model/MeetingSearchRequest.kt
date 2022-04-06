package scheduler.model

import org.bson.types.ObjectId
import scheduler.model.meeting.TimeInterval

class MeetingSearchRequest(val timeInterval: TimeInterval, val roomId: ObjectId? = null) {
}
