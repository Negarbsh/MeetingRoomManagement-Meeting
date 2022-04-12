package domain.assignment

import com.appmattus.kotlinfixture.kotlinFixture
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import schedule.dao.MeetingCRUD
import schedule.domain.assignment.AssignerImpl
import schedule.domain.roomSelect.RoomSelectorImpl
import schedule.model.Room
import schedule.model.meeting.*
import java.sql.Timestamp
import java.util.Date

class AssignerTest {

    @InjectMocks
    lateinit var assigner: AssignerImpl

    @Mock
    lateinit var roomSelector: RoomSelectorImpl

    @Mock
    lateinit var meetingRepo: MeetingCRUD

    val fixture = kotlinFixture()

    @BeforeEach
    fun init() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `scheduleFixedTimeMeeting should return null when there is no possible room`() {
        val request = fixture<TimedMeetingRequest>()
        Mockito.`when`(roomSelector.getBestRoomChoice(request)).thenReturn(null)

        val meetingId = assigner.scheduleFixedTimeMeeting(request)
        Assertions.assertEquals(null, meetingId)
    }

    @Test
    fun `scheduleFixedTimeMeeting should return the created meeting id when there is a room`() {
        val randomMeeting = fixture<Meeting>()
        val request = fixture<TimedMeetingRequest>()

        Mockito.`when`(roomSelector.getBestRoomChoice(request)).thenReturn(fixture<ObjectId>())
        Mockito.`when`(meetingRepo.insert(any<Meeting>() )).thenReturn(randomMeeting)
        val meetingId = assigner.scheduleFixedTimeMeeting(request)

        Assertions.assertEquals(randomMeeting.meetingId, meetingId)
    }

    @Test
    fun `finalizeMeetingCreation should insert it in db and return the meetingId`() {
        val randomMeeting = fixture<Meeting>()
        val request = fixture<TimedMeetingRequest>()

        Mockito.`when`(meetingRepo.insert(any<Meeting>())).thenReturn(randomMeeting)
        val meetingId = assigner.finalizeMeetingCreation(request, randomMeeting.roomId)

        Assertions.assertEquals(randomMeeting.meetingId, meetingId)
    }

    @Test
    fun `getEarliestMeetingChance should return null when no room is available ever`() {
        Mockito.`when`(roomSelector.getBestRoomChoice(any())).thenReturn(null)
        val bestMeetingChance =
            assigner.getEarliestMeetingChance(fixture<Timestamp>(), fixture(), 5, 100)
        Assertions.assertEquals(null, bestMeetingChance)
    }

    @Test
    fun `getEarliestMeetingChance should return a chance in which participants are free`() {
        val request = fixture<MeetingRequest>()
        val room = fixture<Room>()
        val requestTime = Timestamp(Date().time)
        val anHourLater = Timestamp(requestTime.time + 1000 * 3600)

        Mockito.`when`(roomSelector.getBestRoomChoice(any())).thenReturn(room.id)

        // we make the participants busy from now until 1 hour
        val meetingFixture = kotlinFixture {
            factory<Participant> { request.participants.first() }
            factory<TimeInterval> { TimeInterval(requestTime, Timestamp(anHourLater.time - 1000 * 60 * 1)) }
        }
        Mockito.`when`(meetingRepo.findAllInterferingWithInterval(any(), any())).thenReturn(setOf(meetingFixture()))

        val meetingChance = assigner.getEarliestMeetingChance(null, request, 5, 100)

        // the meeting offer should be for after an hour
        Assertions.assertNotNull(meetingChance)
        if (meetingChance != null) {
            val roomId = meetingChance.first
            val meetingStart = meetingChance.second
            Assertions.assertEquals(room.id, roomId)
            Assertions.assertFalse(meetingStart.before(anHourLater))
        }
    }

}