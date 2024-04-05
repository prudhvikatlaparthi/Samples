package com.sgs.citytax.ui.fragments

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
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.GetAssetList4Return
import com.sgs.citytax.api.response.AssetDetailsBySycotax
import com.sgs.citytax.api.response.AssetsForReturnResponse
import com.sgs.citytax.api.response.ValidateAssetForAssignAndReturnResponse
import com.sgs.citytax.databinding.FragmentAssetListForReturnBinding
import com.sgs.citytax.model.AssetListForReturn
import com.sgs.citytax.ui.AssetBookingActivity
import com.sgs.citytax.ui.ScanActivity
import com.sgs.citytax.ui.adapter.AssetListForReturnAdapter
import com.sgs.citytax.util.Constant

class AssetReturnListFragment : BaseFragment(), AssetListForReturnAdapter.Listener {
    private lateinit var mBinding: FragmentAssetListForReturnBinding
    private var mListener: Listener? = null
    private var adapter: AssetListForReturnAdapter? = null
    private var mAssetLists: ArrayList<AssetListForReturn> = arrayListOf()
    private var fromScreen: Constant.QuickMenu? = null

    var pageIndex: Int = 1
    val pageSize: Int = 10
    var hasMoreData: Boolean = true
    var isLoading: Boolean = false


    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = context as Listener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement Listener")
        }
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_asset_list_for_return, container, false)
        initComponents()
        return mBinding.root
    }

    override fun initComponents() {
        processIntent()
        setViews()
        bindData()
        initialiseListeners()
    }

    private fun processIntent() {
        arguments?.let {
            fromScreen = arguments?.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
        }
    }

    private fun setViews() {
        mBinding.rcvAssets.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
        adapter = AssetListForReturnAdapter(this)
        mBinding.rcvAssets.adapter = adapter
    }

    private fun initialiseListeners() {
        mBinding.rcvAssets.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val visibleItemCount: Int = recyclerView.layoutManager!!.childCount
                val linearLayoutManager: LinearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount: Int = linearLayoutManager.itemCount
                val firstVisibleItemPosition: Int = linearLayoutManager.findFirstVisibleItemPosition()
                if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0 && hasMoreData && !isLoading) {
                    bindData()
                }
            }
        })
    }

    private fun bindData() {
        mListener?.showProgressDialog()
        val assetListForReturn = GetAssetList4Return()
        assetListForReturn.pageIndex = pageIndex
        assetListForReturn.pageSize = pageSize

        val assetList = AssetListForReturn()
        assetList.isLoading = true
        adapter?.clear()
        adapter?.add(assetList)
        isLoading = true

        APICall.getAssetListForReturn(assetListForReturn, object : ConnectionCallBack<AssetsForReturnResponse> {
            override fun onSuccess(response: AssetsForReturnResponse) {
                mListener?.dismissDialog()
                if (response.assetsListForResult?.assetsLists != null && response.assetsListForResult?.assetsLists!!.isNotEmpty()) {
                    mAssetLists = response.assetsListForResult?.assetsLists!!
                    val count: Int = mAssetLists.size
                    if (count < pageSize) {
                        hasMoreData = false
                    } else
                        pageIndex += 1
                    adapter?.remove(assetList)
                    adapter!!.addAll(mAssetLists)
                    isLoading = false
                } else {
                    adapter?.remove(assetList)
                    isLoading = false
                }
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
                adapter?.remove(assetList)
                isLoading = false
            }
        })
    }

    override fun onItemClick(assetList: AssetListForReturn, position: Int) {
        mListener?.showProgressDialog()
        APICall.searchAssetDetails(assetList.assetSycoTaxId, object : ConnectionCallBack<AssetDetailsBySycotax> {
            override fun onSuccess(response: AssetDetailsBySycotax) {
                mListener?.dismissDialog()
                if (response.assetDetails != null) {
                    validateAssetForReturn(response.assetDetails?.assetNumber ?: "")
                } else {
                    mListener?.showAlertDialog(getString(R.string.no_record), DialogInterface.OnClickListener { dialogInterface, _ ->
                        dialogInterface.dismiss()
                    })
                }
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(getString(R.string.no_record), DialogInterface.OnClickListener { dialogInterface, _ ->
                    dialogInterface.dismiss()
                })
            }
        })
    }

    private fun validateAssetForReturn(assetNo: String) {
        APICall.validateAsset4Return(assetNo, object : ConnectionCallBack<ValidateAssetForAssignAndReturnResponse> {
            override fun onSuccess(response: ValidateAssetForAssignAndReturnResponse) {
                mListener?.dismissDialog()
                navigateToAssetReturnScreen(response)

            }

            override fun onFailure(message: String) {
                mListener?.showAlertDialog(message, DialogInterface.OnClickListener { dialogInterface, _ ->
                    dialogInterface.dismiss()
                })
            }
        })
    }

    private fun navigateToAssetReturnScreen(responseAssignAndReturn: ValidateAssetForAssignAndReturnResponse) {
            val assetReturnIntent = Intent(requireContext(), AssetBookingActivity::class.java)
            assetReturnIntent.putExtra(Constant.KEY_QUICK_MENU, fromScreen as Constant.QuickMenu)
            assetReturnIntent.putExtra(Constant.KEY_ASSET_ID, responseAssignAndReturn.assetId)
            assetReturnIntent.putExtra(Constant.KEY_VALIDATE_ASSET, responseAssignAndReturn)
//            startActivity(assetReturnIntent)
            startActivityForResult(assetReturnIntent, Constant.REQUEST_CODE_ASSET)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        bindData()
    }


    interface Listener {
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


}