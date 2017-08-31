package com.dmitrykazanbaev.virus_game.model.dao

import io.realm.RealmList
import io.realm.RealmObject
import java.util.*

interface AbstractLevelDAO

open class FirstLevelDAO : AbstractLevelDAO, RealmObject() {
    var buildingList = RealmList<BuildingDAO>()
}

open class BuildingDAO(var infectedComputers: Int = 0) : RealmObject()

open class ServiceInformationDAO(var date: Date = Date()) : RealmObject()

open class UserDAO(var balance: Int = 0,
                   var wifiLevel: Int = 0,
                   var bluetoothLevel: Int = 0,
                   var ethernetLevel: Int = 0,
                   var statsNetLevel: Int = 0,
                   var theifLevel: Int = 0,
                   var controlLevel: Int = 0,
                   var spamLevel: Int = 0,
                   var maskLevel: Int = 0,
                   var invinsibleLevel: Int = 0,
                   var newVirusLevel: Int = 0,
                   var phoneLevel: Int = 0,
                   var pcLevel: Int = 0,
                   var smartHomeLevel: Int = 0) : RealmObject()
