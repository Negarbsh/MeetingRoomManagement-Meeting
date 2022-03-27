package dao

import model.meeting.Meeting
import org.bson.types.ObjectId
import java.sql.Timestamp

class MeetingRepository : MeetingCRUD {
    override fun insert(meeting: Meeting): ObjectId? {
        TODO("Not yet implemented")
    }

    override fun update(meetingId: ObjectId, newMeeting: Meeting) {
        TODO("Not yet implemented")
    }

    override fun delete(meetingId: ObjectId) {
        TODO("Not yet implemented")
    }

    override fun read(meetingId: ObjectId): Meeting {
        TODO("Not yet implemented")
    }

    override fun getAllMeetings(): HashMap<ObjectId, Meeting> {
        TODO("Not yet implemented")
    }

    override fun getMeetingsInPeriod(startTime: Timestamp, endTime: Timestamp): HashMap<ObjectId, Meeting> {
        TODO("Not yet implemented")
    }

    override fun upsert(meetingId: ObjectId, newMeeting: Meeting) {
        TODO("Not yet implemented")
    }
}