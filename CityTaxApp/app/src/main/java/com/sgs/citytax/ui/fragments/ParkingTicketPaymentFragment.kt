package com.sgs.citytax.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.GetParkingTaxTransactionsList
import com.sgs.citytax.api.response.GetParkingTaxTransactionResponse
import com.sgs.citytax.api.response.ParkingPaymentTrans
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.FragmentParkingPaymentTicketBinding
import com.sgs.citytax.ui.AllTaxNoticesActivity
import com.sgs.citytax.ui.PaymentActivity
import com.sgs.citytax.ui.adapter.ParkingTicketPaymentListAdapter
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.formatWithPrecision
import java.math.BigDecimal

//this is parking ticket Payment module
class ParkingTicketPaymentFragment : BaseFragment(), View.OnClickListener, ParkingTicketPaymentListAdapter.Listener, IClickListener {
    private lateinit var mBinding: FragmentParkingPaymentTicketBinding
    private var mListener: Listener? = null
    private var parkingPaymentTrans: ParkingPaymentTrans? = null
    private var adapter: ParkingTicketPaymentListAdapter? = null
    private var parkingPaymentList: List<ParkingPaymentTrans> = arrayListOf()

    var maxAmt: BigDecimal = BigDecimal.ZERO
    var minAmt: BigDecimal = BigDecimal.ZERO
    var currentDue: BigDecimal = BigDecimal.ZERO
    var customerID: Int = 0

    private var fromScreen: Any? = null
    private var vehicleNo: String? = ""
    private var parkingID: Int = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = if (parentFragment != null) {
                parentFragment as Listener
            } else {
                context as Listener
            }
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement Listener")
        }
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_parking_payment_ticket, container, false)
        initComponents()
        return mBinding.root
    }

    override fun initComponents() {
        arguments?.let {
            fromScreen = it.getSerializable(Constant.KEY_QUICK_MENU)
            vehicleNo = it.getString(Constant.KEY_VEHICLE_NO)
            parkingID = it.getInt(Constant.KEY_PARKING_PLACE_ID, 0)
        }

         setViews()
         bindData()
    }

    private fun setViews() {
        //mBinding.btnCancel.setOnClickListener(this)
        mBinding.payFull.setOnClickListener(this)
        mBinding.rcvImpundmentsList.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
        adapter = ParkingTicketPaymentListAdapter(this, this)
        mBinding.rcvImpundmentsList.adapter = adapter
    }

    fun bindData() {
        val getParkingTaxTransactionsList = GetParkingTaxTransactionsList()
        getParkingTaxTransactionsList.onlydue = "Y"
        getParkingTaxTransactionsList.vehno = vehicleNo
        getParkingTaxTransactionsList.parkingplcid = parkingID


        mListener?.showProgressDialog()
        APICall.getParkingTicketPaymentList(getParkingTaxTransactionsList, object : ConnectionCallBack<GetParkingTaxTransactionResponse> {
            override fun onSuccess(response: GetParkingTaxTransactionResponse) {
                mListener?.dismissDialog()
                if (response.results != null && response.results.isNotEmpty()) {
                    mBinding.massPayCard.visibility = View.VISIBLE
                    parkingPaymentList = response?.results
                    adapter!!.addAll(parkingPaymentList)

                    for (ParkingPaymentTrans in parkingPaymentList) {
                        maxAmt = maxAmt.add(ParkingPaymentTrans.amount)
                        minAmt = minAmt.add(ParkingPaymentTrans.minmumPayAmount)
                        currentDue = currentDue.add(ParkingPaymentTrans.currentDue)
                        customerID = ParkingPaymentTrans?.vehicleOwnerAccountID!!
                    }
                    mBinding.tvTotalMinAmount.text = formatWithPrecision(minAmt)
                    mBinding.tvTotalCurrentDues.text = formatWithPrecision(currentDue)
                    mBinding.tvTotalDue.text = formatWithPrecision(maxAmt)

                }
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
                mBinding.rcvImpundmentsList.adapter = null

                mBinding.massPayCard.visibility = View.GONE
            }
        })
    }


    interface Listener {
        fun popBackStack()
        fun finish()
        fun showProgressDialog()
        fun dismissDialog()
        fun showAlertDialog(message: String?)
        fun showAlertDialog(message: String?, okListener: DialogInterface.OnClickListener)
        fun showAlertDialog(message: String?, okListener: DialogInterface.OnClickListener, cancelListener: DialogInterface.OnClickListener)
        fun showToolbarBackButton(title: Int)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        fun hideKeyBoard()
        fun showSnackbarMsg(message: String?)
    }

    fun onBackPressed() {
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.pay_full -> {
                val payment = MyApplication.resetPayment()

                payment.amountDue = currentDue
                payment.amountTotal = currentDue
                payment.minimumPayAmount = minAmt
                payment.paymentType = Constant.PaymentType.PARKING_TICKET_PAY
                payment.parkingPlaceID = parkingID
                payment.vehicleNo = vehicleNo
                payment.customerID = customerID

                val intent = Intent(requireContext(), PaymentActivity::class.java)
                intent.putExtra(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_PARKING_COLLECT)
                startActivityForResult(intent, Constant.REQUEST_CODE_PAYMENT_SUCCESS)

            }
            R.id.btnCancel -> {
                mListener?.popBackStack()
            }

        }

    }

    override fun onClick(view: View, position: Int, obj: Any) {
        when (view.id) {
            R.id.btn_pay -> {
                val parkingPaymentTrans = obj as ParkingPaymentTrans
                val payment = MyApplication.resetPayment()

                payment.amountDue = parkingPaymentTrans.currentDue!!
                payment.amountTotal = parkingPaymentTrans.currentDue!!
                payment.minimumPayAmount = parkingPaymentTrans.minmumPayAmount!!
                payment.paymentType = Constant.PaymentType.PARKING_TICKET_PAY
                payment.productCode = parkingPaymentTrans.prodcode!!
                payment.TransactionTypeCode = parkingPaymentTrans.transactiontypcode!!
                payment.TransactionNo = parkingPaymentTrans.transactionNo!!
                payment.customerID = parkingPaymentTrans?.vehicleOwnerAccountID!!
                payment.parkingPlaceID = parkingPaymentTrans?.parkingPlaceID!!
                payment.vehicleNo = parkingPaymentTrans?.vehicleNo!!

                val intent = Intent(requireContext(), PaymentActivity::class.java)
                intent.putExtra(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_PARKING_COLLECT)
                startActivityForResult(intent, Constant.REQUEST_CODE_PAYMENT_SUCCESS)
            }
        }
    }

    override fun onLongClick(view: View, position: Int, obj: Any) {
        TODO("Not yet implemented")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_PAYMENT_SUCCESS) {
            data?.let {
                if (it.hasExtra(Constant.KEY_ADVANCE_RECEIVED_ID) && it.getIntExtra(Constant.KEY_ADVANCE_RECEIVED_ID, 0) > 0) {
                    Log.e("Ticket payment frag", ">>>>>>>> Payment response came")


                    val intent = Intent(requireContext(), AllTaxNoticesActivity::class.java)
                    if (it.hasExtra(Constant.KEY_ADVANCE_RECEIVED_ID))
                        intent.putExtra(Constant.KEY_ADVANCE_RECEIVED_ID, it.getIntExtra(Constant.KEY_ADVANCE_RECEIVED_ID, 0))
                    intent.putExtra(Constant.KEY_TAX_RULE_BOOK_CODE, Constant.TaxRuleBook.PARKING_TICKET_PAYMENT.Code)
                    startActivity(intent)
                    mListener?.finish()
                }
            }
        }
    }

    override fun onItemClick(list: ParkingPaymentTrans, position: Int) {
        TODO("Not yet implemented")
    }
}