# Budget

An Android application for budget management

<p align="center">
    <img src="https://raw.github.com/7h1b0/Budget/master/framed.png" alt="Budget"/>
</p>

## Troubleshooting

# Unable to load class 'javax.xml.bind.annotation.XmlSchema'
Go to /Library/Java/JavaVirtualMachines.
Remove the directory whose name matches the following format by executing the rm command as a root user or by using the sudo tool:
/Library/Java/JavaVirtualMachines/jdk-interim.update.patch.jdk
For example, to uninstall 10 Interim 0 Update 2 Patch 1:

$ rm -rf jdk-10.0.2.1.jdk

install jdk 8

# Could not initialize class com.android.sdklib.repository.AndroidSdkHandler
    
JDK Location
Project Structure/SDK Location
Change
/Applications/Android Studio.app/Contents/jre/jdk/Contents/Home
to
/Library/Java/JavaVirtualMachines/jdk1.8.0_291.jdk

# Failed to find target with hash string 'android-27' in: /Users/dangle/Library/Android/sdk
Install Android SDK Platform 27

# Cannot resolve symbol 'R'
Mismatch among ApplicationId in gradle and package name in manifes
change package="com.dlit01.budget" => package="com.dlit01.budget"
Then
Build -> clean project
Build -> rebuild project
(Maybe Restart android studio) 

# Debug
Click debug many until ok.

## Technical
# Currency input
mView.value.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {

      }
      private String current = "";
      @Override
      public void afterTextChanged(Editable s) {
        if(!s.toString().equals(current)){
          mView.value.removeTextChangedListener(this);

          String cleanString = s.toString().replaceAll("[$,.]", "");

          double parsed = Double.parseDouble(cleanString);
          String formatted = CurrencyUtil.formatToUSD(parsed/100);
          //String formatted = NumberFormat.getCurrencyInstance().format((parsed/100));

          current = formatted;
          mView.value.setText(formatted);
          mView.value.setSelection(formatted.length());
          mView.value.addTextChangedListener(this);
          }
      }
    });
    
    
# android save state onPause
https://stackoverflow.com/questions/10906219/save-state-for-onpause-and-onresume

# Build with logs