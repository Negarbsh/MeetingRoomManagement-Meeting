package schedule.model.enums

enum class MeetingPurpose {
    PD_CHAT, SPEC_REVIEW, GROOMING, SPRINT_PLANNING, INTERVIEW;

    /* If the purpose is an interview or a PD-chat, only small rooms are possible. This  function returns the max capacity.
    * returns -1 if there is no max capacity*/
    fun getMaxCapacity(): Int {
        var maxCapacity = -1
        if (this == PD_CHAT || this == INTERVIEW)
            maxCapacity = 3
        return maxCapacity
    }
}

