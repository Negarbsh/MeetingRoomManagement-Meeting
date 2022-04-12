package domain.roomSelect

import com.appmattus.kotlinfixture.kotlinFixture
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import schedule.dao.MeetingCRUD
import schedule.dao.roomGrpc.RoomRepoImpl
import schedule.domain.roomSelect.RoomSelectorImpl
import schedule.model.Room
import schedule.model.enums.Feature
import schedule.model.enums.MeetingPurpose
import schedule.model.enums.Office
import schedule.model.meeting.Meeting
import schedule.model.meeting.TimedMeetingRequest

class RoomSelectorTest {

    @InjectMocks
    lateinit var roomSelector: RoomSelectorImpl

    @Mock
    lateinit var roomRepo: RoomRepoImpl

    @Mock
    lateinit var meetingCrud: MeetingCRUD

    val idFixture = kotlinFixture()
    private val mashhad = Room(
        idFixture(), "mashhad", Office.TEHRAN, listOf(Feature.WHITE_BOARD, Feature.PROJECTOR), 8
    )
    private val shiraz = Room(
        idFixture(), "shiraz", Office.TEHRAN, listOf(Feature.WHITE_BOARD, Feature.SOUND_PROOF), 6
    )
    private val tehran = Room(
        idFixture(), "tehran", Office.TEHRAN, listOf(Feature.PROJECTOR, Feature.WHITE_BOARD, Feature.SOUND_PROOF), 20
    )

    @BeforeEach
    fun init() {
        MockitoAnnotations.openMocks(this)
        Mockito.`when`(roomRepo.findAllRooms()).thenReturn(
            listOf(tehran, shiraz, mashhad)
        )
    }

    @Test
    fun `getAllPossibleRooms for a request with no interfering meeting`() {
        val fixture = kotlinFixture {
            repeatCount { 7 } //to make sure the number of request participants is 7
            factory<Feature> { Feature.WHITE_BOARD }
            factory<MeetingPurpose> { MeetingPurpose.GROOMING }
            factory<Office> { Office.TEHRAN }
        }
        val request = fixture<TimedMeetingRequest>()
        Mockito.`when`(roomRepo.searchRooms(request.features, request.population, -1)).thenReturn(
            listOf(tehran, mashhad)
        )

        val rooms = roomSelector.getAllPossibleRooms(request)
        Assertions.assertEquals(2, rooms.size)
    }

    @Test
    fun `getAllPossibleRooms for a request when with interfering meeting`() {
        val fixture = kotlinFixture {
            repeatCount { 7 } //to make sure the number of request participants is 7
            factory<Feature> { Feature.WHITE_BOARD }
            factory<MeetingPurpose> { MeetingPurpose.GROOMING }
            factory<Office> { Office.TEHRAN }
        }
        val request = fixture<TimedMeetingRequest>()
        Mockito.`when`(roomRepo.searchRooms(request.features, request.population, -1))
            .thenReturn(listOf(tehran, mashhad))
        Mockito.`when`(meetingCrud.findAllInterferingWithInterval(request.timeInterval.start, request.timeInterval.end))
            .thenReturn(setOf<Meeting>(Meeting(request, mashhad.id)))
        Mockito.`when`(roomRepo.findAllByIds(listOf(mashhad.id))).thenReturn(listOf(mashhad))

        val rooms = roomSelector.getAllPossibleRooms(request)
        Assertions.assertEquals(1, rooms.size)
    }

    @Test
    fun `getBestRoomChoice should return the room with less capacity`() {
        val fixture = kotlinFixture {
            repeatCount { 7 } //to make sure the number of request participants is 7
            factory<Feature> { Feature.WHITE_BOARD }
            factory<MeetingPurpose> { MeetingPurpose.GROOMING }
            factory<Office> { Office.TEHRAN }
        }
        val request = fixture<TimedMeetingRequest>()
        Mockito.`when`(roomRepo.searchRooms(request.features, request.population, -1))
            .thenReturn(listOf(tehran, mashhad))
        val roomId = roomSelector.getBestRoomChoice(request)
        Assertions.assertEquals(mashhad.id, roomId)
    }
}