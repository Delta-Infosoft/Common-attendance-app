package com.i.common.attendance.ui.home.myportfolio.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.i.common.attendance.R
import com.i.common.attendance.base.BaseFragment
import com.i.common.attendance.databinding.FragmentMyPortfolioDetailsBinding
import com.i.common.attendance.network.response.ViewPortFolioModel
import com.i.common.attendance.ui.home.activity.HomeActivity
import com.i.common.attendance.ui.home.myportfolio.adapter.PortfolioListAdapter
import com.i.common.attendance.ui.home.myportfolio.viewmodel.GetViewPortFolioState
import com.i.common.attendance.ui.home.myportfolio.viewmodel.MyPortfolioViewModel
import com.i.common.attendance.ui.home.newcustomerdealer.fragment.NewCustomerDealerFragment
import com.i.common.attendance.utils.EncryptedPrefHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyPortfolioFragment : BaseFragment() {
    private lateinit var binding : FragmentMyPortfolioDetailsBinding
    private val viewModel: MyPortfolioViewModel by viewModels()
    @Inject lateinit var sharedPref: EncryptedPrefHelper
    private var fullList: List<ViewPortFolioModel> = emptyList()

    private val portfolioAdapter by lazy{
        PortfolioListAdapter(
            onCallClick = { phoneNumber ->
                makePhoneCall(phoneNumber)
            },
            onMapClick = { lat, long ->
                openGoogleMaps(lat, long)
            },
            onEditClick = { item ->
                openEditScreen(item)
            }
        )
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyPortfolioDetailsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        manageToolBar()
        setUpRecyclerView()
        manageOnClickListeners()
        initApiCall()

        manageSearchView()
        observePortfolioData()
    }

    private fun initApiCall() {
        viewModel.getViewPortFolioAPI(
            mobileNo = sharedPref.getUser()?.MobileNo?:""
        )
    }

    private fun manageToolBar() {
        (activity as HomeActivity).apply {
            manageToolBar(isVisible = true)
            manageToolBarTitle(getString(R.string.label_my_portfolio))
            manageBackButtonClick(true)
            setDrawerEnabled(false)
            manageInfo(false)
        }
    }
    private fun manageOnClickListeners() = with(binding){
    }
    private fun setUpRecyclerView() = with(binding){
        recyclerViewMyPortfolio.apply {
            adapter = portfolioAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL,false)
        }
    }
    private fun observePortfolioData(){
        viewModel.viewPortFolioState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is GetViewPortFolioState.Idle -> {}
                is GetViewPortFolioState.Loading -> {
                    showLoader()
                }
                is GetViewPortFolioState.Success -> {
                    hideLoader()
                    fullList = state.data
                    portfolioAdapter.submitList(state.data)
                }
                is GetViewPortFolioState.Empty -> {
                    hideLoader()
                    showToast(state.message)
                }
                is GetViewPortFolioState.Error -> {
                    hideLoader()
                    showToast(state.message)
                }
            }
        }
    }

    private fun manageSearchView(){
        binding.searchView.queryHint = "Search by Company Name"
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                filterList(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }
        })
    }

    private fun filterList(query: String?) {
        if (query.isNullOrBlank()) {
            portfolioAdapter.submitList(fullList)
            return
        }

        val filteredList = fullList.filter {
            it.CompanyName?.contains(query, ignoreCase = true) == true
        }

        portfolioAdapter.submitList(filteredList)
    }


    // -------------------- ACTIONS --------------------

    private fun makePhoneCall(number: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$number")
        }
        startActivity(intent)
    }
    private fun openGoogleMaps(lat: String, long: String) {
        Log.e("Lat Long","${lat} ${long}")
        if (lat.isNotEmpty() && lat != "0.0" && long.isNotEmpty() && long != "0.0") {
            try {
                // ✅ Clean and correct URI format
                val uri = Uri.parse(
                    "https://www.google.com/maps/dir/?api=1" +
                            "&destination=$lat,$long" +   // ✅ destination pin on map
                            "&travelmode=driving"          // driving / walking / bicycling / transit
                )

                val mapsIntent = Intent(Intent.ACTION_VIEW, uri).apply {
                    setPackage("com.google.android.apps.maps")  // ✅ better than setClassName
                }

                if (mapsIntent.resolveActivity(requireActivity().packageManager) != null) {
                    startActivity(mapsIntent)
                } else {
                    // Fallback if Google Maps not installed
                    startActivity(Intent(Intent.ACTION_VIEW, uri))
                }

            } catch (e: Exception) {
                e.printStackTrace()
                showToast("Unable to open Maps.")
            }
        } else {
            showToast("Lat Long Not Available.")
        }
    }
    private fun openEditScreen(item: ViewPortFolioModel) {
        loadFragment(
            fragment = NewCustomerDealerFragment.newInstance(item),
            isAdd = false,
            isAddBackStack = true
        )
    }

    private fun showLoader() = with(binding){
        progressBarPJC.visibility = View.VISIBLE
    }
    private fun hideLoader()= with(binding) {
        progressBarPJC.visibility = View.GONE
    }

}