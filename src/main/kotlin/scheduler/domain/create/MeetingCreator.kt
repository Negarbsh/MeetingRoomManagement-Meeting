package scheduler.domain.create

import scheduler.dao.MeetingCRUD
import scheduler.dao.RoomReader
import scheduler.dao.RoomRepository
import scheduler.model.Room
import scheduler.model.meeting.Meeting
import scheduler.model.meeting.MeetingRequest
import scheduler.model.meeting.Participant
import scheduler.model.meeting.TimedMeetingRequest
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import scheduler.domain.create.reorganizing.ReorganizeHandler
import scheduler.domain.create.reorganizing.Reorganizer
import scheduler.domain.mail.MailSender
import scheduler.domain.room.RoomAllocator
import scheduler.domain.room.RoomSearch
import java.sql.Timestamp
import java.time.Instant

@Service
class MeetingCreator(private val meetingDAO: MeetingCRUD) : Creator {
    private val roomSearcher: RoomSearch = RoomAllocator(meetingDAO)
    private val roomDAO: RoomReader = RoomRepository()
    private val reorganizeHandler: Reorganizer = ReorganizeHandler()
    private val mailSender: MailSender = MailSender()

    override fun createFixedTimeMeeting(timedMeetingRequest: TimedMeetingRequest): ObjectId? {
        val roomId: ObjectId? = roomSearcher.getBestRoomChoice(timedMeetingRequest)
        if (roomId != null)
            return finalizeMeetingCreation(timedMeetingRequest, roomId)
        val (changedMeetings, newRoomId) = createByReorganization(timedMeetingRequest)
        if (newRoomId == null) return null
        val meetingId = insertMeetingInDb(timedMeetingRequest, newRoomId)
        resetDB(changedMeetings)
        return meetingId
    }

    private fun finalizeMeetingCreation(
        timedMeetingRequest: TimedMeetingRequest,
        roomId: ObjectId
    ): ObjectId? {
        mailSender.sendMail(timedMeetingRequest) //todo async
        return insertMeetingInDb(timedMeetingRequest, roomId)
    }

    private fun createByReorganization(timedMeetingRequest: TimedMeetingRequest): Pair<List<Meeting>?, ObjectId?> {
        val meetings = meetingDAO.findAll()
        val rooms: List<Room> = roomDAO.getAllRooms()
        val (changedMeetings, newRoomId) = reorganizeHandler.reorganizeByMeeting(
            meetings,
            rooms,
            timedMeetingRequest,
        )
        return Pair(changedMeetings, newRoomId)
    }

    private fun resetDB(overwritingMeetings: List<Meeting>?) {
        if (overwritingMeetings == null) return
        for (meeting in overwritingMeetings) {
            meetingDAO.save(meeting) //todo will it be overwritten?
        }
    }

    /*Inserts the meeting in the database and returns the meetingId*/
    private fun insertMeetingInDb(timedMeetingRequest: TimedMeetingRequest, roomId: ObjectId): ObjectId? {
        val meeting = Meeting(timedMeetingRequest, roomId)
        return meetingDAO.insert(meeting).meetingId
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
            val roomId = roomSearcher.getBestRoomChoice(timedMeetingRequest) ?: continue
            return Pair(roomId, startTime)
        }
        return null
    }

    private fun areParticipantsFree(
        participants: List<Participant>,
        startTime: Timestamp,
        endTime: Timestamp
    ): Boolean {
        val meetingsToCheck = getMeetingsInPeriod(startTime, endTime, meetingDAO)
        for (meeting in meetingsToCheck) {
            val commonParticipants = meeting.participants.intersect(participants.toSet())
            if (commonParticipants.isNotEmpty()) return false
        }
        return true
    }

    private fun getMeetingsInPeriod(startTime: Timestamp, endTime: Timestamp, meetingDAO: MeetingCRUD): List<Meeting> {
        return meetingDAO.findAll() //todo query
    }

}