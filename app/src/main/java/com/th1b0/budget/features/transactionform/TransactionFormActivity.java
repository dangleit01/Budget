package com.th1b0.budget.features.transactionform;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import com.th1b0.budget.R;
import com.th1b0.budget.databinding.ActivityTransactionFormBinding;
import com.th1b0.budget.model.Budget;
import com.th1b0.budget.model.Category;
import com.th1b0.budget.model.Transaction;
import com.th1b0.budget.util.BudgetPickerDialog;
import com.th1b0.budget.util.CurrencyUtil;
import com.th1b0.budget.util.DataManager;
import com.th1b0.budget.util.DateUtil;
import com.th1b0.budget.util.TargetBudgetPickerDialog;

import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * Created by 7h1b0.
 */

public final class TransactionFormActivity extends AppCompatActivity
    implements DatePickerDialog.OnDateSetListener, CategoryDialog.OnCategorySet,
    TransactionFormView, BudgetPickerDialog.OnBudgetSet, TargetBudgetPickerDialog.OnTargetBudgetSet {

  private ActivityTransactionFormBinding mView;
  private Transaction mTransaction;
  private TransactionFormPresenter mPresenter;
  private ArrayList<Category> mCategories;
  private ArrayList<Budget> mBudgets;

  public static Intent newInstance(@NonNull Context context) {
    return new Intent(context, TransactionFormActivity.class);
  }

  public static Intent newInstance(@NonNull Context context, @Nullable Transaction transaction) {
    Intent intent = new Intent(context, TransactionFormActivity.class);
    intent.putExtra(Transaction.TRANSACTION, transaction);
    return intent;
  }

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mView = DataBindingUtil.setContentView(this, R.layout.activity_transaction_form);
    mPresenter = new TransactionFormPresenterImpl(DataManager.getInstance(this));
    mPresenter.attach(this);
    mCategories = new ArrayList<>();
    mBudgets = new ArrayList<>();

    if (savedInstanceState != null) {
      mTransaction = savedInstanceState.getParcelable(Transaction.TRANSACTION);
    } else if (isEditMode()) {
      mTransaction = getIntent().getExtras().getParcelable(Transaction.TRANSACTION);
    } else {
      mTransaction = new Transaction();
    }

    setupToolbar();
    fillForm();
    setupListener();

    if (!isCategoriesAndBudgetsLoaded(savedInstanceState)) {
      mPresenter.loadCategoriesAndBudgets();
    }
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
          updateTransactionFromForm();
          if (isEditMode()) {
            mPresenter.updateTransaction(mTransaction);
          } else {
            mPresenter.addTransaction(mTransaction);
          }
        }
        return true;

      default:
        return false;
    }
  }

  @Override protected void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    mCategories = savedInstanceState.getParcelableArrayList(Category.CATEGORIES);
    mBudgets = savedInstanceState.getParcelableArrayList(Budget.BUDGETS);
    updateCategory();
    updateBudget();
    updateTargetBudget();
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putParcelable(Transaction.TRANSACTION, mTransaction);
    outState.putParcelableArrayList(Category.CATEGORIES, mCategories);
    outState.putParcelableArrayList(Budget.BUDGETS, mBudgets);
  }

  @Override protected void onStop() {
    super.onStop();
    mPresenter.detach();
  }

  private void setupToolbar() {
    setSupportActionBar(mView.included.toolbar);
    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_close);
      if (isEditMode()) {
        getSupportActionBar().setTitle(R.string.edit_transaction);
      } else {
        getSupportActionBar().setTitle(R.string.add_transaction);
      }
    }
  }

  private void fillForm() {
    mView.description.setText(mTransaction.getDescription());
    if (mTransaction.getValue() != 0) {
      mView.value.setText(CurrencyUtil.formatToUSD(mTransaction.getValue()));
    }
    updateDate();
  }

  private void updateDate() {
    mView.date.setText(DateUtil.formatDate(mTransaction.getYear(), mTransaction.getMonth(),
        mTransaction.getDay()));
  }

  private void updateCategory() {
    int position = findCategoryPosition(mTransaction.getIdCategory());
    if (position > -1) {
      mView.category.setText(mCategories.get(position).getTitle());
    }
  }

  private void updateBudget() {
    int position = findBudgetPosition(mTransaction.getIdBudget());
    if (position > -1) {
      mView.budget.setText(mBudgets.get(position).getTitle());
    }
  }

  private void updateTargetBudget() {
    int position = findBudgetPosition(mTransaction.getIdTargetBudget());
    if (position > -1) {
      mView.targetBudget.setText(mBudgets.get(position).getTitle());
    } else {
      if(!mBudgets.isEmpty()) {
        mTransaction.setIdTargetBudget(mBudgets.get(0).getId());
        mView.targetBudget.setText(mBudgets.get(0).getTitle());
      }
    }
  }

  private void setupListener() {
    mView.dateLayout.setOnClickListener(v -> {
      hideKeyboard();
      AlertDialog dialog =
          new DatePickerDialog(this, this, mTransaction.getYear(), mTransaction.getMonth(),
              mTransaction.getDay());
      dialog.show();
    });

    mView.categoryLayout.setOnClickListener(v -> {
      hideKeyboard();
      int position = findCategoryPosition(mTransaction.getIdCategory());
      CategoryDialog.newInstance(mCategories, position).show(getFragmentManager(), null);
    });

    mView.containerLayout.setOnClickListener(v -> {
      hideKeyboard();
      int position = findBudgetPosition(mTransaction.getIdBudget());
      BudgetPickerDialog.newInstance(mBudgets, position).show(getFragmentManager(), null);
    });

    mView.containerLayoutTarget.setOnClickListener(v -> {
      hideKeyboard();
      int position = findBudgetPosition(mTransaction.getIdTargetBudget());
      TargetBudgetPickerDialog.newInstance(mBudgets, position).show(getFragmentManager(), null);
    });

    mView.thousand.setOnClickListener(v -> {
      if(mView.value.getText().length() > 0) {
        String cleanString = mView.value.getText().toString().toString().replaceAll("[$,.]", "");
        double parsed = Double.parseDouble(cleanString);
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

          double parsed = Double.parseDouble(cleanString);
          String formatted = CurrencyUtil.formatToUSD(parsed/100);
          //String formatted = NumberFormat.getCurrencyInstance().format((parsed/100));

          current = formatted;
          mView.value.setText(formatted);
          mView.value.setSelection(formatted.length());
          mView.value.addTextChangedListener(this);
          }
      }
    });
  }

  private void updateTransactionFromForm() {
    mTransaction.setDescription(mView.description.getText().toString());

    try {
      double value = Double.parseDouble(mView.value.getText().toString());
      mTransaction.setValue(value);
    } catch (NumberFormatException e) {
      // Don't update transaction value;
    }
  }

  private boolean isEditMode() {
    return getIntent().hasExtra(Transaction.TRANSACTION)
        && getIntent().getExtras().getParcelable(Transaction.TRANSACTION) != null;
  }

  private boolean isFormValid() {
    boolean isValid = true;
    if (mView.description.getText().length() == 0) {
      mView.descriptionInputLayout.setErrorEnabled(true);
      mView.descriptionInputLayout.setError(getString(R.string.no_empty_field));
      isValid = false;
    } else {
      mView.descriptionInputLayout.setErrorEnabled(false);
    }

    if (mView.value.getText().length() == 0) {
      mView.valueInputLayout.setErrorEnabled(true);
      mView.valueInputLayout.setError(getString(R.string.no_empty_field));
      isValid = false;
    } else {
      mView.valueInputLayout.setErrorEnabled(false);
    }

    if (mTransaction.getIdBudget() == mTransaction.getIdTargetBudget()) {
      mView.targetBudgetError.setVisibility(View.VISIBLE);
      mView.targetBudgetError.setText(getString(R.string.no_same_budget));
      isValid = false;
    } else {
      mView.targetBudgetError.setVisibility(View.INVISIBLE);
    }

    return isValid;
  }

  private boolean isCategoriesAndBudgetsLoaded(@Nullable Bundle savedInstanceState) {
    return savedInstanceState != null
        && savedInstanceState.getParcelableArray(Category.CATEGORY) != null
        && savedInstanceState.getParcelableArray(Budget.BUDGETS) != null;
  }

  @Override public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
    mTransaction.setYear(year);
    mTransaction.setMonth(month);
    mTransaction.setDay(dayOfMonth);
    updateDate();
  }

  @Override public void onCategorySet(@NonNull Category category) {
    mTransaction.setIdCategory(category.getId());
    mTransaction.setIdBudget(category.getIdBudget());
    updateCategory();
    updateBudget();
  }

  @Override public void onBudgetSet(@NonNull Budget budget) {
    mTransaction.setIdBudget(budget.getId());
    mView.budget.setText(budget.getTitle());
  }

  @Override public void onTargetBudgetSet(@NonNull Budget budget) {
    mTransaction.setIdTargetBudget(budget.getId());
    mView.targetBudget.setText(budget.getTitle());
  }

  @Override public void onCategoriesLoaded(@NonNull ArrayList<Category> categories) {
    mCategories = categories;

    Category category;
    int positionCategory = findCategoryPosition(mTransaction.getIdCategory());
    if (positionCategory == -1) {
      category = categories.get(0);
      mTransaction.setIdCategory(category.getId());
    } else {
      category = mCategories.get(positionCategory);
    }

    if (!mTransaction.isBudgetIdDefined()) {
      if (category.isBudgetIdDefined()) {
        mTransaction.setIdBudget(category.getIdBudget());
      } else {
        mTransaction.setIdBudget(mBudgets.get(0).getId());
      }
    }

    updateCategory();
    updateBudget();
  }

  @Override public void onBudgetsLoaded(@NonNull ArrayList<Budget> budgets) {
    mBudgets = budgets;
    updateTargetBudget();
  }

  @Override public void onError(@Nullable final String error) {
    String msg = error;
    if (TextUtils.isEmpty(error)) {
      msg = getString(R.string.error_occurred);
    }

    Snackbar.make(mView.coordinator, msg, Snackbar.LENGTH_LONG).show();
  }

  @Override public void onAddSucceeded() {
    finish();
  }

  @Override public void onUpdateSucceeded() {
    finish();
  }

  @NonNull @Override public Context getContext() {
    return this;
  }

  private int findCategoryPosition(long selected) {
    for (int index = 0; index < mCategories.size(); index++) {
      if (mCategories.get(index).getId() == selected) {
        return index;
      }
    }
    return -1;
  }

  private int findBudgetPosition(long selected) {
    for (int index = 0; index < mBudgets.size(); index++) {
      if (mBudgets.get(index).getId() == selected) {
        return index;
      }
    }
    return -1;
  }

  private void hideKeyboard() {
    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(mView.getRoot().getWindowToken(), 0);
  }
}
