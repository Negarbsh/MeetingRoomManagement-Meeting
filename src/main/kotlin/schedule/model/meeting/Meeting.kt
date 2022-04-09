package schedule.model.meeting

import schedule.model.enums.Feature
import schedule.model.enums.MeetingPurpose
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import schedule.model.enums.Office


@Document(collection = "meetings")
class Meeting(
    @Id
    var meetingId: ObjectId? = null,
    val title: String,
    val description: String,
    val participants: List<Participant>,
    val meetingOrganizer: MeetingOrganizer,
    val purpose: MeetingPurpose,
    val timeInterval: TimeInterval,
    val features: List<Feature>?,
    var roomId: ObjectId,
    val office: Office
) {
    constructor(meetingRequest: TimedMeetingRequest, roomId: ObjectId) : this(
        title = meetingRequest.title,
        description = meetingRequest.description,
        participants = meetingRequest.participants,
        meetingOrganizer = meetingRequest.meetingOrganizer,
        purpose = meetingRequest.purpose,
        timeInterval = meetingRequest.timeInterval,
        features = meetingRequest.features,
        roomId = roomId,
        office = meetingRequest.office
    )

    constructor(meeting: Meeting) : this(
        meetingId = meeting.meetingId,
        title = meeting.title,
        description = meeting.description,
        participants = meeting.participants,
        meetingOrganizer = meeting.meetingOrganizer,
        purpose = meeting.purpose,
        timeInterval = meeting.timeInterval,
        features = meeting.features,
        roomId = meeting.roomId,
        office = meeting.office
    )

    override fun toString(): String {
        return "id: $meetingId\n" +
                "title: $title\n" +
                "description: $description\n" +
                "participants: $participants\n" +
                "purpose: $purpose\n" +
                "start: ${timeInterval.start} - end: ${timeInterval.end}\n" +
                "roomId: $roomId"
    }
}