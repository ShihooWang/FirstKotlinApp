### kotlin-extends
稍后我会把第一个成熟的项目推到git上,该项目起始于2018-03

## 1.架构
#### kotlin + mvp + okhttp3 + rxjava2 + retrofit2 + dagger2

## 2. 2018-11-29 功能描述


#### 1.注册登录相关的就不说了

#### 2.实时显示心率数据。

    由于算法需要缓存一段时间的数据，才能计算出心率值，就需要时间去填缓冲区，这一块涉及的蓝牙4.0大数据传输问题，
    可以见我的博客：https://blog.csdn.net/hello_json/article/details/79541853

#### 3.蓝牙BLE连接通信。

    这一块有时间单独介绍，我还有做个一个BLE通信的SDK，用来和公司nordic 51822系列芯片通信。
    可见我的博客：
    https://blog.csdn.net/hello_json/article/details/74931919
    https://blog.csdn.net/hello_json/article/details/74931987
    https://blog.csdn.net/hello_json/article/details/74935636
    这两篇博客转自我的同事，但是蓝牙相关的内容，却不折不扣的是我的原创，所以我就转过来了，算是我的半个作品。

#### 4.心电图绘制。

    基于RxJava的线程调度异步通信，缓存数据，异步读取。自定义View，及保存为标准心电图png。

#### 5.心电诊断算法。

    基于硬件工程师C,C++，生成的so库，使用native去调用。后期希望能基于谷歌人工智能框架TensorFlow去实现。
    关于机器学习，抽时间我会把学习过的基于TensorFlow的项目推到GitHub上。

#### 6.报告页面。

    分页式加载，网上资料很多。

#### 7.仅用于个人学习使用！
    本项目仅供学习交流使用，不用于任何商业用途！

### 如果有帮助到你，请给点个star！
