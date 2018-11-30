package cc.bodyplus.health.mvp.view.login

import android.content.Intent
import android.text.TextUtils
import android.view.KeyEvent
import cc.bodyplus.health.R
import cc.bodyplus.health.ext.showToast
import cc.bodyplus.health.mvp.view.BaseActivity
import kotlinx.android.synthetic.main.activity_enter_name.*

/**
 * Created by rui.gao on 2018-05-09.
 */
class EnterNameActivity : BaseActivity(){
    private var text_name : String ?= ""

    override fun initInject() {
    }

    override fun layoutId(): Int = R.layout.activity_enter_name

    override fun initData() {

    }

    override fun initView() {
        tv_next.setOnClickListener {
            text_name =  et_enter_name.text.toString()
            if (TextUtils.isEmpty(text_name)) run { showToast("请输入姓名") } else {
                val intent = Intent(this, RegisterAgeActivity::class.java)
                intent.putExtra("nickName", text_name)
                startActivity(intent)
            }
        }
        title_back.setOnClickListener { finish() }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            return true
        }
        return false
    }
}