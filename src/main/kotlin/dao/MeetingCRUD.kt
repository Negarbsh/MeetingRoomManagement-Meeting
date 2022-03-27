package dao

import model.meeting.Meeting
import org.bson.types.ObjectId
import java.sql.Timestamp

interface MeetingCRUD {

    //if successful, returns the meetingId in db
    fun insert(meeting: Meeting): ObjectId?

    fun update(meetingId: ObjectId, newMeeting: Meeting)

    fun upsert(meetingId: ObjectId, newMeeting: Meeting)

    fun delete(meetingId: ObjectId)

    fun read(meetingId: ObjectId): Meeting

    fun getAllMeetings(): HashMap<ObjectId, Meeting>

    fun getMeetingsInPeriod(startTime: Timestamp, endTime: Timestamp): HashMap<ObjectId, Meeting>
}