package scheduler.domain.room

import scheduler.model.enums.MeetingPurpose
import org.springframework.stereotype.Service

@Service
class LargeRoomAllocation {

    fun getMaxCapacity(purpose: MeetingPurpose): Int {
        var maxCapacity = -1
        if (purpose == MeetingPurpose.PD_CHAT || purpose == MeetingPurpose.INTERVIEW)
            maxCapacity = 3
        return maxCapacity
    }
}