package ca.sfu.cmpt362.ayusharora.myruns.userprofile

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

//Code adapted from lecture 2 demo (CameraDemoKotlin)
class UserProfileViewModel : ViewModel() {
    val userImageUri = MutableLiveData<Uri?>()
}