package com.dmitrykazanbaev.virus_game.model.virus

import com.dmitrykazanbaev.virus_game.service.ApplicationContextHolder

abstract class Modification(val maxLevel: Int = 3,
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
            0 -> ApplicationContextHolder.context.resources.getInteger(zeroLevelValueResource)
            1 -> ApplicationContextHolder.context.resources.getInteger(firstLevelValueResource)
            2 -> ApplicationContextHolder.context.resources.getInteger(secondLevelValueResource)
            3 -> ApplicationContextHolder.context.resources.getInteger(thirdLevelValueResource)

            else -> 0
        }
    }

    private fun getUpgradeCostWithLevel(level: Int): Int {
        return when (level) {
            0 -> ApplicationContextHolder.context.resources.getInteger(zeroLevelUpgradeCostResource)
            1 -> ApplicationContextHolder.context.resources.getInteger(firstLevelUpgradeCostResource)
            2 -> ApplicationContextHolder.context.resources.getInteger(secondLevelUpgradeCostResource)

            else -> 0
        }
    }

    private fun getDescriptionWithLevel(level: Int): String {
        return when (level) {
            0 -> ApplicationContextHolder.context.resources.getString(zeroLevelUpgradeDescriptionResource)
            1 -> ApplicationContextHolder.context.resources.getString(firstLevelUpgradeDescriptionResource)
            2 -> ApplicationContextHolder.context.resources.getString(secondLevelUpgradeDescriptionResource)

            else -> ""
        }
    }
}

abstract class VirusComponent {
    abstract fun synchronize()
}

class VirusManager(val propagation: PropagationComponent = PropagationComponent(),
                   val abilities: AbilityComponent = AbilityComponent(),
                   val resistance: ResistanceComponent = ResistanceComponent(),
                   val devices: DeviceComponent = DeviceComponent()) {
    fun synchronize() {
        propagation.synchronize()
        abilities.synchronize()
        resistance.synchronize()
        devices.synchronize()
    }
}