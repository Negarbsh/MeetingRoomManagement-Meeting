package domain.assignment

import notifService.Notifier
import notifService.NotifierImpl
import org.mockito.InjectMocks
import org.mockito.Mock
import schedule.domain.assignment.AssignerImpl
import schedule.domain.roomSelect.RoomSelector

class AssignerTest {

    @InjectMocks
    lateinit var assigner: AssignerImpl

    @Mock
    lateinit var roomSearcher: RoomSelector
    private val notifier: Notifier = NotifierImpl()

}