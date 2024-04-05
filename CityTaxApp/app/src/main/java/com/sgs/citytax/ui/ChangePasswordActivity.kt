package com.sgs.citytax.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.Agent
import com.sgs.citytax.api.payload.StoreAgentDetails
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ActivityChangePasswordBinding
import com.sgs.citytax.util.*

class ChangePasswordActivity : BaseActivity() {
    private var isFromProfile: Boolean = false
    lateinit var binding: ActivityChangePasswordBinding
    var agent: Agent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_password)
        binding.edtNewPassword.transformationMethod=AsteriskPasswordTransformationMethod()
        binding.edtConfirmPassword.transformationMethod=AsteriskPasswordTransformationMethod()
        showToolbarBackButton(R.string.title_change_password)
        processIntent()
        setListeners()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    private fun processIntent() {
        if (intent.getParcelableExtra<Agent>(Constant.KEY_AGENT) != null) {
            agent = intent.getParcelableExtra(Constant.KEY_AGENT)
        }
        if (intent.hasExtra(Constant.KEY_FROM_PROFILE) && intent.getBooleanExtra(Constant.KEY_FROM_PROFILE,false)){
            isFromProfile = true
        }
    }

    private fun setListeners() {
        binding.btnSave.setOnClickListener {
            hideKeyBoard()
            if (validateView()) {
                if (!TextUtils.isEmpty(binding.edtCurrentPassword.text.toString().trim())) {
                    agent?.password = doEncrypt(binding.edtCurrentPassword.text.toString().trim(), prefHelper.getStaticToken())
                }

                if (!TextUtils.isEmpty(binding.edtNewPassword.text.toString().trim())) {
                    agent?.newPassword = doEncrypt(binding.edtNewPassword.text.toString().trim(), prefHelper.getStaticToken())
                }

                val storeAgentDetails = StoreAgentDetails()
                storeAgentDetails.agent = agent!!

                showProgressDialog(R.string.msg_please_wait)
                APICall.storeAgentDetails(storeAgentDetails, object : ConnectionCallBack<Boolean> {
                    override fun onSuccess(response: Boolean) {
                        hideKeyBoard()
                        dismissDialog()
                        if (storeAgentDetails.agent.agentid != 0)
                            showToast(R.string.password_update)
                        else
                            showToast(R.string.password_success)
                        /*val intent = Intent()
                        intent.putExtra("Password", doDecrypt(agent?.newPassword!!, prefHelper.getStaticToken()))
                        setResult(Activity.RESULT_OK, intent)*/
                        if (!isFromProfile) {
                            startActivity(
                                Intent(
                                    this@ChangePasswordActivity,
                                    LoginActivity::class.java
                                )
                            )
                            finishAffinity()
                        }
                    }

                    override fun onFailure(message: String) {
                        dismissDialog()
                        showAlertDialog(message)
                    }
                })
            }
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }

        binding.newPasswordEyeBtn.setOnClickListener {
            if(binding.newPasswordEyeBtn.tag.toString()=="show"){
                binding.edtNewPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                binding.edtNewPassword.setSelection(binding.edtNewPassword.length())
                binding.newPasswordEyeBtn.setImageResource(R.drawable.eye_hide)
                binding.newPasswordEyeBtn.tag="hide"
            }else{
                binding.edtNewPassword.transformationMethod = AsteriskPasswordTransformationMethod()
                binding.edtNewPassword.setSelection(binding.edtNewPassword.length())
                binding.newPasswordEyeBtn.setImageResource(R.drawable.eye_show)
                binding.newPasswordEyeBtn.tag="show"
            }
        }

        binding.confirmPasswordEyeBtn.setOnClickListener {
            if(binding.confirmPasswordEyeBtn.tag.toString()=="show"){
                binding.edtConfirmPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                binding.edtConfirmPassword.setSelection(binding.edtNewPassword.length())
                binding.confirmPasswordEyeBtn.setImageResource(R.drawable.eye_hide)
                binding.confirmPasswordEyeBtn.tag="hide"
            }else{
                binding.edtConfirmPassword.transformationMethod = AsteriskPasswordTransformationMethod()
                binding.edtConfirmPassword.setSelection(binding.edtNewPassword.length())
                binding.confirmPasswordEyeBtn.setImageResource(R.drawable.eye_show)
                binding.confirmPasswordEyeBtn.tag="show"
            }
        }
    }



    fun validateView(): Boolean {

        if (binding.edtCurrentPassword.text.toString().isEmpty()) {
            showSnackbarMsg(R.string.msg_enter_current_password)
            return false
        }

        if (binding.edtCurrentPassword.text != null && !TextUtils.isEmpty(binding.edtCurrentPassword.text.toString()) && doDecrypt(agent?.password!!, prefHelper.getStaticToken()) != binding.edtCurrentPassword.text.toString().trim()) {
            showSnackbarMsg(R.string.msg_invalid_password)
            return false
        }

        if (binding.edtNewPassword.text != null && TextUtils.isEmpty(binding.edtNewPassword.text.toString())) {
            showSnackbarMsg(R.string.msg_password_empty)
            binding.edtNewPassword.requestFocus()
            return false
        }
        if (binding.edtConfirmPassword.text.toString() != binding.edtNewPassword.text.toString()) {
            showSnackbarMsg(R.string.msg_password_not_match)
            binding.edtConfirmPassword.requestFocus()
            return false
        }

        if (binding.edtCurrentPassword.text.toString().trim().equals(binding.edtNewPassword.text.toString().trim())) {
            showSnackbarMsg(R.string.msg_password_same)
            return false
        }

        if (!isStrongPassword(binding.edtNewPassword.text.toString().trim())) {
            showAlertDialog(getString(R.string.msg_strong_password))
            return false
        }

        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    private fun isStrongPassword(value: String): Boolean {

        var regex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[@._$#^`]).{8,24}$");
        var regex2 = Regex("[\\s]");
        if (!regex.containsMatchIn(value) || regex2.containsMatchIn(value)) {
            return false
        }

        return true

/*        var regix = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[@._$#^`])(^.{8,24}\$)")
        return regix.containsMatchIn(value)*/
    }


}