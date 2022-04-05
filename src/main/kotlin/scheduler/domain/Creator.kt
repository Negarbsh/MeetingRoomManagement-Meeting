package scheduler.domain

import scheduler.model.meeting.MeetingRequest
import scheduler.model.meeting.TimedMeetingRequest
import org.bson.types.ObjectId
import java.sql.Timestamp

interface Creator {

    /* if successful, returns the meeting id*/

    fun createFixedTimeMeeting(timedMeetingRequest: TimedMeetingRequest): ObjectId?

    /*Checks if we can hold the meeting after the "timeLowerBound", then returns the roomId and the start time if it was possible*/
    fun getEarliestMeetingChance(timeLowerBound: Timestamp? = null, meetingRequest: MeetingRequest): Pair<ObjectId, Timestamp>?

}