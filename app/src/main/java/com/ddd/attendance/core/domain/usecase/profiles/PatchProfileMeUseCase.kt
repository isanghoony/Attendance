package com.ddd.attendance.core.domain.usecase.profiles

import com.ddd.attendance.core.data.repository.DefaultProfilesRepository
import javax.inject.Inject

class PatchProfileMeUseCase @Inject constructor(
    private val repository: DefaultProfilesRepository
) {
    operator fun invoke(
        name: String,
        inviteCodeId: String,
        role: String,
        team: String,
        responsibility: String,
        cohort: String
    ) = repository.patchProfileMe(
        name = name,
        inviteCodeId = inviteCodeId,
        role = role,
        team = team,
        responsibility = responsibility,
        cohort = cohort
    )
}