package com.example.spwn1;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

public class NotificationListener extends NotificationListenerService {
    public NotificationListener() {
    }
    /*
            These are the package names of the apps. for which we want to
            listen the notifications
         */
    private static final class ApplicationPackageNames {
        public static final String WHATSAPP_PACK_NAME = "com.whatsapp";
        public static final String INSTAGRAM_PACK_NAME = "com.instagram.android";
    }

    /*
        These are the return codes we use in the method which intercepts
        the notifications, to decide whether we should do something or not
     */
    public static final class InterceptedNotificationCode {
        public static final int BABY = 1;
        public static final int OTHER_NOTIFICATIONS_CODE = 4; // We ignore all notification with code == 4
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn){
        int notificationCode = matchNotificationCode(sbn);

//        Log. i ( "TAG" , "ID :" + sbn.getId() + " \t " + sbn.getNotification().tickerText + " \t " + sbn.getPackageName()) ;
            Intent intent = new  Intent("Got_notif");
            intent.putExtra("Notification Code", notificationCode);
            sendBroadcast(intent);




    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn){
        int notificationCode = matchNotificationCode(sbn);

        if(notificationCode != InterceptedNotificationCode.OTHER_NOTIFICATIONS_CODE) {

            StatusBarNotification[] activeNotifications = this.getActiveNotifications();

            if(activeNotifications != null && activeNotifications.length > 0) {
                for (int i = 0; i < activeNotifications.length; i++) {
                    if (notificationCode == matchNotificationCode(activeNotifications[i])) {
                        Intent intent = new  Intent("Got_notif");
                        intent.putExtra("Notification Code", notificationCode);
                        sendBroadcast(intent);
                        break;
                    }
                }
            }
        }
    }

    public String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }

    private int matchNotificationCode(StatusBarNotification sbn) {
        int id = sbn.getId();

        Bundle extras = sbn.getNotification().extras;
        String packageName = sbn.getPackageName();

        Object oTitle = extras.get(Notification.EXTRA_TITLE);
        Object oText = extras.get(Notification.EXTRA_TEXT);
        String title = "null";
        String text = "null";
        if (oTitle != null){
            title = oTitle.toString();
        }
        if (oText != null){
            text = oText.toString();
        }


        Log.d("TAG", "Notification - : " +
                " \npackageName: " + packageName +
                " \nTitle      : " + title +
                " \nText       : " + text +
                " \n ID        : " + id);


        if (packageName.equals(ApplicationPackageNames.WHATSAPP_PACK_NAME) && title.equals("Babe" + "🍑")){
            return(InterceptedNotificationCode.BABY);
        }
        else{
            return(InterceptedNotificationCode.OTHER_NOTIFICATIONS_CODE);
        }
    }
}