package schedule.domain

import org.bson.types.ObjectId
import schedule.model.MeetingSearchRequest
import schedule.model.meeting.*
import java.sql.Timestamp

interface MeetingService {
    /* if successful, returns the meeting id*/
    fun schedule(timedMeetingRequest: TimedMeetingRequest): ObjectId?

    /* Checks if we can hold the meeting after the "timeLowerBound", then returns the roomId and the start time if it was possible*/
    fun getEarliestMeetingChance(
        timeLowerBound: Timestamp? = Timestamp(System.currentTimeMillis()),
        meetingRequest: MeetingRequest
    ): Pair<ObjectId, Timestamp>?

    /* gets the editRequest and returns the new room id if the edit process was successful. If it wasn't, returns null */
    fun edit(editRequest: MeetingEditRequest, editorMail: String): ObjectId?

    /* cancels the meeting with specified meetingId and returns true if it was successful */
    fun cancel(meetingId: ObjectId): Boolean

    /* gets the search request and returns the matching meetings */
    fun searchMeeting(meetingSearchRequest: MeetingSearchRequest): Set<Meeting>

    /* returns all meetings in an interval */
    fun getMeetingsInInterval(interval: TimeInterval): Set<Meeting>
}