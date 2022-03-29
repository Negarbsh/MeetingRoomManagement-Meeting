package model.meeting

import model.enums.Feature
import model.enums.MeetingPurpose
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import java.sql.Timestamp

class Meeting(
    @Id
    val meetingId: ObjectId? = null,
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
        title = meetingRequest.title,
        description = meetingRequest.description,
        participants = meetingRequest.participants,
        meetingOrganizer = meetingRequest.meetingOrganizer,
        purpose = meetingRequest.purpose,
        start = meetingRequest.startTime,
        end = meetingRequest.endTime,
        features = meetingRequest.features,
        roomId = roomId
    )

    constructor(meeting : Meeting): this(
        meetingId = meeting.meetingId,
        title = meeting.title,
        description = meeting.description,
        participants = meeting.participants,
        meetingOrganizer = meeting.meetingOrganizer,
        purpose = meeting.purpose,
        start = meeting.start,
        end = meeting.end,
        features = meeting.features,
        roomId = meeting.roomId
    )

    val duration: Int = end.nanos - start.nanos
    val population = participants.size
}