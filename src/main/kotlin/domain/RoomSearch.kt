package domain

import model.meeting.TimedMeetingRequest
import org.bson.types.ObjectId

interface RoomSearch { // TODO:  think for a better name
    fun searchForRoom(
        meetingRequest: TimedMeetingRequest
    ): ObjectId?
}
