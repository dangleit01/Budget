package com.dlit01.budget.features.categoryform;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.util.Log;
import com.jakewharton.rxbinding2.widget.RxSeekBar;
import com.dlit01.budget.R;
import com.dlit01.budget.databinding.DialogColorPickerBinding;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by 7h1b0.
 */

public final class ColorPickerDialog extends DialogFragment {
  public static final String COLOR = "color";

  private int mCurrentColor;
  private DialogColorPickerBinding mView;
  private CompositeDisposable mSubscription;
  private OnColorSet mListener;

  public interface OnColorSet {
    void onColorSet(@ColorInt int color);
  }

  public static ColorPickerDialog newInstance(@ColorInt int color) {
    ColorPickerDialog dialog = new ColorPickerDialog();
    Bundle args = new Bundle();
    args.putInt(COLOR, color);
    dialog.setArguments(args);
    return dialog;
  }

  @Override public void onAttach(Activity activity) {
    super.onAttach(activity);
    if (!(activity instanceof OnColorSet)) {
      throw new IllegalStateException("Activity must implement OnColorSet");
    } else {
      mListener = (OnColorSet) activity;
    }
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mSubscription = new CompositeDisposable();
  }

  @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    mView = DataBindingUtil.inflate(getActivity().getLayoutInflater(), R.layout.dialog_color_picker,
        null, false);

    mCurrentColor = savedInstanceState == null ? getArguments().getInt(COLOR)
        : savedInstanceState.getInt(COLOR);
    int red = Color.red(mCurrentColor);
    int green = Color.green(mCurrentColor);
    int blue = Color.blue(mCurrentColor);

    mView.red.setProgress(red);
    mView.green.setProgress(green);
    mView.blue.setProgress(blue);

    mView.preview.setBackgroundColor(Color.rgb(red, green, blue));

    mSubscription.add(
        Observable.combineLatest(RxSeekBar.changes(mView.red), RxSeekBar.changes(mView.green),
            RxSeekBar.changes(mView.blue), (r, g, b) -> {
              mView.redValue.setText(String.valueOf(r));
              mView.greenValue.setText(String.valueOf(g));
              mView.blueValue.setText(String.valueOf(b));
              return Color.rgb(r, g, b);
            })
            .skip(1)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe(this::updatePreview,
                error -> Log.e("DetailsHueActivity", error.getMessage())));

    return new AlertDialog.Builder(getActivity()).setView(mView.getRoot())
        .setPositiveButton(R.string.ok, (dialog, which) -> {
          if (mListener != null) {
            mListener.onColorSet(mCurrentColor);
          }
        })
        .setNegativeButton(R.string.cancel, null)
        .create();
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putInt(COLOR, mCurrentColor);
  }

  @Override public void onDismiss(DialogInterface dialog) {
    super.onDismiss(dialog);
    mSubscription.clear();
  }

  private void updatePreview(@ColorInt int colorTo) {
    int colorFrom = mCurrentColor;
    mCurrentColor = colorTo;

    ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
    colorAnimation.setDuration(350);
    colorAnimation.addUpdateListener(
        animator -> mView.preview.setBackgroundColor((int) animator.getAnimatedValue()));
    colorAnimation.start();
  }
}
