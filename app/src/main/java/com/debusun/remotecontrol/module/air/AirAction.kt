//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//
package com.debusun.remotecontrol.module.air

import android.os.Parcel
import android.os.Parcelable

enum class AirConditionState(var state: Int) : Parcelable {
    AC_BLOWER(1), HVAC_VD_MODE(2), AC_LEFT_TEMP(3), AC_RIGHT_TEMP(4), AC_SWITCH(5), AC_MAX_SWITCH(6), AC_AUTO(
        7
    ),
    AC_POWER_SWITCH(8), AC_FRONT_DEFROST_SWITCH(9), AC_REAR_DEFROST_SWITCH(10), AC_RECIRC_AIR(11), AC_DUAL(
        12
    ),
    AC_FLOW_MODE(13), AC_TEMP_DEM(14), AC_HEAT_SWITCH(15), AC_TEMP_DEM_ADD(16), AC_TEMP_DEM_DEC(17), AC_BLOWER_DEM_ADD(
        18
    ),
    AC_BLOWER_DEM_DEC(19), AC_ION_SWITCH(23), AC_PM_SWITCH(24), AC_AQS_SWITCH(25), AC_TEMP_OUTCAR(20), AC_PM_LEVEL(
        21
    ),
    AC_RUNNING_STATE(22),
    @Deprecated("")
    AC_COMBINATION_FUNCTION_1(23),
    @Deprecated("")
    AC_COMBINATION_FUNCTION_2(24),
    @Deprecated("")
    AC_COMBINATION_FUNCTION_3(25),
    @Deprecated("")
    AC_COMBINATION_FUNCTION_4(26),
    @Deprecated("")
    AC_COMBINATION_FUNCTION_5(27),
    @Deprecated("")
    AC_COMBINATION_FUNCTION_6(28),
    @Deprecated("")
    AC_COMBINATION_FUNCTION_7(29),
    @Deprecated("")
    AC_COMBINATION_FUNCTION_8(30), AC_RAPID_COOLING_MODE(31), AC_ONE_BUTTON_WARMTH_MODE(32), AC_HAZE_MODE(
        33
    ),
    AC_BABY_CARE_MODE(34), AC_AIR_CLEANER(35), AC_RAIN_SNOW_MODE(36), AC_SMOKING_MODE(37), AC_STOP_CAR_MODE(
        38
    ),
    AC_REAR_BLOWER_SWITCH(39), AC_SCENE_MODE(40), AC_PTC_SWITCH(41);

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(ordinal)
        dest.writeInt(state)
    }

    companion object {
        const val SWITCH_OFF = 0
        const val SWITCH_ON = 1
        const val NO_REQ = 0
        const val SCENE_MODE_ENTER = 1
        const val SCENE_MODE_EXIT = 2
        const val FLOW_MODE_NO = 0
        const val FLOW_MODE_FACE = 1
        const val FLOW_MODE_LEG = 2
        const val FLOW_MODE_FACE_LEG = 3
        const val FLOW_MODE_LEG_DEF = 4
        const val FLOW_MODE_DEF = 5
        const val FLOW_MODE_DEF_CLOSE = 6
        const val LEVEL_NO = 0
        const val LEVEL_1 = 1
        const val LEVEL_2 = 2
        const val LEVEL_3 = 3
        const val LEVEL_4 = 4
        const val LEVEL_5 = 5
        const val LEVEL_6 = 6
        const val LEVEL_7 = 7
        const val LEVEL_8 = 8
        const val AUTO_BLOWING_IN = 0
        const val FRONT_GLASS_BLOWING_IN = 1
        const val TO_DOWN_BLOWING_IN = 2
        const val PARALLEL_BLOWING_IN = 4
        const val TO_DOWN_AND_PARALLEL_BLOWING_IN = 6
        const val TO_UP_BLOWING_IN = 8
        const val TO_UP_AND_TO_DOWN_BLOWING_IN = 10
        const val PARALLEL_AND_TO_UP_BLOWING_IN = 12
        const val TO_UP_AND_PARALLEL_AND_TO_DOWN_BLOWING_IN = 14
        const val DEFORST_AND_TO_DOWN_BLOWING_IN = 18
        const val DEFAULT_LOOP = 0
        const val INTERNAL_LOOP = 1
        const val EXTERNAL_LOOP = 2
        const val INVAILD_LOOP = 3
        @JvmField
        val CREATOR: Parcelable.Creator<AirConditionState?> =
            object : Parcelable.Creator<AirConditionState?> {
                override fun createFromParcel(source: Parcel): AirConditionState? {
                    val state = values()[source.readInt()]
                    state.state = source.readInt()
                    return state
                }

                override fun newArray(size: Int): Array<AirConditionState?> {
                    return arrayOfNulls(size)
                }
            }
    }
}