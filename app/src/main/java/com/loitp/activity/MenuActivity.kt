package com.loitp.activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import com.annotation.IsFullScreen
import com.annotation.LogTag
import com.core.base.BaseApplication
import com.core.base.BaseFontActivity
import com.core.common.Constants
import com.core.helper.gallery.albumonly.GalleryCorePhotosOnlyFrm
import com.core.utilities.*
import com.google.android.material.tabs.TabLayout
import com.loitp.R
import com.loitp.model.Flickr
import kotlinx.android.synthetic.main.activity_menu.*

@LogTag("loitppMenuActivity")
@IsFullScreen(false)
class MenuActivity : BaseFontActivity() {

    companion object {
        const val KEY_LAST_PAGE = "KEY_LAST_PAGE"
    }

    private val listFlickr = ArrayList<Flickr>()
    private var currentPage = 0

    override fun setLayoutResourceId(): Int {
        return R.layout.activity_menu
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupViews()
    }

    public override fun onPause() {
        super.onPause()
        adView.pause()
    }

    public override fun onResume() {
        super.onResume()
        adView.resume()
    }

    public override fun onDestroy() {
        super.onDestroy()
        adView.destroy()
    }

    private fun setupViews() {

        //setup data
        listFlickr.add(Flickr("Hài hước", Constants.FLICKR_ID_VN_HAIHUOC))
        listFlickr.add(Flickr("Độc đáo thú vị", Constants.FLICKR_ID_VN_DOCDAOTHUVI))
        listFlickr.add(Flickr("Troll", Constants.FLICKR_ID_VN_TROLL))
        listFlickr.add(Flickr("Truyện bựa", Constants.FLICKR_ID_VN_TRUYENBUA))
        listFlickr.add(Flickr("Truyện ngắn", Constants.FLICKR_ID_VN_TRUYENNGAN))
        listFlickr.add(Flickr("Tuổi thơ dữ dội", Constants.FLICKR_ID_VN_TUOITHODUDOI))
        listFlickr.add(Flickr("Ảnh chế", Constants.FLICKR_ID_VN_ANHCHESACHGIAOKHOA))
        listFlickr.add(Flickr("Ảnh theo tên", Constants.FLICKR_ID_VN_ANHTHEOTEN))
        listFlickr.add(Flickr("Màn hình", Constants.FLICKR_ID_VN_FUNNYMANHINH))
        listFlickr.add(Flickr("Funny thể thao", Constants.FLICKR_ID_VN_FUNNYTHETHAO))
        listFlickr.add(Flickr("Funny manga", Constants.FLICKR_ID_VN_FUNNYMANGA))
        listFlickr.add(Flickr("Status vui", Constants.FLICKR_ID_VN_STTVUI))
        listFlickr.add(Flickr("Status đểu chất", Constants.FLICKR_ID_VN_STTDEUCHAT))
        listFlickr.add(Flickr("Cosplay", Constants.FLICKR_ID_COSPLAY))
        listFlickr.add(Flickr("Hại não", Constants.FLICKR_ID_HAINAO))
        listFlickr.add(Flickr("Bạn có biết", Constants.FLICKR_ID_VN_BANCOBIET))
        listFlickr.add(Flickr("Cung hoàng đạo", Constants.FLICKR_ID_VN_CUNGHOANGDAOFUNTFACT))
        listFlickr.add(Flickr("Hehehoro", Constants.FLICKR_ID_VN_CUNGHOANGDAOHEHEHORO))
        listFlickr.add(Flickr("Devvui", Constants.FLICKR_ID_VN_DEVVUI))

        listFlickr.forEach {
            tabLayout.addTab(tabLayout.newTab().setText(it.title))
        }
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
//                logD("onTabSelected " + tabLayout.selectedTabPosition)
                showFragment()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })

        currentPage = LSharedPrefsUtil.instance.getInt(KEY_LAST_PAGE, 0)
        if (currentPage == 0) {
            showFragment()
        } else {
            tabLayout.post {
                tabLayout.getTabAt(currentPage)?.select()
            }
        }

        LUIUtil.createAdBanner(adView = adView)
    }

    override fun onBackPressed() {
        LSharedPrefsUtil.instance.putInt(KEY_LAST_PAGE, tabLayout.selectedTabPosition)
        showBottomSheetOptionFragment(
                isCancelableFragment = true,
                isShowIvClose = true,
                title = getString(R.string.app_name),
                message = getString(R.string.do_you_want_to_exit),
                textButton1 = getString(R.string.share_app_en),
                textButton2 = getString(R.string.rate_app_en),
                textButton3 = getString(R.string.exit),
                onClickButton1 = {
                    LSocialUtil.shareApp(this)
                },
                onClickButton2 = {
                    LSocialUtil.moreApp(this)
                },
                onClickButton3 = {
                    finish()
                    LActivityUtil.tranOut(this)
                },
                onDismiss = {
                    //do nothing
                }
        )
    }

    private val mHandlerScroll = Handler(Looper.getMainLooper())
    private fun showFragment() {
        val flickr = listFlickr[tabLayout.selectedTabPosition]
        logD("showFragment flickr " + BaseApplication.gson.toJson(flickr))

        val frm = GalleryCorePhotosOnlyFrm(
                onTop = {
                    logD("onTop")
                },
                onBottom = {
                    logD("onBottom")

                },
                onScrolled = { isScrollDown ->
                    logD("onScrolled isScrollDown $isScrollDown")
                    mHandlerScroll.removeCallbacksAndMessages(null)
                    mHandlerScroll.postDelayed({
                        tabLayout?.let { tl ->
                            if (isScrollDown) {
                                LScreenUtil.toggleFullscreen(activity = this, isFullScreen = true)
                                tl.visibility = View.GONE
                            } else {
                                LScreenUtil.toggleFullscreen(activity = this, isFullScreen = false)
                                tl.visibility = View.VISIBLE
                            }
                        }
                    }, 300)
                }
        )
        val bundle = Bundle()
        bundle.putString(Constants.SK_PHOTOSET_ID, flickr.flickrId)
        frm.arguments = bundle
        LScreenUtil.addFragment(
                activity = this,
                containerFrameLayoutIdRes = R.id.flContainer,
                fragment = frm,
                isAddToBackStack = false
        )
    }

}
