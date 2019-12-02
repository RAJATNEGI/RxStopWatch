# RxStopWatch
This is a simple example of a Stopwatch using RxJava.
In this project has one important constraint and that is use of RxJava only due to which it is a pure Reactive project.

In this project I have used two libraries 
1.RxJava and
2.RxBindings

To include these dependencies in your project add these dependency in your gradle 


    //rxJava for kotlin
    implementation 'io.reactivex.rxjava2:rxkotlin:2.3.0'
    //rxJava for android
    implementation 'io.reactivex.rxjava2:rxandroid:2.1.1'
    
    //rxbinding for android by jake wharton
    implementation 'com.jakewharton.rxbinding3:rxbinding:3.0.0-alpha2'
    implementation 'com.jakewharton.rxrelay2:rxrelay:2.1.0'
    
    //life cycle for android(ViewModel,LiveData)
    implementation 'androidx.lifecycle:lifecycle-extensions:2.2.0-rc02'
