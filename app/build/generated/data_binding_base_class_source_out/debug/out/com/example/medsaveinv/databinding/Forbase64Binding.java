// Generated by view binder compiler. Do not edit!
package com.example.medsaveinv.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.medsaveinv.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class Forbase64Binding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final Button btncopy;

  @NonNull
  public final EditText text;

  private Forbase64Binding(@NonNull LinearLayout rootView, @NonNull Button btncopy,
      @NonNull EditText text) {
    this.rootView = rootView;
    this.btncopy = btncopy;
    this.text = text;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static Forbase64Binding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static Forbase64Binding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.forbase64, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static Forbase64Binding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.btncopy;
      Button btncopy = ViewBindings.findChildViewById(rootView, id);
      if (btncopy == null) {
        break missingId;
      }

      id = R.id.text;
      EditText text = ViewBindings.findChildViewById(rootView, id);
      if (text == null) {
        break missingId;
      }

      return new Forbase64Binding((LinearLayout) rootView, btncopy, text);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
