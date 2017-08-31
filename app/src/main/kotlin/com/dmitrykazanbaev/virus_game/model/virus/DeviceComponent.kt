package com.dmitrykazanbaev.virus_game.model.virus

import com.dmitrykazanbaev.virus_game.R
import com.dmitrykazanbaev.virus_game.service.ApplicationContextHolder


class PhoneModification : Modification(
        zeroLevelValueResource = R.dimen.phone_zero_level_value,
        firstLevelValueResource = R.dimen.phone_first_level_value,
        secondLevelValueResource = R.dimen.phone_second_level_value,
        thirdLevelValueResource = R.dimen.phone_third_level_value,

        zeroLevelUpgradeCostResource = R.dimen.phone_zero_level_upgrade_cost,
        firstLevelUpgradeCostResource = R.dimen.phone_first_level_upgrade_cost,
        secondLevelUpgradeCostResource = R.dimen.phone_second_level_upgrade_cost,

        zeroLevelUpgradeDescriptionResource = R.string.phone_zero_level_upgrade_description,
        firstLevelUpgradeDescriptionResource = R.string.phone_first_level_upgrade_description,
        secondLevelUpgradeDescriptionResource = R.string.phone_second_level_upgrade_description) {

    override val title: String = ApplicationContextHolder.context.resources.getString(R.string.phone_title)
}

class PCModification : Modification(
        zeroLevelValueResource = R.dimen.pc_zero_level_value,
        firstLevelValueResource = R.dimen.pc_first_level_value,
        secondLevelValueResource = R.dimen.pc_second_level_value,
        thirdLevelValueResource = R.dimen.pc_third_level_value,

        zeroLevelUpgradeCostResource = R.dimen.pc_zero_level_upgrade_cost,
        firstLevelUpgradeCostResource = R.dimen.pc_first_level_upgrade_cost,
        secondLevelUpgradeCostResource = R.dimen.pc_second_level_upgrade_cost,

        zeroLevelUpgradeDescriptionResource = R.string.pc_zero_level_upgrade_description,
        firstLevelUpgradeDescriptionResource = R.string.pc_first_level_upgrade_description,
        secondLevelUpgradeDescriptionResource = R.string.pc_second_level_upgrade_description) {

    override val title: String = ApplicationContextHolder.context.resources.getString(R.string.pc_title)
}

class SmartHomeModification : Modification(
        zeroLevelValueResource = R.dimen.smart_home_zero_level_value,
        firstLevelValueResource = R.dimen.smart_home_first_level_value,
        secondLevelValueResource = R.dimen.smart_home_second_level_value,
        thirdLevelValueResource = R.dimen.smart_home_third_level_value,

        zeroLevelUpgradeCostResource = R.dimen.smart_home_zero_level_upgrade_cost,
        firstLevelUpgradeCostResource = R.dimen.smart_home_first_level_upgrade_cost,
        secondLevelUpgradeCostResource = R.dimen.smart_home_second_level_upgrade_cost,

        zeroLevelUpgradeDescriptionResource = R.string.smart_home_zero_level_upgrade_description,
        firstLevelUpgradeDescriptionResource = R.string.smart_home_first_level_upgrade_description,
        secondLevelUpgradeDescriptionResource = R.string.smart_home_second_level_upgrade_description) {

    override val title: String = ApplicationContextHolder.context.resources.getString(R.string.smart_home_title)
}

class DeviceComponent(val phone: Modification = PhoneModification(),
                      val pc: Modification = PCModification(),
                      val smartHome: Modification = SmartHomeModification()) : VirusComponent() {
    override fun synchronize() {
        phone.synchronize()
        pc.synchronize()
        smartHome.synchronize()
    }
}

