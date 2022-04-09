package schedule.model.meeting

import org.bson.types.ObjectId

class MeetingEditRequest(val meetingId: ObjectId, val newMeetingRequest: TimedMeetingRequest)
