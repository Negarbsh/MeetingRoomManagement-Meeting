package scheduler.model.meeting

import scheduler.model.enums.Feature
import scheduler.model.enums.MeetingPurpose
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

    constructor(meeting: Meeting) : this(
        meeting.title, meeting.description, meeting.participants, meeting.meetingOrganizer, meeting.purpose, meeting.start, meeting.end, meeting.features
    )
}
