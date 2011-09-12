package com.google.code.ant.growlnotify;

class GrowlNotification
{
    public static final int HIGH = 2;
    public static final int NORMAL = 0;
    public final String appName;
    public final String title;
    public final String msg;
    public final boolean sticky;
    public final int priority;
    public final String img;

    public GrowlNotification(String appName, String title, String msg, int priority, boolean sticky, String img)
    {
        this.appName = appName;
        this.title = title;
        this.msg = msg;
        this.priority = priority;
        this.sticky = sticky;
        this.img = img;
    }

}
