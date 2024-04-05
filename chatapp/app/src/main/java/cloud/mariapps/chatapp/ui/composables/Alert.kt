package cloud.mariapps.chatapp.ui.composables

import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.DialogProperties
import cloud.mariapps.chatapp.R
import cloud.mariapps.chatapp.utils.Global.getString

@Composable
fun Alert(
    dismissOnBackPress: Boolean,
    dismissOnClickOutside: Boolean,
    showAlert: Boolean,
    alertTitle: AlertTitle,
    message: String,
    posBtnText: String,
    negBtnText: String?,
    posBtnListener: () -> Unit,
    negBtnListener: () -> Unit,
    onDismissRequest: () -> Unit
) {
    if (!showAlert) {
        return
    }
    AlertDialog(onDismissRequest = {}, properties = DialogProperties(
        dismissOnBackPress = dismissOnBackPress, dismissOnClickOutside = dismissOnClickOutside
    ), title = {
        Text(text = alertTitle.value)
    }, text = {
        Text(text = message)
    }, confirmButton = {
        TextButton(onClick = posBtnListener) {
            Text(text = posBtnText)
        }
    }, dismissButton = {
        if (negBtnText != null) {
            TextButton(onClick = negBtnListener) {
                Text(text = negBtnText)
            }
        }
    })
    /*Dialog(
        onDismissRequest = onDismissRequest, properties = DialogProperties(
            dismissOnBackPress = dismissOnBackPress, dismissOnClickOutside = dismissOnClickOutside
        )
    ) {
        Column(
            modifier = Modifier
                .background(
                    color = Color.White, shape = RoundedCornerShape(10.dp)
                )
                .width(250.dp)
                .height(100.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(15.dp))
            Text(text = message)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (negBtnText != null) {
                    TextButton(onClick = negBtnListener) {
                        Text(text = negBtnText)
                    }
                }
                TextButton(onClick = posBtnListener) {
                    Text(text = posBtnText)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }*/
}

enum class AlertTitle(val value: String) {
    ALERT(getString(R.string.alert)), ERROR(getString(R.string.error)), CONFIRM(getString(R.string.confirm))
}

data class AlertItem(
    var alertTitle: AlertTitle = AlertTitle.ALERT,
    var dismissOnBackPress: Boolean = false,
    var dismissOnClickOutside: Boolean = false,
    var message: String,
    var posBtnText: String,
    var negBtnText: String? = null,
    var posBtnListener: () -> Unit = {},
    var negBtnListener: (() -> Unit)? = null,
)