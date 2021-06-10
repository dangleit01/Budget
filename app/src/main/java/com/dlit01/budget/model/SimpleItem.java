package com.dlit01.budget.model;

/**
 * Created by 7h1b0.
 */

public interface SimpleItem {

  String getTitle();

  double getValue();

  boolean equals(Object o);

  int hashCode();
}
