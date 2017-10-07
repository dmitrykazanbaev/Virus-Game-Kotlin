package com.dmitrykazanbaev.virus_game.model.virus

import com.dmitrykazanbaev.virus_game.AbstractLevelActivity
import com.dmitrykazanbaev.virus_game.R


class InvisibleModification(context: AbstractLevelActivity) : Modification(
        context = context,

        zeroLevelValueResource = R.dimen.invisible_zero_level_value,
        firstLevelValueResource = R.dimen.invisible_first_level_value,
        secondLevelValueResource = R.dimen.invisible_second_level_value,
        thirdLevelValueResource = R.dimen.invisible_third_level_value,

        zeroLevelUpgradeCostResource = R.dimen.invisible_zero_level_upgrade_cost,
        firstLevelUpgradeCostResource = R.dimen.invisible_first_level_upgrade_cost,
        secondLevelUpgradeCostResource = R.dimen.invisible_second_level_upgrade_cost,

        zeroLevelUpgradeDescriptionResource = R.string.invisible_zero_level_upgrade_description,
        firstLevelUpgradeDescriptionResource = R.string.invisible_first_level_upgrade_description,
        secondLevelUpgradeDescriptionResource = R.string.invisible_second_level_upgrade_description) {

    override val title: String = context.resources.getString(R.string.invisible_title)
}

class MaskModification(context: AbstractLevelActivity) : Modification(
        context = context,

        zeroLevelValueResource = R.dimen.mask_zero_level_value,
        firstLevelValueResource = R.dimen.mask_first_level_value,
        secondLevelValueResource = R.dimen.mask_second_level_value,
        thirdLevelValueResource = R.dimen.mask_third_level_value,

        zeroLevelUpgradeCostResource = R.dimen.mask_zero_level_upgrade_cost,
        firstLevelUpgradeCostResource = R.dimen.mask_first_level_upgrade_cost,
        secondLevelUpgradeCostResource = R.dimen.mask_second_level_upgrade_cost,

        zeroLevelUpgradeDescriptionResource = R.string.mask_zero_level_upgrade_description,
        firstLevelUpgradeDescriptionResource = R.string.mask_first_level_upgrade_description,
        secondLevelUpgradeDescriptionResource = R.string.mask_second_level_upgrade_description) {

    override val title: String = context.resources.getString(R.string.mask_title)
}

class NewVirusModification(context: AbstractLevelActivity) : Modification(
        context = context,

        zeroLevelValueResource = R.dimen.new_virus_zero_level_value,
        firstLevelValueResource = R.dimen.new_virus_first_level_value,
        secondLevelValueResource = R.dimen.new_virus_second_level_value,
        thirdLevelValueResource = R.dimen.new_virus_third_level_value,

        zeroLevelUpgradeCostResource = R.dimen.new_virus_zero_level_upgrade_cost,
        firstLevelUpgradeCostResource = R.dimen.new_virus_first_level_upgrade_cost,
        secondLevelUpgradeCostResource = R.dimen.new_virus_second_level_upgrade_cost,

        zeroLevelUpgradeDescriptionResource = R.string.new_virus_zero_level_upgrade_description,
        firstLevelUpgradeDescriptionResource = R.string.new_virus_first_level_upgrade_description,
        secondLevelUpgradeDescriptionResource = R.string.new_virus_second_level_upgrade_description) {

    override val title: String = context.resources.getString(R.string.new_virus_title)
}

class ResistanceComponent(val context: AbstractLevelActivity,
                          val invisible: Modification = InvisibleModification(context),
                          val mask: Modification = MaskModification(context),
                          val newVirus: Modification = NewVirusModification(context)) : VirusComponent() {
    override fun synchronize() {
        invisible.synchronize()
        mask.synchronize()
        newVirus.synchronize()
    }
}