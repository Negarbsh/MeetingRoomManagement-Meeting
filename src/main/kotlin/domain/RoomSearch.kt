package domain

import model.Room
import model.meeting.TimedMeetingRequest
import org.bson.types.ObjectId

interface RoomSearch { // TODO:  think for a better name

    fun getBestRoomChoice(meetingRequest: TimedMeetingRequest): ObjectId?

    /*
    gets a timed meeting request, and find all possible rooms for that request (regardless of optimization)
     */
    fun getAllPossibleRooms(meetingRequest: TimedMeetingRequest): HashMap<ObjectId, Room>
}
