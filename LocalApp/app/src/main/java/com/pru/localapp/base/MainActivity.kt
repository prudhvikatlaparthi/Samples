package com.pru.localapp.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.pru.localapp.dao.entitydao.CrmCustomerDao
import com.pru.localapp.dao.entitydao.CrmCustomerVehicleDao
import com.pru.localapp.dao.transactiondao.VuCrmCustomerDao
import com.pru.localapp.dao.transactiondao.VuCrmCustomerVehicleDao
import com.pru.localapp.dao.viewdao.ViewCrmCustomerDao
import com.pru.localapp.databinding.ActivityMainBinding
import com.pru.localapp.models.entities.CrmCustomer
import com.pru.localapp.models.entities.CrmCustomerVehicle
import com.pru.localapp.models.transactions.VuCrmCustomer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.security.SecureRandom
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var activityMainBinding: ActivityMainBinding

    @Inject
    lateinit var crmCustomerDao: CrmCustomerDao

    @Inject
    lateinit var crmCustomerVehicleDao: CrmCustomerVehicleDao

    @Inject
    lateinit var vuCrmCustomerVehicleDao: VuCrmCustomerVehicleDao

    @Inject
    lateinit var vuCrmCustomerDao: VuCrmCustomerDao

    @Inject
    lateinit var viewCrmCustomerDao: ViewCrmCustomerDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
//        startWorker()
//        activityMainBinding.searchEdit.addTextChangedListener {

        lifecycleScope.launchWhenStarted {
            viewCrmCustomerDao.getCustomers()
                .observe(this@MainActivity, {
                    var data = ""
                    it.forEach { item ->
                        data += item.vehicleNo + " " + item.name + "\n"
                    }
                    activityMainBinding.viewData.text = data
                })
            /*vuCrmCustomerDao.getCustomers()
                .observe(this@MainActivity, {
                    var data = ""
                    it.forEach { item ->
                        data += item.crmCustomerVehicle?.vehicleNo + " " + item.crmCustomer?.name + "\n"
                    }
                    activityMainBinding.viewData.text = data
                })*/
        }
//        }

        activityMainBinding.setData.setOnClickListener {
            val customerId = SecureRandom().nextInt()
            val crmCustomer = CrmCustomer(
                customerId = customerId,
                name = activityMainBinding.searchEdit.text.toString(),
                mobile = "99999${SecureRandom().nextInt()}",
                email = "abc${SecureRandom().nextInt()}@mail.com"
            )
            val customerVehicle = CrmCustomerVehicle(
                vehicleNo = "vehicleNo ${SecureRandom().nextInt()}",
                vehicleOwnerType = "vehicleOwnerType ${SecureRandom().nextInt()}",
                vehicleType = "vehicleType ${SecureRandom().nextInt()}",
                accountId = customerId
            )
            lifecycleScope.launch(Dispatchers.IO) {
                crmCustomerDao.insertAll(crmCustomer = listOf(crmCustomer))
                crmCustomerVehicleDao.insertAll(crmCustomerVehicle = listOf(customerVehicle))
            }
        }
    }
}