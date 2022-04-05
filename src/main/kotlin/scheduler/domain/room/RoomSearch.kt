package scheduler.domain.room

import scheduler.model.Room
import scheduler.model.meeting.TimedMeetingRequest
import org.bson.types.ObjectId

interface RoomSearch { // TODO:  think for a better name

    fun getBestRoomChoice(meetingRequest: TimedMeetingRequest): ObjectId?

    /*
    gets a timed meeting request, and finds all possible rooms for that request (regardless of optimization)
     */
    fun getAllPossibleRooms(meetingRequest: TimedMeetingRequest): List<Room>
}
