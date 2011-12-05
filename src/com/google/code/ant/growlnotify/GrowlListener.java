package com.google.code.ant.growlnotify;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.SubBuildListener;

public class GrowlListener implements SubBuildListener
{

    private static final String ANT_GROWL_HOME = System.getProperty("user.home") + File.separator + ".ant-growl";
    private static final String TARGET_IGNORE = System.getenv("ANT_GROWL_TARGET_FILTER");
    private static final String SUCC_FAIL_ONLY = System.getenv("ANT_GROWL_SUCC_FAIL_ONLY");
    private static final String GOOD_TIMES_IMG_FOLDER = ANT_GROWL_HOME + File.separator + "success";
    private static final String BAD_TIMES_IMG_FOLDER = ANT_GROWL_HOME + File.separator + "fail";

    private static final String ANT_IMG_PATH = ANT_GROWL_HOME + File.separator + "ant/ant.jpg";

    private List<String> successImgList = new ArrayList();
    private List<String> failImgList = new ArrayList();

    private static final String APP_NAME = "Ant";
    private Growl growl = new Growl();
    private List<String> targetIgnoreFilter = new ArrayList<String>();

    private String antImgPath = ANT_IMG_PATH;
    private boolean successFailOnly = SUCC_FAIL_ONLY != null ? SUCC_FAIL_ONLY : false;

    public GrowlListener()
    {
        try
        {
            successImgList.addAll(getPics(GOOD_TIMES_IMG_FOLDER));
            failImgList.addAll(getPics(BAD_TIMES_IMG_FOLDER));

            if (TARGET_IGNORE != null)
            {
                String[] tokens = TARGET_IGNORE.split(";");
                targetIgnoreFilter.addAll(Arrays.asList(tokens));
            }

        }
        catch (Exception e)
        {
            // don't care
        }
    }

    private List<String> getPics (String folderName)
    {
        List<String> picList = new ArrayList();
        File folder = new File(folderName);
        if (folder.exists() && folder.isDirectory())
        {
            String[] pics = folder.list(new ImgFilter());
            for (String pic : pics)
            {
                picList.add(folderName + File.separator + pic);
            }
        }
        return picList;
    }

    private class ImgFilter implements FilenameFilter
    {

        @Override
        public boolean accept (File dir, String name)
        {
            return name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".tiff")
                    || name.endsWith(".bmp") || name.endsWith(".ico") || name.endsWith(".icns") || name.endsWith(".pdf");
        }

    }

    @Override
    public void buildStarted (BuildEvent event)
    {
        if (successFailOnly) 
        {
            return;
        }
        sendMessage("Build started ", GrowlNotification.NORMAL, false, null);
    }

    @Override
    public void buildFinished (BuildEvent event)
    {
        Throwable exception = event.getException();
        String projectName = event.getProject().getName();
        if (exception != null)
        {
            sendMessage("Build failed: " + exception.toString(), GrowlNotification.HIGH, false, getFailPic());
            return;
        }
        sendMessage("Build finished for " + projectName, GrowlNotification.NORMAL, false, getSuccessPic());
    }

    @Override
    public void targetFinished (BuildEvent event)
    {
        if (successFailOnly) 
        {
            return;
        }
        if (!targetIgnoreFilter.contains(event.getTarget().getName()))
        {
            sendMessage("Finished target: " + event.getTarget(), GrowlNotification.NORMAL, false, getNeutralPic());
        }
    }

    @Override
    public void targetStarted (BuildEvent event)
    {
        if (successFailOnly) 
        {
            return;
        }           
        if (!targetIgnoreFilter.contains(event.getTarget().getName()))
        {
            sendMessage("Started target: " + event.getTarget(), GrowlNotification.NORMAL, false, getNeutralPic());
        }
    }

    @Override
    public void subBuildFinished (BuildEvent event)
    {
        sendMessage("Finished sub project build: " + event.getProject().getName(), GrowlNotification.NORMAL, false, getNeutralPic());
    }

    @Override
    public void subBuildStarted (BuildEvent event)
    {
        if (successFailOnly) 
        {
            return;
        }
        sendMessage("Started target: " + event.getProject().getName(), GrowlNotification.NORMAL, false, getNeutralPic());
    }

    protected void sendMessage (String msg, int priority, boolean sticky, String img)
    {
        try
        {
            growl.sendNotification(new GrowlNotification(APP_NAME, APP_NAME, msg, priority, sticky, img));
        }
        catch (GrowlException e)
        {
            e.printStackTrace();
        }
    }

    private String getFailPic ()
    {
        if (failImgList.size() > 0)
        {
            return failImgList.get(getRandIndex(failImgList.size()));
        }
        return null;
    }

    private String getSuccessPic ()
    {
        if (successImgList.size() > 0)
        {
            return successImgList.get(getRandIndex(successImgList.size()));
        }
        return null;
    }

    private String getNeutralPic ()
    {
        return antImgPath;
    }

    private int getRandIndex (int size)
    {
        return (int) Math.floor((Math.random() * size));
    }

    // stuff we aren't using

    @Override
    public void taskFinished (BuildEvent event)
    {
    }

    @Override
    public void taskStarted (BuildEvent event)
    {
    }

    @Override
    public void messageLogged (BuildEvent event)
    {
    }

}