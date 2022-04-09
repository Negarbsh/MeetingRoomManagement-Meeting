package schedule.domain.assignment

import schedule.model.meeting.MeetingRequest
import schedule.model.meeting.TimedMeetingRequest
import org.bson.types.ObjectId
import java.sql.Timestamp

interface Assigner {

    /* if successful, returns the meeting id*/
    fun scheduleFixedTimeMeeting(timedMeetingRequest: TimedMeetingRequest): ObjectId?

    /*Checks if we can hold the meeting after the "timeLowerBound", then returns the roomId and the start time if it was possible*/
    fun getEarliestMeetingChance(
        timeLowerBound: Timestamp? = null,
        meetingRequest: MeetingRequest,
        minuteIncrementPace: Int,
        maxMinuteIncrement : Int
    ): Pair<ObjectId, Timestamp>?

    fun finalizeMeetingCreation(timedMeetingRequest: TimedMeetingRequest, roomId: ObjectId): ObjectId?
}