# Budget

An Android application for budget management

<p align="center">
    <img src="https://raw.github.com/7h1b0/Budget/master/framed.png" alt="Budget"/>
</p>

## Unable to load class 'javax.xml.bind.annotation.XmlSchema'
Go to /Library/Java/JavaVirtualMachines.
Remove the directory whose name matches the following format by executing the rm command as a root user or by using the sudo tool:
/Library/Java/JavaVirtualMachines/jdk-interim.update.patch.jdk
For example, to uninstall 10 Interim 0 Update 2 Patch 1:

$ rm -rf jdk-10.0.2.1.jdk

install jdk 8

## Could not initialize class com.android.sdklib.repository.AndroidSdkHandler
    
JDK Location
Project Structure/SDK Location
Change
/Applications/Android Studio.app/Contents/jre/jdk/Contents/Home
to
/Library/Java/JavaVirtualMachines/jdk1.8.0_291.jdk

# Failed to find target with hash string 'android-27' in: /Users/dangle/Library/Android/sdk
Install Android SDK Platform 27