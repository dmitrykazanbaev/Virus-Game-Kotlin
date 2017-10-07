package com.dmitrykazanbaev.virus_game.model.virus

import com.dmitrykazanbaev.virus_game.AbstractLevelActivity

abstract class Modification(val context: AbstractLevelActivity,

                            val maxLevel: Int = 3,
                            var currentLevel: Int = 0,
                            var value: Int = 0,
                            var upgradeCost: Int = 0,
                            var upgradeDescription: String = "",

                            private val zeroLevelValueResource: Int,
                            private val firstLevelValueResource: Int,
                            private val secondLevelValueResource: Int,
                            private val thirdLevelValueResource: Int,

                            private val zeroLevelUpgradeCostResource: Int,
                            private val firstLevelUpgradeCostResource: Int,
                            private val secondLevelUpgradeCostResource: Int,

                            private val zeroLevelUpgradeDescriptionResource: Int,
                            private val firstLevelUpgradeDescriptionResource: Int,
                            private val secondLevelUpgradeDescriptionResource: Int) {

    abstract val title: String

    fun upgrade() {
        if (currentLevel < maxLevel) {
            currentLevel++
            synchronize()
        }
    }

    open fun synchronize() {
        value = getValueWithLevel(currentLevel)
        upgradeCost = getUpgradeCostWithLevel(currentLevel)
        upgradeDescription = getDescriptionWithLevel(currentLevel)
    }

    private fun getValueWithLevel(level: Int): Int {
        return when (level) {
            0 -> context.resources.getInteger(zeroLevelValueResource)
            1 -> context.resources.getInteger(firstLevelValueResource)
            2 -> context.resources.getInteger(secondLevelValueResource)
            3 -> context.resources.getInteger(thirdLevelValueResource)

            else -> 0
        }
    }

    private fun getUpgradeCostWithLevel(level: Int): Int {
        return when (level) {
            0 -> context.resources.getInteger(zeroLevelUpgradeCostResource)
            1 -> context.resources.getInteger(firstLevelUpgradeCostResource)
            2 -> context.resources.getInteger(secondLevelUpgradeCostResource)

            else -> 0
        }
    }

    private fun getDescriptionWithLevel(level: Int): String {
        return when (level) {
            0 -> context.resources.getString(zeroLevelUpgradeDescriptionResource)
            1 -> context.resources.getString(firstLevelUpgradeDescriptionResource)
            2 -> context.resources.getString(secondLevelUpgradeDescriptionResource)

            else -> ""
        }
    }
}

abstract class VirusComponent {
    abstract fun synchronize()
}

class VirusManager(val context: AbstractLevelActivity,
                   val propagation: PropagationComponent = PropagationComponent(context),
                   val abilities: AbilityComponent = AbilityComponent(context),
                   val resistance: ResistanceComponent = ResistanceComponent(context),
                   val devices: DeviceComponent = DeviceComponent(context)) {
    fun synchronize() {
        propagation.synchronize()
        abilities.synchronize()
        resistance.synchronize()
        devices.synchronize()
    }
}