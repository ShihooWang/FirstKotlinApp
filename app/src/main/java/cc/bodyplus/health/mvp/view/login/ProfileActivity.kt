package cc.bodyplus.health.mvp.view.login

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.net.Uri
import android.text.TextUtils
import android.view.ViewTreeObserver
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import cc.bodyplus.health.App
import cc.bodyplus.health.R
import cc.bodyplus.health.ext.showToast
import cc.bodyplus.health.mvp.contract.LoginContract
import cc.bodyplus.health.mvp.module.AvatarBean
import cc.bodyplus.health.mvp.module.SysCodeBean
import cc.bodyplus.health.mvp.module.UserBean
import cc.bodyplus.health.mvp.presenter.LoginPresenter
import cc.bodyplus.health.mvp.view.BaseActivity
import cc.bodyplus.health.utils.Config
import cc.bodyplus.health.utils.ImageUtil
import cc.bodyplus.health.utils.SharedPrefHelperUtils
import cc.bodyplus.health.utils.UIutils
import cc.bodyplus.health.widget.dialog.BottomDialog
import cc.bodyplus.health.widget.dialog.GlobalDialog
import cc.bodyplus.health.widget.ucrop.UCrop
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_profile.*
import okhttp3.ResponseBody
import java.io.File
import java.util.ArrayList
import java.util.HashMap

/**
 * Created by rui.gao on 2018-05-09.
 */
class ProfileActivity : BaseActivity() , LoginContract.View {
    override fun setAutoLoginData(userBean: ResponseBody) {

    }

    private var age: String = ""
    private var nickName: String = ""
    private var gender: String = ""
    private var femaleChecked = true

    private var mIsSoftKeyboardShowing = false //软键盘弹出
    private var dialogTranslationDistance: Float = 0f
    private val TAKE_PICTURE = 520
    private val TAKE_PHOTO_ALBUM = 521
    private var cameraUri: Uri? = null
    private var bitmap: Bitmap? = null
    internal var ageList = ArrayList<String>()
    internal var genderList = ArrayList<String>()
    private var ageDialog: BottomDialog? = null
    private var genderDialog: BottomDialog? = null
    private var mLayoutChangeListener: ViewTreeObserver.OnGlobalLayoutListener? = null
    private var picturePath: String? = null

    private var viewProfile : LinearLayout ?= null
    private val mPresenter by lazy { LoginPresenter() }

    init {
        mPresenter.attachView(this)
    }
    override fun initInject() {
        activityComponent.inject(this)
    }

    override fun layoutId(): Int = R.layout.activity_profile



    override fun initView() {
        if (!SharedPrefHelperUtils.getInstance().isPerfect.equals("1")) {
            civ_avatar.setImageDrawable(resources.getDrawable(R.drawable.ic_avatar_female))
            return
        }
        viewProfile = findViewById(R.id.view_profile)
        title_back.setOnClickListener { finish() }
        Glide.with(this).load(SharedPrefHelperUtils.getInstance().avatar)
                .asBitmap()
                .centerCrop()
                .placeholder(R.drawable.ic_avatar_female)
                .into(civ_avatar)

        tv_nick.setText(SharedPrefHelperUtils.getInstance().nickname)
        tv_age.text = SharedPrefHelperUtils.getInstance().age
        tv_nick.setSelection(SharedPrefHelperUtils.getInstance().nickname.length)

        val genderType = SharedPrefHelperUtils.getInstance().gender
        var gender = ""
        gender = if (genderType == "1") {
            "男"
        } else {
            "女"
        }
        //1为男，2为女
        tv_gender.text = (gender)

        civ_avatar.setOnClickListener { clickSelectPicture() }
        linear_age.setOnClickListener {
            if (!mIsSoftKeyboardShowing) {
                ageDialog!!.show()
                setAgeAndGenderViewUpTranslation()
            } }
        linear_gender.setOnClickListener{
            if (!mIsSoftKeyboardShowing) {
                genderDialog!!.show()
                setAgeAndGenderViewUpTranslation()
            }
        }
        linear_nick.setOnClickListener {  }

        tv_nick.setOnEditorActionListener({ v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val newName = tv_nick.text.toString().trim({ it <= ' ' })
                if (TextUtils.isEmpty(newName)) {
                    showToast("昵称不能为空")
                } else {
                    val param = HashMap<String, String>()
                    param.put("realname", newName)
                    param.put("age", SharedPrefHelperUtils.getInstance().age)
                    param.put("gender", SharedPrefHelperUtils.getInstance().gender)
                    mPresenter.requestUserInfoData(param)
                }
            }
            false
        })

        setNicknameViewTranslation()
    }

    override fun initData() {
        for (i in 18..180) {
            ageList.add("" + i)
        }
        genderList.add("男")
        genderList.add("女")

        ageDialog = BottomDialog(this, ageList, true, "年龄", 0 )

        ageDialog?.setOnDismissListener({ setAgeAndGenderViewDownTranslation() })
        genderDialog = BottomDialog(this, genderList, false, "性别", 1)
        genderDialog?.setOnDismissListener({ setAgeAndGenderViewDownTranslation() })

        ageDialog?.setOnDialogSaveClickListener { item ->
            tv_age.text = (item)
            val param = HashMap<String, String>()
            param.put("age", item)
            param.put("gender", SharedPrefHelperUtils.getInstance().gender)
            param.put("realname", SharedPrefHelperUtils.getInstance().nickname)
            mPresenter.requestUserInfoData(param)
        }
        genderDialog?.setOnDialogSaveClickListener { item ->
            tv_gender.text = (item)
            val param = HashMap<String, String>()
            if (item == "男") {
                param.put("gender", "1")
            } else {
                param.put("gender", "2")
            }
            param.put("age", SharedPrefHelperUtils.getInstance().age)
            param.put("realname", SharedPrefHelperUtils.getInstance().nickname)
            mPresenter.requestUserInfoData(param)
        }
    }

    /**
     * dialog隐藏时、AgeAndGender控件动画
     */
    @SuppressLint("ObjectAnimatorBinding")
    private fun setAgeAndGenderViewDownTranslation() {
        val showTranslationY = viewProfile!!.translationY
        val etShowAnimator = ObjectAnimator.ofFloat(viewProfile, "translationY", showTranslationY, 0f)
        etShowAnimator.duration = (100)
        etShowAnimator.start()
    }

    /**
     * 软键盘弹出和隐藏时、编辑框控件动画
     */

    @SuppressLint("ObjectAnimatorBinding")
    private fun setNicknameViewTranslation() {
        mLayoutChangeListener = ViewTreeObserver.OnGlobalLayoutListener {
            //判断窗口可见区域大小
            val r = Rect()
            window.decorView.getWindowVisibleDisplayFrame(r)
            //如果屏幕高度和Window可见区域高度差值大于整个屏幕高度的1/3，则表示软键盘显示中，否则软键盘为隐藏状态。

            val heightDifference = App.instance.PHONE_HEIGHT - (r.bottom - r.top)
            val isKeyboardShowing = heightDifference > App.instance.PHONE_HEIGHT / 3

            //如果之前软键盘状态为显示，现在为关闭，或者之前为关闭，现在为显示，则表示软键盘的状态发生了改变
            if (mIsSoftKeyboardShowing && !isKeyboardShowing || !mIsSoftKeyboardShowing && isKeyboardShowing) {
                mIsSoftKeyboardShowing = isKeyboardShowing
                var distance = 0f
                if (mIsSoftKeyboardShowing) {
                    val showTranslationY = viewProfile!!.getTranslationY()
                    val viewBottom = viewProfile!!.getBottom()
                    distance = viewBottom - r.bottom - App.instance.PHONE_HEIGHT * 5 / 100f
                    val etShowAnimator = ObjectAnimator.ofFloat(viewProfile, "translationY", showTranslationY, -distance)
                    etShowAnimator.duration = 100
                    etShowAnimator.start()

                } else {
                    val hideTranslationY = viewProfile!!.getTranslationY()
                    val etCloseAnimator = ObjectAnimator.ofFloat(viewProfile, "translationY", hideTranslationY, 0f)
                    etCloseAnimator.duration = (100)
                    etCloseAnimator.start()
                }

            }
        }
        //注册布局变化监听
        window.decorView.viewTreeObserver.addOnGlobalLayoutListener(mLayoutChangeListener)
    }

    /**
     * dialog弹出时、AgeAndGender控件动画
     */
    @SuppressLint("ObjectAnimatorBinding")
    private fun setAgeAndGenderViewUpTranslation() {
        val showTranslationY = viewProfile!!.getTranslationY()
        // 动画移动距离 =viewProfile的底部高度- dialog弹出时的屏幕空白高度，App.PHONE_HEIGHT * 5 / 100为防止title被遮挡，而设置的估计数值
        dialogTranslationDistance = viewProfile!!.bottom - (App.instance.PHONE_HEIGHT - UIutils.dip2px(285f)) - App.instance.PHONE_HEIGHT * 5 / 100f
        val etShowAnimator = ObjectAnimator.ofFloat(viewProfile, "translationY", showTranslationY, -dialogTranslationDistance)
        etShowAnimator.setDuration(100)
        etShowAnimator.start()
    }

    private fun clickSelectPicture() {
        GlobalDialog.showListDialog(this, "", arrayOf("照相", "从相册中选择"), true, object : GlobalDialog.DialogItemClickListener {
            override fun confirm(result: String) {
                if (result.equals("从相册中选择", ignoreCase = true)) {
                    ImageUtil.choosePhoto(this@ProfileActivity)
                } else {
                    cameraUri = ImageUtil.openCamera(this@ProfileActivity)
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {


        if (resultCode == RESULT_OK) {
            when (requestCode) {
                TAKE_PHOTO_ALBUM -> {
                    if(data== null){
                        return
                    }
                    val selectedUri = data?.data
                    if (selectedUri != null) {
                        ImageUtil.startCropActivity(data?.data, this@ProfileActivity)
                    } else {
                        showToast("无法获取图片")
                    }
                }
                TAKE_PICTURE -> if (cameraUri != null) {
                    ImageUtil.startCropActivity(cameraUri, this@ProfileActivity)
                } else {
                    showToast("无效的图片")
                }
                UCrop.REQUEST_CROP -> handleCropResult(data)
            }
        }

        if (resultCode == UCrop.RESULT_ERROR) {
            handleCropError(data!!)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun handleCropError(result: Intent) {
        val cropError = UCrop.getError(result)
        if (cropError != null) {
            showToast(cropError.message.toString())
        } else {
            showToast("Unexpected error")
        }
    }

    private fun handleCropResult(result: Intent?) {
        val resultUri = UCrop.getOutput(result!!)
        if (resultUri != null) {
            try {
                val file = ImageUtil.compressIMG(File(resultUri.path))
                picturePath = file.absolutePath
                updateUserInfo()
            } catch (e: Exception) {

            }

            val bitmap = BitmapFactory.decodeFile(picturePath)
            //            Uri imageUri = Uri.parse("file://" + );
            civ_avatar.setImageBitmap(bitmap)

        } else {
            showToast("无法获取剪切到的图片")
        }
    }

    private fun updateUserInfo() {
        val param = HashMap<String, String>()
        param.put("age", SharedPrefHelperUtils.getInstance().age)
        param.put("gender", SharedPrefHelperUtils.getInstance().gender)
        param.put("nickname", SharedPrefHelperUtils.getInstance().nickname)
        mPresenter.requestUserInfoFileData(param, File(picturePath))
    }

    override fun showErrorMsg(errorMsg: String) {
        showToast(errorMsg)
    }

    override fun showLoading() {
    }

    override fun dismissLoading() {
    }

    override fun setLoginData(userBean: UserBean) {
    }

    override fun setSysCodeData(sysCodeBean: SysCodeBean) {
    }

    override fun setUserInfoData(userBean: UserBean) {
        if(userBean!= null) {

            SharedPrefHelperUtils.getInstance().userId = userBean.patientId
            SharedPrefHelperUtils.getInstance().mobile = userBean.mobile
            SharedPrefHelperUtils.getInstance().isBeta = userBean.isBeta
            SharedPrefHelperUtils.getInstance().nickname = userBean.realname
            SharedPrefHelperUtils.getInstance().avatar = userBean.avatar
            SharedPrefHelperUtils.getInstance().gender = userBean.gender
            SharedPrefHelperUtils.getInstance().age = userBean.age
            SharedPrefHelperUtils.getInstance().isPerfect = userBean.isPerfect

            App.instance.execCallBack(Config.UPDATE_USER_INFO, "")
        }

    }

    override fun setUserInfoFileData(bean: AvatarBean) {
        if(bean!= null){
            SharedPrefHelperUtils.getInstance().avatar = bean.avatar
            Glide.with(this).load(SharedPrefHelperUtils.getInstance().avatar)
                    .asBitmap()
                    .centerCrop()
                    .placeholder(R.drawable.ic_avatar_female)
                    .into(civ_avatar)

            App.instance.execCallBack(Config.UPDATE_USER_INFO,"")
        }

    }

}