package domain

import model.meeting.Meeting
import model.Room
import model.meeting.TimedMeetingRequest
import org.bson.types.ObjectId

interface Reorganizer {

    /*
    Gets a new meeting, the list of rooms, and the list of all meetings.
    If it was possible to reorganize the other meetings in a way to find a room for the new meeting,
    returns an array of the changed meetings, the meetingId for the new meeting and the room id (it doesn't work with db).
    else, returns null
    */
    fun reorganizeByMeeting(
        meetings: HashMap<ObjectId, Meeting>,
        rooms: List<Room>,
        timedMeetingRequest: TimedMeetingRequest
    ): Triple<HashMap<ObjectId, Meeting>?, ObjectId?, ObjectId?>
    // new meetings, meetingId , roomId
    //todo check if it's needed to return the room Id
}