package scheduler.model

import org.bson.types.ObjectId
import java.sql.Timestamp

class MeetingSearchRequest (val startTime: Timestamp, val endTime : Timestamp, val roomId: ObjectId? = null){
}
