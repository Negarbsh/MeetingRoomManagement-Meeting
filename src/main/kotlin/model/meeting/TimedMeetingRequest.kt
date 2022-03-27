package model.meeting

import model.enums.Feature
import model.enums.MeetingPurpose
import java.sql.Timestamp

class TimedMeetingRequest(
    title: String,
    description: String,
    participants: List<Participant>,
    meetingOrganizer: MeetingOrganizer,
    purpose: MeetingPurpose,
    val startTime: Timestamp,
    val endTime: Timestamp,
    features: List<Feature>?
) : MeetingRequest(
    title, description, participants, meetingOrganizer, purpose,
    endTime.time - startTime.time, features
) {
    constructor(
        meetingRequest: MeetingRequest,
        startTime: Timestamp,
        endTime: Timestamp
    ) : this(
        meetingRequest.title,
        meetingRequest.description,
        meetingRequest.participants,
        meetingRequest.meetingOrganizer,
        meetingRequest.purpose,
        startTime, endTime,
        meetingRequest.features
    )
}
