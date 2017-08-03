package com.dmitrykazanbaev.virus_game.model.virus

class Virus(val propagation: PropagationComponent = PropagationComponent(),
            val abilities: AbilityComponent = AbilityComponent(),
            val resistance: ResistanceComponent = ResistanceComponent(),
            val devices: DeviceComponent = DeviceComponent())

class PropagationComponent(val wifi: Modification = Modification(),
                           val bluetooth: Modification = Modification(),
                           val ethernet: Modification = Modification(),
                           val mobile: Modification = Modification())

class AbilityComponent()

class ResistanceComponent(val antivirusDetecting: Modification = Modification(),
                          val masking: Modification = Modification(),
                          val partKernel: Modification = Modification())

class DeviceComponent(val smartphones: Modification = Modification(),
                      val computers: Modification = Modification(),
                      val smartHouses: Modification = Modification())

class Modification(val maxLevel: Int = 3,
                   var currentlevel: Int = 0,
                   var upgradeCost: Int = 0,
                   var imageName: String = "",
                   var title: String = "",
                   var description: String = "")