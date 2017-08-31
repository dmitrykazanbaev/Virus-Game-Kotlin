package com.dmitrykazanbaev.virus_game.model.virus

import com.dmitrykazanbaev.virus_game.R
import com.dmitrykazanbaev.virus_game.service.ApplicationContextHolder


class WiFiModification : Modification(
        zeroLevelValueResource = R.dimen.wifi_zero_level_value,
        firstLevelValueResource = R.dimen.wifi_first_level_value,
        secondLevelValueResource = R.dimen.wifi_second_level_value,
        thirdLevelValueResource = R.dimen.wifi_third_level_value,

        zeroLevelUpgradeCostResource = R.dimen.wifi_zero_level_upgrade_cost,
        firstLevelUpgradeCostResource = R.dimen.wifi_first_level_upgrade_cost,
        secondLevelUpgradeCostResource = R.dimen.wifi_second_level_upgrade_cost,

        zeroLevelUpgradeDescriptionResource = R.string.wifi_zero_level_upgrade_description,
        firstLevelUpgradeDescriptionResource = R.string.wifi_first_level_upgrade_description,
        secondLevelUpgradeDescriptionResource = R.string.wifi_second_level_upgrade_description) {

    override val title: String = ApplicationContextHolder.context.resources.getString(R.string.wifi_title)
}

class PropagationComponent(val wifi: Modification = WiFiModification()
        /*val bluetooth: Modification = Modification(),
        val ethernet: Modification = Modification(),
        val mobile: Modification = Modification()*/) : VirusComponent() {
    override fun synchronize() {
        wifi.synchronize()
    }
}
