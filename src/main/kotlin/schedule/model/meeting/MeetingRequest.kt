package schedule.model.meeting

import schedule.model.enums.Feature
import schedule.model.enums.MeetingPurpose

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