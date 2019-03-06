# FileProvider
FileProvider使用示例
Android 7.0-使用FileProvider在应用之间共享文件

FileProvider是ContentProvider的一个特殊子类，通过以content://代替file:/// Uri 来安全地分享与app关联的文件。

”content URI“允许你给授予临时的读写访问权限。当你创建一个包含”content URI“的 Intent 时，想要将它发送给另一个app你需要调用Intent.setFlags()添加权限.Intent被发到Activity时，这些权限在此Activity在栈中活动期间可用。Intent被发到Service时，这些权限在此Service运行期间可用。

作为对比，使用file:/// Uri 控制访问，你必须修改文件的文件系统权限。你提供的权限对所有app可用并保持有效直到你改变它们。这种级别的访问从根本上是不安全的。
”content URI“提供的文件访问安全性使FileProvider成为Android安全基础设施的一个关键部分。

指定对外暴露的目录
创建一个xml文件并在其中指定对外暴露的目录。

>路径节点包含以下两个属性：
name，URI路径片段。用于分享时隐藏真实路径。
path，待分享的子目录。留空表示该类路径的根目录。

#### 有多种路径节点类型：
```
files-path，应用内部存储的files子目录，对应Context.getFilesDir()
例如/data/user/0/${applicationId}/files

cache-path，应用内部存储的cache子目录，对应Context.getCacheDir()
例如/data/user/0/${applicationId}/cache

external-files-path，应用外部存储的files子目录，对应Context.getExternalFilesDir(String)
例如/storage/emulated/0/Android/data/${applicationId}/files

external-cache-path，应用外部存储的cache子目录，对应Context.getExternalCacheDir()
例如/storage/emulated/0/Android/data/${applicationId}/cache

external-path，外部存储根目录，对应Environment.getExternalStorageDirectory()
例如/storage/emulated/0

root-path，系统根目录”/“
```

#### xml/file_paths.xml
```
<paths xmlns:android="http://schemas.android.com/apk/res/android">
    <!-- content://${applicationId}/app_internal_files/ -->
    <!-- file:///data/user/0/${applicationId}/files/example/ -->
    <files-path name="app_internal_files" path="example" />
    
    <!-- content://${applicationId}/app_internal_cache/ -->
    <!-- file:///data/user/0/${applicationId}/cache/example/ -->
    <cache-path name="app_internal_cache" path="example" />
    
    <!-- content://${applicationId}/app_external_files/ -->
    <!-- file:///storage/emulated/0/Android/data/${applicationId}/files/example/ -->
    <external-files-path name="app_external_files" path="example" />
    
    <!-- content://${applicationId}/app_external_cache/ -->
    <!-- file:///storage/emulated/0/Android/data/${applicationId}/cache/example/ -->
    <external-cache-path name="app_external_cache" path="example" />
    
    <!-- content://${applicationId}/external_root/ -->
    <!-- file:///storage/emulated/0/example/ -->
    <external-path name="external_root" path="example" />
    
    <!-- content://${applicationId}/system_root/ -->
    <!-- file:///example/ -->
    <root-path name="system_root" path="example" />
    
     
    <external-cache-path name="images" path="images" />   
</paths>
```

#### 声明FileProvider

在Manifest.xml中声明FileProvider并添加android:name为android.support.FILE_PROVIDER_PATHS的<meta-data/>
android:name，通常用v4包提供的FileProvider，也可以用自定义的。
android:authorities，通常格式为${applicationId}.${yourprovider}。
android:exported，false，表示不需要对外开放。
android:grantUriPermissions，设为true，表示允许授予临时共享权限。

```
<manifest>
    ...
    <application>
        ...
         <provider
            android:name="android.support.v4.content.FileProvider"
            android:authorities="com.whz.fileprovider.Config"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/file_paths"/>
        </provider>
        ...
    </application>
</manifest>
```
授予文件临时权限并分享
```
File Uri uriForFile = getUriForFile(this, "com.whz.fileprovider.Config", mPicPath);
Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
intent.putExtra(MediaStore.EXTRA_OUTPUT, uriForFile);
intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
startActivityForResult(intent, 200);
```
