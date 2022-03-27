package model.meeting

import model.enums.MeetingPurpose
import java.sql.Timestamp

class TimedMeetingRequest(
    title: String,
    description: String,
    participants: List<Participant>,
    meetingOrganizer: MeetingOrganizer,
    purpose: MeetingPurpose,
    val startTime: Timestamp,
    val endTime: Timestamp
) : MeetingRequest(
    title, description, participants, meetingOrganizer, purpose,
    endTime.time - startTime.time
) {
    constructor(meetingRequest: MeetingRequest, startTime: Timestamp, endTime: Timestamp) : this(
        meetingRequest.title,
        meetingRequest.description,
        meetingRequest.participants,
        meetingRequest.meetingOrganizer,
        meetingRequest.purpose,
        startTime, endTime
    )
}
