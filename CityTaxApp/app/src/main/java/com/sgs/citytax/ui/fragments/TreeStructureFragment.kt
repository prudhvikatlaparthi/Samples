package com.sgs.citytax.ui.fragments

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.TextView
import com.example.treestructure.PropTreeData
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.response.PropertyTreeData
import com.sgs.citytax.api.response.PropertyTreeDataList
import com.sgs.citytax.ui.FragmentCommunicator
import com.sgs.citytax.ui.atv.model.TreeNode
import com.sgs.citytax.ui.atv.view.AndroidTreeView
import com.sgs.citytax.ui.viewHolder.IconTreeItemHolder
import com.sgs.citytax.ui.viewHolder.IconTreeItemHolder.IconTreeItem
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.LogHelper
import org.json.JSONException

/**
 * Created by Sriram Vikas on 2/12/15.
 */
class TreeStructureFragment : BaseFragment(), IClickListener {
    private var statusBar: TextView? = null
    private var tView: AndroidTreeView? = null
    private var mListener: FragmentCommunicator? = null

    private var mPropertyID: Int = 0
    private var mCode: Constant.QuickMenu? = null
    private val str = "{\"ReturnValue\":{\"PropertyTreeData\":[{\"id\":41,\"parent\":16,\"ParentID\":\"16\",\"SurveyNo\":null,\"TopParent\":16,\"level\":1,\"expanded\":1,\"loaded\":1,\"isLeaf\":0,\"PropertyName\":\"PRO01\",\"PropertySycotaxID\":\"SYC-P-OSU-1203-000005\",\"PropertyType\":\"Property_02\",\"MonthlyRentAmount\":0,\"EstimatedRentAmount\":0,\"CreatedDate\":\"2020-09-11T06:14:55.130\",\"ElectricityConsumption\":null,\"PhaseOfElectricity\":null,\"WaterConsumption\":null,\"ComfortLevel\":null,\"Area\":0,\"ConstructedDate\":null,\"Status\":\"vérifié\",\"RegistrationNo\":null},{\"id\":102,\"parent\":41,\"ParentID\":\"41\",\"SurveyNo\":\"/1\",\"TopParent\":16,\"level\":2,\"expanded\":0,\"loaded\":1,\"isLeaf\":1,\"PropertyName\":\"AdiltTestProp\",\"PropertySycotaxID\":\"SYC-P-OUAR-0519-000027\",\"PropertyType\":\"Property_02\",\"MonthlyRentAmount\":534,\"EstimatedRentAmount\":4524,\"CreatedDate\":\"2020-09-16T07:21:29.740\",\"ElectricityConsumption\":null,\"PhaseOfElectricity\":null,\"WaterConsumption\":null,\"ComfortLevel\":null,\"Area\":0,\"ConstructedDate\":\"2020-09-11T00:00:00.000\",\"Status\":\"vérifié\",\"RegistrationNo\":\"4848484\"},{\"id\":267,\"parent\":41,\"ParentID\":\"41\",\"SurveyNo\":\"/10\",\"TopParent\":16,\"level\":2,\"expanded\":0,\"loaded\":1,\"isLeaf\":1,\"PropertyName\":\"vijay rent\",\"PropertySycotaxID\":\"SYC-P-OUAR-0519-000160\",\"PropertyType\":\"Property_02\",\"MonthlyRentAmount\":0,\"EstimatedRentAmount\":0,\"CreatedDate\":\"2020-09-24T15:06:56.283\",\"ElectricityConsumption\":null,\"PhaseOfElectricity\":null,\"WaterConsumption\":null,\"ComfortLevel\":null,\"Area\":484,\"ConstructedDate\":\"2020-09-10T00:00:00.000\",\"Status\":\"vérifié\",\"RegistrationNo\":\"234345435\"},{\"id\":103,\"parent\":41,\"ParentID\":\"41\",\"SurveyNo\":\"/2\",\"TopParent\":16,\"level\":2,\"expanded\":0,\"loaded\":1,\"isLeaf\":1,\"PropertyName\":\"AdiltTestProp\",\"PropertySycotaxID\":\"SYC-P-OUAR-0519-000027\",\"PropertyType\":\"Property_02\",\"MonthlyRentAmount\":534,\"EstimatedRentAmount\":4524,\"CreatedDate\":\"2020-09-16T07:39:33.103\",\"ElectricityConsumption\":null,\"PhaseOfElectricity\":null,\"WaterConsumption\":null,\"ComfortLevel\":null,\"Area\":0,\"ConstructedDate\":\"2020-09-11T00:00:00.000\",\"Status\":\"vérifié\",\"RegistrationNo\":\"4848484\"},{\"id\":104,\"parent\":41,\"ParentID\":\"41\",\"SurveyNo\":\"/3\",\"TopParent\":16,\"level\":2,\"expanded\":0,\"loaded\":1,\"isLeaf\":1,\"PropertyName\":\"AdiltTestProp\",\"PropertySycotaxID\":\"SYC-P-OUAR-0519-000027\",\"PropertyType\":\"Property_02\",\"MonthlyRentAmount\":534,\"EstimatedRentAmount\":4524,\"CreatedDate\":\"2020-09-16T07:43:57.613\",\"ElectricityConsumption\":null,\"PhaseOfElectricity\":null,\"WaterConsumption\":null,\"ComfortLevel\":null,\"Area\":0,\"ConstructedDate\":\"2020-09-11T00:00:00.000\",\"Status\":\"vérifié\",\"RegistrationNo\":\"4848484\"},{\"id\":106,\"parent\":41,\"ParentID\":\"41\",\"SurveyNo\":\"/4\",\"TopParent\":16,\"level\":2,\"expanded\":0,\"loaded\":1,\"isLeaf\":1,\"PropertyName\":\"AdiltTestProp\",\"PropertySycotaxID\":\"SYC-P-OUAR-0519-000027\",\"PropertyType\":\"Property_02\",\"MonthlyRentAmount\":534,\"EstimatedRentAmount\":4524,\"CreatedDate\":\"2020-09-16T07:58:40.983\",\"ElectricityConsumption\":null,\"PhaseOfElectricity\":null,\"WaterConsumption\":null,\"ComfortLevel\":null,\"Area\":787,\"ConstructedDate\":\"2020-09-11T00:00:00.000\",\"Status\":\"vérifié\",\"RegistrationNo\":\"4848484\"},{\"id\":109,\"parent\":41,\"ParentID\":\"41\",\"SurveyNo\":\"/5\",\"TopParent\":16,\"level\":2,\"expanded\":0,\"loaded\":1,\"isLeaf\":1,\"PropertyName\":\"AdiltTestProp\",\"PropertySycotaxID\":\"SYC-P-OUAR-0519-000027\",\"PropertyType\":\"Property_02\",\"MonthlyRentAmount\":534,\"EstimatedRentAmount\":4524,\"CreatedDate\":\"2020-09-16T11:04:10.730\",\"ElectricityConsumption\":null,\"PhaseOfElectricity\":null,\"WaterConsumption\":null,\"ComfortLevel\":null,\"Area\":0,\"ConstructedDate\":\"2020-09-11T00:00:00.000\",\"Status\":\"vérifié\",\"RegistrationNo\":\"4848484\"},{\"id\":220,\"parent\":41,\"ParentID\":\"41\",\"SurveyNo\":\"/6\",\"TopParent\":16,\"level\":2,\"expanded\":0,\"loaded\":1,\"isLeaf\":1,\"PropertyName\":\"Sample Android Testing\",\"PropertySycotaxID\":\"SYC-P-OUAR-0519-000120\",\"PropertyType\":\"Property_02\",\"MonthlyRentAmount\":200,\"EstimatedRentAmount\":100,\"CreatedDate\":\"2020-09-22T04:57:25.287\",\"ElectricityConsumption\":null,\"PhaseOfElectricity\":null,\"WaterConsumption\":null,\"ComfortLevel\":null,\"Area\":1,\"ConstructedDate\":\"2020-09-22T00:00:00.000\",\"Status\":\"vérifié\",\"RegistrationNo\":\"435345\"},{\"id\":221,\"parent\":41,\"ParentID\":\"41\",\"SurveyNo\":\"/7\",\"TopParent\":16,\"level\":2,\"expanded\":0,\"loaded\":1,\"isLeaf\":1,\"PropertyName\":\"4rtr\",\"PropertySycotaxID\":\"SYC-P-OUAR-0519-000124\",\"PropertyType\":\"Property_02\",\"MonthlyRentAmount\":0,\"EstimatedRentAmount\":0,\"CreatedDate\":\"2020-09-22T06:02:43.760\",\"ElectricityConsumption\":null,\"PhaseOfElectricity\":null,\"WaterConsumption\":null,\"ComfortLevel\":null,\"Area\":123,\"ConstructedDate\":\"2020-09-18T00:00:00.000\",\"Status\":\"vérifié\",\"RegistrationNo\":\"34534\"},{\"id\":224,\"parent\":41,\"ParentID\":\"41\",\"SurveyNo\":\"/8\",\"TopParent\":16,\"level\":2,\"expanded\":0,\"loaded\":1,\"isLeaf\":1,\"PropertyName\":\"ANASHAGS\",\"PropertySycotaxID\":\"SYC-P-OUAR-0519-000121\",\"PropertyType\":\"Property_02\",\"MonthlyRentAmount\":0,\"EstimatedRentAmount\":0,\"CreatedDate\":\"2020-09-22T09:37:39.627\",\"ElectricityConsumption\":null,\"PhaseOfElectricity\":null,\"WaterConsumption\":null,\"ComfortLevel\":null,\"Area\":30,\"ConstructedDate\":\"2020-09-22T00:00:00.000\",\"Status\":\"vérifié\",\"RegistrationNo\":\"2423423\"},{\"id\":231,\"parent\":41,\"ParentID\":\"41\",\"SurveyNo\":\"/9\",\"TopParent\":16,\"level\":2,\"expanded\":0,\"loaded\":1,\"isLeaf\":1,\"PropertyName\":\"Sample Property test\",\"PropertySycotaxID\":\"SYC-P-OUAR-0519-000130\",\"PropertyType\":\"Property_02\",\"MonthlyRentAmount\":0,\"EstimatedRentAmount\":0,\"CreatedDate\":\"2020-09-23T08:52:28.147\",\"ElectricityConsumption\":null,\"PhaseOfElectricity\":null,\"WaterConsumption\":null,\"ComfortLevel\":null,\"Area\":22,\"ConstructedDate\":\"2020-09-23T00:00:00.000\",\"Status\":\"vérifié\",\"RegistrationNo\":\"23543543\"},{\"id\":16,\"parent\":null,\"ParentID\":\"null\",\"SurveyNo\":\"PRP/5/01\",\"TopParent\":16,\"level\":0,\"expanded\":1,\"loaded\":1,\"isLeaf\":0,\"PropertyName\":\"Test Property 01\",\"PropertySycotaxID\":\"SYC-P-OSU-1203-000001\",\"PropertyType\":\"Property_02\",\"MonthlyRentAmount\":0,\"EstimatedRentAmount\":0,\"CreatedDate\":\"2020-09-09T15:33:04.887\",\"ElectricityConsumption\":null,\"PhaseOfElectricity\":null,\"WaterConsumption\":null,\"ComfortLevel\":null,\"Area\":300,\"ConstructedDate\":\"2020-09-04T00:00:00.000\",\"Status\":\"vérifié\",\"RegistrationNo\":\"PRPTSST786\"},{\"id\":76,\"parent\":16,\"ParentID\":\"16\",\"SurveyNo\":\"PRP/5/01/2\",\"TopParent\":16,\"level\":1,\"expanded\":0,\"loaded\":1,\"isLeaf\":1,\"PropertyName\":\"Priyanka Mansion200\",\"PropertySycotaxID\":\"SYC-P-OUAR-0519-000011\",\"PropertyType\":\"Property_02\",\"MonthlyRentAmount\":989,\"EstimatedRentAmount\":765,\"CreatedDate\":\"2020-09-14T16:13:35.177\",\"ElectricityConsumption\":null,\"PhaseOfElectricity\":null,\"WaterConsumption\":null,\"ComfortLevel\":null,\"Area\":0,\"ConstructedDate\":null,\"Status\":\"vérifié\",\"RegistrationNo\":\"908789\"},{\"id\":135,\"parent\":16,\"ParentID\":\"16\",\"SurveyNo\":\"PRP/5/01/3\",\"TopParent\":16,\"level\":1,\"expanded\":0,\"loaded\":1,\"isLeaf\":1,\"PropertyName\":\"The Willows\",\"PropertySycotaxID\":\"SYC-P-OUAR-0519-000046\",\"PropertyType\":\"Property_02\",\"MonthlyRentAmount\":20000,\"EstimatedRentAmount\":30000,\"CreatedDate\":\"2020-09-17T08:34:29.330\",\"ElectricityConsumption\":null,\"PhaseOfElectricity\":null,\"WaterConsumption\":null,\"ComfortLevel\":null,\"Area\":100000000,\"ConstructedDate\":null,\"Status\":\"vérifié\",\"RegistrationNo\":\"WILLOW01\"},{\"id\":179,\"parent\":16,\"ParentID\":\"16\",\"SurveyNo\":\"PRP/5/01/4\",\"TopParent\":16,\"level\":1,\"expanded\":0,\"loaded\":1,\"isLeaf\":1,\"PropertyName\":\"Android Res 1\",\"PropertySycotaxID\":\"SYC-P-OUAR-0519-000091\",\"PropertyType\":\"Property_02\",\"MonthlyRentAmount\":0,\"EstimatedRentAmount\":0,\"CreatedDate\":\"2020-09-18T10:28:47.853\",\"ElectricityConsumption\":null,\"PhaseOfElectricity\":null,\"WaterConsumption\":null,\"ComfortLevel\":null,\"Area\":4,\"ConstructedDate\":\"2020-09-18T00:00:00.000\",\"Status\":\"vérifié\",\"RegistrationNo\":\"AAD4545\"},{\"id\":207,\"parent\":16,\"ParentID\":\"16\",\"SurveyNo\":\"PRP/5/01/5\",\"TopParent\":16,\"level\":1,\"expanded\":0,\"loaded\":1,\"isLeaf\":1,\"PropertyName\":\"PRO TST 569\",\"PropertySycotaxID\":\"SYC-P-OUAR-0519-000094\",\"PropertyType\":\"Property_02\",\"MonthlyRentAmount\":0,\"EstimatedRentAmount\":0,\"CreatedDate\":\"2020-09-19T15:08:49.960\",\"ElectricityConsumption\":null,\"PhaseOfElectricity\":null,\"WaterConsumption\":null,\"ComfortLevel\":null,\"Area\":50,\"ConstructedDate\":null,\"Status\":\"vérifié\",\"RegistrationNo\":\"43262354\"},{\"id\":250,\"parent\":16,\"ParentID\":\"16\",\"SurveyNo\":\"PRP/5/01/6\",\"TopParent\":16,\"level\":1,\"expanded\":0,\"loaded\":1,\"isLeaf\":1,\"PropertyName\":\"Sujit Property\",\"PropertySycotaxID\":\"SYC-P-OUAR-0519-000147\",\"PropertyType\":\"Property_02\",\"MonthlyRentAmount\":245,\"EstimatedRentAmount\":300,\"CreatedDate\":\"2020-09-23T19:14:42.113\",\"ElectricityConsumption\":null,\"PhaseOfElectricity\":null,\"WaterConsumption\":null,\"ComfortLevel\":null,\"Area\":0,\"ConstructedDate\":null,\"Status\":\"vérifié\",\"RegistrationNo\":\"SUJ PRP 112\"}]},\"ReturnType\":\"Newtonsoft.Json.Linq.JObject\",\"IsSuccess\":true,\"msg\":\"\",\"Schema\":\"\",\"UTCDate\":\"2020-09-30T05:58:17.967\",\"token\":\"\"}"


    companion object {
        @JvmStatic
        fun newInstance(propertyID: Int, fromScreenMode: Constant.QuickMenu) = TreeStructureFragment().apply {
            mPropertyID = propertyID
            mCode = fromScreenMode

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_default, null, false)
        val containerView = rootView.findViewById<View>(R.id.container) as ViewGroup
        statusBar = rootView.findViewById<View>(R.id.status_bar) as TextView
        val root = TreeNode.root()

        arguments?.let {
            if (it.containsKey(Constant.KEY_QUICK_MENU)) {
                mCode = it.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu?
            }
            if (it.containsKey(Constant.KEY_PRIMARY_KEY)) {
                mPropertyID = it.getInt(Constant.KEY_PRIMARY_KEY)
            }
        }
        try {
            //new code
            mPropertyID?.let {
                mListener?.showProgressDialog()
                APICall.getPropertyTree(it, object : ConnectionCallBack<PropertyTreeData> {
                    override fun onSuccess(response: PropertyTreeData) {
                        mListener?.dismissDialog()
                        if (response != null) {
                            try {
                                /*val gson = Gson()
                                val propTreeDataList: PropTreeDataResponse = gson.fromJson(str, PropTreeDataResponse::class.java)
                                val list = propTreeDataList.propTreeDataObj.propTreeData
                               */
                                val list = response.propTreeData
                                val finalResult = getParentList(list)
                                var parentRoot_1: TreeNode? = null
                                val subParent: TreeNode? = null
                                for (j in finalResult.indices) {
                                    parentRoot_1 = TreeNode(IconTreeItem(R.string.ic_folder, finalResult[j].propTreeData?.id.toString(), finalResult[j].propTreeData))
                                    if (finalResult[j].childList.size > 0) {
                                        createChildNodesDynamic(finalResult[j].childList, j, parentRoot_1)
                                        if (subParent != null) parentRoot_1.addChildren(subParent)
                                    }
                                    Log.e("===0===", "===0===")
                                    if (parentRoot_1 != null) root.addChildren(parentRoot_1)
                                }

                            } catch (e: java.lang.Exception) {
                                LogHelper.writeLog(exception = e)
                            }

                            tView = AndroidTreeView(activity, root)
                            tView!!.setDefaultAnimation(true)
                            tView!!.setDefaultContainerStyle(R.style.TreeNodeStyleCustom)
                            tView!!.setDefaultViewHolder(IconTreeItemHolder::class.java)
                            tView!!.setDefaultNodeClickListener(nodeClickListener)
                            tView!!.setDefaultNodeLongClickListener(nodeLongClickListener)
                            containerView.addView(tView!!.view)
                            if (savedInstanceState != null) {
                                val state = savedInstanceState.getString("tState")
                                if (!TextUtils.isEmpty(state)) {
                                    tView!!.restoreState(state)
                                }
                            }
                        }
                    }

                    override fun onFailure(message: String) {
                        mListener?.dismissDialog()
                        mListener?.showAlertDialog(message)
                    }

                })
            }

        } catch (e: JSONException) {
            LogHelper.writeLog(exception = e)
        }

        return rootView
    }

    private fun createChildNodesDynamic(childObj: ArrayList<PropertyTreeDataList>, j: Int, parentRoot: TreeNode) {
        var subParent: TreeNode? = null
        for (i in childObj.indices) {
            val childData = childObj[i]
            subParent = TreeNode(IconTreeItem(R.string.ic_folder, childData.propTreeData?.id.toString(), childData.propTreeData))
            parentRoot.addChildren(subParent)
            if (childData.childList.size > 0) {
                createChildNodesDynamic(childData.childList, childData.propTreeData?.id
                        ?: 0, subParent)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_treeview, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.expandAll -> tView!!.expandAll()
            R.id.collapseAll -> tView!!.collapseAll()
        }
        return true
    }

    private val nodeClickListener = TreeNode.TreeNodeClickListener { node, value ->
        val item = value as IconTreeItem
        statusBar!!.text = getString(R.string.last_click) + "" + item.text
    }
    private val nodeLongClickListener = TreeNode.TreeNodeLongClickListener { node, value ->
        val item = value as IconTreeItem
        // Toast.makeText(activity, "Long click: " + item.text, Toast.LENGTH_SHORT).show()
        true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("tState", tView!!.saveState)
    }

    override fun initComponents() {}
    override fun onClick(view: View, position: Int, obj: Any) {}
    override fun onLongClick(view: View, position: Int, obj: Any) {}

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = try {
            context as FragmentCommunicator
        } catch (e: ClassCastException) {
            throw ClassCastException("\$context must implemeent Listener")
        }
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }


    private fun getParentList(data: ArrayList<PropTreeData>): ArrayList<PropertyTreeDataList> {
        val list: ArrayList<PropertyTreeDataList> = arrayListOf()
        for (propTreeData in data) {
            if (propTreeData.parent == null) {
                val propertyTreeDataList = PropertyTreeDataList()
                propertyTreeDataList.propTreeData = propTreeData
                propertyTreeDataList.propTreeData = propTreeData
                propertyTreeDataList.childList = getChildList(data, propTreeData.id)
                list.add(propertyTreeDataList)
            }
        }
        return list
    }

    private fun getChildList(data: ArrayList<PropTreeData>, id: Int?): ArrayList<PropertyTreeDataList> {
        val list: ArrayList<PropertyTreeDataList> = arrayListOf()
        for (propTreeData in data) {
            if (propTreeData.parent == id) {
                val propertyTreeDataList = PropertyTreeDataList()
                propertyTreeDataList.propTreeData = propTreeData
                propertyTreeDataList.childList = getChildList(data, propTreeData.id)
                list.add(propertyTreeDataList)
            }
        }
        return list
    }
}