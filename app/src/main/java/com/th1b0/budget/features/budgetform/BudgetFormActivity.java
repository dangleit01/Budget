package com.th1b0.budget.features.budgetform;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.th1b0.budget.R;
import com.th1b0.budget.databinding.ActivityBudgetFormBinding;
import com.th1b0.budget.model.Budget;
import com.th1b0.budget.util.CurrencyUtil;
import com.th1b0.budget.util.DataManager;

/**
 * Created by 7h1b0.
 */

public final class BudgetFormActivity extends AppCompatActivity implements BudgetFormView {

  private Budget mBudget;
  private BudgetFormPresenter mPresenter;
  private ActivityBudgetFormBinding mView;

  public static Intent newInstance(@NonNull Context context) {
    return new Intent(context, BudgetFormActivity.class);
  }

  public static Intent newInstance(@NonNull Context context, @Nullable Budget budget) {
    Intent intent = new Intent(context, BudgetFormActivity.class);
    intent.putExtra(Budget.BUDGET, budget);
    return intent;
  }

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mView = DataBindingUtil.setContentView(this, R.layout.activity_budget_form);
    mPresenter = new BudgetFormPresenterImpl(DataManager.getInstance(this));
    mPresenter.attach(this);

    if (savedInstanceState != null) {
      mBudget = savedInstanceState.getParcelable(Budget.BUDGET);
    } else if (isEditMode()) {
      mBudget = getIntent().getExtras().getParcelable(Budget.BUDGET);
    } else {
      mBudget = new Budget();
    }

    setupToolbar();
    fillForm();
    setupListener();
  }

  private void setupListener() {

    mView.addingToNumber.thousand.setOnClickListener(v -> {
      if(mView.value.getText().length() > 0) {
        String cleanString = mView.value.getText().toString().toString().replaceAll("[$,.]", "");
        double parsed = Double.parseDouble(cleanString)/100;
        String formatted = CurrencyUtil.formatToUSD(parsed*1000);
        mView.value.setText(formatted);
        mView.value.setSelection(formatted.length());
        //double dValue = Double.parseDouble(CurrencyUtil.removeCurrencySymbol(mView.value.getText().toString())) * 1000;
        //mView.value.setText(CurrencyUtil.formatToUSD(dValue));
      }
    });

    mView.addingToNumber.hundredThousand.setOnClickListener(v -> {
      if(mView.value.getText().length() > 0) {
        String cleanString = mView.value.getText().toString().toString().replaceAll("[$,.]", "");
        double parsed = Double.parseDouble(cleanString)/100;
        String formatted = CurrencyUtil.formatToUSD(parsed*100000);
        mView.value.setText(formatted);
        mView.value.setSelection(formatted.length());
        //double dValue = Double.parseDouble(CurrencyUtil.removeCurrencySymbol(mView.value.getText().toString())) * 1000;
        //mView.value.setText(CurrencyUtil.formatToUSD(dValue));
      }
    });

    mView.addingToNumber.hundred.setOnClickListener(v -> {
      if(mView.value.getText().length() > 0) {
        String cleanString = mView.value.getText().toString().toString().replaceAll("[$,.]", "");
        double parsed = Double.parseDouble(cleanString)/100;
        String formatted = CurrencyUtil.formatToUSD(parsed*100);
        mView.value.setText(formatted);
        mView.value.setSelection(formatted.length());
        //double dValue = Double.parseDouble(CurrencyUtil.removeCurrencySymbol(mView.value.getText().toString())) * 1000;
        //mView.value.setText(CurrencyUtil.formatToUSD(dValue));
      }
    });

    mView.addingToNumber.ten.setOnClickListener(v -> {
      if(mView.value.getText().length() > 0) {
        String cleanString = mView.value.getText().toString().toString().replaceAll("[$,.]", "");
        double parsed = Double.parseDouble(cleanString)/100;
        String formatted = CurrencyUtil.formatToUSD(parsed*10);
        mView.value.setText(formatted);
        mView.value.setSelection(formatted.length());
        //double dValue = Double.parseDouble(CurrencyUtil.removeCurrencySymbol(mView.value.getText().toString())) * 1000;
        //mView.value.setText(CurrencyUtil.formatToUSD(dValue));
      }
    });

    mView.value.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {

      }
      private String current = "";
      @Override
      public void afterTextChanged(Editable s) {
        if(!s.toString().equals(current)){
          mView.value.removeTextChangedListener(this);

          String cleanString = s.toString().replaceAll("[$,.]", "");

          double parsed = Double.parseDouble(cleanString)/100;
          String formatted = CurrencyUtil.formatToUSD(parsed);
          //String formatted = NumberFormat.getCurrencyInstance().format((parsed/100));

          current = formatted;
          mView.value.setText(formatted);
          mView.value.setSelection(formatted.length());
          mView.value.addTextChangedListener(this);
        }
      }
    });
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.wizard, menu);
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        finish();
        return true;

      case R.id.save:
        if (isFormValid()) {
          updateBudgetFromForm();
          if (isEditMode()) {
            mPresenter.updateBudget(mBudget);
          } else {
            mPresenter.addBudget(mBudget);
          }
        }
        return true;

      default:
        return false;
    }
  }

  private boolean isEditMode() {
    return getIntent().hasExtra(Budget.BUDGET)
        && getIntent().getExtras().getParcelable(Budget.BUDGET) != null;
  }

  private void setupToolbar() {
    setSupportActionBar(mView.included.toolbar);
    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_close);
      if (isEditMode()) {
        getSupportActionBar().setTitle(R.string.edit_budget);
      } else {
        getSupportActionBar().setTitle(R.string.add_budget);
      }
    }
  }

  private void fillForm() {
    mView.title.setText(mBudget.getTitle());
    if (mBudget.getValue() != 0) {
      String formatted = CurrencyUtil.formatToUSD(mBudget.getValue());
      mView.value.setText(formatted);
      //mView.value.setText(String.valueOf(mBudget.getValue()));
    }
  }

  private boolean isFormValid() {
    boolean isValid = true;
    if (mView.title.getText().length() == 0) {
      mView.titleInputLayout.setErrorEnabled(true);
      mView.titleInputLayout.setError(getString(R.string.no_empty_field));
      isValid = false;
    } else {
      mView.titleInputLayout.setErrorEnabled(false);
    }

    if (mView.value.getText().length() == 0) {
      mView.valueInputLayout.setErrorEnabled(true);
      mView.valueInputLayout.setError(getString(R.string.no_empty_field));
      isValid = false;
    } else {
      mView.valueInputLayout.setErrorEnabled(false);
    }

    return isValid;
  }

  private void updateBudgetFromForm() {
    mBudget.setTitle(mView.title.getText().toString());
    try {
      String cleanString = mView.value.getText().toString().toString().replaceAll("[$,.]", "");
      double parsed = Double.parseDouble(cleanString)/100;
      mBudget.setValue(parsed);
      //mBudget.setValue(Double.parseDouble(mView.value.getText().toString()));
    } catch (NumberFormatException e) {
      // Nothing
    }
  }

  @Override public void onAddSucceeded() {
    finish();
  }

  @Override public void onUpdateSucceeded() {
    finish();
  }

  @Override public void onError(@Nullable String error) {
    // Nothing yet
  }
}

