package schedule.model.meeting

import schedule.model.enums.Feature
import schedule.model.enums.MeetingPurpose
import schedule.model.enums.Office

class TimedMeetingRequest(
    title: String,
    description: String,
    participants: List<Participant>,
    meetingOrganizer: MeetingOrganizer,
    purpose: MeetingPurpose,
    val timeInterval: TimeInterval,
    features: List<Feature>?,
    office: Office
) : MeetingRequest(
    title, description, participants, meetingOrganizer, purpose,
    timeInterval.duration.toLong(), features, office
) {
    constructor(
        meetingRequest: MeetingRequest,
        timeInterval: TimeInterval
    ) : this(
        meetingRequest.title,
        meetingRequest.description,
        meetingRequest.participants,
        meetingRequest.meetingOrganizer,
        meetingRequest.purpose,
        timeInterval,
        meetingRequest.features,
        meetingRequest.office
    )

    constructor(meeting: Meeting) : this(
        meeting.title,
        meeting.description,
        meeting.participants,
        meeting.meetingOrganizer,
        meeting.purpose,
        meeting.timeInterval,
        meeting.features,
        meeting.office
    )
}
