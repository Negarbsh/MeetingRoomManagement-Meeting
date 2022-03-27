package domain

import dao.MeetingCRUD
import dao.MeetingRepository
import dao.RoomReader
import dao.RoomRepository
import model.Room
import model.meeting.Meeting
import model.meeting.MeetingRequest
import model.meeting.Participant
import model.meeting.TimedMeetingRequest
import org.bson.types.ObjectId
import java.sql.Timestamp
import java.time.Instant

class MeetingCreator : Creator {
    private val roomSearcher: RoomSearch = RoomAllocator()
    private val meetingDAO: MeetingCRUD = MeetingRepository()
    private val roomDAO: RoomReader = RoomRepository()
    private val reorganizeHandler: Reorganizer = ReorganizeHandler()

    override fun createFixedTimeMeeting(timedMeetingRequest: TimedMeetingRequest): ObjectId? {
        val roomId: ObjectId? = roomSearcher.searchForRoom(timedMeetingRequest)
        if (roomId != null) return addMeetingToDB(timedMeetingRequest, roomId)
        val (newMeetings, meetingId) = createByReorganization(timedMeetingRequest)
        resetDB(newMeetings)
        return meetingId
    }

    private fun createByReorganization(timedMeetingRequest: TimedMeetingRequest): Pair<HashMap<ObjectId, Meeting>?, ObjectId?> {
        val meetings = meetingDAO.getAllMeetings()
        val rooms: List<Room> = roomDAO.getAllRooms()
        val (newMeetings, meetingId, roomId) = reorganizeHandler.reorganizeByMeeting(
            meetings,
            rooms,
            timedMeetingRequest
        )
        return Pair(newMeetings, meetingId)
    }

    private fun resetDB(overwritingMeetings: HashMap<ObjectId, Meeting>?) {
        if (overwritingMeetings == null) return
        for ((id, meeting) in overwritingMeetings) {
            meetingDAO.upsert(id, meeting)
        }
    }

    private fun addMeetingToDB(timedMeetingRequest: TimedMeetingRequest, roomId: ObjectId): ObjectId? {
        val meeting = Meeting(timedMeetingRequest, roomId)
        return meetingDAO.insert(meeting)
    }

    //todo It can be cleaner! + I'm not sure about 15 and 100...0
    override fun getEarliestMeetingChance(
        timeLowerBound: Timestamp?, meetingRequest: MeetingRequest
    ): Pair<ObjectId, Timestamp>? {
        var minuets = 0
        val duration = meetingRequest.duration
        var startTime: Timestamp = Timestamp.from(Instant.now())
        if (timeLowerBound != null) startTime = timeLowerBound
        while (minuets < 1000000000) {
            minuets += 15
            startTime = Timestamp(startTime.time + 15 * 60 * 1000)
            val endTime = Timestamp(startTime.time + duration)

            if (!areParticipantsFree(meetingRequest.participants, startTime, endTime)) continue
            val timedMeetingRequest = TimedMeetingRequest(meetingRequest, startTime, endTime)
            val roomId = roomSearcher.searchForRoom(timedMeetingRequest) ?: continue
            return Pair(roomId, startTime)
        }
        return null
    }

    private fun areParticipantsFree(
        participants: List<Participant>,
        startTime: Timestamp,
        endTime: Timestamp
    ): Boolean {
        val meetingsToCheck = meetingDAO.getMeetingsInPeriod(startTime, endTime)
        for ((_, meeting) in meetingsToCheck) {
            val commonParticipants = meeting.participants.intersect(participants.toSet())
            if (commonParticipants.isNotEmpty()) return false
        }
        return true
    }

}