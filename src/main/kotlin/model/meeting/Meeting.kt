package model.meeting

import model.enums.Feature
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
    val features: List<Feature>?,
    var roomId: ObjectId
) {
    constructor(meetingRequest: TimedMeetingRequest, roomId: ObjectId) : this(
        meetingRequest.title,
        meetingRequest.description,
        meetingRequest.participants,
        meetingRequest.meetingOrganizer,
        meetingRequest.purpose,
        meetingRequest.startTime,
        meetingRequest.endTime,
        meetingRequest.features,
        roomId
    )

    constructor(meeting : Meeting): this(
        meeting.title,
        meeting.description,
        meeting.participants,
        meeting.meetingOrganizer,
        meeting.purpose,
        meeting.start,
        meeting.end,
        meeting.features,
        meeting.roomId
    )

    val duration: Int = end.nanos - start.nanos
    val population = participants.size
}