package schedule.domain.assignment

import schedule.dao.MeetingCRUD
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import notifService.Notifier
import schedule.domain.roomSelect.RoomSelectorImpl
import schedule.domain.roomSelect.RoomSelector
import schedule.model.meeting.*
import java.sql.Timestamp
import java.time.Instant

@Service
class AssignerImpl(private val meetingDAO: MeetingCRUD) : Assigner {
    private val roomSearcher: RoomSelector = RoomSelectorImpl(meetingDAO)
    private val notifier: Notifier = Notifier()

    override fun scheduleFixedTimeMeeting(timedMeetingRequest: TimedMeetingRequest): ObjectId? {
        val roomId: ObjectId? = roomSearcher.getBestRoomChoice(timedMeetingRequest)
        if (roomId != null)
            return finalizeMeetingCreation(timedMeetingRequest, roomId)
        return null
    }

    override fun finalizeMeetingCreation(
        timedMeetingRequest: TimedMeetingRequest,
        roomId: ObjectId
    ): ObjectId? {
        notifier.sendMail(timedMeetingRequest)
        return insertMeetingInDb(timedMeetingRequest, roomId)
    }

    /*Inserts the meeting in the database and returns the meetingId*/
    private fun insertMeetingInDb(timedMeetingRequest: TimedMeetingRequest, roomId: ObjectId): ObjectId? {
        val meeting = Meeting(timedMeetingRequest, roomId)
        return meetingDAO.insert(meeting).meetingId
    }

    //todo It can be cleaner!
    override fun getEarliestMeetingChance(
        timeLowerBound: Timestamp?,
        meetingRequest: MeetingRequest,
        minuteIncrementPace: Int,
        maxMinuteIncrement: Int
    ): Pair<ObjectId, Timestamp>? {
        var minuets = 0
        val duration = meetingRequest.duration
        var startTime: Timestamp = Timestamp.from(Instant.now())
        if (timeLowerBound != null) startTime = timeLowerBound
        while (minuets < maxMinuteIncrement) {
            minuets += minuteIncrementPace
            startTime = Timestamp(startTime.time + minuteIncrementPace * 60 * 1000)
            val endTime = Timestamp(startTime.time + duration)

            if (!areParticipantsFree(meetingRequest.participants, TimeInterval(startTime, endTime))) continue
            val timedMeetingRequest = TimedMeetingRequest(meetingRequest, TimeInterval(startTime, endTime))
            val roomId = roomSearcher.getBestRoomChoice(timedMeetingRequest) ?: continue
            return Pair(roomId, startTime)
        }
        return null
    }

    private fun areParticipantsFree(
        participants: List<Participant>,
        timeInterval: TimeInterval
    ): Boolean {
        val meetingsToCheck = getMeetingsInInterval(timeInterval, meetingDAO)
        for (meeting in meetingsToCheck) {
            val commonParticipants = meeting.participants.intersect(participants.toSet())
            if (commonParticipants.isNotEmpty()) return false
        }
        return true
    }

    private fun getMeetingsInInterval(interval: TimeInterval, meetingDAO: MeetingCRUD): Set<Meeting> {
        return meetingDAO.findAllInsideTimeInterval(interval.start, interval.end)
    }

}