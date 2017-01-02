import javax.naming.Context;

ipackage com.model;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.net.Uri;
import android.os.Build;
import android.os.RemoteException;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dieulinh.unstaller.R;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.Semaphore;

public class UninstallerAdapter extends ArrayAdapter<ApplicationInfo> {
    Activity activity;
    int resource;
    List<ApplicationInfo> objects;
    PackageManager packageManager;
    ImageButton ibtnDelete;//ibtnDetail;
    TextView txtKB;
    Context context;

    ApplicationInfo data;

    public UninstallerAdapter(Activity activity, int resource, List<ApplicationInfo> objects) {
        super(activity, resource, objects);
        this.activity = activity;
        this.resource = resource;
        this.objects = objects;
        packageManager = activity.getPackageManager();


    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater=this.activity.getLayoutInflater();
         final View row = inflater.inflate(this.resource, null);

        ImageView imgIconApp = (ImageView) row.findViewById(R.id.imgIconApp);
        TextView txtNameApp = (TextView) row.findViewById(R.id.txtNameApp);
        ibtnDelete = (ImageButton) row.findViewById(R.id.ibtnDelete);
        //ibtnDetail = (ImageButton) row.findViewById(R.id.ibtnDetail);
        //txtKB= (TextView) row.findViewById(R.id.txtKB);


        data = this.objects.get(position);
       if (data != null) {
            txtNameApp.setText(data.loadLabel(packageManager));
            imgIconApp.setImageDrawable(data.loadIcon(packageManager));
            getPackageSizeInfo();
            //txtKB.setText(codeSize+"KB");
        }


        ibtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pkgName = objects.get(position).packageName;
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_DELETE);
                intent.setData(Uri.parse("package:" + pkgName));
                activity.startActivity(intent);
            }
        });

        /*ibtnDetail.setOnClickListener(new View.OnClickListener() {
            Intent intent;
            @Override
            public void onClick(View v) {
                //try{
                    String pkgName = objects.get(position).packageName;
                    intent=new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse("package:"+pkgName));
                    activity.startActivity(intent);
           *//* }catch (ActivityNotFoundException e){
                    intent=new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                    activity.startActivity(intent);
                }*//*
            }
        });*/
        return row;
    }


    public void getPackageSizeInfo() {

        context=activity.getApplicationContext();
        packageManager=context.getPackageManager();
        //List<ApplicationInfo> installedApplications=
        //        packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        //Semaphore to handle concurrency:Semaphore để xử lý đồng thời
        final Semaphore codeSizeSemaphore=new Semaphore(1,true);

        //long codeSize=0;
        // for(ApplicationInfo appInfo: installedApplications){
        try{
            codeSizeSemaphore.acquire();
        }catch (InterruptedException e){
            e.printStackTrace(System.err);
        }
        //Collect some other statistics:Thu thập số liệu thống kê khác

        //Collect code size
        try{
            Method getPackageSizeInfo=packageManager.getClass().getMethod("getPackageSizeInfo",
                    String.class,
                    IPackageStatsObserver.class);
            getPackageSizeInfo.invoke(packageManager,
                    data.packageName,
                    new IPackageStatsObserver.Stub()

                    {
                        // Examples in the Internet usually have this method as @Override.
                        // I got an error with @Override. Perfectly works without it.
                        public void onGetStatsCompleted(PackageStats pStats, boolean succeedded)
                                throws RemoteException
                        {
                            long codeSize= pStats.codeSize;
                            codeSizeSemaphore.release();
                        }
                    }
            );

        }catch (Exception e){
            e.printStackTrace(System.err);
        }


    }


}
