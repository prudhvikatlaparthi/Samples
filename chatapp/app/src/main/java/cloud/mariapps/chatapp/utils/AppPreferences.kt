package cloud.mariapps.chatapp.utils

import android.content.Context
import android.content.SharedPreferences
import cloud.mariapps.chatapp.appContext

object AppPreferences : SharedPreferences by appContext.getSharedPreferences(
    Constants.kPreferenceName, Context.MODE_PRIVATE
) {


}