package model.meeting

import model.enums.MeetingPurpose
import org.bson.types.ObjectId
import java.sql.Timestamp

class Meeting(
    val title: String,
    val description: String,
    val participants: List<Participant>,
    val meetingOrganizer: MeetingOrganizer,
    val purpose: MeetingPurpose,
    val start: Timestamp,
    val end: Timestamp,
    val roomId: ObjectId
) {
    constructor(meetingRequest: TimedMeetingRequest, roomId: ObjectId) : this(
        meetingRequest.title,
        meetingRequest.description,
        meetingRequest.participants,
        meetingRequest.meetingOrganizer,
        meetingRequest.purpose,
        meetingRequest.startTime,
        meetingRequest.endTime,
        roomId
    )

    val duration: Int = end.nanos - start.nanos
    val population = participants.size
}