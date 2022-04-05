package scheduler.model.meeting

import scheduler.model.enums.Feature
import scheduler.model.enums.MeetingPurpose

open class MeetingRequest(
    val title: String,
    val description: String,
    val participants: List<Participant>,
    val meetingOrganizer: MeetingOrganizer,
    val purpose: MeetingPurpose,
    val duration: Long,
    val features: List<Feature>?
) {
    val population = participants.size
}