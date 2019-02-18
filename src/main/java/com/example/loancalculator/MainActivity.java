package com.example.loancalculator;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.DeadObjectException;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;



public class MainActivity extends AppCompatActivity {
    private ArrayList<String> loanTypesArray = new ArrayList<>();
    private Spinner loanSpinner;
    private SeekBar sbAmount;
    private SeekBar sbRate;
    private SeekBar sbYears;
    private TextView tvAmount;
    private TextView tvRate;
    private TextView tvYears;
    private double amount;
    private double rate;
    private int years;
    private ListView lvResult;
    private static ArrayList<Termin> terminArray;
    private static int loanType;

    private static final int REQUEST_ID_WRITE_PERMISSION = 200;

    static final String STATE_AMOUNT = "amount";
    static final String STATE_RATE = "rate";
    static final String STATE_YEARS = "years";
    static final String STATE_LOAN_TYPE = "loanType";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        reset();
        setSeekbarValues();
        addListenerToSpinner();

    }

    private void addListenerToSpinner() {
        loanSpinner = this.findViewById(R.id.action_bar_spinner);
        final String[] loanTypes = getResources().getStringArray(R.array.spinner_array);

        // Fanger opp event:
        loanSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loanType = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<String> loanArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, loanTypes);
        loanSpinner.setAdapter(loanArrayAdapter);
        loanArrayAdapter.notifyDataSetChanged();
    }

    private void setSeekbarValues() {
        sbAmount = findViewById(R.id.sbAmount);
        sbRate = findViewById(R.id.sbRate);
        sbYears = findViewById(R.id.sbYears);

        tvAmount = findViewById(R.id.tvAmount);
        tvRate =  findViewById(R.id.tvRate);
        tvYears = findViewById(R.id.tvYears);

        sbAmount.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                        amount = ((double) progressValue) / 100;
                        tvAmount.setText(amount + " " + getString(R.string.mill));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {}

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {}
                });

        sbRate.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                        rate = ((double) progressValue) / 10;
                        tvRate.setText(rate + getString(R.string.percent));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {}

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {}
                });

        sbYears.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                        years = progressValue;
                        tvYears.setText(Integer.toString(years));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {}

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {}
                });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    /** Called when the user taps a button */
    public void btnClick(View view) {
        switch (view.getId()) {
            case R.id.btnReset:
                reset();
                break;
            case R.id.btnCalculate:
                if(amount == 0 || years == 0 || rate == 0 || loanType == 0){
                    Toast.makeText(MainActivity.this, R.string.errorCalculate , Toast.LENGTH_LONG).show();
                } else if(loanType == 1){
                    calculateAnnuity();
                } else if(loanType == 2){
                    calculateSerial();
                }
                break;
        }
    }

    /** Called when the user taps a menu item */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_export:
                if(terminArray == null) {
                    Toast.makeText(MainActivity.this, R.string.errorExport , Toast.LENGTH_LONG).show();
                } else if(terminArray.isEmpty()){
                    Toast.makeText(MainActivity.this, R.string.errorExport , Toast.LENGTH_LONG).show();
                } else {
                    askPermissionAndWriteFile();
                }
                return true;

            case R.id.action_reset:
                reset();
                return true;

            case R.id.action_help:
                help();
                return true;

            case R.id.action_exit:
                this.finish();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    // Vi har brukt tutorialen her: https://o7planning.org/en/10541/android-external-storage-tutorial
    private void export() {
        JSONArray terminJson = new JSONArray();
        JSONObject sampleObject;
        String fileName = "Loan_" +  String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())) +".json";

        File extStore = Environment.getExternalStorageDirectory();
        // ==> /storage/emulated/0/note.txt
        String path = extStore.getAbsolutePath() + "/Download/" + fileName;

        for (int i = 0; i < years; i++) {

            try {
                sampleObject = new JSONObject();
                sampleObject.put("year", String.format("%d", terminArray.get(i).getYear()));
                sampleObject.put("totalPayment", String.format("%.2f", terminArray.get(i).getTotalPayment()));
                sampleObject.put("interests", String.format("%.2f", terminArray.get(i).getInterests()));
                sampleObject.put("principal", String.format("%.2f", terminArray.get(i).getPrincipal()));
                sampleObject.put("remainingDebt", String.format("%.2f", terminArray.get(i).getRemainingDebt()));
                terminJson.put(sampleObject);
            } catch (JSONException e) {
                e.printStackTrace();

            }
        }
        try {
            File myFile = new File(path);
            myFile.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(myFile);

            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            outputStreamWriter.append(terminJson.toString());

            outputStreamWriter.close();
            fileOutputStream.close();
            Toast.makeText(MainActivity.this, R.string.exported, Toast.LENGTH_LONG).show();
        }
        catch (IOException e) {
            Log.e("TAG", "File write failed: " + e.toString());
        }

    }

    private void askPermissionAndWriteFile() {
        boolean canWrite = askPermission();
        //
        if (canWrite) {
            export();
        }
    }

    private boolean askPermission() {
        if (android.os.Build.VERSION.SDK_INT >= 23) {

            // Check if we have permission
            int permission = ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (permission != PackageManager.PERMISSION_GRANTED) {
                // If don't have permission so prompt the user.
                this.requestPermissions(
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_ID_WRITE_PERMISSION
                );
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //
        // Note: If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0) {
            switch (requestCode) {
                case REQUEST_ID_WRITE_PERMISSION: {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        askPermissionAndWriteFile();
                    }
                }
            }
        } else {
            Toast.makeText(getApplicationContext(), R.string.permission_denied, Toast.LENGTH_SHORT).show();
        }
    }

    private void calculateAnnuity() {

        AnnuityLoan loan = new AnnuityLoan(amount, rate, years);
        terminArray = loan.calculateTerminArray();

        lvResult = findViewById(R.id.lvResult);
        TerminAdapter terminArrayAdapter = new TerminAdapter(this, R.layout.listview_layout, terminArray);
        terminArrayAdapter.notifyDataSetChanged();
        lvResult.setAdapter(terminArrayAdapter);
    }

    private void calculateSerial() {
        //TODO: calculate serial loan
        Toast.makeText(MainActivity.this, R.string.calculateSeries , Toast.LENGTH_LONG).show();

    }

    private void reset() {
        tvAmount = findViewById(R.id.tvAmount);
        tvRate =  findViewById(R.id.tvRate);
        tvYears = findViewById(R.id.tvYears);

        sbAmount = findViewById(R.id.sbAmount);
        sbRate = findViewById(R.id.sbRate);
        sbYears = findViewById(R.id.sbYears);

        lvResult = findViewById(R.id.lvResult);

        tvAmount.setText(getString(R.string.txt_amountHint) + " " + getString(R.string.mill));
        tvRate.setText(getString(R.string.txt_rateHint) + getString(R.string.percent));
        tvYears.setText(getString(R.string.txt_yearsHint));

        sbAmount.setProgress(0);
        sbRate.setProgress(0);
        sbYears.setProgress(0);

        lvResult.setAdapter(null);

        if(terminArray != null) {
            terminArray.clear();
        }

    }

    private void help() {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle(getString(R.string.menu_Help));
        alertDialog.setMessage(getString(R.string.txt_Help));
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.txt_Confirm),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putDouble(STATE_AMOUNT, amount);
        savedInstanceState.putDouble(STATE_RATE, rate);
        savedInstanceState.putInt(STATE_YEARS, years);
        savedInstanceState.putInt(STATE_LOAN_TYPE, loanType);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        amount = savedInstanceState.getDouble(STATE_AMOUNT);
        rate = savedInstanceState.getDouble(STATE_RATE);
        years = savedInstanceState.getInt(STATE_YEARS);
        loanType = savedInstanceState.getInt(STATE_LOAN_TYPE);

        switch (loanType){
            case 0:
                Toast.makeText(MainActivity.this, R.string.errorCalculate , Toast.LENGTH_LONG).show();
                break;
            case 1:
                calculateAnnuity();
                break;
            case 2:
                calculateSerial();

        }

        super.onRestoreInstanceState(savedInstanceState);
    }
}
