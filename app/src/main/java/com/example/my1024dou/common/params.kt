package com.example.my1024dou.common

//app版本
const val appVersion = 1.1

// 分享应用
var shareContent = "1024老司机带你回家了：https://docs.qq.com/doc/DVGhIR05ZR3lTWGRa"

// 间隔刷贡献的贡献头
var duringGx = "//"

// 更多推荐页面
var moreUrl = "https://1024shen.com/gohome.html"

// Useragent列表
var userAgentLists = mutableListOf<String>(
    "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_6_8; en-us) AppleWebKit/534.50 (KHTML, like Gecko) Version/5.1 Safari/534.50",
    "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-us) AppleWebKit/534.50 (KHTML, like Gecko) Version/5.1 Safari/534.50",
    "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0;",
    "Mozilla/5.0 (Windows NT 6.1; rv,2.0.1) Gecko/20100101 Firefox/4.0.1",
    "Opera/9.80 (Macintosh; Intel Mac OS X 10.6.8; U; en) Presto/2.8.131 Version/11.11",
    "Opera/9.80 (Windows NT 6.1; U; en) Presto/2.8.131 Version/11.11",
    "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Maxthon 2.0)",
    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_0) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.56 Safari/535.11",
    "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)",
    "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Trident/4.0; SE 2.X MetaSr 1.0; SE 2.X MetaSr 1.0; .NET CLR 2.0.50727; SE 2.X MetaSr 1.0)",
    "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; 360SE)",
    "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)",
    "Mozilla/5.0 (iPhone; U; CPU iPhone OS 4_3_3 like Mac OS X; en-us) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/5.0.2 Mobile/8J2 Safari/6533.18.5",
    "Mozilla/5.0 (iPad; U; CPU OS 4_3_3 like Mac OS X; en-us) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/5.0.2 Mobile/8J2 Safari/6533.18.5",
    "Mozilla/5.0 (BlackBerry; U; BlackBerry 9800; en) AppleWebKit/534.1+ (KHTML, like Gecko) Version/6.0.0.337 Mobile Safari/534.1+",
    "Mozilla/5.0 (compatible; MSIE 9.0; Windows Phone OS 7.5; Trident/5.0; IEMobile/9.0; HTC; Titan)",
    "Mozilla/5.0 (iPhone; CPU iPhone OS 11_2_2 like Mac OS X) AppleWebKit/604.4.7 (KHTML, like Gecko) Mobile/15C202 MicroMessenger/6.6.1 NetType/4G Language/zh_CN",
    "Mozilla/4.0 (compatible; MSIE 6.0; ) Opera/UCWEB7.0.2.37/28/999",
    "MQQBrowser/26 Mozilla/5.0 (Linux; U; Android 2.3.7; zh-cn; MB200 Build/GRJ22; CyanogenMod-7) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1",
    "Mozilla/5.0 (Linux; U; Android 3.0; en-us; Xoom Build/HRI39) AppleWebKit/534.13 (KHTML, like Gecko) Version/4.0 Safari/534.13",
    "Chrome/17.0.963.56 Safari/535.11"
)

// 使用帮助
var useHelp = "<h3>使用帮助:</h3>点击左上角，可以切换到草榴社区、91社区、黑料等地址。" +
        "当切换到对应的界面后，再点击右上角，就会显示当前界面的三个地址和永久地址，" +
        "外部打开则会使用外部浏览器打开当前页面。复制地址则会复制当前页面地址，" +
        "可以分享给朋友或者粘贴到浏览器打开。<br><h3>注意事项:</h3> "


// app信息对象
data class AppInfo(
    var update: Boolean,
    var version: Double,
    var upurl: String = "https://wwx.lanzoui.com/iFKcRtafx1i",
    var upcontent: String,
    var showmessage: Boolean,
    var message: String,
    var headers: String,
    var header_ms: String,
    var article_ad: String = "文章头部AD",
    var commit_ad: String = "评论区AD",
    var porn_video_1ad: String = "91评论区AD",
    var porn_video_2ad: String = "评论区AD",
    var porn_video_3ad: String = "评论区AD",
    var porn_video_4ad: String = "评论区AD",
    var porn_video_5ad: String = "评论区AD",
    var porn_video_6ad: String = "评论区AD",
    var porn_video_footer: String = "视频底部",
    var porn_photo_header: String = "论坛头部",
    var porn_photo_header2: String = "论坛头部2",
    var porn_photo_footer: String = "论坛底部",
    var porn_photo_wentou: String = "论坛文章开头",
    var heiliao_header: String = "黑料头部",
    var heiliao_footer: String = "黑料头部",
    var heiliao_artical: String = "黑料文章",
    var about: String = "关于我们",
    var mazinote: String = "需要邀请码才可以注册哦"
) {
    override fun toString(): String {
        return "AppInfo(update=$update, version=$version, upurl='$upurl', upcontent='$upcontent', showmessage=$showmessage, header_ms='$message', headers='$headers', weixinxin='$header_ms', article_ad='$article_ad', about='$about', mazinote='$mazinote')"
    }
}

// 存储appInfo
var appInfoObj: AppInfo? = null
var appInfoError = false

// 如果升级失败，就打开此链接
var errorUrl = "https://docs.qq.com/doc/DVEZ3Y0RWdUhFVmpC"
const val errorContent = "网络可能有问题，请保持网络通畅，或更换网络后刷新再试。因为有的电信运营商可能把网站屏蔽了。或者是网站需要翻墙才能访问"

var currentUrl = "https://www.baidu.com"
var caoHome0 = "https://www.t66y.com"
var caoHome1 = "https://cl.291x.xyz"
var caoHome2 = "https://cl.291y.xyz"
var caoHome3 = "https://cl.291z.xyz"
var caoHomeApp = "https://private70.ghuws.win/index.php"
var doumei = "https://v.nrzj.vip/"
var porn91VideoApp = "https://its.better2021app.com"
var porn91VideoWeb1 = "https://up.91p22.net//index.php"
var porn91VideoWeb2 = "http://0728.91p50.com/index.php"
var porn91VideoWeb3 = "https://f0601.workgreat11.live/index.php"
var porn91PhotoWeb1 = "https://f1113.workarea1.live/index.php"
var porn91PhotoWeb2 = "https://f.wonderfulday28.live/index.php"
var porn91PhotoWeb3 = "https://f.wonderfulday28.live/index.php"
var porn91SourceVideo = "https://91porn.com/index.php"
var heiLiaoApp = "https://www.jusebao.biz/"
var heiLiaoWeb1 = "https://zztt10.com/"
var heiLiaoWeb2 = "https://zztt11.com/"
var heiLiaoWeb3 = "https://zztt12.com/"
var heiliaoSOurce = "https://668.su/"

// 贡献头
var gongXianList = mutableListOf<String>(
    "/index.php?u=567982&ext=b813c"
)

// app信息获取的url
var appInfoUrl = mutableListOf<String>(
    "https://www.cnblogs.com/sdfasdf/p/15640498.html",
    "https://blog.csdn.net/weixin_44786530/article/details/119567136",
    "https://1024shen.com/archives/5304"
)

// 回销请求的地址列表
var huiXiaoUrl = mutableListOf<String>(
    "https://segmentfault.com/",
    "https://www.52pojie.cn/forum-24-1.html",
    "https://www.93img.com/111882",
    "https://www.jianshu.com/",
    "http://www.jj20.com/",
    "http://m.jj20.com/bz/nxxz/shxz/338323_4.html",
    "https://gitcafe.net/daohang.html"
)


data class CaoLiuHome(
    var url1: String,
    var url2: String,
    var url3: String,
    var app: String,
    var update: String,
    var note: String,
    var appVer: String
) {
    override fun toString(): String {
        return "CaoLiuHome(url1='$url1', url2='$url2', url3='$url3', app='$app', update='$update', note='$note', appVer='$appVer')"
    }
}