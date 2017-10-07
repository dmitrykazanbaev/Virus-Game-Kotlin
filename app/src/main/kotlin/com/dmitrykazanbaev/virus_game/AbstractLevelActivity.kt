package com.dmitrykazanbaev.virus_game

import android.graphics.Canvas
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.RadioGroup
import android.widget.RelativeLayout
import com.dmitrykazanbaev.virus_game.custom_views.ModificationButton
import com.dmitrykazanbaev.virus_game.custom_views.TrapezeButton
import com.dmitrykazanbaev.virus_game.model.User
import com.dmitrykazanbaev.virus_game.model.dao.ServiceInformationDAO
import com.dmitrykazanbaev.virus_game.model.dao.UserDAO
import com.dmitrykazanbaev.virus_game.model.level.AbstractLevel
import com.dmitrykazanbaev.virus_game.model.level.FirstLevel
import com.dmitrykazanbaev.virus_game.model.virus.Modification
import com.dmitrykazanbaev.virus_game.screen.AbstractLevelView
import com.dmitrykazanbaev.virus_game.service.*
import io.realm.Realm
import kotlinx.android.synthetic.main.level_activity.*
import kotlinx.coroutines.experimental.*
import java.text.SimpleDateFormat
import java.util.*


abstract class AbstractLevelActivity : AppCompatActivity() {
    abstract val levelView: AbstractLevelView

    private val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.US)
    private val calendar = GregorianCalendar()

    private var tickJob: Job? = null
    private var drawJob: Job? = null

    abstract fun doSpecificLevelActions()

    val user by lazy { User(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.level_activity)

        user.balance = 10000
        user.virus.synchronize()

        Realm.init(this)

        levelView.holder.addCallback(levelView)

        mainframe.addView(levelView, 0)

        horizontal_scroll_view_background.setOnTouchListener { _, _ -> true }
        vertical_scroll_view_background.setOnTouchListener { _, _ -> true }

        characteristic_window.visibility = View.INVISIBLE

        radiogroup.setOnCheckedChangeListener { group, viewId ->
            buy_modification_window.visibility = View.INVISIBLE
            updateButtonColor(group)
            updateModificationButtonController(viewId)
        }

        date_button.text = "  ${dateFormat.format(calendar.time)}"

        if (!intent.getBooleanExtra("new_game", false))
            initFromRealm()
    }

    private fun startJobs() {
        initDrawJob()
        initTickJob()
    }

    private suspend fun stopJobs() {
        drawJob?.cancel()
        tickJob?.cancel()

        drawJob?.join()
        tickJob?.join()
    }

    private fun initDrawJob() {
        if (drawJob == null || drawJob?.isCompleted!!) {
            drawJob = launch(CommonPool) {
                var canvas: Canvas?
                while (isActive) {
                    canvas = levelView.holder.lockCanvas()

                    synchronized(levelView.holder) {
                        canvas?.let { levelView.drawLevel(it) }
                    }

                    canvas?.let { levelView.holder.unlockCanvasAndPost(it) }
                }
            }
        }
    }

    private fun initTickJob() {
        if (tickJob == null || tickJob?.isCompleted!!) {
            tickJob = launch(CommonPool) {
                while (isActive) {
                    checkWinOrLose()
                    tryToInfectPhone(levelView.level)
                    tryToInfectComputer(levelView.level)
                    tryToInfectSmartHome(levelView.level)
                    tryToDetectVirus(levelView.level)
                    tryToProgressAntivirus(levelView.level)
                    tryToCureDevices(levelView.level)
                    updateDate()
                    doSpecificLevelActions()
                    delay(500)
                }
            }
        }
    }

    private fun checkWinOrLose() {
        val firstLevel = levelView.level as FirstLevel

        if (firstLevel.infectedPhones == firstLevel.phones &&
                firstLevel.infectedComputers == firstLevel.computers &&
                firstLevel.infectedSmartHome == firstLevel.infectedSmartHome)

            runOnUiThread { statistics_button.text = "  WIN" }

        if (firstLevel.antivirusProgress == 100 && firstLevel.infectedPhones == 0 &&
                firstLevel.infectedComputers == 0 && firstLevel.infectedSmartHome == 0)

            runOnUiThread { statistics_button.text = "  LOSE" }

    }

    open fun tryToInfectPhone(level: AbstractLevel): Boolean {
        val maxPhonesCanBeInfected: Int = user.virus.devices.phone.value * (level.phones - level.curedPhones) / 100
        if (level.infectedPhones < maxPhonesCanBeInfected) {

            val random = Random().nextInt(100)
            if (random <= user.virus.propagation.mobile.value +
                    user.virus.propagation.bluetooth.value +
                    user.virus.propagation.wifi.value) {

                level.infectPhone()
                user.getProfit(level.profitPhone)
                runOnUiThread { virus_name.text = "${level.infectedPhones}" }
                return true
            }
        }
        return false
    }

    fun tryToInfectComputer(level: AbstractLevel) {
        val maxComputersCanBeInfected: Int = user.virus.devices.pc.value * (level.computers - level.curedComputers) / 100
        if (level.infectedComputers < maxComputersCanBeInfected) {

            val random = Random().nextInt(100)
            if (random <= user.virus.propagation.ethernet.value +
                    user.virus.propagation.bluetooth.value +
                    user.virus.propagation.wifi.value) {

                level.infectComputer()
                user.getProfit(level.profitComputer)
            }
        }
    }

    fun tryToInfectSmartHome(level: AbstractLevel) {
        val maxSmartHomeCanBeInfected: Int = user.virus.devices.smartHome.value * (level.smartHome - level.curedSmartHome) / 100
        if (level.infectedSmartHome < maxSmartHomeCanBeInfected) {

            val random = Random().nextInt(100)
            if (random <= user.virus.propagation.ethernet.value +
                    user.virus.propagation.mobile.value +
                    user.virus.propagation.wifi.value) {

                level.infectSmartHome()
                user.getProfit(level.profitSmartHome)
            }
        }
    }

    fun tryToDetectVirus(level: AbstractLevel) {
        if (level.detectedDevices < level.countDetectedDevicesForStartAntivirusDevelopment) {

            val allDevices = level.computers + level.smartHome + level.phones
            val infectedDevices = level.infectedComputers + level.infectedSmartHome + level.infectedPhones
            val resistance = user.virus.resistance.mask.value
            val detection = user.virus.abilities.detection()
            val random = Random().nextInt(allDevices * resistance)

            if (random < detection * infectedDevices * level.levelCoefficient) {
                level.detectedDevices++
                runOnUiThread { virus_button.text = "${level.detectedDevices}" }
            }
        }
    }

    fun tryToProgressAntivirus(level: AbstractLevel) {
        if (level.detectedDevices == level.countDetectedDevicesForStartAntivirusDevelopment &&
                level.antivirusProgress < 100) {

            val allDevices = level.computers + level.smartHome + level.phones
            val infectedDevices = level.infectedComputers + level.infectedSmartHome + level.infectedPhones
            val resistance = user.virus.resistance.mask.value
            val detection = user.virus.abilities.detection()
            val random = Random().nextInt(allDevices * resistance)

            if (random < detection * infectedDevices * level.levelCoefficient) {
                level.antivirusProgress++
                runOnUiThread { antivirus_button.text = "${level.antivirusProgress}%  " }
            }

            runOnUiThread { antivirus.visibility = View.VISIBLE }
        }
    }

    fun tryToCureDevices(level: AbstractLevel) {
        if (level.antivirusProgress == 100) {

            level.curePhone()
            level.cureComputer()
            level.cureSmartHome()
        }
    }

    private fun updateDate() {
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        runOnUiThread { date_button?.text = "  ${dateFormat.format(calendar.time)}" }
    }

    private fun initFromRealm() {
        levelView.initLevelFromRealm()
        initUserFromRealm()
        initServiceInfoFromRealm()
    }

    private fun initUserFromRealm() {
        val realm = Realm.getDefaultInstance()

        val userDAO = realm.where(UserDAO::class.java).findFirst()
        user.balance = userDAO.balance
        user.virus.propagation.wifi.currentLevel = userDAO.wifiLevel
        user.virus.propagation.bluetooth.currentLevel = userDAO.bluetoothLevel
        user.virus.propagation.ethernet.currentLevel = userDAO.ethernetLevel
        user.virus.propagation.mobile.currentLevel = userDAO.mobileLevel
        user.virus.abilities.thief.currentLevel = userDAO.thiefLevel
        user.virus.abilities.control.currentLevel = userDAO.controlLevel
        user.virus.abilities.spam.currentLevel = userDAO.spamLevel
        user.virus.resistance.invisible.currentLevel = userDAO.invisibleLevel
        user.virus.resistance.mask.currentLevel = userDAO.maskLevel
        user.virus.resistance.newVirus.currentLevel = userDAO.newVirusLevel
        user.virus.devices.phone.currentLevel = userDAO.phoneLevel
        user.virus.devices.pc.currentLevel = userDAO.pcLevel
        user.virus.devices.smartHome.currentLevel = userDAO.smartHomeLevel

        user.virus.synchronize()
    }

    private fun initServiceInfoFromRealm() {
        val realm = Realm.getDefaultInstance()

        val serviceInfo = realm.where(ServiceInformationDAO::class.java).findFirst()
        calendar.time = serviceInfo.date
    }

    private fun saveToRealm() {
        levelView.saveLevelToRealm()
        saveUserToRealm()
        saveServiceInfoToRealm()
    }

    private fun saveUserToRealm() {
        val realm = Realm.getDefaultInstance()

        realm.executeTransaction {
            it.where(UserDAO::class.java).findAll().deleteAllFromRealm()

            val userDAO = UserDAO(user.balance,
                    user.virus.propagation.wifi.currentLevel,
                    user.virus.propagation.bluetooth.currentLevel,
                    user.virus.propagation.ethernet.currentLevel,
                    user.virus.propagation.mobile.currentLevel,
                    user.virus.abilities.thief.currentLevel,
                    user.virus.abilities.control.currentLevel,
                    user.virus.abilities.spam.currentLevel,
                    user.virus.resistance.invisible.currentLevel,
                    user.virus.resistance.mask.currentLevel,
                    user.virus.resistance.newVirus.currentLevel,
                    user.virus.devices.phone.currentLevel,
                    user.virus.devices.pc.currentLevel,
                    user.virus.devices.smartHome.currentLevel)

            it.copyToRealm(userDAO)
        }
    }

    private fun saveServiceInfoToRealm() {
        val realm = Realm.getDefaultInstance()

        realm.executeTransaction {
            it.where(ServiceInformationDAO::class.java).findAll().deleteAllFromRealm()

            val serviceInfo = ServiceInformationDAO(calendar.time)

            it.copyToRealm(serviceInfo)
        }
    }

    private fun updateButtonColor(group: RadioGroup) {
        (0 until group.childCount).
                map { i -> group.getChildAt(i) }.
                forEach {
                    val button = it as TrapezeButton
                    if (button.isChecked) {
                        button.buttonPaint.color = button.selectedColor
                    } else button.buttonPaint.color = button.unselectedColor
                    button.invalidate()
                }
    }

    private fun updateModificationButtonController(viewId: Int) {
        modification_controller.removeAllViews()

        val modificationButtonController = ModificationButtonController(this, sourceViewId = viewId)
        modificationButtonController.layoutParams =
                RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT)
        modificationButtonController.setOnCheckedChangeListener { radioGroup, _ -> radioGroup.invalidate() }
        modification_controller.addView(modificationButtonController)
    }

    fun onTouch(view: View) {
        when (view.id) {
            R.id.virus_button -> {
                runBlocking {
                    stopJobs()
                }
                showCharacteristicWindow(this)
            }
            R.id.close_characteristics_button -> {
                startJobs()
                closeCharacteristicWindow(this)
            }
            R.id.devices_button, R.id.propagation_button,
            R.id.resistance_button, R.id.abilities_button -> if (!(view as TrapezeButton).isChecked) view.isChecked = true
            R.id.buy_button -> buyModification(this)
        }
    }

    fun upgradeAndInvalidate(modification: Modification,
                             modificationButton: ModificationButton,
                             modificationButtonController: ModificationButtonController) {
        if (user.balance >= modification.upgradeCost) {
            user.balance -= modification.upgradeCost
            modification.upgrade()
            updateModificationWindow(modification)
            modificationButton.modificationLevel = modification.currentLevel
            modificationButtonController.invalidate()
        }
    }

    fun updateModificationWindow(modification: Modification) {
        if (modification.currentLevel != modification.maxLevel) {
            updateModificationTitle(modification.title)
            updateModificationDescription(modification.upgradeDescription)
            updateModificationUpgradeCost(modification.upgradeCost)

            buy_modification_window.visibility = View.VISIBLE
        } else buy_modification_window.visibility = View.INVISIBLE
    }

    override fun onBackPressed() {
        if (characteristic_window.visibility == View.VISIBLE)
            closeCharacteristicWindow(this)
    }

    override fun onResume() {
        startJobs()
        super.onResume()
    }

    override fun onPause() {
        runBlocking { stopJobs() }
        super.onPause()
    }

    override fun onStop() {
        saveToRealm()
        super.onStop()
    }

    fun updateBalance(newBalance: Int) {
        runOnUiThread {
            balance_button.text = "  $newBalance$"
            characteristic_window_balance.text = "$newBalance$"
        }
    }

    fun updateModificationTitle(title: String) {
        modification_name.text = title
    }

    fun updateModificationDescription(description: String) {
        modification_description.text = description
    }

    fun updateModificationUpgradeCost(cost: Int) {
        modification_cost.text = "$cost$"
    }
}