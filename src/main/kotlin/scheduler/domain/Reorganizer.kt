package scheduler.domain

import scheduler.model.meeting.Meeting
import scheduler.model.Room
import scheduler.model.meeting.TimedMeetingRequest
import org.bson.types.ObjectId

interface Reorganizer {

    /*
    Gets a new meeting, the list of rooms, and the list of all meetings.
    If it was possible to reorganize the other meetings in a way to find a room for the new meeting,
    returns an array of the changed meetings and the room id (it doesn't work with db).
    else, returns null
    */
    fun reorganizeByMeeting(
        meetings: List<Meeting>,
        rooms: HashMap<ObjectId, Room>,
        timedMeetingRequest: TimedMeetingRequest
    ): Pair<List<Meeting>?, ObjectId?>
    // new meetings, roomId
}