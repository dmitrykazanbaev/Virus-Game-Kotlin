package com.dmitrykazanbaev.virus_game.model.virus

import com.dmitrykazanbaev.virus_game.R
import com.dmitrykazanbaev.virus_game.service.ApplicationContextHolder


class ThiefModification : Modification(
        zeroLevelValueResource = R.dimen.thief_zero_level_value,
        firstLevelValueResource = R.dimen.thief_first_level_value,
        secondLevelValueResource = R.dimen.thief_second_level_value,
        thirdLevelValueResource = R.dimen.thief_third_level_value,

        zeroLevelUpgradeCostResource = R.dimen.thief_zero_level_upgrade_cost,
        firstLevelUpgradeCostResource = R.dimen.thief_first_level_upgrade_cost,
        secondLevelUpgradeCostResource = R.dimen.thief_second_level_upgrade_cost,

        zeroLevelUpgradeDescriptionResource = R.string.thief_zero_level_upgrade_description,
        firstLevelUpgradeDescriptionResource = R.string.thief_first_level_upgrade_description,
        secondLevelUpgradeDescriptionResource = R.string.thief_second_level_upgrade_description) {

    override val title: String = ApplicationContextHolder.context.resources.getString(R.string.thief_title)

}

class ControlModification : Modification(
        zeroLevelValueResource = R.dimen.control_zero_level_value,
        firstLevelValueResource = R.dimen.control_first_level_value,
        secondLevelValueResource = R.dimen.control_second_level_value,
        thirdLevelValueResource = R.dimen.control_third_level_value,

        zeroLevelUpgradeCostResource = R.dimen.control_zero_level_upgrade_cost,
        firstLevelUpgradeCostResource = R.dimen.control_first_level_upgrade_cost,
        secondLevelUpgradeCostResource = R.dimen.control_second_level_upgrade_cost,

        zeroLevelUpgradeDescriptionResource = R.string.control_zero_level_upgrade_description,
        firstLevelUpgradeDescriptionResource = R.string.control_first_level_upgrade_description,
        secondLevelUpgradeDescriptionResource = R.string.control_second_level_upgrade_description) {

    override val title: String = ApplicationContextHolder.context.resources.getString(R.string.control_title)

}

class SpamModification : Modification(
        zeroLevelValueResource = R.dimen.spam_zero_level_value,
        firstLevelValueResource = R.dimen.spam_first_level_value,
        secondLevelValueResource = R.dimen.spam_second_level_value,
        thirdLevelValueResource = R.dimen.spam_third_level_value,

        zeroLevelUpgradeCostResource = R.dimen.spam_zero_level_upgrade_cost,
        firstLevelUpgradeCostResource = R.dimen.spam_first_level_upgrade_cost,
        secondLevelUpgradeCostResource = R.dimen.spam_second_level_upgrade_cost,

        zeroLevelUpgradeDescriptionResource = R.string.spam_zero_level_upgrade_description,
        firstLevelUpgradeDescriptionResource = R.string.spam_first_level_upgrade_description,
        secondLevelUpgradeDescriptionResource = R.string.spam_second_level_upgrade_description) {

    override val title: String = ApplicationContextHolder.context.resources.getString(R.string.spam_title)

}

class AbilityComponent(val thief: ThiefModification = ThiefModification(),
                       val control: ControlModification = ControlModification(),
                       val spam: SpamModification = SpamModification()) : VirusComponent() {
    override fun synchronize() {
        thief.synchronize()
        control.synchronize()
        spam.synchronize()
    }

}
