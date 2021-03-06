package tool.xfy9326.floattext;

import android.*;
import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.content.res.*;
import android.os.*;
import android.preference.*;
import android.provider.*;
import android.support.design.widget.*;
import android.support.v4.view.*;
import android.support.v4.widget.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.view.*;
import android.view.View.*;
import java.util.*;
import tool.xfy9326.floattext.*;
import tool.xfy9326.floattext.Activity.*;
import tool.xfy9326.floattext.Method.*;
import tool.xfy9326.floattext.Utils.*;
import tool.xfy9326.floattext.View.*;

import android.app.AlertDialog;
import tool.xfy9326.floattext.R;

public class FloatManage extends AppCompatActivity
{
    public static int FLOATTEXT_RESULT_CODE = 0;
    public static int RESHOW_PERMISSION_RESULT_CODE = 2;
    public static int FLOATSET_RESULT_CODE = 3;
    public static int FLOAT_TEXT_IMPORT_CODE = 5;
	public static int FLOAT_TEXT_IMPORT_PERMISSION = 7;
	public static int FLOAT_TEXT_EXPORT_PERMISSION = 8;
    private AdvanceRecyclerView listview = null;
    private ListViewAdapter listadapter = null;
    private SharedPreferences spdata;
    private SharedPreferences.Editor spedit;
    private ArrayList<String> FloatDataName;
    private AlertDialog ag_loading;
    private Handler mHandler;
	private static int Snackbarusetime = 0;

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		FloatManageMethod.RootTask(this);
        FloatManageMethod.LanguageInit(this);
        setContentView(R.layout.activity_float_manage);
		contentset();
        ag_loading = FloatManageMethod.setLoadingDialog(this);
		setHandle();
        SetAll(this);
    }

	private void ListViewSet ()
    {
        App utils = ((App)getApplicationContext());
        FloatDataName = utils.getFloatText();
        listview = (AdvanceRecyclerView) findViewById(R.id.listview_floatmanage);
		listview.setHasFixedSize(true);
		LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
		mLayoutManager.setOrientation(OrientationHelper.VERTICAL);
		listview.setLayoutManager(mLayoutManager);
        listview.setEmptyView(findViewById(R.id.textview_floatmanage_empty));
        listadapter = new ListViewAdapter(this, FloatDataName);
        listview.setAdapter(listadapter);
        ((App)getApplicationContext()).setListviewadapter(listadapter);
    }

	private void contentset ()
	{
		Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(tb);
		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatbutton);
		fab.setOnClickListener(new OnClickListener(){
				public void onClick (View v)
				{
					FloatManageMethod.addFloatWindow(FloatManage.this, FloatDataName);
				}
			});
		final DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
		NavigationView navigationView = (NavigationView) findViewById(R.id.main_navigation_view);
		getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, tb, R.string.on, R.string.off);
		mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();
		if (navigationView != null)
		{
			navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
					@Override
					public boolean onNavigationItemSelected (MenuItem item) 
					{
						mDrawerLayout.closeDrawers();
						switch (item.getItemId())
						{
							case R.id.menu_import:
								if (Build.VERSION.SDK_INT > 22)
								{
									if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
									{
										requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, FLOAT_TEXT_IMPORT_PERMISSION);
									}
									else
									{
										FloatManageMethod.selectFile(FloatManage.this);
									}
								}
								else
								{
									FloatManageMethod.selectFile(FloatManage.this);
								}
								break;
							case R.id.menu_export:
								if (Build.VERSION.SDK_INT > 22)
								{
									if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
									{
										requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, FLOAT_TEXT_EXPORT_PERMISSION);
									}
									else
									{
										FloatManageMethod.exporttxt(FloatManage.this);
									}
								}
								else
								{
									FloatManageMethod.exporttxt(FloatManage.this);
								}
								break;
							case R.id.menu_wordlist:
								FloatTextSettingMethod.showDlist(FloatManage.this);
								break;
							case R.id.menu_about:
								Intent aboutintent = new Intent(FloatManage.this, AboutActivity.class);
								startActivity(aboutintent);
								break;
							case R.id.menu_set:
								Intent setintent = new Intent(FloatManage.this, GlobalSetActivity.class);
								startActivityForResult(setintent, FLOATSET_RESULT_CODE);
								break;
							case R.id.menu_back:
								DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_drawer_layout);
								if (drawer.isDrawerOpen(GravityCompat.START))
								{
									drawer.closeDrawer(GravityCompat.START);
								}
								FloatManageMethod.RunInBack(FloatManage.this);
								break;
							case R.id.menu_exit:
								FloatManageMethod.ShutSown(FloatManage.this);
								break;
						}
						return false;
					}
				});
		}
	}

    private void SetAll (final Activity ctx)
    {
        App utils = ((App)getApplicationContext());
        spdata = PreferenceManager.getDefaultSharedPreferences(ctx);
        spedit = spdata.edit();
        if (!utils.GetSave)
        {
            runOnUiThread(new Runnable(){
                    public void run ()
                    {
                        ag_loading.show();
                    }
                });
            Thread t = FloatManageMethod.getSaveData(ctx, utils, spdata, mHandler);
            t.start();
            FloatManageMethod.preparefolder();
            FloatManageMethod.setWinManager(FloatManage.this);
            utils.setGetSave(true);
        }
        else
        {
            ListViewSet();
			FloatManageMethod.startservice(FloatManage.this);
        }
        Intent intent = getIntent();
        int importresult = intent.getIntExtra("ImportText", 0);
        if (importresult == 1)
        {
            snackshow(ctx, getString(R.string.text_import_success));
        }
        else if (importresult == 2)
        {
            snackshow(ctx, getString(R.string.text_import_error));
        }
    }

    private void closeag ()
    {
        FloatManage.this.runOnUiThread(new Runnable(){
                public void run ()
                {
                    ag_loading.dismiss();
                }
            });
    }

    private void importtxt (Intent data)
    {
        final String Path = data.getStringExtra("FilePath");
        final Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Thread thread = new Thread(new Runnable(){
                public void run ()
                {
                    boolean result = FloatManageMethod.importtxt(FloatManage.this, Path);
                    if (result)
                    {
                        intent.putExtra("ImportText", 1);
                    }
                    else
                    {
                        intent.putExtra("ImportText", 2);
                    }
                }
            });
        thread.start();
        try
        {
            thread.join();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        startActivity(intent);
        finishAndRemoveTask();
		System.exit(0);
    }

	private void setHandle ()
	{
		mHandler = new Handler() {
			public void handleMessage (Message msg)
			{
				switch (msg.what)
				{
					case 0:
						boolean close_ag = false;
						App utils = ((App)getApplicationContext());
						Thread r = FloatManageMethod.PrepareSave(FloatManage.this, mHandler);
						if (r != null)
						{
							if (utils.FloatWinReshow)
							{
								r.start();
								close_ag = true;
								utils.setFloatReshow(false);
							}
						}
						FloatManageMethod.floattext_typeface_check(FloatManage.this);
						FloatManageMethod.startservice(FloatManage.this);
						FloatManageMethod.first_ask_for_premission(FloatManage.this);
						ListViewSet();
						if (!close_ag)
						{
							closeag();
						}
						break;
					case 1:
						closeag();
						break;
				}
			}};
	}

	public static void snackshow (Activity ctx, String str)
	{
		CoordinatorLayout cl = (CoordinatorLayout) ctx.findViewById(R.id.FloatManage_MainLayout);
		Snackbar sb = new Snackbar();
		sb.make(cl, str, Snackbar.LENGTH_SHORT).show();
		Snackbarusetime++;
		if (Snackbarusetime >= 5)
		{
			Snackbarusetime = 0;
			System.gc();
		}
	}

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data)
    {
		if (listadapter == null)
		{
			listadapter = ((App)getApplicationContext()).getListviewadapter();
		}
		if (requestCode == FLOAT_TEXT_IMPORT_PERMISSION)
		{
			if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
			{
				FloatManageMethod.selectFile(this);
			}
		}
		if (requestCode == FLOAT_TEXT_EXPORT_PERMISSION)
		{
			if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
			{
				FloatManageMethod.exporttxt(this);
			}
		}
        if (requestCode == FLOATTEXT_RESULT_CODE)
        {
			if (data != null)
			{
				int s = data.getIntExtra("RESULT", 0);
				int p = data.getIntExtra("POSITION", 0);
				boolean e = data.getBooleanExtra("EDITMODE", false);
				switch (s)
				{
					case 1:
						new Handler().postDelayed(new Runnable(){   
								public void run ()
								{   
									snackshow(FloatManage.this, getString(R.string.save_text_ok));
								}   
							}, 300);   
						break;
					case 2:
						new Handler().postDelayed(new Runnable(){   
								public void run ()
								{   
									snackshow(FloatManage.this, getString(R.string.delete_text_ok));
								}   
							}, 300);   
						break;
					case 3:
						new Handler().postDelayed(new Runnable(){   
								public void run ()
								{   
									snackshow(FloatManage.this, getString(R.string.premission_ask_failed));
								}   
							}, 300);   
						break;
				}
				FloatDataName = ((App)getApplicationContext()).getFloatText();
				if (e)
				{
					listadapter.notifyItemChanged(p);
				}
				else
				{
					listadapter.notifyItemInserted(p);
					listadapter.notifyItemRangeChanged(p, listadapter.getItemCount());
				}
			}
            FloatData dat = new FloatData(this);
            dat.savedata();
        }
        else if (requestCode == RESHOW_PERMISSION_RESULT_CODE)
        {
            if (Build.VERSION.SDK_INT >= 23)
            {
                if (Settings.canDrawOverlays(this))
                {
                    Thread t = FloatManageMethod.Reshow(this, null);
                    t.start();
                }
                else
                {
                    snackshow(this, getString(R.string.premission_ask_failed));
                }
            }
			listadapter.notifyDataSetChanged();
        }
        if (requestCode == FLOAT_TEXT_IMPORT_CODE)
        {
            if (data != null)
            {
                importtxt(data);
				listadapter.notifyDataSetChanged();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onKeyDown (int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
			DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_drawer_layout);
			if (drawer.isDrawerOpen(GravityCompat.START))
			{
				drawer.closeDrawer(GravityCompat.START);
			}
			else
			{
				FloatManageMethod.CloseApp(this);
			}
        }
		if (keyCode == KeyEvent.KEYCODE_MENU)
		{
			DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_drawer_layout);
			if (drawer.isDrawerOpen(GravityCompat.START))
			{
				drawer.closeDrawer(GravityCompat.START);
			}
			else
			{
				drawer.openDrawer(GravityCompat.START);
			}
		}
        return false;
    }

    @Override
    public void onConfigurationChanged (Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        spdata = PreferenceManager.getDefaultSharedPreferences(this);
        spedit = spdata.edit();
        ListViewSet();
    }

    @Override
    protected void onDestroy ()
    {
        System.gc();
        super.onDestroy();
    }

}
