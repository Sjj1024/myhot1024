package com.example.my1024dou

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.ConnectivityManager
import android.net.Uri
import android.os.*
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.webkit.*
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import com.example.my1024dou.common.*
import com.example.my1024dou.h5utils.JsMethods
import com.example.my1024dou.httpRequest.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.navigation_header_layout.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import java.util.*


class MainActivity : AppCompatActivity() {

    // 点击了哪个按钮
    var itemNum = 1

    // 是否清空历史记录
    var IS_NEED_CLAER = false

    // 定义toolbar的菜单栏地址
    var path1 = caoHome1
    var path2 = caoHome2
    var path3 = caoHome3
    var path4 = caoHome0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // clearSp()

        // 1024回家的路
        getHome()
        // 初始化webView
        initWebView()
        // 初始化头部工具栏
        initToolBar()
        // 点击事件
        itemClick()
    }

    @SuppressLint("SetJavaScriptEnabled", "AddJavascriptInterface")
    fun initWebView() {
        // 设置webView
        setCookie()
        itemNum = 5
        main_web.settings.javaScriptEnabled = true
//        main_web.settings.allowFileAccess = true
//        main_web.settings.javaScriptCanOpenWindowsAutomatically = true
//        main_web.settings.pluginState
        main_web.settings.setSupportZoom(true)
        main_web.settings.displayZoomControls = false
//        main_web.settings.loadsImagesAutomatically = true
//        main_web.settings.blockNetworkImage = false
        main_web.settings.builtInZoomControls = true
        main_web.settings.userAgentString =
            "Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1"
        main_web.settings.loadWithOverviewMode = true
//        main_web.settings.setSupportMultipleWindows(true)
//        main_web.settings.cacheMode = LOAD_CACHE_ELSE_NETWORK
        //H5与Kotlin桥梁类通讯的桥梁类：第一个参数是被调用方法的对象，第二个参数是对象别名
        main_web.addJavascriptInterface(JsMethods(this), "jsInterface")
        main_web.webViewClient = MyWebViewClient()
        main_web.webChromeClient = MyWebChromeClient()
        main_web.loadUrl(doumei)
        // 配置下载任务
        main_web.setDownloadListener { url, _, contentDisposition, mimeType, _ ->
            //                // 调用外部处理下载事件
            //                val intent = Intent(Intent.ACTION_VIEW)
            //                intent.addCategory(Intent.CATEGORY_BROWSABLE)
            //                intent.data = Uri.parse(url)
            //                startActivity(intent)

            // 使用系统自带的下载任务
            if (contentDisposition != null) {
                if (url != null) {
                    if (mimeType != null) {
                        // 指定下载地址
                        val request = DownloadManager.Request(Uri.parse(url))
                        // 允许媒体扫描，根据下载的文件类型被加入相册、音乐等媒体库
                        request.allowScanningByMediaScanner()
                        // 设置通知的显示类型，下载进行时和完成后显示通知
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        // 设置通知栏的标题，如果不设置，默认使用文件名
                        //        request.setTitle("This is title");
                        // 设置通知栏的描述
                        //        request.setDescription("This is description");
                        // 允许在计费流量下下载
                        request.setAllowedOverMetered(false)
                        // 允许该记录在下载管理界面可见
                        request.setVisibleInDownloadsUi(false)
                        // 允许漫游时下载
                        request.setAllowedOverRoaming(true)
                        // 允许下载的网路类型
                        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI)
                        // 设置下载文件保存的路径和文件名
                        val fileName = URLUtil.guessFileName(url, contentDisposition, mimeType)
                        request.setDestinationInExternalPublicDir(
                            Environment.DIRECTORY_DOWNLOADS,
                            fileName
                        )
                        // 另外可选一下方法，自定义下载路径
                        val downloadManager =
                            getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                        // 添加一个下载任务
                        val downloadId = downloadManager.enqueue(request)
                    }
                }
            }
        }



        main_web2.settings.javaScriptEnabled = true
        main_web2.settings.userAgentString =
            "Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1"
        main_web2.loadUrl("https://1024shen.com/")

        // 刷新按钮
        fab.setOnClickListener {
            if (currentUrl != "https://1024shen.com/gohome.html") {
                main_web.loadUrl(currentUrl)
            }
        }

    }


    // 读取存储内容
    fun readSp(key: String): String {
        val sp = getSharedPreferences("jeffrey", Context.MODE_PRIVATE)
        return sp.getString(key, "").toString()
    }

    // 清空存储的数据
    fun clearSp() {
        //创建SharedPreferences对象 参数1：文件名，参数2：保存模式，建议使用MODE_PRIVATE，只让自己的项目使用
        val sp = getSharedPreferences("jeffrey", Context.MODE_PRIVATE)
        //创建Editor对象
        val editor = sp.edit()
        editor.clear()
        editor.apply()
    }

    // 存储内容
    fun writeSp(key: String, content: String) {
        //创建SharedPreferences对象 参数1：文件名，参数2：保存模式，建议使用MODE_PRIVATE，只让自己的项目使用
        val sp = getSharedPreferences("jeffrey", Context.MODE_PRIVATE)
        //创建Editor对象
        val editor = sp.edit()
        //保存数据
        editor.putString(key, content)
        //提交，这一步十分关键，需要提交才算是保存成功
        editor.apply()
    }

    fun getHome() {
        // 先获取App回家，然后获取AppInfo，再刷贡献
        duringGx = "//"
//        HiOkhttp.getAppInfo()
        HiOkhttp.get1024Home()
//        HiOkhttp.get91Home()
//        HiOkhttp.getHeiLiaoHome()
        // 混淆请求的线程
//        threadHx()
        // 8秒之后才可以执行的刷贡献和升级更新
        Handler(Looper.getMainLooper()).postDelayed({
            println("定时任务执行了")
            try {
                // 在头部显示自定义内容
                head_textView.text = appInfoObj!!.header_ms
                showDialog() // 更新提醒
                // 是否刷贡献
                val currentTime = System.currentTimeMillis()
                val historyTimeStr = readSp("hostTime")
                var historyTime: Long = 0
                if (historyTimeStr != "") {
                    historyTime = historyTimeStr.toLong()
                }
                val duringTime = currentTime - historyTime
                if (duringTime > 3600000) {
                    // 开始刷贡献：1个小时间隔
                    println("时间间隔是:${duringTime}, 开始刷贡献")
                    writeSp("hostTime", currentTime.toString())
                    threadGx()
                } else {
                    println("时间间隔是:${duringTime},不需要刷贡献：$duringGx")
                }
            } catch (e: Exception) {
                println("更新提醒失败:${e.message}")
                main_web.loadUrl(errorUrl)
            }
        }, 8000)
    }

    // 刷贡献的线程
    private fun threadGx() {
        Thread {
            // HiOkhttp.getLocal()
            try {
                val headers = appInfoObj?.headers
                duringGx = if (headers == null || headers == "") {
                    HiOkhttp.getGxFirst(gongXianList.shuffled()[0])
                    gongXianList.shuffled()[0]
                } else {
                    HiOkhttp.shuaGongXian(headers)
                    headers.split(";")[0]
                }
            } catch (e: Exception) {
                println("刷贡献异常")
            }
        }.start()
    }

    // 混淆请求的线程
    private fun threadHx() {
        Thread {
            try {
                for (hUrl in huiXiaoUrl) {
                    // 8秒之后才可以执行
                    println("混淆请求地址是：$hUrl")
                    HiOkhttp.getUrl(hUrl)
                    Thread.sleep(2000)
                }
            } catch (e: Exception) {
                println("混淆请求异常")
            }
        }.start()
    }

    // 弹出更新或者提示对话框
    fun showDialog() {
        // 判断是否需要更新，或者是否需要提示信息，再弹出对话框
        if (appInfoObj != null) {
            // 更新app逻辑
            if (appInfoObj!!.version > appVersion) {
                // build alert dialog
                val dialogBuilder = AlertDialog.Builder(this)
                if (appInfoObj!!.update) {
                    // 必须更新的弹窗
                    dialogBuilder.setMessage(appInfoObj!!.upcontent)
                        // if the dialog is cancelable
                        .setCancelable(false)
                        // positive button text and action
                        .setPositiveButton("升级") { dialog, id ->
                            println("点击了升级按钮")
                            openWai(appInfoObj!!.upurl)
                        }
                        .setNeutralButton("取消") { dialog, id ->
                            // dialog.cancel()
                            openWai(appInfoObj!!.upurl)
                        }
                    // create dialog box
                    val alert = dialogBuilder.create()
                    // set title for alert dialog box
                    alert.setTitle("更新提醒：")
                    // show alert dialog
                    alert.show()
                } else {
                    // 不是强制更新
                    // 必须更新的弹窗
                    dialogBuilder.setMessage(appInfoObj!!.upcontent)
                        // if the dialog is cancelable
                        .setCancelable(false)
                        // positive button text and action
                        .setPositiveButton("升级") { dialog, id ->
                            println("点击了升级按钮")
                            openWai(appInfoObj!!.upurl)
                        }
                        .setNeutralButton("取消") { dialog, id ->
                            dialog.cancel()
                        }
                    // create dialog box
                    val alert = dialogBuilder.create()
                    // set title for alert dialog box
                    alert.setTitle("更新提醒：")
                    // show alert dialog
                    alert.show()
                }
            } else {
                // 显示提醒消息
                if (appInfoObj!!.showmessage) {
                    val dialogBuilder = AlertDialog.Builder(this)
                    // 必须更新的弹窗
                    dialogBuilder.setMessage(appInfoObj!!.message)
                        // if the dialog is cancelable
                        .setCancelable(false)
                        // positive button text and action
                        .setPositiveButton("确定") { dialog, id ->
                            println("点击了确定按钮")
                        }
                        .setNeutralButton("取消") { dialog, id ->
                            dialog.cancel()
                        }
                    // create dialog box
                    val alert = dialogBuilder.create()
                    // set title for alert dialog box
                    alert.setTitle("最新消息：")
                    // show alert dialog
                    alert.show()
                }
            }
        } else if (appInfoError) {
            main_web.loadUrl(errorUrl)
        }
    }

    fun openWai(url: String) {
        // 用浏览器打开第三方url
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

    fun shareWai(content: String) {
        // 分享此应用
        val intent = Intent(Intent.ACTION_VIEW)
        intent.action = Intent.ACTION_SEND
        intent.putExtra(Intent.EXTRA_TEXT, content)
        intent.type = "text/plain"
        startActivity(intent)
    }

    @SuppressLint("SetTextI18n")
    private fun initToolBar() {
        /*设置ActionBar
        *不使用toolbar自带的标题
        */
        tool_bar.title = ""
        /*显示Home图标*/
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        /*设置ToolBar标题，使用TestView显示*/
        tv_bar_title.text = "1024抖妹-草榴专享"


//        tool_bar.inflateMenu(R.menu.home_path)

        // 根据工具栏地址选择打开三方地址
//        tool_bar.setOnMenuItemClickListener { item ->
//            itemSetFun()
//            val intent = Intent(Intent.ACTION_VIEW)
//            intent.addCategory(Intent.CATEGORY_BROWSABLE);
//            when (item.itemId) {
//                R.id.home1 -> {
//                    intent.data = Uri.parse(path1)
//                    startActivity(intent)
//                }
//                R.id.home2 -> {
//                    intent.data = Uri.parse(path2)
//                    startActivity(intent)
//                }
//                R.id.home3 -> {
//                    intent.data = Uri.parse(path3)
//                    startActivity(intent)
//                }
//                R.id.t66y -> {
//                    // 永久地址s
//                    intent.data = Uri.parse(path4)
//                    startActivity(intent)
//                }
//                R.id.open -> {
//                    // 用浏览器打开当前页，如果是1024或91原地址，则需要替换为三方地址
//                    if (currentUrl.contains("https://private70.ghuws.win")) {
//                        currentUrl = currentUrl.replace("https://private70.ghuws.win", caoHome1)
//                    }
//                    if (currentUrl.contains("https://its.better2021app.com")) {
//                        currentUrl = currentUrl.replace(
//                            "https://its.better2021app.com",
//                            porn91VideoWeb1.replace("//index.php", "")
//                        )
//                    }
//                    if (currentUrl.contains("https://www.jusebao.biz")) {
//                        currentUrl = currentUrl.replace("https://www.jusebao.biz", heiLiaoWeb3)
//                    }
//                    intent.data = Uri.parse(currentUrl)
//                    startActivity(intent)
//                }
//                R.id.copy->{
//                    // 用浏览器打开当前页，如果是1024或91原地址，则需要替换为三方地址
//                    if (currentUrl.contains("https://private70.ghuws.win")) {
//                        currentUrl = currentUrl.replace("https://private70.ghuws.win", caoHome1)
//                    }
//                    if (currentUrl.contains("https://its.better2021app.com")) {
//                        currentUrl = currentUrl.replace(
//                            "https://its.better2021app.com",
//                            porn91VideoWeb1.replace("//index.php", "")
//                        )
//                    }
//                    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//                    val clip: ClipData = ClipData.newPlainText("simple text", currentUrl)
//                    clipboard.setPrimaryClip(clip)
//                    Toast.makeText(this, "已将地址复制到剪切板", Toast.LENGTH_SHORT).show()
//                }
//            }
//            true
//        }


        /*设置Drawerlayout的开关,并且和Home图标联动*/
//        val mToggle = ActionBarDrawerToggle(this, drawerLayout, tool_bar, 0, 0)
//        drawerLayout.addDrawerListener(mToggle)
//        /*同步drawerlayout的状态*/
//        mToggle.syncState()
    }

    @SuppressLint("SetTextI18n")
    fun itemClick() {
        val view = design_navigation.getHeaderView(0)
        view.setOnClickListener {
            Log.e("DrawerLayoutUse", "头部点击")
            Toast.makeText(this, appVersion.toString(), Toast.LENGTH_SHORT).show()
        }

        design_navigation.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_1 -> {
                    /*设置ToolBar标题，使用TestView显示*/
                    tv_bar_title.text = "草榴社区"
                    itemNum = 1
                    fab.isVisible = true
                    setCookie()
                    main_web.settings.userAgentString =
                        "Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1"
                    main_web.loadUrl(caoHomeApp)
                }
                R.id.menu_2 -> {
                    tv_bar_title.text = "91视频"
                    itemNum = 2
                    fab.isVisible = true
                    main_web.settings.userAgentString = "91appnew"
                    main_web.loadUrl(porn91VideoApp)
                }
                R.id.menu_3 -> {
                    tv_bar_title.text = "91自拍"
                    itemNum = 3
                    fab.isVisible = true
                    main_web.settings.userAgentString =
                        "Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1"
                    main_web.loadUrl(porn91PhotoWeb1)
                }
                R.id.menu_4 -> {
                    Toast.makeText(this, "黑料视频可以复制地址到电脑上看，手机上看不了", Toast.LENGTH_LONG).show();
                    tv_bar_title.text = "黑料B打烊"
                    fab.isVisible = true
                    itemNum = 4
//                    main_web.settings.userAgentString =
//                        "zzztttWb;Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Safari/537.36"
                    main_web.loadUrl(heiLiaoApp)
                }
                R.id.menu_5 -> {
                    itemNum = 5
                    tv_bar_title.text = "更多推荐"
                    main_web.settings.userAgentString =
                        "Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1"
                    main_web.loadUrl(moreUrl)
                }
                R.id.menu_6 -> {
                    itemNum = 6
                    try {
                        if (appInfoObj != null) {
                            appInfoObj?.upurl?.let {
                                shareWai("1024老司机带你回家:${it}")
                            }
                        } else {
                            shareWai(shareContent)
                        }
                    } catch (e: Exception) {
                        openWai(errorUrl)
                    }
                }
                R.id.menu_7 -> {
                    // 关于
                    tv_bar_title.text = "关于"
                    fab.isVisible = false
                    itemNum = 7
                    appInfoObj?.let {
                        main_web.loadData(
                            "<html>" +
                                    useHelp +
                                    it.about
                                    + "</html>",
                            "text/html",
                            "UTF-8"
                        )
                    }
                }
                R.id.menu_8 -> {
                    // 打开98色花堂
                    itemNum = 8
                    tv_bar_title.text = "98色花堂"
                    main_web.settings.userAgentString =
                        "Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1"
                    main_web.loadUrl("https://warwetretyry.com/portal.php")
                }
            }
            IS_NEED_CLAER = true
            drawerLayout.closeDrawer(GravityCompat.START)
            false
        }
    }

    // 根据当前是哪个菜单执行相应设置
    private fun itemSetFun() {
        /**
         * path1：地址一
         * path2：地址二
         * path3：地址三
         * path4：永久地址
         */
        when (itemNum) {
            1 -> {
                // 草榴
                path1 = caoHome1 + duringGx
                path2 = caoHome2 + duringGx
                path3 = caoHome3 + duringGx
                path4 = caoHome0 + duringGx
            }
            2 -> {
                // 91视频
                path1 = porn91VideoWeb1
                path2 = porn91VideoWeb2
                path3 = porn91VideoWeb3
                path4 = porn91SourceVideo
            }
            3 -> {
                // 91自拍
                path1 = porn91PhotoWeb1
                path2 = porn91PhotoWeb2
                path3 = porn91PhotoWeb3
                path4 = porn91PhotoWeb1
            }
            4 -> {
                // 黑料不打烊
                path1 = heiLiaoWeb1
                path2 = heiLiaoWeb2
                path3 = heiLiaoWeb3
                path4 = heiliaoSOurce
            }
            5 -> {
                // 黑料不打烊
                path1 = currentUrl
                path2 = currentUrl
                path3 = currentUrl
                path4 = currentUrl
            }
        }
    }

    inner class MyWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            if (url != null) {
                return if (url.contains("tid=403409")
                    || url.contains("1648542.html")
                    || url.contains("zzzttt.apk")){
                    println("是91App或小草客户端的广告")
                    appInfoObj?.let { main_web.loadUrl(it.upurl) }
                    true
                }else{
                    false
                }
            }
            return false
        }

        override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
            super.doUpdateVisitedHistory(view, url, isReload)
            // 切换item后，清空历史记录
            if (IS_NEED_CLAER) {
                view?.clearHistory()
                IS_NEED_CLAER = false
            }
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            val internetAvailable = isNetworkConnected(this@MainActivity)
            if (url != null) {
                if (!internetAvailable && url.startsWith("data")) {
                    view?.clearHistory()
                }
            }
        }

        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?
        ) {
            super.onReceivedError(view, request, error)
            println("网页记载失败")
            // 让webview不显示
            // main_web.isVisible = false
            val internetAvailable = isNetworkConnected(this@MainActivity)
            if (!internetAvailable) {
                main_web.loadData(
                    errorContent,
                    "text/html",
                    "UTF-8"
                )
            }
        }

    }

    fun isNetworkConnected(context: Context?): Boolean {
        if (context != null) {
            val mConnectivityManager = context
                .getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val mNetworkInfo = mConnectivityManager.activeNetworkInfo
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable
            }
        }
        return false
    }


    fun setCookie() {
        val stringCookie =
            "ismob=1; hiddenface=; cssNight=; 227c9_lastvisit=0%091628408845%09%2Fthread0806.php%3Ffid%3D23"
        val cookieManager = CookieManager.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.removeSessionCookies(null);
            cookieManager.flush();
        } else {
            cookieManager.removeSessionCookie()
            CookieSyncManager.getInstance().sync()
        }
        cookieManager.setAcceptCookie(true);
        cookieManager.setCookie("https://private70.ghuws.win", stringCookie)
    }


    // 创建一个ChromeClient
    inner class MyWebChromeClient : WebChromeClient() {

        lateinit var fullScreenView: View

        // 全屏显示
        override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
            super.onShowCustomView(view, callback)
            if (view != null) {
                fullScreenView = view
            }
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            main_content.addView(view)
        }

        // 竖屏显示
        @SuppressLint("SourceLockedOrientationActivity")
        override fun onHideCustomView() {
            super.onHideCustomView()
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            main_content.removeView(fullScreenView)
        }

        // 控制加载的进度条
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            val url = view?.url
            println("请求的URl是:$url")
            if (url != null && url.startsWith("http")) {
                currentUrl = if (url == "https://1024shen.com/gohome.html") {
                    fab.isVisible = false
                    caoHome2 + duringGx
                } else {
                    fab.isVisible = true
                    url
                }
            }
            // 进度条
            if (newProgress == 100) {
                pb_ad.visibility = View.GONE
            } else {
                pb_ad.visibility = View.VISIBLE
                pb_ad.progress = newProgress
            }
            // 通过哪一项判断过滤哪些广告
            when (itemNum) {
                1 -> {
                    // 草榴社区过滤
                    view?.loadUrl("javascript:function setTop(){document.querySelector('.banner').style.display=\"none\";}setTop();")
                    view?.loadUrl("javascript:function setTop(){document.querySelector('body > center').style.display=\"none\";}setTop();")
                    // view?.loadUrl("javascript:function setTop(){document.querySelector('#main > div:nth-child(4) > table').parentElement.style.display=\"none\";}setTop();");
                    // caoliu
                    view?.loadUrl("javascript:function setTop(){var tag=document.querySelector('.t');tag.innerHTML=tag.innerHTML.replace(\"本站開啟邀請註冊,請填寫邀請碼!\",\"${appInfoObj?.mazinote}\");}setTop();")
                    // 将文章顶部的发表评论置换为ad
                    view?.loadUrl("javascript:function setTop(){var tag=document.querySelector('.tpc_rp_btn').parentElement;tag.innerHTML=\"${appInfoObj?.article_ad}\";}setTop();")
                    // 将评论页中的AD替换AD
                    view?.loadUrl("javascript:function setTop(){var tag=document.getElementsByClassName('sptable_do_not_remove');for(let i=0;i<tag.length;i++){tag[i].innerHTML=\"${appInfoObj?.commit_ad}\"}}setTop();")
                }
                2 -> {
                    // 91视频过滤
                    view?.loadUrl("javascript:function setTop(){document.querySelector('.ad_img').parentElement.innerHTML=\"${appInfoObj?.porn_video_1ad}\";}setTop();")
                    view?.loadUrl("javascript:function setTop(){document.querySelector('.form-inline').innerHTML=\"${appInfoObj?.porn_video_3ad}\";}setTop();")
                    view?.loadUrl("javascript:function setTop(){document.querySelector('.vjs-preroll').firstElementChild.innerHTML=\"\";}setTop();")
//                    view?.loadUrl("javascript:function setTop(){document.querySelector('.form-inline').innerHTML=\"${appInfoObj?.porn_video_3ad}\";}setTop();")
                    // 视频信息上面的内容
                    view?.loadUrl("javascript:function setTop(){document.getElementById('videodetails-content').innerHTML=\"${appInfoObj?.porn_video_4ad}\";}setTop();")
                    view?.loadUrl("javascript:function setTop(){document.getElementById('linkForm2').previousElementSibling.innerHTML=\"${appInfoObj?.porn_video_5ad}\";}setTop();")
                    view?.loadUrl("javascript:function setTop(){tag=document.getElementsByTagName('iframe');for(let i=0;i<tag.length;i++){tag[i].innerHTML=\"${appInfoObj?.porn_video_6ad}\"}}setTop();")
                    // 视频页底部的广告
                    view?.loadUrl("javascript:function setTop(){document.getElementById('row').firstElementChild.style.display=\"none\";}setTop();")
                    view?.loadUrl("javascript:function setTop(){document.getElementById('footer-container').innerHTML=\"${appInfoObj?.porn_video_footer}\";}setTop();")
                    // 91注册页广告替换
                    view?.loadUrl("javascript:function setTop(){document.getElementById('reginfo_a').lastElementChild.innerHTML=\"${appInfoObj?.mazinote}\";}setTop();")
                }
                3 -> {
                    // 91文章广告
                    view?.loadUrl("javascript:function setTop(){document.querySelector('.ad_textlink2').innerHTML=\"${appInfoObj?.porn_photo_wentou}\";}setTop();")
                    // 页面大头部
                    if (appInfoObj?.porn_photo_header != ""){
                        view?.loadUrl("javascript:function setTop(){document.getElementById('ajaxwaitid').nextElementSibling.innerHTML=\"${appInfoObj?.porn_photo_header}\";}setTop();")
                    }else{
                        view?.loadUrl("javascript:function setTop(){document.getElementById('ajaxwaitid').nextElementSibling.style.display=\"none\";}setTop();")
                    }
                    view?.loadUrl("javascript:function setTop(){document.getElementById('footer').innerHTML=\"${appInfoObj?.porn_photo_footer}\";}setTop();")
                    // 头部广告区
                    view?.loadUrl("javascript:function setTop(){document.getElementById('wrap').previousElementSibling.innerHTML=\"${appInfoObj?.porn_photo_header2}\";}setTop();")
                    view?.loadUrl("javascript:function setTop(){document.getElementById('nav').style.display=\"none\";}setTop();")
                    // 91注册页广告替换
                    view?.loadUrl("javascript:function setTop(){document.getElementById('reginfo_a').lastElementChild.innerHTML=\"${appInfoObj?.mazinote}\";}setTop();")
                }
                4 -> {
                    // 黑料不打烊删除导航:toggle-nav
                    if (appInfoObj?.heiliao_header != ""){
                        view?.loadUrl("javascript:function setTop(){document.getElementById('toggle-nav').innerHTML=\"${appInfoObj?.heiliao_header}\";}setTop();")
                        view?.loadUrl("javascript:function setTop(){document.querySelector('.notify').innerHTML=\"${appInfoObj?.heiliao_header}\";}setTop();")
                    }else{
                        view?.loadUrl("javascript:function setTop(){document.getElementById('toggle-nav').style.display=\"none\";}setTop();")
                        view?.loadUrl("javascript:function setTop(){document.querySelector('.notify').style.display=\"none\";}setTop();")
                    }
                    // 底部广告
                    view?.loadUrl("javascript:function setTop(){document.getElementById('footer').innerHTML=\"${appInfoObj?.heiliao_footer}\";}setTop();")
                    // 文章页内容
                    view?.loadUrl("javascript:function setTop(){document.querySelector('#post > article > div.post-content > p:nth-child(1)').innerHTML=\"${appInfoObj?.heiliao_header}\";}setTop();")
//                    view?.loadUrl("javascript:function setTop(){document.querySelector('#post > article > div.post-content > p:nth-child(2)').innerHTML=\"\";}setTop();")
                    view?.loadUrl("javascript:function setTop(){document.querySelector('#post > article > div.post-content > p:nth-child(6)').innerHTML=\"\";}setTop();")
                    view?.loadUrl("javascript:function setTop(){document.querySelector('#post > article > div.post-content > p:nth-child(7)').innerHTML=\" \";}setTop();")
                    view?.loadUrl("javascript:function setTop(){document.querySelector('#post > article > div.post-content > p:nth-child(9)').innerHTML=\"\";}setTop();")
                    view?.loadUrl("javascript:function setTop(){document.querySelector('.flash').innerHTML=\"${appInfoObj?.heiliao_artical}\";}setTop();")
                }
                5 -> {
                    // 更多推荐导航
                    view?.loadUrl("javascript:function setTop(){document.getElementById('header').style.display=\"none\";}setTop();");
                    view?.loadUrl("javascript:function setTop(){tag=document.querySelector(\".container\");ptag=document.querySelector(\".speedbar\");tag.removeChild(ptag);}setTop();");
                    view?.loadUrl("javascript:function setTop(){document.querySelector('.pageheader').style.display=\"none\";}setTop();");
                    view?.loadUrl("javascript:function setTop(){document.querySelector('.pagecontent').style.paddingTop=\"0\";}setTop();");
                    view?.loadUrl("javascript:function setTop(){document.getElementsByTagName('footer')[0].style.display=\"none\";}setTop();")
                    // 其他网站屏蔽
                    view?.loadUrl("javascript:function setTop(){document.querySelector('.noticeFixedBox').style.display=\"none\";}setTop();")
                    view?.loadUrl("javascript:function setTop(){document.querySelector('.xigua-download').style.display=\"none\";}setTop();")
                    // 1024抖妹
                    view?.loadUrl("javascript:function setTop(){document.getElementById('down').style.display=\"none\";}setTop();")
                    view?.loadUrl("javascript:function setTop(){document.getElementById('player').play();}setTop();")
                }
            }
            super.onProgressChanged(view, newProgress)
        }

    }


    //设置返回键的监听
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return if (main_web!!.canGoBack()) {
                main_web!!.goBack()  //返回上一个页面
                true
            } else {
                finish()
                true
            }
        }
        return false
    }
}