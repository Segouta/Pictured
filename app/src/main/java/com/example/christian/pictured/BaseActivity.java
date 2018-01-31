package com.example.christian.pictured;

/*
 * By Christian Bijvoets, Minor Programmeren UvA, January 2018.
 * This activity shows a progress dialog when logging in with Google.
 */

import android.app.ProgressDialog;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    @VisibleForTesting
    public ProgressDialog mProgressDialog;

    public void showProgressDialog() {
        // Create and setup new progress dialog if it does not exist already.
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        // Show the dialog.
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        // Dismiss the dialog. But only if it exists and it is showing currently.
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onStop() {
        // When the app is left, hide the progress dialog.
        super.onStop();
        hideProgressDialog();
    }

}