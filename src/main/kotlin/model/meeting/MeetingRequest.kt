package model.meeting

import model.enums.MeetingPurpose

open class MeetingRequest(
    val title: String,
    val description: String,
    val participants: List<Participant>,
    val meetingOrganizer: MeetingOrganizer,
    val purpose: MeetingPurpose,
    val duration: Long
)