package com.dlit01.budget.features.categoryform;

import android.support.annotation.NonNull;
import com.dlit01.budget.R;
import com.dlit01.budget.model.Budget;
import com.dlit01.budget.model.Category;
import com.dlit01.budget.util.DataManager;
import com.dlit01.budget.util.PresenterImpl;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by 7h1b0.
 */

final class CategoryFormPresenterImpl extends PresenterImpl<CategoryFormView>
    implements CategoryFormPresenter {

  CategoryFormPresenterImpl(@NonNull final DataManager dataManager) {
    super(dataManager);
  }

  @Override public void addCategory(@NonNull final Category category) {
    mSubscription.add(mDataManager.addCategory(category).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(id -> {
          CategoryFormView view = getView();
          if (view != null) {
            view.onAddSucceeded();
          }
        }, err -> {
          CategoryFormView view = getView();
          if (view != null) {
            view.onError(err.getMessage());
          }
        }));
  }

  @Override public void updateCategory(@NonNull final Category category) {
    mSubscription.add(mDataManager.updateCategory(category).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(id -> {
          CategoryFormView view = getView();
          if (view != null) {
            view.onUpdateSucceeded();
          }
        }, err -> {
          CategoryFormView view = getView();
          if (view != null) {
            view.onError(err.getMessage());
          }
        }));
  }

  @Override public void loadBudget() {
    mSubscription.add(mDataManager.getBudgets()
        .map(budgets -> {
          budgets.add(0,
              new Budget(Budget.NONE, getView().getContext().getString(R.string.none), 0));
          return budgets;
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(budgets -> {
          CategoryFormView view = getView();
          if (view != null) {
            view.onBudgetLoaded(budgets);
          }
        }, error -> {
          CategoryFormView view = getView();
          if (view != null) {
            view.onError(error.getMessage());
          }
        }));
  }
}
