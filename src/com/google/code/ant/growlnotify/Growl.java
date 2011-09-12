package com.google.code.ant.growlnotify;

import java.io.IOException;
import java.util.ArrayList;

class Growl
{

    public void sendNotification (GrowlNotification notification) throws GrowlException
    {
        ArrayList args = new ArrayList();
        args.add("growlnotify");
        args.add("-m");
        args.add(notification.msg);
        args.add("-n");
        args.add(notification.appName);
        args.add("-t");
        args.add(notification.title);
        if (notification.sticky)
        {
            args.add("-s");
        }

        if (notification.img != null)
        {
            args.add("--image");
            args.add(notification.img);
        }

        try
        {
            Process exec = Runtime.getRuntime().exec((String[]) args.toArray(new String[] {}));
        }
        catch (IOException e)
        {
            throw new GrowlException(e);
        }
    }
}

// add images
// add filter list
