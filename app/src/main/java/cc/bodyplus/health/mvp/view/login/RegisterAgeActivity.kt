package cc.bodyplus.health.mvp.view.login

import android.content.Intent
import cc.bodyplus.health.R
import cc.bodyplus.health.mvp.view.BaseActivity
import cc.bodyplus.health.widget.wheelview.WheelView
import cc.bodyplus.health.widget.wheelview.adapter.MyWheelAdapter
import kotlinx.android.synthetic.main.activity_enter_name.*
import java.util.ArrayList

/**
 * Created by rui.gao on 2018-05-09.
 */
class RegisterAgeActivity : BaseActivity(){
    var nickName : String = ""

    override fun layoutId(): Int = R.layout.activity_register_age

    override fun initData() {
    }

    override fun initView() {
        nickName = intent.getStringExtra("nickName")

        var wheelView: WheelView<String> ?= null
        wheelView = findViewById(R.id.view_wheel)
//        view_wheel.setWheelAdapter(MyWheelAdapter(this))
        wheelView.setWheelAdapter(MyWheelAdapter(this))
        wheelView.skin = WheelView.Skin.Holo
        wheelView.setWheelSize(5)
        wheelView.setWheelData(createArrays())
        wheelView.selection = 32
        val style = WheelView.WheelViewStyle()

        style.holoBorderColor = resources.getColor(R.color.color_divider)
        style.holoBorderWidth = 1
        style.textSize = 30
        style.textColor = resources.getColor(R.color.color_text_1)
        wheelView.style = style

        tv_next.setOnClickListener {
            val age = wheelView.selectionItem as String
            val intent = Intent(this, RegisterGenderActivity::class.java)
            intent.putExtra("age", age)
            intent.putExtra("nickName", nickName)
            startActivity(intent)
        }
        title_back.setOnClickListener { finish() }
    }

    override fun initInject() {
    }

    private fun createArrays(): ArrayList<String> {
        val heightOptionsItems = (18..179).mapTo(ArrayList<String>()) { "" + it }
        return heightOptionsItems
    }
}