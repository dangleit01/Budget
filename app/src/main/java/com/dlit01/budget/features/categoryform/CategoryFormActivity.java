package com.dlit01.budget.features.categoryform;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import com.dlit01.budget.R;
import com.dlit01.budget.databinding.ActivityCategoryFormBinding;
import com.dlit01.budget.model.Budget;
import com.dlit01.budget.model.Category;
import com.dlit01.budget.util.BudgetPickerDialog;
import com.dlit01.budget.util.DataManager;
import com.dlit01.budget.util.FromBudgetPickerDialog;

import java.util.ArrayList;

/**
 * Created by 7h1b0.
 */

public final class CategoryFormActivity extends AppCompatActivity
    implements ColorPickerDialog.OnColorSet, IconPickerDialog.OnIconSet, CategoryFormView,
    BudgetPickerDialog.OnBudgetSet, FromBudgetPickerDialog.OnFromBudgetSet {

  private Category mCategory;
  private CategoryFormPresenter mPresenter;
  private ActivityCategoryFormBinding mView;
  private ArrayList<Budget> mBudgets;

  public static Intent newInstance(@NonNull Context context) {
    return new Intent(context, CategoryFormActivity.class);
  }

  public static Intent newInstance(@NonNull Context context, @Nullable Category category) {
    Intent intent = new Intent(context, CategoryFormActivity.class);
    intent.putExtra(Category.CATEGORY, category);
    return intent;
  }

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mView = DataBindingUtil.setContentView(this, R.layout.activity_category_form);
    mPresenter = new CategoryFormPresenterImpl(DataManager.getInstance(this));
    mPresenter.attach(this);
    mBudgets = new ArrayList<>();

    if (savedInstanceState != null) {
      mCategory = savedInstanceState.getParcelable(Category.CATEGORY);
    } else if (isEditMode()) {
      mCategory = getIntent().getExtras().getParcelable(Category.CATEGORY);
    } else {
      mCategory = new Category(ContextCompat.getColor(this, R.color.colorPrimary));
    }

    setupToolbar();
    fillForm();
    setupListener();

    if (!isBudgetsLoaded(savedInstanceState)) {
      mPresenter.loadBudget();
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
          updateCategoryFromForm();
          if (isEditMode()) {
            mPresenter.updateCategory(mCategory);
          } else {
            mPresenter.addCategory(mCategory);
          }
        }
        return true;

      default:
        return false;
    }
  }

  @Override protected void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    mBudgets = savedInstanceState.getParcelableArrayList(Budget.BUDGETS);
    updateBudget();
    updateFromBudget();
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putParcelable(Category.CATEGORY, mCategory);
    outState.putParcelableArrayList(Budget.BUDGETS, mBudgets);
  }

  private boolean isEditMode() {
    return getIntent().hasExtra(Category.CATEGORY)
        && getIntent().getExtras().getParcelable(Category.CATEGORY) != null;
  }

  private boolean isBudgetsLoaded(@Nullable Bundle savedInstanceState) {
    return savedInstanceState != null
        && savedInstanceState.getParcelableArrayList(Budget.BUDGETS) != null;
  }

  private void setupToolbar() {
    setSupportActionBar(mView.included.toolbar);
    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_close);
      if (isEditMode()) {
        getSupportActionBar().setTitle(R.string.edit_category);
      } else {
        getSupportActionBar().setTitle(R.string.add_category);
      }
    }
  }

  private void fillForm() {
    mView.title.setText(mCategory.getTitle());
    mView.description.setText(mCategory.getDescription());
    updateColor();
    updateIcon();
  }

  private void updateColor() {
    mView.color.setColorFilter(mCategory.getColor());
  }

  private void updateIcon() {
    mView.icon.setImageResource(mCategory.getIcon());
  }

  private void updateBudget() {
    if (mBudgets == null) return;

    int position = findBudgetPosition(mCategory.getIdBudget());
    if (position > -1) {
      mView.budget.setText(mBudgets.get(position).getTitle());
    }
  }

  private void updateFromBudget() {
    if (mBudgets == null) return;

    int position = findBudgetPosition(mCategory.getIdFromBudget());
    if (position > -1) {
      mView.fromBudget.setText(mBudgets.get(position).getTitle());
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

    if (mView.description.getText().length() == 0) {
      mView.descriptionInputLayout.setErrorEnabled(true);
      mView.descriptionInputLayout.setError(getString(R.string.no_empty_field));
      isValid = false;
    } else {
      mView.descriptionInputLayout.setErrorEnabled(false);
    }

    return isValid;
  }

  private void updateCategoryFromForm() {
    mCategory.setTitle(mView.title.getText().toString());
    mCategory.setDescription(mView.description.getText().toString());
  }

  private void setupListener() {
    mView.colorLayout.setOnClickListener(v -> {
      hideKeyboard();
      ColorPickerDialog.newInstance(mCategory.getColor()).show(getFragmentManager(), null);
    });

    mView.iconLayout.setOnClickListener(v -> {
      hideKeyboard();
      IconPickerDialog.newInstance().show(getFragmentManager(), null);
    });

    mView.containerLayout.setOnClickListener(v -> {
      hideKeyboard();
      int position = findBudgetPosition(mCategory.getIdBudget());
      BudgetPickerDialog.newInstance(mBudgets, position).show(getFragmentManager(), null);
    });

    mView.containerLayoutFromBudget.setOnClickListener(v -> {
      hideKeyboard();
      int position = findBudgetPosition(mCategory.getIdFromBudget());
      FromBudgetPickerDialog.newInstance(mBudgets, position).show(getFragmentManager(), null);
    });
  }

  @Override public void onIconSet(@DrawableRes int icon) {
    mCategory.setIcon(icon);
    updateIcon();
  }

  @Override public void onColorSet(@ColorInt int color) {
    mCategory.setColor(color);
    updateColor();
  }

  @Override public void onBudgetLoaded(@NonNull ArrayList<Budget> budgets) {
    mBudgets = budgets;
    if (mCategory.getIdBudget() == -1 && !budgets.isEmpty()) {
      mCategory.setIdBudget(budgets.get(0).getId());
    }
    if (mCategory.getIdFromBudget() == -1 && !budgets.isEmpty()) {
      mCategory.setIdFromBudget(budgets.get(0).getId());
    }
    updateBudget();
    updateFromBudget();
  }

  @Override public void onError(@Nullable final String error) {
    String msg = error;
    if (TextUtils.isEmpty(msg)) {
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

  private int findBudgetPosition(long selected) {
    for (int index = 0; index < mBudgets.size(); index++) {
      if (mBudgets.get(index).getId() == selected) {
        return index;
      }
    }
    return -1;
  }

  @Override public void onBudgetSet(@NonNull Budget budget) {
    mCategory.setIdBudget(budget.getId());
    updateBudget();
  }

  @Override public void onFromBudgetSet(@NonNull Budget budget) {
    mCategory.setIdFromBudget(budget.getId());
    updateFromBudget();
  }

  private void hideKeyboard() {
    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(mView.getRoot().getWindowToken(), 0);
  }
}
