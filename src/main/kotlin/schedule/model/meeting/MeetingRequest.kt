package schedule.model.meeting

import schedule.model.enums.Feature
import schedule.model.enums.MeetingPurpose
import schedule.model.enums.Office

open class MeetingRequest(
    val title: String,
    val description: String,
    val participants: List<Participant>,
    val meetingOrganizer: MeetingOrganizer,
    val purpose: MeetingPurpose,
    val duration: Long,
    val features: List<Feature>?,
    val office: Office
) {
    val population = participants.size
}