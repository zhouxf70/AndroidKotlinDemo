package com.example.androidkotlindemo.mvvm

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidkotlindemo.R
import com.example.androidkotlindemo.common.KLog
import com.example.androidkotlindemo.network.bean.UserInfo
import kotlinx.coroutines.*
import java.util.concurrent.FutureTask

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var viewModel: HomeViewModel

    private var tvHw: TextView? = null
    private var rv: RecyclerView? = null
    private var adapter: Adapter? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        Fragment
//        supportFragmentManager
//        ViewPager
        val futureTask = FutureTask { "hah" }
        futureTask.run()

        viewModel = ViewModelProvider(this, ViewModelFactory()).get(HomeViewModel::class.java)
        viewModel.userInfoLiveData.observe(this, Observer<State<UserInfo>> {
            if (it.state == State.SUCCESS)
                KLog.d(it.data)
            else
                KLog.d(it.err)

            KLog.d(Thread.currentThread().name)
            tvHw?.text = "success"

            adapter?.update(Array(100) { i -> "item:$i" })

            Thread {
                // 竟然可以在非UI线程改控件
                // 调用sleep方法之后才会抛异常。。。
                KLog.d(Thread.currentThread().name)
//                Thread.sleep(10)
//                tvHw.text = "success2"
            }.start()
        })

        initView()
    }

    private fun initView() {
        findViewById<Button>(R.id.bt_query_user_info).setOnClickListener(this)
        findViewById<Button>(R.id.bt_coroutine).setOnClickListener(this)

        tvHw = findViewById(R.id.tv_hw)
        rv = findViewById(R.id.rv)

        adapter = Adapter(this, Array(10) { "item:$it" })
        rv?.adapter = adapter
        rv?.layoutManager = LinearLayoutManager(this)
//        rv.hasFixedSize()
//        rv.addItemDecoration()
    }

    private var launch: Job? = null

    private fun coroutine() {
        // 要手动控制生命周期
        launch = GlobalScope.launch {
            KLog.t("coroutine")
            val doWork1 = async { doWork1("111") }
            val doWork11 = async { doWork1("111111") }
            KLog.t("wait")
            val doWork2 = doWork2(doWork1.await() + doWork11.await())
            KLog.t(doWork2)
        }
        for (i in 0..10) {
            KLog.t(i)
        }
    }

    private suspend fun doWork1(s: String): String {
        KLog.t("doWork1 start:$s")
        delay(10000)
        KLog.t("doWork1 over:$s")
        return "work1 result $s"
    }

    private suspend fun doWork2(s: String): String {
        KLog.t("doWork2 start:$s")
        delay(10000)
        KLog.t("doWork2 over:$s")
        return "work1 result $s"
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.bt_query_user_info -> viewModel.queryUserInfo("zhouxf70")
            R.id.bt_coroutine -> coroutine()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        launch?.cancel()
    }

    class Adapter(private val context: Context, private var data: Array<String>?) :
        RecyclerView.Adapter<Adapter.VH>() {

        private var countVH = 0;

        fun update(data: Array<String>) {
            this.data = data
            notifyDataSetChanged()
        }

        override fun getItemViewType(position: Int): Int {
            return position % 2
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            countVH++
            KLog.d("onCreateViewHolder:$countVH")
            val layout =
                if (viewType == 0) LayoutInflater.from(context).inflate(R.layout.item_rv, null)
                else LayoutInflater.from(context).inflate(R.layout.item_rv2, null)
            return VH(layout)
        }

        override fun getItemCount(): Int {
            KLog.d("getItemCount:${data?.size ?: 0}")
            return data?.size ?: 0
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            KLog.d("onBindViewHolder:$position")
            holder.itemTv.text = data?.get(position).toString()
        }

        class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {

            val itemTv: TextView
                get() = itemView.findViewById(R.id.item_rv_tv)

        }
    }

}
