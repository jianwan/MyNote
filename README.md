# My Note

## 2022/05/04更新

* 更新maven仓库地址


## 其他说明

* 此仓库apk对应的后台在bmob上已经不维护了，
    * 如果你要使用此apk，建议到bmob上注册一个账号并新建应用（具体操作查看bmob文档说明），然后将EditNoteActivity + SplashActivity中的AppKey和AppSecret替换成你的，对应的后台表自行维护；
    * 如果你只是需要查看内容，可以使用下面的演示账号
        > 账号：test@qq.com
        > 密码：123456

* 因为长时间不更新，因此适配是有bug的，请结合代码以及自身需要处理对应的bug。



*更新于2019/06/11,已完成，暂时不再更新*

##
### 一款`Material Design`风格的`Android`记事本(由 [bigggge](https://github.com/bigggge/Note) 而来)

`APK` 在 `imgs` 文件夹下，可以自行下载。

![启动页](/imgs/splash.jpg)
![登录页](/imgs/login.jpg "登录页")
![主页](/imgs/main.jpg "首页")
![抽屉式布局](/imgs/left.jpg "抽屉式布局")
![笔记编辑页面](/imgs/note_edit.jpg "笔记编辑页面")
![分享页面](/imgs/share.jpg "分享页面")

### 实现的功能如下

* 启动页；

* 登录注册；

* 主页面
	* 下拉刷新，从`Bmob`获取数据（将先清除本地数据）；

	* 更新，将本地数据更新到远程`Bmob`后台；

	* 清除本地所有数据；

* 新建笔记

	* 闹钟提醒功能（定时）；
	
	* 优先级功能（排序）；

	* 分享到微信

##  
 
**欢迎大家`star`以及`fork`，有问题请联系我 `jianwan1221@qq.com`**.

