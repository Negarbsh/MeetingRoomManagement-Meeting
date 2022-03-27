package domain

import model.enums.MeetingPurpose

class LargeRoomAllocation {

    fun getMaxCapacity(purpose: MeetingPurpose): Int {
        var maxCapacity = -1
        if (purpose == MeetingPurpose.PD_CHAT || purpose == MeetingPurpose.INTERVIEW)
            maxCapacity = 3
        return maxCapacity
    }
}