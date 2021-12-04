package com.example.my1024dou.httpRequest

import android.util.Base64
import com.example.my1024dou.common.*
import com.example.my1024dou.common.AppInfo
import com.google.gson.Gson
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException
import java.lang.Exception
import java.util.concurrent.TimeUnit

object HiOkhttp {

    private val client: OkHttpClient
    // 刷贡献专用client
    private val gxClient: OkHttpClient

    private val gson = Gson()

    init {
        // 可以打印网络请求日志的拦截器
        val httpLoggingInter = HttpLoggingInterceptor()
        httpLoggingInter.setLevel(HttpLoggingInterceptor.Level.BODY)
        client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
//            .addInterceptor(httpLoggingInter)
            .build()
        // 创建刷贡献的okhttp客户端
        val cookie = object : CookieJar {
            private val map = HashMap<String, MutableList<Cookie>>()
            override fun loadForRequest(url: HttpUrl): MutableList<Cookie> {
                return map[url.host] ?: ArrayList<Cookie>()
            }

            override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                map[url.host] = cookies as MutableList<Cookie>
                println("得到的cookie是:${map}")
            }
        }
        gxClient = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(httpLoggingInter)
            .cookieJar(cookie)
            .build()

    }

    // 混淆请求
    fun getUrl(url:String) {
        val request = Request.Builder()
            .url(url)
            .build()
        val newCall = client.newCall(request)
        try {
            newCall.enqueue(
                object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        println("混淆请求:错误信息是:${e.message}")
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val body = response.body?.string()
                        // 先获得91App上的91地址
                        println("混淆请求:${body}")
                    }
                }
            )
        } catch (e: Exception) {
            println("混淆请求接口失败:错误信息是:${e.message}")
        }
    }

    // 从三方获得appInfo信息
    fun getAppInfo() {
        var appInfoB64 = ""
        var body = ""
        // 先从ebay上获得原地址
        val request = Request.Builder()
            .url(appInfoUrl[0])
            .addHeader("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.106 Safari/537.36")
            .build()
        val newCall = client.newCall(request)
        try {
            newCall.enqueue(
                object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        println("调用接口失败:错误信息是:${e.message}")
                        appInfoError = true
                    }
                    override fun onResponse(call: Call, response: Response) {
                        try {
                            body = response.body?.string().toString()
//                        println("得到的B64加密信息是:$body")
                            val appInfoParttern = Regex("pythonpython(.*?)pythonpython")
                            appInfoB64 = body.let { appInfoParttern.find(it)?.groupValues?.get(1) }
                                .toString()
//                        println("得到的B64加密信息是:$appInfoB64")
                            // 从csdn上获取信息
                            if (appInfoB64 == "" || appInfoB64 == "null") {
                                val request2 = Request.Builder()
                                    .url(appInfoUrl[1])
                                    .addHeader(
                                        "user-agent",
                                        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.106 Safari/537.36"
                                    )
                                    .build()
                                val newCall2 = client.newCall(request2)
                                val res = newCall2.execute()
                                body = res.body?.string().toString()
                                appInfoB64 =
                                    body.let { appInfoParttern.find(it)?.groupValues?.get(1) }
                                        .toString().replace("&#61;", "=")
                                // 从三方获取信息
                                if (appInfoB64 == "" || appInfoB64 == "null") {
                                    val request3 = Request.Builder()
                                        .url(appInfoUrl[2])
                                        .addHeader(
                                            "user-agent",
                                            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.106 Safari/537.36"
                                        )
                                        .build()
                                    val newCall3 = client.newCall(request3)
                                    val res3 = newCall3.execute()
                                    body = res3.body?.string().toString()
                                    val appInfoParttern2 = Regex(">pythonpython(.*?)pythonpython")
                                    appInfoB64 =
                                        body.let { appInfoParttern2.find(it)?.groupValues?.get(1) }
                                            .toString()
                                }
                            }
                            val decode = Base64.decode(appInfoB64, Base64.DEFAULT).decodeToString()
                            appInfoObj = gson.fromJson(decode, AppInfo::class.java)
                            println("得到AppInfo消息：${appInfoObj}")
                        } catch (e: Exception) {
                            appInfoError = true
                            println("获取AppInfo接口失败:错误信息是:${e.message}")
                        }
                    }
                }
            )
        } catch (e: Exception) {
            println("调用接口失败:错误信息是:${e.message}")
            appInfoError = true
        }
    }

    // 开始刷贡献信息
    fun shuaGongXian(gxHeaders: String) {
        val gxList = gxHeaders.split(";").filter {
            it.isNotEmpty()
        }
        println("得到的列表是:$gxList, 长度是:${gxList.size}")
        for (s in gxList) {
            getGxFirst(s)
        }
    }

    // 请求本地网址
    fun getLocal() {
        val request = Request.Builder()
            .url("http://192.168.1.23:5000/")
            .addHeader("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.106 Safari/537.36")
            .build()
        // 构造请求对象
        val call = gxClient.newCall(request)
        val res = call.execute()
        val body = res.body?.string()
        println("请求本地网址网页内容是:$body")
    }

    // 第一次获取贡献页面数据
    fun getGxFirst(gxUrl: String) {
        val userAgent = userAgentLists.shuffled()[0]
        val request = Request.Builder()
            .url(caoHome1 + gxUrl)
            .addHeader("user-agent", userAgent)
            .build()
        // 构造请求对象
        val call = gxClient.newCall(request)
        val res = call.execute()
        val body = res.body?.string()
        println("第一次刷贡献得到的网页内容是:$body")
        // 提取网页内容,用作第二次刷贡献使用
        val gxNeedMap = mutableMapOf<String, String>()
        val uParttern = Regex("u=(.*?)&")
        val u = body?.let { uParttern.find(it)?.groupValues?.get(1) }
        val vcencodeParttern = Regex("vcencode=(.*?)\"")
        val vcencode = body?.let { vcencodeParttern.find(it)?.groupValues?.get(1) }
        val extParttern = Regex("ext\" value=\"(.*?)\"")
        val ext = body?.let { extParttern.find(it)?.groupValues?.get(1) }
        val adsactionParttern = Regex("adsaction\" value=\"(.*?)\"")
        val adsaction = body?.let { adsactionParttern.find(it)?.groupValues?.get(1) }
        if (u != null) {
            gxNeedMap["u"] = u
        }
        if (vcencode != null) {
            gxNeedMap["vcencode"] = vcencode
        }
        if (ext != null) {
            gxNeedMap["ext"] = ext
        }
        if (adsaction != null) {
            gxNeedMap["adsaction"] = adsaction
        }
        gxNeedMap["urlSource"] = caoHome1 + gxUrl
        gxNeedMap["userAgent"] = userAgent
        println("贡献页面得到的Map是:$gxNeedMap")
        secondShuaGx(gxNeedMap)
    }

    // 第二次开始刷贡献
    fun secondShuaGx(needMap: Map<String, String>) {
        val gxUrl = """${caoHome1}/index.php?u=${needMap["u"]}&vcencode=${needMap["vcencode"]}"""
        val ext = needMap["ext"]
        val adsaction = needMap["adsaction"]
        val urlSource = needMap["urlSource"]
        val userAgent = needMap["userAgent"]
        if (ext != null && adsaction != null && urlSource!= null && userAgent != null) {
            val formBody = FormBody.Builder()
                .add("ext", ext)
                .add("adsaction", adsaction)
                .build()
            val request = Request.Builder()
                .url(gxUrl)
                .addHeader("user-agent", userAgent)
                .addHeader("referer", urlSource)
                .post(formBody)
                .build()
            val newCall = gxClient.newCall(request)
            try {
                val res = newCall.execute()
                val body = res.body?.string()
//                println("第二次刷贡献得到的网页内容是:$body")
            } catch (e: Exception) {
                println("调用接口失败:错误信息是:${e.message}")
            }
        }

    }

    // 获取1024回家的路
    fun get1024Home() {
        var resStr = ""
        val formBody = FormBody.Builder()
            .add("a", "get18")
            .add("system", "android")
            .build()
        val request = Request.Builder()
            .url("https://get.xunfs.com/app/listapp.php")
            .post(formBody)
            .build()
        val newCall = client.newCall(request)
        try {
            newCall.enqueue(
                object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        resStr = "调用接口失败:错误信息是:${e.message}"
                        println(resStr)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val body = response.body?.string()
//                        println(body)
                        val caoLiuHome = gson.fromJson(body, CaoLiuHome::class.java)
//                        println("得到的草榴地址信息是:$caoLiuHome")
                        caoHome1 = "https://${caoLiuHome.url1}"
                        caoHome2 = "https://${caoLiuHome.url2}"
                        caoHome3 = "https://${caoLiuHome.url3}"
                        getAppInfo()
                    }
                }
            )
        } catch (e: Exception) {
            resStr = "调用接口失败:错误信息是:${e.message}"
            println(resStr)
        }
    }

    // 获取黑料不打烊的路
    fun getHeiLiaoHome() {
        val request = Request.Builder()
            .url("https://zzzttt.online/")
            .build()
        val newCall = client.newCall(request)
        try {
            newCall.enqueue(
                object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        println("调用getHeiLiaoHome接口失败:错误信息是:${e.message}")
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val body = response.body?.string()
                        val emailParttern = Regex("class=\"btnLink\">(.*?)</div>")
                        val heiLiaoHome = body?.let { emailParttern.findAll(it) }
                        val homeList = heiLiaoHome?.toList()
                        for (i in 0..2) {
                            val home = homeList?.get(i)?.value
                                ?.replace("class=\"btnLink\">", "")
                                ?.replace("</div>","")
                            if (home != null) {
                                println("黑料地址是:${home}")
                            }
                            when (i) {
                                0 -> {
                                    if (home != null) {
                                        heiLiaoWeb1 = home
                                    }
                                }
                                1 -> {
                                    if (home != null) {
                                        heiLiaoWeb2 = home
                                    }
                                }
                                2 -> {
                                    if (home != null) {
                                        heiLiaoWeb3 = home
                                    }
                                }
                            }
                        }
                    }
                }
            )
        } catch (e: Exception) {
            println("调用get91Home接口失败:错误信息是:${e.message}")
        }
    }


    // 获取91回家的路
    fun get91Home() {
        // 先从ebay上获得原地址
        val request = Request.Builder()
            .url("https://www.ebay.com/usr/kellygolfjames")
            .build()
        val newCall = client.newCall(request)
        try {
            newCall.enqueue(
                object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        println("调用get91Home接口失败:错误信息是:${e.message}")
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val body = response.body?.string()
                        val emailParttern = Regex("killcovid.*?killcovid")
                        val home91 = body?.let { emailParttern.find(it)?.value }
                        val realHome = home91?.let { home91.replace("killcovid", "") }
                        // 先获得91App上的91地址
                        println("得到的91回家地址是:${realHome}")
                        if (realHome != null) {
                            porn91VideoApp = realHome
                            get91WebHome()
                        }

                    }
                }
            )
        } catch (e: Exception) {
            println("调用get91Home接口失败:错误信息是:${e.message}")
        }
    }

    fun get91WebHome() {
        // 先从ebay上获得原地址
        val request = Request.Builder()
            .url(porn91VideoApp)
            .addHeader("user-agent", "91appnew")
            .build()
        val newCall = client.newCall(request)
        try {
            newCall.enqueue(
                object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        println("调用get91WebHome接口失败:错误信息是:${e.message}")
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val body = response.body?.string()
//                        println("91appHome源代码是:${body}")
                        val homeParttern = Regex("""baseurl = '(.*?)';""")
                        val home91Web1 = body?.let { homeParttern.find(it)?.groupValues?.get(1) }
                        porn91VideoWeb1 = "${home91Web1}index.php"
                        println("得到的91Video地址是:${porn91VideoWeb1}")
                        get91Img()
                        get91Web2()
                    }
                }
            )
        } catch (e: Exception) {
            println("调用get91WebHome接口失败:错误信息是:${e.message}")
        }
    }

    fun get91Web2() {
        // 先从ebay上获得原地址
        val request = Request.Builder()
            .url("${porn91VideoWeb1}//image")
            .addHeader("user-agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1")
            .build()
        val client91web2 = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
//            .addInterceptor(httpLoggingInter)
            .build()
        val newCall = client91web2.newCall(request)
        try {
            newCall.enqueue(
                object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        println("调用get91Web2接口失败:错误信息是:${e.message}")
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val body = response.body?.string()
//                        println("91appHome源代码是:${body}")
                        val homeParttern = Regex("""91PORN备用地址， <a href=(.*?)>""")
                        val home91Web2 = body?.let { homeParttern.find(it)?.groupValues?.get(1) }
                        porn91VideoWeb2 = "${home91Web2}index.php"
                        println("得到的porn91VideoWeb2地址是:${porn91VideoWeb2}")
                    }
                }
            )
        } catch (e: Exception) {
            println("调用get91Web2接口失败:错误信息是:${e.message}")
        }
    }

    fun get91Img() {
        // 先从ebay上获得原地址
        val request = Request.Builder()
            .url(porn91VideoWeb1)
            .addHeader("user-agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1")
            .build()

        val client91img = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
//            .addInterceptor(httpLoggingInter)
            .build()
        val newCall = client91img.newCall(request)
        try {
            newCall.enqueue(
                object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        println("调用get91Img接口失败:错误信息是:${e.message}")
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val body = response.body?.string()
//                        println("91appHome源代码是:${body}")
                        val homeParttern = Regex("""<li><a target=blank href="(.*?)" >forum""")
                        val home91Photo = body?.let { homeParttern.find(it)?.groupValues?.get(1) }
                        porn91PhotoWeb1 = "${home91Photo}/index.php"
                        println("得到的91Img地址是:${porn91PhotoWeb1}")
                        get91Web3()
                    }
                }
            )
        } catch (e: Exception) {
            println("调用get91Img接口失败:错误信息是:${e.message}")
        }
    }

    fun get91Web3() {
        // 先从ebay上获得原地址
        val request = Request.Builder()
            .url(porn91PhotoWeb1)
            .addHeader("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.106 Safari/537.36")
            .build()
        val newCall = client.newCall(request)
        try {
            newCall.enqueue(
                object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        println("调用get91Web3接口失败:错误信息是:${e.message}")
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val body = response.body?.string()
//                        println("91appHome源代码是:${body}")
                        val homeParttern = Regex("""class="menu_6"><a href="(.*?)"""")
                        val home91Web3 = body?.let { homeParttern.find(it)?.groupValues?.get(1) }
                        if (home91Web3 != null) {
                            porn91VideoWeb3 = home91Web3
                        }
                        println("得到的porn91VideoWeb3地址是:${porn91VideoWeb2}")
                    }
                }
            )
        } catch (e: Exception) {
            println("调用get91Web3接口失败:错误信息是:${e.message}")
        }
    }

}

fun main() {
//    println("在这里先试试刷贡献")
//    val gxHeaders = "/index.php?u=563023&ext=d767b;"
//    HiOkhttp.shuaGongXian(gxHeaders)

//    HiOkhttp.getAppInfo()

    HiOkhttp.get91Home()
}