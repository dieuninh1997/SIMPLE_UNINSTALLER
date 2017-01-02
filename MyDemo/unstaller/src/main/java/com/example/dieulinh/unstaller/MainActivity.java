
package com.example.dieulinh.unstaller;
import javax.swing.text.html.ListView;


import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.Color;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.model.UninstallerAdapter;

import java.lang.reflect.Method;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Semaphore;

import static android.content.Intent.ACTION_DELETE;
import static android.content.Intent.ACTION_PACKAGE_REMOVED;

public class MainActivity extends AppCompatActivity {

    ArrayList<ApplicationInfo> dsUninstaller;
    ListView lvUninstaller;
    UninstallerAdapter uninstallerAdapter;
    PackageManager packageManager;
    private SearchView searchView;
    private SearchManager searchManager;


    private Toolbar my_toolbar;
    SearchView.SearchAutoComplete searchAutoComplete;

    BroadcastReceiver deleteReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction().equals(Intent.ACTION_DELETE)){
                Toast.makeText(context,"Ok bắt được sự kiện rồi",Toast.LENGTH_LONG).show();


                onContentChanged();

            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter=new IntentFilter(ACTION_DELETE);
        filter.addDataScheme("package");
        registerReceiver(deleteReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(deleteReceiver!=null){
            unregisterReceiver(deleteReceiver);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        packageManager = getPackageManager();

        my_toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(my_toolbar);
        getSupportActionBar().setTitle(R.string.my_toolbar_title);
        getSupportActionBar().setIcon(R.drawable.ic_delete);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        addControls();

        sapxep_sort();
       // getPackageSizeInfo();

    }



    //tạo mảng
    private ArrayList<ApplicationInfo> taoDsUninstaller(List<ApplicationInfo> list) {
        ArrayList<ApplicationInfo> dsUninstaller = new ArrayList<>();

        for (ApplicationInfo i : list) {
                try {
                    if (packageManager.getLaunchIntentForPackage(i.packageName) != null ) {
                        dsUninstaller.add(i);
                        uninstallerAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        return dsUninstaller;
    }


    private void addControls() {
        lvUninstaller = (ListView) findViewById(R.id.lvUninstaller);
        dsUninstaller = taoDsUninstaller(packageManager.getInstalledApplications(PackageManager.GET_META_DATA));
        uninstallerAdapter = new UninstallerAdapter(MainActivity.this, R.layout.item_row, dsUninstaller);
        lvUninstaller.setAdapter(uninstallerAdapter);

    }

    //sap xep sort
    public void sapxep_sort() {
        uninstallerAdapter.sort(new Comparator<ApplicationInfo>() {
            Collator collator = Collator.getInstance();

            @Override
            public int compare(ApplicationInfo lhs, ApplicationInfo rhs) {
                return collator.compare(lhs.loadLabel(packageManager), rhs.loadLabel(packageManager));
            }
        });
        uninstallerAdapter.notifyDataSetChanged();
    }
TA
    //tạo menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        searchView = (SearchView) menu.findItem(R.id.menu_search_timkiem).getActionView();

        //đổi màu chữ khi search
        searchAutoComplete = (SearchView.SearchAutoComplete) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchAutoComplete.setHintTextColor(Color.WHITE);
        searchAutoComplete.setTextColor(Color.WHITE);

        View searchPlate = (View) searchView.findViewById(android.support.v7.appcompat.R.id.search_plate);
        searchPlate.setBackgroundResource(R.drawable.textfiled_custom_searchview);

       searchAutoComplete.setThreshold(1);//gõ 1 chữ là cho gợi ý
        searchAutoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {
                    addControls();
                    sapxep_sort();
                } else {
                    searchItemListview(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_quit:
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
    //tìm kiếm

    public void searchItemListview(String text) {
        String s = searchAutoComplete.getText().toString();
        int textLength = s.length();

        String sApp;
        int appSize = dsUninstaller.size();
        ArrayList<ApplicationInfo> appsListSort = new ArrayList<ApplicationInfo>();



        for (int i = 0; i < appSize; i++ ) {


            sApp = (String)dsUninstaller.get(i).loadLabel(packageManager);

            if (s.equalsIgnoreCase((String) sApp.subSequence(0, textLength))) {
                appsListSort.add(dsUninstaller.get(i));

            }
        }

        dsUninstaller.clear();
        for (int j = 0; j < appsListSort.size(); j++) {
            dsUninstaller.add(appsListSort.get(j));
        }
        uninstallerAdapter.notifyDataSetChanged();
    }


}
