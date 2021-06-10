package com.dlit01.budget.features.categoryform;

import android.support.annotation.NonNull;
import com.dlit01.budget.model.Category;
import com.dlit01.budget.util.BasePresenter;

/**
 * Created by 7h1b0.
 */

interface CategoryFormPresenter extends BasePresenter<CategoryFormView> {

  void addCategory(@NonNull Category category);

  void updateCategory(@NonNull Category category);

  void loadBudget();
}
