// Generated by view binder compiler. Do not edit!
package com.example.medsaveinv.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.medsaveinv.R;
import de.hdodenhof.circleimageview.CircleImageView;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class DrawerBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final LinearLayout llInvestigationQSurvey;

  @NonNull
  public final LinearLayout lllogout;

  @NonNull
  public final CircleImageView profileImage;

  @NonNull
  public final TextView tvUserName;

  private DrawerBinding(@NonNull LinearLayout rootView,
      @NonNull LinearLayout llInvestigationQSurvey, @NonNull LinearLayout lllogout,
      @NonNull CircleImageView profileImage, @NonNull TextView tvUserName) {
    this.rootView = rootView;
    this.llInvestigationQSurvey = llInvestigationQSurvey;
    this.lllogout = lllogout;
    this.profileImage = profileImage;
    this.tvUserName = tvUserName;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static DrawerBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static DrawerBinding inflate(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent,
      boolean attachToParent) {
    View root = inflater.inflate(R.layout.drawer, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static DrawerBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.llInvestigationQSurvey;
      LinearLayout llInvestigationQSurvey = ViewBindings.findChildViewById(rootView, id);
      if (llInvestigationQSurvey == null) {
        break missingId;
      }

      id = R.id.lllogout;
      LinearLayout lllogout = ViewBindings.findChildViewById(rootView, id);
      if (lllogout == null) {
        break missingId;
      }

      id = R.id.profileImage;
      CircleImageView profileImage = ViewBindings.findChildViewById(rootView, id);
      if (profileImage == null) {
        break missingId;
      }

      id = R.id.tvUserName;
      TextView tvUserName = ViewBindings.findChildViewById(rootView, id);
      if (tvUserName == null) {
        break missingId;
      }

      return new DrawerBinding((LinearLayout) rootView, llInvestigationQSurvey, lllogout,
          profileImage, tvUserName);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
