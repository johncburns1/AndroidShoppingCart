package hu.ait.android.shoppingkartapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import hu.ait.android.shoppingkartapp.Adapter.KartRecyclerAdapter;
import hu.ait.android.shoppingkartapp.Data.KartItem;
import hu.ait.android.shoppingkartapp.Touch.KartItemTouchHelperCallback;
import io.realm.Realm;
import io.realm.RealmResults;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

public class MainActivity extends AppCompatActivity {

    public static String KEY_ITEM_ID = "KEY_ITEM_ID";
    public static String KEY_EDIT = "KEY_EDIT";
    public static final int REQUEST_CODE_EDIT = 1001;
    public static final int REQUEST_CODE_NEW = 1000;
    public static final int DST_SCALE = 150;

    private KartRecyclerAdapter adapter;
    private DrawerLayout drawerLayout;
    private CoordinatorLayout layoutContent;
    private int positionToEdit = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((ShoppingKartApplication) getApplication()).openRealm();

        RealmResults<KartItem> allItems = getRealm().where(KartItem.class).findAll();
        KartItem itemArray[] = new KartItem[allItems.size()];
        List<KartItem> itemResult = new ArrayList<KartItem>(Arrays.asList(allItems.toArray(itemArray)));

        adapter = new KartRecyclerAdapter(itemResult, this, ((ShoppingKartApplication) getApplication()).getRealmKart());

        RecyclerView recyclerViewKart = (RecyclerView) findViewById(R.id.recyclerKart);
        recyclerViewKart.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewKart.setHasFixedSize(true);
        recyclerViewKart.setAdapter(adapter);

        ItemTouchHelper.Callback callback = new KartItemTouchHelperCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerViewKart);

        layoutContent = (CoordinatorLayout) findViewById(
                R.id.layoutContent);

        FloatingActionButton fabAdd = (FloatingActionButton) findViewById(R.id.btnAdd);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCreateItemActivity();
            }
        });

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {

                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        switch (menuItem.getItemId()) {
                            case R.id.action_add:
                                openCreateItemActivity();
                                drawerLayout.closeDrawer(GravityCompat.START);
                                break;
                            case R.id.action_clear:
                                adapter.clearList();
                                drawerLayout.closeDrawer(GravityCompat.START);
                                break;
                            case R.id.action_about:
                                showSnackBarMessage(getString(R.string.txt_about));
                                drawerLayout.closeDrawer(GravityCompat.START);
                                break;
                            case R.id.action_help:
                                showSnackBarMessage(getString(R.string.txt_help));
                                drawerLayout.closeDrawer(GravityCompat.START);
                                break;
                        }

                        menuItem.setChecked(false);
                        return false;
                    }
                });

        setUpToolBar();

        new MaterialTapTargetPrompt.Builder(MainActivity.this)
                .setTarget(findViewById(R.id.btnAdd))
                .setPrimaryText("New Item")
                .setSecondaryText("Click here to add a new item")
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void setUpToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationIcon(resize(R.drawable.menu_bars));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    private void openCreateItemActivity() {
        Intent intentStart = new Intent(MainActivity.this,
                CreateItem.class);
        startActivityForResult(intentStart, REQUEST_CODE_NEW);
    }

    public void showEditItemActivity(String itemID, int position) {
        Intent intentStart = new Intent(MainActivity.this,
                CreateItem.class);
        positionToEdit = position;

        intentStart.putExtra(KEY_EDIT, itemID);
        startActivityForResult(intentStart, REQUEST_CODE_EDIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                String itemID = data.getStringExtra(
                        CreateItem.KEY_ITEM);

                KartItem item = getRealm().where(KartItem.class)
                        .equalTo("itemID", itemID)
                        .findFirst();

                if (requestCode == REQUEST_CODE_NEW) {
                    adapter.addItem(item);
                    showSnackBarMessage(item.getItemTitle().toString());

                } else if (requestCode == REQUEST_CODE_EDIT) {
                    adapter.updateItem(positionToEdit, item);
                    showSnackBarMessage(getString(hu.ait.android.shoppingkartapp.R.string.edited));
                }

                break;

            case RESULT_CANCELED:
                showSnackBarMessage(getString(R.string.cancelled));
                break;
        }
    }

    public Realm getRealm() {
        return ((ShoppingKartApplication) getApplication()).getRealmKart();
    }

    public void deleteItem(KartItem item) {
        getRealm().beginTransaction();
        item.deleteFromRealm();
        getRealm().commitTransaction();
    }

    private void showSnackBarMessage(String message) {
        Snackbar.make(layoutContent,
                message,
                Snackbar.LENGTH_LONG
        ).show();
    }

    private Drawable resize(int image) {
        Bitmap b = BitmapFactory.decodeResource(getResources(), image);
        Bitmap bitmapResized =
                Bitmap.createScaledBitmap(b, DST_SCALE, DST_SCALE, false);
        return new BitmapDrawable(getResources(), bitmapResized);
    }

    @Override
    protected void onDestroy() {

        ((ShoppingKartApplication) getApplication()).closeRealm();

        super.onDestroy();
    }
}
