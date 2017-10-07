package com.dmitrykazanbaev.virus_game.model.virus

import com.dmitrykazanbaev.virus_game.AbstractLevelActivity
import com.dmitrykazanbaev.virus_game.R


class WiFiModification(context: AbstractLevelActivity) : Modification(
        context = context,

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

    override val title: String = context.resources.getString(R.string.wifi_title)
}

class BluetoothModification(context: AbstractLevelActivity) : Modification(
        context = context,

        zeroLevelValueResource = R.dimen.bluetooth_zero_level_value,
        firstLevelValueResource = R.dimen.bluetooth_first_level_value,
        secondLevelValueResource = R.dimen.bluetooth_second_level_value,
        thirdLevelValueResource = R.dimen.bluetooth_third_level_value,

        zeroLevelUpgradeCostResource = R.dimen.bluetooth_zero_level_upgrade_cost,
        firstLevelUpgradeCostResource = R.dimen.bluetooth_first_level_upgrade_cost,
        secondLevelUpgradeCostResource = R.dimen.bluetooth_second_level_upgrade_cost,

        zeroLevelUpgradeDescriptionResource = R.string.bluetooth_zero_level_upgrade_description,
        firstLevelUpgradeDescriptionResource = R.string.bluetooth_first_level_upgrade_description,
        secondLevelUpgradeDescriptionResource = R.string.bluetooth_second_level_upgrade_description) {

    override val title: String = context.resources.getString(R.string.bluetooth_title)
}

class EthernetModification(context: AbstractLevelActivity) : Modification(
        context = context,

        zeroLevelValueResource = R.dimen.ethernet_zero_level_value,
        firstLevelValueResource = R.dimen.ethernet_first_level_value,
        secondLevelValueResource = R.dimen.ethernet_second_level_value,
        thirdLevelValueResource = R.dimen.ethernet_third_level_value,

        zeroLevelUpgradeCostResource = R.dimen.ethernet_zero_level_upgrade_cost,
        firstLevelUpgradeCostResource = R.dimen.ethernet_first_level_upgrade_cost,
        secondLevelUpgradeCostResource = R.dimen.ethernet_second_level_upgrade_cost,

        zeroLevelUpgradeDescriptionResource = R.string.ethernet_zero_level_upgrade_description,
        firstLevelUpgradeDescriptionResource = R.string.ethernet_first_level_upgrade_description,
        secondLevelUpgradeDescriptionResource = R.string.ethernet_second_level_upgrade_description) {

    override val title: String = context.resources.getString(R.string.ethernet_title)
}

class MobileModification(context: AbstractLevelActivity) : Modification(
        context = context,

        zeroLevelValueResource = R.dimen.mobile_zero_level_value,
        firstLevelValueResource = R.dimen.mobile_first_level_value,
        secondLevelValueResource = R.dimen.mobile_second_level_value,
        thirdLevelValueResource = R.dimen.mobile_third_level_value,

        zeroLevelUpgradeCostResource = R.dimen.mobile_zero_level_upgrade_cost,
        firstLevelUpgradeCostResource = R.dimen.mobile_first_level_upgrade_cost,
        secondLevelUpgradeCostResource = R.dimen.mobile_second_level_upgrade_cost,

        zeroLevelUpgradeDescriptionResource = R.string.mobile_zero_level_upgrade_description,
        firstLevelUpgradeDescriptionResource = R.string.mobile_first_level_upgrade_description,
        secondLevelUpgradeDescriptionResource = R.string.mobile_second_level_upgrade_description) {

    override val title: String = context.resources.getString(R.string.mobile_title)
}

class PropagationComponent(val context: AbstractLevelActivity,
                           val wifi: Modification = WiFiModification(context),
                           val bluetooth: Modification = BluetoothModification(context),
                           val ethernet: Modification = EthernetModification(context),
                           val mobile: Modification = MobileModification(context)) : VirusComponent() {
    override fun synchronize() {
        wifi.synchronize()
        bluetooth.synchronize()
        ethernet.synchronize()
        mobile.synchronize()
    }
}
