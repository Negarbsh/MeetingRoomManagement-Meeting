package domain

import model.meeting.Meeting
import model.Room
import model.meeting.TimedMeetingRequest
import org.bson.types.ObjectId

interface Reorganizer {

    /*
    Gets a new meeting, the list of rooms, and the list of all meetings.
    If it was possible to reorganize the other meetings to find a room for the new meeting,
    returns an array of the  new meetings (it doesn't work with db), the meetingId for the new meeting and the room id.
    else, returns null
    */
    fun reorganizeByMeeting(
        meetings: HashMap<ObjectId, Meeting>,
        rooms: List<Room>,
        timedMeetingRequest: TimedMeetingRequest
    ): Triple<HashMap<ObjectId, Meeting>?, ObjectId?, ObjectId?>
    // new meetings, meetingId , roomId
    //todo check if it's needed to return the room Id
    //todo decide if we should return all the meetings or just the meetings that have changed
}