package ca.sfu.cmpt362.ayusharora.myruns.userprofile

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

//Code copied from lecture 2 demo (CameraDemoKotlin)
class UserProfileViewModel : ViewModel() {
    val userImage = MutableLiveData<Bitmap?>()
}