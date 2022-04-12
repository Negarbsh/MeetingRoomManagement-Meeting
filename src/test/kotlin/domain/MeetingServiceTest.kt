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
import org.mockito.kotlin.atLeastOnce
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import schedule.dao.MeetingCRUD
import schedule.dao.roomGrpc.RoomRepoImpl
import schedule.domain.MeetingServiceImpl
import schedule.domain.assignment.AssignerImpl
import schedule.domain.assignment.reassignment.ReassignerImpl
import schedule.model.MeetingSearchRequest
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

    @Test
    fun `cancel should return false if the meeting doesn't exist`() {
        Mockito.`when`(meetingCRUD.existsById(any())).thenReturn(false)
        val isCanceled = meetingService.cancel(fixture())
        Assertions.assertFalse(isCanceled)
    }

    @Test
    fun `cancel successfully when the meeting exists`() {
        Mockito.`when`(meetingCRUD.existsById(any())).thenReturn(true)
        val isCanceled = meetingService.cancel(fixture())
        Assertions.assertTrue(isCanceled)
        verify(meetingCRUD, atLeastOnce()).deleteById(any())
    }

    @Test
    fun `edit should return null when the meeting doesn't exist`() {
        Mockito.`when`(meetingCRUD.existsById(any())).thenReturn(false)
        val editedMeetingId = meetingService.edit(fixture(), fixture())
        Assertions.assertNull(editedMeetingId)
    }

    @Test
    fun `edit should cancel and then schedule if the meeting exists`() {
        Mockito.`when`(meetingCRUD.existsById(any())).thenReturn(true)
        val editedMeetingId = meetingService.edit(fixture(), fixture())
        //todo how do I check this without caring about the already tested functions?
    }

    @Test
    fun `getMeetingsInInterval should call and return the meetingCrud same method`() {
        val expectedInterferingMeetings = fixture<Set<Meeting>>()
        Mockito.`when`(meetingCRUD.findAllInterferingWithInterval(any(), any())).thenReturn(expectedInterferingMeetings)
        val meetingsInInterval = meetingService.getMeetingsInInterval(fixture())
        Assertions.assertEquals(expectedInterferingMeetings, meetingsInInterval)
        verify(meetingCRUD, atLeastOnce()).findAllInterferingWithInterval(any(), any())
    }

    @Test
    fun `search meetings with specific interval when roomId is null`() {
        val meetingSearchFixture = kotlinFixture { factory<ObjectId?> { null } }
        val meetingSearchRequest = meetingSearchFixture<MeetingSearchRequest>()
        val expectedMeetings = fixture<Set<Meeting>>()

        Mockito.`when`(meetingCRUD.findAllInterferingWithInterval(any(), any())).thenReturn(expectedMeetings)
        val searchResult = meetingService.searchMeeting(meetingSearchRequest)

        Assertions.assertEquals(expectedMeetings, searchResult)
    }

    @Test
    fun `search meetings with specific roomId and interval`() {
        val requestFixture = kotlinFixture {
            factory<ObjectId?> { fixture<ObjectId>() } //to make sure the room id won't be null
        }
        val meetingSearchRequest = requestFixture<MeetingSearchRequest>()
        val timeMatchedMeetings = fixture<Set<Meeting>>()
        val roomMatchedMeetings = fixture<ArrayList<Meeting>>()
        roomMatchedMeetings.addAll(timeMatchedMeetings)

        Mockito.`when`(meetingCRUD.findAllInterferingWithInterval(any(), any())).thenReturn(timeMatchedMeetings)
        Mockito.`when`(meetingCRUD.findAllByRoomId(any())).thenReturn(roomMatchedMeetings)
        val searchRequest = meetingService.searchMeeting(meetingSearchRequest)

        Assertions.assertEquals(
            timeMatchedMeetings.intersect(roomMatchedMeetings.toSet()), searchRequest
        )
    }

}