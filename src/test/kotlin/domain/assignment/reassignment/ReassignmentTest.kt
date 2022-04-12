package domain.assignment.reassignment

import com.appmattus.kotlinfixture.kotlinFixture
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import schedule.domain.assignment.reassignment.ReassignerImpl
import schedule.model.Room
import schedule.model.enums.Feature
import schedule.model.enums.Office
import schedule.model.meeting.Meeting
import schedule.model.meeting.TimeInterval
import schedule.model.meeting.TimedMeetingRequest

class ReassignmentTest {

    private val reassigner: ReassignerImpl = ReassignerImpl()

    /*  room capacities: {a : 1, b:2, c:3}
     *  current meetings: { B:{population : 1, room:a}, C:{population:3 , room:C} }
     *  new meeting request (A): {population : 2}
     *  the reassigner should assign a to A, b to B and c to C */
    @Test
    fun `one level reassignment`() {
        val timeInterval = kotlinFixture()<TimeInterval>()
        val roomsList = getRoomsList(timeInterval, 1, 2, 3)
        val meetingsList = getMeetingsList(roomsList[1], roomsList[2], timeInterval)

        val request = kotlinFixture {
            repeatCount { 2 }                           //the number of participants would be 2
            factory<Office> { Office.TEHRAN }
            factory<TimeInterval> { timeInterval }
            factory<List<Feature>?> { null }            //to make sure that required features won't cause any problem
        }<TimedMeetingRequest>()

        val reassignResult = reassigner.checkReassignment(1, roomsList, meetingsList, request)

        Assertions.assertNotNull(reassignResult)
        if (reassignResult != null)
            Assertions.assertEquals(reassignResult.second, roomsList[1].id)
    }

    /*
    * The rooms and the meetings are the same as above test, but the algorithm level is 2*/
    @Test
    fun `two level reassignment algorithm that needs only one level`() {
        val timeInterval = kotlinFixture()<TimeInterval>()
        val roomsList = getRoomsList(timeInterval, 1, 2, 3)
        val meetingsList = getMeetingsList(roomsList[1], roomsList[2], timeInterval)

        val request = kotlinFixture {
            repeatCount { 2 }                           //the number of participants would be 2
            factory<Office> { Office.TEHRAN }
            factory<TimeInterval> { timeInterval }
            factory<List<Feature>?> { null }            //to make sure that required features won't cause any problem
        }<TimedMeetingRequest>()

        val reassignResult = reassigner.checkReassignment(2, roomsList, meetingsList, request)

        Assertions.assertNotNull(reassignResult)
        if (reassignResult != null)
            Assertions.assertEquals(reassignResult.second, roomsList[1].id)
    }

    @Test
    fun `unsuccessful one level reassignment`() {
        val timeInterval = kotlinFixture()<TimeInterval>()
        val roomsList = getRoomsList(timeInterval, 1, 2, 3)
        val meetingsList = getMeetingsList(roomsList[1], roomsList[2], timeInterval)

        val request = kotlinFixture {
            repeatCount { 10 }                           //the number of participants would be 10
            factory<Office> { Office.TEHRAN }
            factory<TimeInterval> { timeInterval }
            factory<List<Feature>?> { null }            //to make sure that required features won't cause any problem
        }<TimedMeetingRequest>()

        val reassignResult = reassigner.checkReassignment(1, roomsList, meetingsList, request)

        Assertions.assertNull(reassignResult)
    }

    private fun getMeetingsList(
        firstMeetingRoom: Room,
        secondMeetingRoom: Room,
        timeInterval: TimeInterval
    ) = arrayListOf<Meeting>(
        kotlinFixture {
            repeatCount { 1 }                       //the number of participants would be 1
            factory<ObjectId> { firstMeetingRoom.id }   // the meeting's room will be the room with capacity 2
            factory<Office> { Office.TEHRAN }
            factory<TimeInterval> { timeInterval }
        }(),
        kotlinFixture {
            repeatCount { 3 }                       //the number of participants would be 3
            factory<ObjectId> { secondMeetingRoom.id }   // the meeting's room will be the room with capacity 3
            factory<Office> { Office.TEHRAN }
            factory<TimeInterval> { timeInterval }
        }(),
    )

    private fun getRoomsList(timeInterval: TimeInterval, firstCapacity: Int, secondCapacity: Int, thirdCapacity: Int) =
        listOf<Room>(
            kotlinFixture {
                factory<Int> { firstCapacity }
                factory<Office> { Office.TEHRAN }
                factory<TimeInterval> { timeInterval }
            }(),
            kotlinFixture {
                factory<Int> { secondCapacity }
                factory<Office> { Office.TEHRAN }
                factory<TimeInterval> { timeInterval }
            }(),
            kotlinFixture {
                factory<Int> { thirdCapacity }
                factory<Office> { Office.TEHRAN }
                factory<TimeInterval> { timeInterval }
            }()
        )

}