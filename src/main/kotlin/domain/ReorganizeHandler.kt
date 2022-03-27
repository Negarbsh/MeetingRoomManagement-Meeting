package domain

import model.meeting.Meeting
import model.Room
import model.meeting.TimedMeetingRequest
import org.bson.types.ObjectId

class ReorganizeHandler : Reorganizer {
    override fun reorganizeByMeeting(
        meetings: HashMap<ObjectId, Meeting>,
        rooms: List<Room>,
        timedMeetingRequest: TimedMeetingRequest
    ): Triple<HashMap<ObjectId, Meeting>?, ObjectId?, ObjectId?> {
        TODO("Not yet implemented")
    }
}