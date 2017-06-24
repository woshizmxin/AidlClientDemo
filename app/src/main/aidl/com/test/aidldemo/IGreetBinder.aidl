// IPerson.aidl
package com.test.aidldemo;
import com.test.aidldemo.IGreetCallback;

interface IGreetBinder {
       String greet(String someone);
       void registerCallback(IGreetCallback cb);
       void unregisterCallback(IGreetCallback cb);
}
