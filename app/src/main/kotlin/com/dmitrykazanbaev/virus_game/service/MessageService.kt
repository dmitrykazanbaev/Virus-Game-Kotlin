package com.dmitrykazanbaev.virus_game.service

import android.content.Context
import com.dmitrykazanbaev.virus_game.R
import java.util.*


fun getNormalMessage(context: Context): String {
    return getMessage(R.array.msgs_without_virus, context)
}

fun getStartVirusMessage(context: Context): String {
    return getMessage(R.array.msgs_start_virus, context)
}

fun getTheftBankMessage(context: Context): String {
    return getMessage(R.array.msgs_theft_bank, context)
}

fun getAppAccessMessage(context: Context): String {
    return getMessage(R.array.msgs_app_access, context)
}

fun getSMSMessage(context: Context): String {
    return getMessage(R.array.msgs_sms, context)
}

fun getMoneyTransferMessage(context: Context): String {
    return getMessage(R.array.msgs_money_transfer, context)
}

fun getBlockAdMessage(context: Context): String {
    return getMessage(R.array.msgs_block_ad, context)
}

fun getBannerMessage(context: Context): String {
    return getMessage(R.array.msgs_banner, context)
}

fun getPornoBannerMessage(context: Context): String {
    return getMessage(R.array.msgs_porno_banner, context)
}

private fun getMessage(resource: Int, context: Context): String {
    with(context) {
        val messages = resources.getStringArray(resource)
        return messages[Random().nextInt(messages.size)]
    }
}