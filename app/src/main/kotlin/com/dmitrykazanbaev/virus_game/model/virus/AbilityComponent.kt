package com.dmitrykazanbaev.virus_game.model.virus

import com.dmitrykazanbaev.virus_game.R
import com.dmitrykazanbaev.virus_game.service.ApplicationContextHolder

abstract class AbilityModification(zeroLevelValueResource: Int,
                                   firstLevelValueResource: Int,
                                   secondLevelValueResource: Int,
                                   thirdLevelValueResource: Int,

                                   zeroLevelUpgradeCostResource: Int,
                                   firstLevelUpgradeCostResource: Int,
                                   secondLevelUpgradeCostResource: Int,

                                   zeroLevelUpgradeDescriptionResource: Int,
                                   firstLevelUpgradeDescriptionResource: Int,
                                   secondLevelUpgradeDescriptionResource: Int,

                                   var detection: Int = 0,

                                   private val zeroLevelDetectionResource: Int,
                                   private val firstLevelDetectionResource: Int,
                                   private val secondLevelDetectionResource: Int,
                                   private val thirdLevelDetectionResource: Int) : Modification(
        zeroLevelValueResource = zeroLevelValueResource,
        firstLevelValueResource = firstLevelValueResource,
        secondLevelValueResource = secondLevelValueResource,
        thirdLevelValueResource = thirdLevelValueResource,

        zeroLevelUpgradeCostResource = zeroLevelUpgradeCostResource,
        firstLevelUpgradeCostResource = firstLevelUpgradeCostResource,
        secondLevelUpgradeCostResource = secondLevelUpgradeCostResource,

        zeroLevelUpgradeDescriptionResource = zeroLevelUpgradeDescriptionResource,
        firstLevelUpgradeDescriptionResource = firstLevelUpgradeDescriptionResource,
        secondLevelUpgradeDescriptionResource = secondLevelUpgradeDescriptionResource) {

    override fun synchronize() {
        super.synchronize()
        detection = getDetectionWithLevel(currentLevel)
    }

    private fun getDetectionWithLevel(level: Int): Int {
        return when (level) {
            0 -> ApplicationContextHolder.context.resources.getInteger(zeroLevelDetectionResource)
            1 -> ApplicationContextHolder.context.resources.getInteger(firstLevelDetectionResource)
            2 -> ApplicationContextHolder.context.resources.getInteger(secondLevelDetectionResource)
            3 -> ApplicationContextHolder.context.resources.getInteger(thirdLevelDetectionResource)

            else -> 0
        }
    }
}

class ThiefModification : AbilityModification(
        zeroLevelValueResource = R.dimen.thief_zero_level_value,
        firstLevelValueResource = R.dimen.thief_first_level_value,
        secondLevelValueResource = R.dimen.thief_second_level_value,
        thirdLevelValueResource = R.dimen.thief_third_level_value,

        zeroLevelUpgradeCostResource = R.dimen.thief_zero_level_upgrade_cost,
        firstLevelUpgradeCostResource = R.dimen.thief_first_level_upgrade_cost,
        secondLevelUpgradeCostResource = R.dimen.thief_second_level_upgrade_cost,

        zeroLevelUpgradeDescriptionResource = R.string.thief_zero_level_upgrade_description,
        firstLevelUpgradeDescriptionResource = R.string.thief_first_level_upgrade_description,
        secondLevelUpgradeDescriptionResource = R.string.thief_second_level_upgrade_description,

        zeroLevelDetectionResource = R.dimen.thief_zero_level_detection,
        firstLevelDetectionResource = R.dimen.thief_first_level_detection,
        secondLevelDetectionResource = R.dimen.thief_second_level_detection,
        thirdLevelDetectionResource = R.dimen.thief_third_level_detection) {

    override val title: String = ApplicationContextHolder.context.resources.getString(R.string.thief_title)

}

class ControlModification : AbilityModification(
        zeroLevelValueResource = R.dimen.control_zero_level_value,
        firstLevelValueResource = R.dimen.control_first_level_value,
        secondLevelValueResource = R.dimen.control_second_level_value,
        thirdLevelValueResource = R.dimen.control_third_level_value,

        zeroLevelUpgradeCostResource = R.dimen.control_zero_level_upgrade_cost,
        firstLevelUpgradeCostResource = R.dimen.control_first_level_upgrade_cost,
        secondLevelUpgradeCostResource = R.dimen.control_second_level_upgrade_cost,

        zeroLevelUpgradeDescriptionResource = R.string.control_zero_level_upgrade_description,
        firstLevelUpgradeDescriptionResource = R.string.control_first_level_upgrade_description,
        secondLevelUpgradeDescriptionResource = R.string.control_second_level_upgrade_description,

        zeroLevelDetectionResource = R.dimen.control_zero_level_detection,
        firstLevelDetectionResource = R.dimen.control_first_level_detection,
        secondLevelDetectionResource = R.dimen.control_second_level_detection,
        thirdLevelDetectionResource = R.dimen.control_third_level_detection) {

    override val title: String = ApplicationContextHolder.context.resources.getString(R.string.control_title)

}

class SpamModification : AbilityModification(
        zeroLevelValueResource = R.dimen.spam_zero_level_value,
        firstLevelValueResource = R.dimen.spam_first_level_value,
        secondLevelValueResource = R.dimen.spam_second_level_value,
        thirdLevelValueResource = R.dimen.spam_third_level_value,

        zeroLevelUpgradeCostResource = R.dimen.spam_zero_level_upgrade_cost,
        firstLevelUpgradeCostResource = R.dimen.spam_first_level_upgrade_cost,
        secondLevelUpgradeCostResource = R.dimen.spam_second_level_upgrade_cost,

        zeroLevelUpgradeDescriptionResource = R.string.spam_zero_level_upgrade_description,
        firstLevelUpgradeDescriptionResource = R.string.spam_first_level_upgrade_description,
        secondLevelUpgradeDescriptionResource = R.string.spam_second_level_upgrade_description,

        zeroLevelDetectionResource = R.dimen.spam_zero_level_detection,
        firstLevelDetectionResource = R.dimen.spam_first_level_detection,
        secondLevelDetectionResource = R.dimen.spam_second_level_detection,
        thirdLevelDetectionResource = R.dimen.spam_third_level_detection) {

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

    fun detection(): Int {
        return if (thief.detection + control.detection + spam.detection == 3) 0
        else thief.detection + control.detection + spam.detection
    }

    fun value(): Float {
        val percent = (thief.value + control.value + spam.value) / 100f
        return if (percent < 1) percent else 1f
    }
}
