package hu.ait.android.shoppingkartapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Date;
import java.util.UUID;

import hu.ait.android.shoppingkartapp.Data.KartItem;
import io.realm.Realm;

/**
 * Created by johnc on 11/9/2017.
 */

public class CreateItem extends AppCompatActivity {
    public static final String KEY_ITEM = "KEY_ITEM";
    private Spinner spinnerItemType;
    private EditText etItem;
    private EditText etItemDesc;
    private EditText etItemCost;
    private CheckBox cbPurchased;
    private KartItem itemToEdit = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_item);

        setupUI();

        if (getIntent().getSerializableExtra(MainActivity.KEY_EDIT) != null) {
            initEdit();
        } else {
            initCreate();
        }
    }

    private void initCreate() {
        getRealm().beginTransaction();
        itemToEdit = getRealm().createObject(KartItem.class, UUID.randomUUID().toString());
        itemToEdit.setCreateDate(new Date(System.currentTimeMillis()).toString());
        getRealm().commitTransaction();
    }

    private void setupUI() {
        spinnerItemType = (Spinner) findViewById(R.id.spinnerItemType);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.itemtype_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerItemType.setAdapter(adapter);

        etItem = (EditText) findViewById(R.id.etItemName);
        etItemDesc = (EditText) findViewById(R.id.etItemDesc);
        etItemCost = (EditText) findViewById(R.id.etItemCost);
        cbPurchased = (CheckBox) findViewById(R.id.cbCreatePurchased);

        Button btnSave = (Button) findViewById(R.id.btnCreateSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveItem();
            }
        });
    }

    private void saveItem() {
        Intent intentResult = new Intent();

        if (etItem.getText().toString().trim().equalsIgnoreCase(getString(R.string.ignore_case)) ||
                etItemCost.getText().toString().trim().equalsIgnoreCase(getString(R.string.ignore_case)) ||
                etItemDesc.getText().toString().trim().equalsIgnoreCase(getString(R.string.ignore_case))) {

            if (etItemCost.getText().toString().trim().equalsIgnoreCase(getString(R.string.ignore_case))) {
                etItemCost.setError(getString(R.string.empty_error_msg));
            }

            if (etItem.getText().toString().trim().equalsIgnoreCase(getString(R.string.ignore_case))) {
                etItem.setError(getString(R.string.empty_error_msg));
            }

            if (etItemDesc.getText().toString().trim().equalsIgnoreCase(getString(R.string.ignore_case))) {
                etItemDesc.setError(getString(R.string.empty_error_msg));
            }
        } else {
            if (itemToEdit != null) {
                getRealm().beginTransaction();
                itemToEdit.setItemTitle(etItem.getText().toString());
                itemToEdit.setItemDescription(etItemDesc.getText().toString());
                itemToEdit.setItemCost(etItemCost.getText().toString());
                itemToEdit.setDone(cbPurchased.isChecked());
                itemToEdit.setItemType(spinnerItemType.getSelectedItemPosition());
                getRealm().commitTransaction();
            }

            intentResult.putExtra(KEY_ITEM, itemToEdit.getItemID());
            setResult(RESULT_OK, intentResult);
            finish();
        }
    }

    private void initEdit() {
        String itemID = getIntent().getStringExtra(MainActivity.KEY_EDIT);
        itemToEdit = getRealm().where(KartItem.class)
                .equalTo("itemID", itemID)
                .findFirst();

        if (itemToEdit != null) {
            etItem.setText(itemToEdit.getItemTitle());
            etItemDesc.setText(itemToEdit.getItemDescription());
            etItemCost.setText(itemToEdit.getItemCost());
            cbPurchased.setChecked(itemToEdit.isDone());
            spinnerItemType.setSelection(itemToEdit.getItemType().getValue());
        }
    }

    public Realm getRealm() {
        return ((ShoppingKartApplication) getApplication()).getRealmKart();
    }
}