package schedule.domain.roomSelect

import schedule.model.meeting.TimedMeetingRequest
import org.bson.types.ObjectId

interface RoomSelector {
    /* Along all possible rooms, optimizes the room with the least capacity */
    fun getBestRoomChoice(meetingRequest: TimedMeetingRequest): ObjectId?
}
