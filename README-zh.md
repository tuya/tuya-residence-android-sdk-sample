# 涂鸦 智慧居住 Adnroid SDK 示例

 [English](README.md) | [中文版](README-zh.md)

---

本示例演示了如何使用智慧居住 Adnroid SDK 构建智能住宅应用程序。智慧居住 Adnroid SDK 分为多个功能组，包括用户注册流程、站点管理、App 通行管理和密码通行管理。


## 运行环境

- Android Stidio  Arctic Fox | 2020.3.1 Patch 3
Build #AI-203.7717.56.2031.7784292, built on October 1, 2021
- Gradle 7.0及以上版本

## 运行示例

1. 智慧居住 Android SDK 通过 Gradle 进行集成，需要安装相关资源
2. Clone或者下载本示例源码
3. 运行本示例需要**AppKey**、**SecretKey** 和 **安全图片**，你可以前往 [涂鸦智能 IoT 平台](https://developer.tuya.com/cn/) 注册成为开发者，并通过以下步骤获取：

   1. 登录 [涂鸦智能 IoT 平台](https://iot.tuya.com/)，在左侧导航栏面板分别选择： **App** -> **SDK 开发**
   2. 点击 **创建APP** 进行创建应用.
   3. 填写必要的信息，包括应用名称、Package Name等
   4. 点击创建好的应用，在**获取密钥**面板，可以获取 SDK 的 AppKey，AppSecret，安全图片等信息

4. 打开本示例工程的 `AndroidManiFest.xml` 
5. 在 **AndroidManiFest.xml** 中将AppKey、SecretKey填写

```
        <meta-data
            android:name="TUYA_SMART_APPKEY"
            android:value="" />
        <meta-data
            android:name="TUYA_SMART_SECRET"
            android:value="" />
```
6. 下载**安全图片**并重命名为`t_s.bmp`，将安全图片拖拽到 `assets`文件夹下
 
    
**注意**: Package Name、 AppKey、AppSecret和安全图片必须跟你在 [涂鸦智能 IoT 平台](https://iot.tuya.com/)创建的应用保持一致，如果不一致则无法正常运行本示例工程。

7. 本示例使用Compose进行开发，请参考官方文档，配置相关运行环境

8. Gradle sync

## 开发文档

关于智慧居住 Android SDK 的更多信息，请参考： [智慧居住 App SDK](https://developer.tuya.com/cn/docs/app-development).
