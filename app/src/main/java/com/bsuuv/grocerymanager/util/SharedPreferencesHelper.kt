package com.bsuuv.grocerymanager.util

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.bsuuv.grocerymanager.data.db.entity.FoodItemEntity
import com.bsuuv.grocerymanager.ui.SettingsFragment
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * An utility class to handle all interactions with `SharedPreferences`.
 */
class SharedPreferencesHelper @Inject constructor(@ApplicationContext context: Context) {

    val sharedPreferences: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)
    private val gson: Gson

    companion object {
        const val GROCERY_DAYS_KEY = "grocerydays"
    }

    init {
        val builder = GsonBuilder()
        this.gson = builder.create()
    }

    /**
     * Returns a list of grocery days, as a `MutableSet` of lowercase strings, selected by the user
     * in [SettingsFragment].
     */
    fun getGroceryDays(): MutableSet<String> = sharedPreferences
        .getStringSet(GROCERY_DAYS_KEY, HashSet())!!

    fun saveList(foodItems: MutableList<FoodItemEntity>, key: String) {
        val json = gson.toJson(foodItems)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString(key, json)
        editor.apply()
    }

    fun getList(key: String): MutableList<FoodItemEntity> {
        val json = sharedPreferences.getString(key, "")
        return if (json == "") ArrayList() else gson.fromJson<MutableList<FoodItemEntity>>(json)
    }

    fun clearList(key: String) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.remove(key)
        editor.apply()
    }

    fun registerOnSharedPreferenceChangeListener(
        listener: SharedPreferences.OnSharedPreferenceChangeListener
    ) {
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }

    private inline fun <reified T> Gson.fromJson(json: String?) =
        fromJson<T>(json, object : TypeToken<T>() {}.type)
}
