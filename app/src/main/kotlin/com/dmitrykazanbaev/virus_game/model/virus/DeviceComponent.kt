package com.dmitrykazanbaev.virus_game.model.virus

import com.dmitrykazanbaev.virus_game.AbstractLevelActivity
import com.dmitrykazanbaev.virus_game.R


class PhoneModification(context: AbstractLevelActivity) : Modification(
        context = context,

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

    override val title: String = context.resources.getString(R.string.phone_title)
}

class PCModification(context: AbstractLevelActivity) : Modification(
        context = context,

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

    override val title: String = context.resources.getString(R.string.pc_title)
}

class SmartHomeModification(context: AbstractLevelActivity) : Modification(
        context = context,

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

    override val title: String = context.resources.getString(R.string.smart_home_title)
}

class DeviceComponent(val context: AbstractLevelActivity,
                      val phone: Modification = PhoneModification(context),
                      val pc: Modification = PCModification(context),
                      val smartHome: Modification = SmartHomeModification(context)) : VirusComponent() {
    override fun synchronize() {
        phone.synchronize()
        pc.synchronize()
        smartHome.synchronize()
    }
}

