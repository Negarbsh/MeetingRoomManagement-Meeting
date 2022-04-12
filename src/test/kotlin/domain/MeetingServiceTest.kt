package domain

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
import org.mockito.kotlin.eq
import schedule.dao.MeetingCRUD
import schedule.dao.roomGrpc.RoomRepoImpl
import schedule.domain.MeetingServiceImpl
import schedule.domain.assignment.AssignerImpl
import schedule.domain.assignment.reassignment.ReassignerImpl
import schedule.model.Room
import schedule.model.enums.Feature
import schedule.model.enums.Office
import schedule.model.meeting.Meeting
import schedule.model.meeting.MeetingRequest
import schedule.model.meeting.TimedMeetingRequest
import java.sql.Timestamp

class MeetingServiceTest {

    @InjectMocks
    lateinit var meetingService: MeetingServiceImpl

    @Mock
    lateinit var roomRepo: RoomRepoImpl

    @Mock
    lateinit var meetingCRUD: MeetingCRUD

    @Mock
    lateinit var assigner: AssignerImpl

    @Mock
    lateinit var reassigner: ReassignerImpl

    val fixture = kotlinFixture()

    @BeforeEach
    fun init() {
        MockitoAnnotations.openMocks(this)
        Mockito.`when`(roomRepo.findAllRooms()).thenReturn(
            listOf(
                Room(
                    ObjectId(),
                    "tehran",
                    Office.TEHRAN,
                    listOf(Feature.PROJECTOR, Feature.WHITE_BOARD, Feature.SOUND_PROOF),
                    20
                ),
                Room(
                    ObjectId(),
                    "shiraz",
                    Office.TEHRAN,
                    listOf(Feature.WHITE_BOARD, Feature.SOUND_PROOF),
                    6
                ),
                Room(
                    ObjectId("000000642d276104ab000064"),
                    "mashhad",
                    Office.TEHRAN,
                    listOf(Feature.WHITE_BOARD, Feature.PROJECTOR),
                    8
                )
            )
        )
    }

    @Test
    fun `schedule should return null when assigner can't schedule`() {
        val request = fixture<TimedMeetingRequest>()
        Mockito.`when`(assigner.scheduleFixedTimeMeeting(request)).thenReturn(null)

        val response = meetingService.schedule(request)
        Assertions.assertEquals(null, response)
    }

    @Test
    fun `schedule should return null when both assignment and reassignment fail`() {
        val fixture = kotlinFixture()
        val request = fixture<TimedMeetingRequest>()
        Mockito.`when`(assigner.scheduleFixedTimeMeeting(request)).thenReturn(null)
        Mockito.`when`(reassigner.reassignByMeeting(any(), any(), eq(request), any()))
            .thenReturn(null)
        val response = meetingService.schedule(request)
        Assertions.assertEquals(null, response)
    }

    @Test
    fun `if assignment fails but reassignment succeeds, schedule should return the reassignment result`() {
        val request = fixture<TimedMeetingRequest>()
        val reassignResult = fixture<Pair<ArrayList<Meeting>, ObjectId>>()
        val meetingId = fixture<ObjectId>()
        Mockito.`when`(assigner.scheduleFixedTimeMeeting(request)).thenReturn(null)
        Mockito.`when`(reassigner.reassignByMeeting(any(), any(), eq(request), any())).thenReturn(reassignResult)

        Mockito.`when`(assigner.finalizeMeetingCreation(request, reassignResult.second)).thenReturn(meetingId)

        val receivedMeetingId = meetingService.schedule(request)

        Assertions.assertEquals(meetingId, receivedMeetingId)
    }

    @Test
    fun `getEarliestMeetingChance should return assigners output`() {
        val timeLowerBound = fixture<Timestamp>()
        val request = fixture<MeetingRequest>()
        val mockedMeetingChance = fixture<Pair<ObjectId, Timestamp>>()

        Mockito.`when`(assigner.getEarliestMeetingChance(eq(timeLowerBound), eq(request), any(), any()))
            .thenReturn(mockedMeetingChance)
        val meetingChance = meetingService.getEarliestMeetingChance(timeLowerBound, request)
        Assertions.assertEquals(mockedMeetingChance, meetingChance)
    }
}