package com.dlit01.budget.features.history;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.dlit01.budget.model.PresentationHistory;
import java.util.ArrayList;

/**
 * Created by 7h1b0.
 */

interface HistoryView {

  void onHistoryLoaded(@NonNull ArrayList<PresentationHistory> histories);

  void onError(@Nullable String error);
}
