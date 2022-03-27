package domain

import model.meeting.MeetingRequest
import model.meeting.TimedMeetingRequest
import org.bson.types.ObjectId
import java.sql.Timestamp

interface Creator {

    fun createFixedTimeMeeting(timedMeetingRequest: TimedMeetingRequest): ObjectId?

    //Checks if we can hold the meeting after the "timeLowerBound", then returns the meetingId if successful
    fun getEarliestMeetingChance(timeLowerBound : Timestamp?, meetingRequest: MeetingRequest): Pair<ObjectId, Timestamp>?
}
