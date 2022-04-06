package schedule.domain.assignment.reassignment

import schedule.model.meeting.Meeting
import schedule.model.Room
import schedule.model.meeting.TimedMeetingRequest
import org.bson.types.ObjectId

interface Reassigner {

    /*
    Gets a new meeting, the list of rooms, and the list of all meetings.
    If it was possible to reorganize the other meetings in a way to find a room for the new meeting,
    returns an array of the changed meetings and the room id (it doesn't work with db).
    else, returns null
    */
    fun reorganizeByMeeting(
        meetings: List<Meeting>,
        rooms: List<Room>,
        timedMeetingRequest: TimedMeetingRequest
    ): Pair<List<Meeting>?, ObjectId?>
    // new meetings, roomId
}