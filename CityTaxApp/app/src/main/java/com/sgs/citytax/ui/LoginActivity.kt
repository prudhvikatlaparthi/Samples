package com.sgs.citytax.ui

import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.method.HideReturnsTransformationMethod
import android.view.View
import android.widget.AdapterView
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.work.*
import com.bumptech.glide.Glide
import com.google.android.gms.maps.model.LatLng
import com.sgs.citytax.BuildConfig
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.api.payload.Agent
import com.sgs.citytax.api.payload.LoginPayload
import com.sgs.citytax.api.response.UMXUserOrganizations
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.ActivityLoginBinding
import com.sgs.citytax.ui.fragments.PrivacyPolicyDialogFragment
import com.sgs.citytax.util.*
import com.sgs.citytax.workers.LogUploadWorker
import java.util.*


class LoginActivity : BaseActivity(), PrivacyPolicyDialogFragment.Listener {
    private lateinit var binding: ActivityLoginBinding
    lateinit var mainHandler: Handler
    private var locationHelper: LocationHelper? = null
    private var latLng: LatLng? = null
    private val spinnerKeys = arrayListOf("EN", "FR")
    private var municipalLogoPath : String? = null
    var userName = ""
    var password = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        hideToolbar()
        callLogo()
        bindData()
        setListeners()
        isSerialNoExists()
    }

    private fun callLogo() {
        binding.logoImgView.visibility = View.GONE
        binding.icSgsFbbLogo.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
        val context = SecurityContext()
        context.domain = BuildConfig.DOMAIN
        //context.userOrgBranchID = 11
        //context.userOrgID = 100
        context.userOrgBranchID = 2
        context.userOrgID = 101
        val logingPalyod = LoginPayload()
        logingPalyod.context = context

        APICall.getOrganizationLogo(logingPalyod, object : ConnectionCallBack<UMXUserOrganizations> {
            override fun onSuccess(response: UMXUserOrganizations) {
                if (response != null && response.list != null) {
                    if (response.list.size > 0) {
                        val listData = response.list.get(0)
                        val logoPath = listData.androidLogoAWSPath
                        municipalLogoPath = listData.municipalLogoAWSPath
                        logoPath?.let { getBitmapFromURL(it) }
                    }
                }
            }

            override fun onFailure(message: String) {
                showAlertDialog(message)
            }
        })
    }

    private fun isSerialNoExists(): Boolean {
        return if (prefHelper.serialNumber.isEmpty()) {
            prefHelper.serialNumber = getSerialNumber()
            false
        } else true
    }

    override fun onResume() {
        super.onResume()
        getLocation()
    }

    private fun getLocation() {
        showProgressDialog(R.string.msg_location_fetching)
        locationHelper = LocationHelper(this, binding.edtUsername, this)

        locationHelper?.fetchLocation()
        locationHelper?.setListener(object : LocationHelper.Location {
            override fun found(latitude: Double, longitude: Double) {
                dismissDialog()
                latLng = LatLng(latitude, longitude)
            }

            override fun start() {
            }
        })
    }

    fun doLogin(view: View) {
        try {

            if (!isSerialNoExists()) {
                return
            }

            hideKeyBoard()
            if (validateCredentials()) {

                if (binding.edtUsername.text != null) {
                    userName = binding.edtUsername.text.toString().trim()
                }
                if (binding.edtPassword.text != null) {
                    password = binding.edtPassword.text.toString().trim()
                }

                showProgressDialog(R.string.msg_authenticating)

                APICall.authenticateUser(userName, password, latLng, object : ConnectionCallBack<Boolean> {
                    override fun onSuccess(response: Boolean) {

                        //region Periodic Sync
                        mainHandler = Handler(Looper.getMainLooper())
                        mainHandler.post(updateLocation)
                        //endregion

                        if (!prefHelper.isFirstAPICallDone) {
                            prefHelper.isSearchEnabled = true
                            prefHelper.isScanEnabled = true
                            prefHelper.isCashEnabled = true
                            prefHelper.isOrangeWalletPaymentEnabled = true
                            prefHelper.printEnabled = true
                            prefHelper.qrCodeEnabled = true
                            prefHelper.showTaxNotice = true
                            callConnectedDevice()
                        } else {
                            dismissDialog()
                            //navigateNext()
                            acceptPrivacyPolicy()
                        }
                    }

                    override fun onFailure(message: String) {
                        dismissDialog()
                        showAlertDialog(message)
                    }
                })
            }
        } catch (e: Exception) {
            dismissDialog()
            showAlertDialog(e.message)
        }
    }

    fun callConnectedDevice() {
        showProgressDialog(R.string.msg_register_device)
        APICall.updateConnectedDevice(latLng, object : ConnectionCallBack<Boolean> {
            override fun onSuccess(response: Boolean) {
                prefHelper.isFirstAPICallDone = true
                dismissDialog()
                //navigateNext()
                acceptPrivacyPolicy()
            }

            override fun onFailure(message: String) {
                dismissDialog()
                showAlertDialog(message)
            }
        })
    }

    /*fun acceptPrivacyPolicy() {
        showProgressDialog()
        APICall.acceptPrivacyPolicy(object : ConnectionCallBack<String?> {
            override fun onSuccess(response: String?) {
                dismissDialog()
                if (response != null && response.isNotEmpty()) {
                    showPrivacyStatement(response)
                } else {
                    if (prefHelper.loginCount == 1)
                        changePasswordAlert()
                    else
                        navigateNext()
                }

                //showPrivacyStatement("test")
            }

            override fun onFailure(message: String) {
                dismissDialog()
                showAlertDialog(message)
            }
        })
    }*/
    fun acceptPrivacyPolicy() {
        showProgressDialog()
        APICall.acceptPrivacyPolicy(object : ConnectionCallBack<String?> {
            override fun onSuccess(response: String?) {
                dismissDialog()
                if (prefHelper.agentTypeCode != Constant.AgentTypeCode.GCA.name && prefHelper.agentTypeCode != Constant.AgentTypeCode.LCA.name) {
                    if (response != null && response.isNotEmpty()) {
                        showPrivacyStatement(response)
                    } else {
                        if (prefHelper.loginCount == 1)
                            changePasswordAlert()
                        else
                            navigateNext()
                    }
                } else {
                    showAlertDialog(resources.getString(R.string.invalid_login))
                }


                //showPrivacyStatement("test")
            }

            override fun onFailure(message: String) {
                dismissDialog()
                showAlertDialog(message)
            }
        })
    }

    private fun changePasswordAlert() {
        showAlertDialog(getString(R.string.security_reasons), DialogInterface.OnClickListener { _, _ ->
            navigateToChangePasswordScreen()
        })
    }

    private fun navigateToChangePasswordScreen() {
        val intent = Intent(this, ChangePasswordActivity::class.java)
        intent.putExtra(Constant.KEY_AGENT, getAgent())
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        startActivity(intent)
        finish()
    }

    private fun getAgent(): Agent {
        val agent = Agent()
        agent.frstname = prefHelper.agentFName
        agent.mddlename = prefHelper.agentMName
        agent.lastname = prefHelper.agentLName
        agent.email = prefHelper.agentEmail
        agent.mobile = prefHelper.agentMobile
        agent.password = prefHelper.agentPassword
        agent.newPassword = ""
        agent.salutation = prefHelper.agentSalutation
        agent.agentid = prefHelper.agentID
        agent.agenttypeid = prefHelper.agentTypeID
        agent.ownrorgbrid = prefHelper.agentOwnerOrgBranchID
        agent.agentUserID = prefHelper.agentUserID
        return agent
    }


    private fun showPrivacyStatement(content: String?) {
        supportFragmentManager.let {
            PrivacyPolicyDialogFragment.newInstance(content
                    ?: "",userName).show(supportFragmentManager, PrivacyPolicyDialogFragment::class.java.simpleName)
        }
    }

    private val updateLocation = object : Runnable {
        override fun run() {
            getCurrentLocation()
            mainHandler.postDelayed(this, 5 * 60 * 1000)
        }
    }


    fun setListeners() {

        binding.eyeId.setOnClickListener {
            if(binding.eyeId.tag.toString()=="show"){
                binding.edtPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                binding.edtPassword.setSelection(binding.edtPassword.length())
                binding.eyeId.setImageResource(R.drawable.eye_hide)
                binding.eyeId.tag = "hide"
            }else{
                binding.edtPassword.transformationMethod = AsteriskPasswordTransformationMethod()
                binding.edtPassword.setSelection(binding.edtPassword.length())
                binding.eyeId.setImageResource(R.drawable.eye_show)
                binding.eyeId.tag = "show"
            }
        }


        binding.spnLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(view: AdapterView<*>?) {

            }

            override fun onItemSelected(view: AdapterView<*>?, p1: View?, position: Int, id: Long) {
                val spinnerValue = binding.spnLanguage.selectedItem.toString()
                if (spinnerValue == getString(R.string.english)) {
                    MyApplication.updateLanguages(this@LoginActivity, "EN")
                    updateValues()
                } else {
                    MyApplication.updateLanguages(this@LoginActivity, "FR")
                    updateValues()
                }
            }

        }
    }

    fun navigateNext() {
        /*startActivity(Intent(this, DashboardActivity::class.java))
        finish()*/
       // prefHelper.authUniqueKey=""
//        val intent=Intent(this, AuthenticationActivity::class.java)
        if (MyApplication.getPrefHelper().logUploadTime.isEmpty()) {
            MyApplication.getPrefHelper().logUploadTime = formatDateTimeInMillisecond(Date())
        }
        val lastLogUploadTime =
            getDateTimeInMillisecond(MyApplication.getPrefHelper().logUploadTime)
        lastLogUploadTime?.let {
            if (isDayDifference(it, 1) && LogHelper.isLogFileExist()) {
                val constraints =
                    Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
                val oneTimeWorkRequest =
                    OneTimeWorkRequest.Builder(LogUploadWorker::class.java).setConstraints(
                        constraints
                    ).addTag("LogUploadWorker").build()
                WorkManager.getInstance(applicationContext).enqueue(oneTimeWorkRequest)
            }
        }
        navigateLoginAuthActivity()
    }

    private fun navigateLoginAuthActivity() {
        val intent = Intent(this, LoginAuthActivity::class.java)
        val username = binding.edtUsername.text.toString().trim()
        val bundle = Bundle()
        bundle.putString(Constant.USERNAME, username)
        intent.putExtras(bundle)
        startActivity(intent)
        finish()
    }

    fun bindData() {
//        binding.edtUsername.setText("7111")
//        binding.edtPassword.setText("WELcome@1")
        binding.edtPassword.transformationMethod=AsteriskPasswordTransformationMethod()
        for ((index, obj) in spinnerKeys.withIndex()) {
            if (prefHelper.language.isNotEmpty()) {
                if (prefHelper.language == obj) {
                    binding.spnLanguage.setSelection(index)
                    break
                }
            } else if (obj == "FR") {
                binding.spnLanguage.setSelection(index)
                break
            }
        }
        binding.txtAppVersion.text = String.format("V%s %s BUILD(%d)", BuildConfig.VERSION, BuildConfig.BUILD_VARIANT, BuildConfig.VERSION_CODE)

        prefHelper.isFromHistory = false
        /*if (Build.VERSION.SDK_INT >= 24)
            binding.tvDevelopedBy.text = Html.fromHtml(getString(R.string.developed_by), Html.FROM_HTML_MODE_COMPACT)
        else binding.tvDevelopedBy.text = Html.fromHtml(getString(R.string.developed_by))
        binding.tvDevelopedBy.movementMethod = LinkMovementMethod.getInstance()
        stripUnderlines(binding.tvDevelopedBy)*/
    }

    private fun getCurrentLocation() {
        locationHelper = LocationHelper(this, binding.edtUsername, this)
        locationHelper?.fetchLocation()
        locationHelper?.setListener(object : LocationHelper.Location {
            override fun found(latitude: Double, longitude: Double) {
                latLng = LatLng(latitude, longitude)
                val compressionWork = OneTimeWorkRequest.Builder(BackgroundService::class.java)
                val data = Data.Builder()
                data.putDouble("KEY_LAT", latLng?.latitude!!)
                data.putDouble("KEY_LONG", latLng?.longitude!!)
                compressionWork.setInputData(data.build())
                WorkManager.getInstance(applicationContext).enqueue(compressionWork.build())
            }

            override fun start() {
            }
        })
    }

    private fun validateCredentials(): Boolean {
        val userName = binding.edtUsername.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()

        if (userName.isEmpty()) {
            showSnackbarMsg(R.string.msg_username_empty)
            return false
        }

        if (password.isEmpty()) {
            showSnackbarMsg(R.string.msg_password_empty)
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constant.REQUEST_PHONE_STATE) {
            if (isPermissionGranted(grantResults)) {
                prefHelper.serialNumber = getSerialNumber()
            }
        }
    }

    private fun getSerialNumber(): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                return try {
                    Build.getSerial()
                } catch (e: java.lang.Exception) {
                    Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
                }
            } else
                requestForPermission(this, arrayOf(android.Manifest.permission.READ_PHONE_STATE), Constant.REQUEST_PHONE_STATE)
        } else {
            return Build.SERIAL
        }
        return ""
    }

    fun updateValues() {
        binding.txtInpLayUsername.hint = getString(R.string.lbl_username)
        binding.txtInpLayPassword.hint = getString(R.string.lbl_password)
        binding.lngText.text = getString(R.string.language_title)
        binding.btnLogin.text = getString(R.string.login)
        /*if (Build.VERSION.SDK_INT >= 24)
            binding.tvDevelopedBy.text = Html.fromHtml(getString(R.string.developed_by), Html.FROM_HTML_MODE_COMPACT)
        else binding.tvDevelopedBy.text = Html.fromHtml(getString(R.string.developed_by))*/
    }

    private fun getBitmapFromURL(src: String?) {
        try {
            binding.progressBar.visibility = View.GONE
            binding.logoImgView.visibility = View.VISIBLE
            binding.icSgsFbbLogo.visibility = View.VISIBLE
            Glide.with(applicationContext).load(src).into(binding.logoImgView)
            Glide.with(applicationContext).load(municipalLogoPath).into(binding.icSgsFbbLogo)
        } catch (e: Exception) {
            LogHelper.writeLog(exception = e)
        }
    }
}